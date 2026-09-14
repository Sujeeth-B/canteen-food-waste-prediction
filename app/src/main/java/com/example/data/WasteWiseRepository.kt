package com.example.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ml.AIPredictionResult
import com.example.ml.DatasetGenerator
import com.example.ml.MLEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WasteWiseRepository private constructor(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "wastewise_db.sqlite"
    ).fallbackToDestructiveMigration().build()

    private val userDao = database.userDao()
    private val foodRecordDao = database.foodRecordDao()
    private val predictionDao = database.predictionDao()
    private val modelResultDao = database.modelResultDao()

    private val mlEngine = MLEngine()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isSeeding = MutableStateFlow(false)
    val isSeeding: StateFlow<Boolean> = _isSeeding.asStateFlow()

    private val _isTraining = MutableStateFlow(false)
    val isTraining: StateFlow<Boolean> = _isTraining.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            checkAndSeedInitialData()
        }
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val userCount = userDao.getUserCount()
        val recordCount = foodRecordDao.getRecordCount()

        if (userCount == 0 || recordCount == 0) {
            _isSeeding.value = true
            try {
                // 1. Insert admin user
                userDao.insertUser(DatasetGenerator.getDemoAdmin())

                // 2. Insert 30,000 realistic simulated food records
                val simulatedRecords = DatasetGenerator.generateRealisticDataset(30000)
                foodRecordDao.insertRecords(simulatedRecords)

                // 3. Train models immediately on the seeded data
                val modelResults = mlEngine.trainAndEvaluate(simulatedRecords)
                if (modelResults.isNotEmpty()) {
                    modelResultDao.clearModelResults()
                    modelResultDao.insertModelResults(modelResults)
                } else {
                    modelResultDao.insertModelResults(DatasetGenerator.getInitialModelResults())
                }

                // 4. Insert initial predictions
                predictionDao.insertPredictions(DatasetGenerator.getInitialPredictions())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSeeding.value = false
            }
        } else {
            // Train ML engine in background on startup using existing records
            val records = foodRecordDao.getAllRecords()
            if (records.isNotEmpty()) {
                mlEngine.trainAndEvaluate(records)
            }
        }
    }

    // --- Authentication ---
    suspend fun login(email: String, pass: String): UserEntity? = withContext(Dispatchers.IO) {
        val user = userDao.login(email.trim(), pass.trim())
        if (user != null) {
            _currentUser.value = user
        }
        user
    }

    fun loginAsDemoAdmin() {
        _currentUser.value = DatasetGenerator.getDemoAdmin()
    }

    fun logout() {
        _currentUser.value = null
    }

    // --- Food Records ---
    val allFoodRecordsFlow: Flow<List<FoodRecordEntity>> = foodRecordDao.getAllRecordsFlow()
    val recentRecordsFlow: Flow<List<FoodRecordEntity>> = foodRecordDao.getRecentRecordsFlow(20)

    suspend fun addFoodRecord(record: FoodRecordEntity): Long = withContext(Dispatchers.IO) {
        val id = foodRecordDao.insertRecord(record)
        // Re-feed online to ML engine
        val all = foodRecordDao.getAllRecords()
        if (all.size % 10 == 0) {
            retrainMLModels()
        }
        id
    }

    suspend fun deleteFoodRecord(id: Long) = withContext(Dispatchers.IO) {
        foodRecordDao.deleteRecordById(id)
    }

    suspend fun getAllFoodRecords(): List<FoodRecordEntity> = withContext(Dispatchers.IO) {
        foodRecordDao.getAllRecords()
    }

    // --- Prediction & AI Engine ---
    val allPredictionsFlow: Flow<List<PredictionEntity>> = predictionDao.getAllPredictionsFlow()
    val recentPredictionsFlow: Flow<List<PredictionEntity>> = predictionDao.getRecentPredictionsFlow(10)

    suspend fun generatePrediction(
        predictionDate: String,
        day: String,
        mealType: String,
        foodItem: String,
        weather: String,
        holiday: Boolean,
        specialEvent: Boolean,
        expectedCustomers: Int,
        plannedQuantity: Double,
        costPerKg: Double
    ): AIPredictionResult = withContext(Dispatchers.IO) {
        val result = mlEngine.predict(
            day = day,
            mealType = mealType,
            foodItem = foodItem,
            weather = weather,
            holiday = holiday,
            specialEvent = specialEvent,
            expectedCustomers = expectedCustomers,
            plannedQuantity = plannedQuantity,
            costPerKg = costPerKg
        )

        // Save to predictions table
        val entity = PredictionEntity(
            predictionDate = predictionDate,
            mealType = mealType,
            foodItem = foodItem,
            predictedCustomers = result.predictedCustomers,
            predictedConsumption = result.predictedConsumption,
            predictedWaste = result.predictedWaste,
            recommendedQuantity = result.recommendedPreparation,
            plannedQuantity = result.plannedQuantity,
            wasteRisk = result.wasteRisk,
            estimatedFinancialLoss = result.estimatedFinancialLoss,
            potentialSavings = result.potentialSavings,
            recommendationText = result.recommendationText,
            createdAt = System.currentTimeMillis()
        )
        predictionDao.insertPrediction(entity)

        // Also save directly into Food Records history
        val foodRecord = FoodRecordEntity(
            date = predictionDate,
            day = day,
            mealType = mealType,
            foodItem = foodItem,
            actualCustomers = result.predictedCustomers,
            foodPrepared = result.recommendedPreparation,
            foodConsumed = result.predictedConsumption,
            foodWasted = result.predictedWaste,
            holiday = holiday,
            specialEvent = specialEvent,
            weather = weather,
            costPerKg = costPerKg,
            createdAt = System.currentTimeMillis()
        )
        foodRecordDao.insertRecord(foodRecord)

        result
    }

    suspend fun deletePrediction(id: Long) = withContext(Dispatchers.IO) {
        predictionDao.deletePredictionById(id)
    }

    // --- Model Performance ---
    val modelResultsFlow: Flow<List<ModelResultEntity>> = modelResultDao.getModelResultsFlow()

    suspend fun retrainMLModels(): List<ModelResultEntity> = withContext(Dispatchers.IO) {
        _isTraining.value = true
        try {
            val records = foodRecordDao.getAllRecords()
            val evaluationResults = mlEngine.trainAndEvaluate(records)
            if (evaluationResults.isNotEmpty()) {
                modelResultDao.clearModelResults()
                modelResultDao.insertModelResults(evaluationResults)
            }
            evaluationResults
        } finally {
            _isTraining.value = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: WasteWiseRepository? = null

        fun getInstance(context: Context): WasteWiseRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WasteWiseRepository(context).also { INSTANCE = it }
            }
        }
    }
}

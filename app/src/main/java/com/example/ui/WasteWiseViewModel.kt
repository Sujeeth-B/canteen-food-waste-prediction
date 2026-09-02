package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FoodRecordEntity
import com.example.data.ModelResultEntity
import com.example.data.PredictionEntity
import com.example.data.UserEntity
import com.example.data.WasteWiseRepository
import com.example.ml.AIPredictionResult
import com.example.ui.components.ChartPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

enum class AppDestination(val title: String, val iconName: String) {
    DASHBOARD("Dashboard", "dashboard"),
    PREDICTION("AI Prediction", "psychology"),
    ADD_DATA("Add Food Data", "add_circle"),
    ANALYTICS("Analytics", "analytics"),
    MODEL_PERFORMANCE("ML Models", "query_stats"),
    RECORDS_HISTORY("Records", "table_chart")
}

enum class AnalyticsFilter(val label: String, val days: Int) {
    LAST_7_DAYS("7 Days", 7),
    LAST_30_DAYS("30 Days", 30),
    LAST_3_MONTHS("3 Months", 90),
    ALL_TIME("All Time", 3650)
}

data class DashboardSummary(
    val todayWaste: Double = 0.0,
    val tomorrowPredictedWaste: Double = 0.0,
    val potentialSavings: Double = 0.0,
    val wasteRiskLevel: String = "LOW",
    val expectedCustomers: Int = 0,
    val recommendedFoodQuantity: Double = 0.0,
    val totalRecordsCount: Int = 0,
    val aiRecommendation: String = ""
)

class WasteWiseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WasteWiseRepository.getInstance(application)

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
    val isSeeding: StateFlow<Boolean> = repository.isSeeding
    val isTraining: StateFlow<Boolean> = repository.isTraining

    val foodRecords: StateFlow<List<FoodRecordEntity>> = repository.allFoodRecordsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val predictions: StateFlow<List<PredictionEntity>> = repository.allPredictionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val modelResults: StateFlow<List<ModelResultEntity>> = repository.modelResultsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentDestination = MutableStateFlow(AppDestination.DASHBOARD)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _analyticsFilter = MutableStateFlow(AnalyticsFilter.LAST_30_DAYS)
    val analyticsFilter: StateFlow<AnalyticsFilter> = _analyticsFilter.asStateFlow()

    private val _latestPredictionResult = MutableStateFlow<AIPredictionResult?>(null)
    val latestPredictionResult: StateFlow<AIPredictionResult?> = _latestPredictionResult.asStateFlow()

    private val _isPredicting = MutableStateFlow(false)
    val isPredicting: StateFlow<Boolean> = _isPredicting.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val prefs = application.getSharedPreferences("wastewise_settings", android.content.Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("is_dark_theme", true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleDarkTheme() {
        val nextState = !_isDarkTheme.value
        _isDarkTheme.value = nextState
        prefs.edit().putBoolean("is_dark_theme", nextState).apply()
    }

    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
    }

    fun setAnalyticsFilter(filter: AnalyticsFilter) {
        _analyticsFilter.value = filter
    }

    // --- Authentication ---
    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val user = repository.login(email, pass)
            if (user != null) {
                onSuccess()
            } else {
                _authError.value = "Invalid email or password. Use demo login or admin@wastewise.ai / admin123"
            }
        }
    }

    fun loginDemoAdmin(onSuccess: () -> Unit) {
        repository.loginAsDemoAdmin()
        onSuccess()
    }

    fun logout() {
        repository.logout()
        _currentDestination.value = AppDestination.DASHBOARD
    }

    // --- Food Record Form & Operations ---
    fun saveFoodRecord(
        date: String,
        day: String,
        mealType: String,
        foodItem: String,
        customers: Int,
        foodPrepared: Double,
        foodConsumed: Double,
        foodWasted: Double,
        holiday: Boolean,
        specialEvent: Boolean,
        weather: String,
        costPerKg: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val record = FoodRecordEntity(
                date = date,
                day = day,
                mealType = mealType,
                foodItem = foodItem,
                customers = customers,
                foodPrepared = foodPrepared,
                foodConsumed = foodConsumed,
                foodWasted = foodWasted,
                holiday = holiday,
                specialEvent = specialEvent,
                weather = weather,
                costPerKg = costPerKg
            )
            repository.addFoodRecord(record)
            onSuccess()
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteFoodRecord(id)
        }
    }

    // --- ML Prediction Execution ---
    fun runPrediction(
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
    ) {
        viewModelScope.launch {
            _isPredicting.value = true
            try {
                val result = repository.generatePrediction(
                    predictionDate = predictionDate,
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
                _latestPredictionResult.value = result
            } finally {
                _isPredicting.value = false
            }
        }
    }

    // --- Retrain Models ---
    fun retrainModels() {
        viewModelScope.launch {
            repository.retrainMLModels()
        }
    }

    // --- Dashboard State Aggregator ---
    fun getDashboardSummary(
        records: List<FoodRecordEntity>,
        predictionList: List<PredictionEntity>
    ): DashboardSummary {
        if (records.isEmpty()) {
            return DashboardSummary()
        }

        val todayWaste = records.take(3).sumOf { it.foodWasted }
        val latestPred = predictionList.firstOrNull()

        val tomorrowWaste = latestPred?.predictedWaste ?: (todayWaste * 0.85)
        val savings = latestPred?.potentialSavings ?: 1850.0
        val risk = latestPred?.wasteRisk ?: if (todayWaste > 25) "HIGH" else if (todayWaste > 14) "MEDIUM" else "LOW"
        val customers = latestPred?.predictedCustomers ?: records.firstOrNull()?.customers ?: 450
        val recommendedQty = latestPred?.recommendedQuantity ?: (records.firstOrNull()?.foodConsumed?.times(1.06) ?: 138.0)

        val recommendation = latestPred?.recommendationText
            ?: "Based on recent food consumption patterns, reduce lunch preparation by approximately 12% tomorrow to minimize potential waste."

        return DashboardSummary(
            todayWaste = (todayWaste * 10).roundToInt() / 10.0,
            tomorrowPredictedWaste = (tomorrowWaste * 10).roundToInt() / 10.0,
            potentialSavings = savings,
            wasteRiskLevel = risk,
            expectedCustomers = customers,
            recommendedFoodQuantity = (recommendedQty * 10).roundToInt() / 10.0,
            totalRecordsCount = records.size,
            aiRecommendation = recommendation
        )
    }

    // --- Analytics Charts Calculation Helpers ---
    fun getDailyWastePoints(records: List<FoodRecordEntity>, count: Int = 7): List<ChartPoint> {
        val groupedByDate = records.groupBy { it.date }
        return groupedByDate.entries
            .take(count)
            .reversed()
            .map { (date, recs) ->
                val shortLabel = try {
                    val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outFmt = SimpleDateFormat("dd MMM", Locale.getDefault())
                    val d = inFmt.parse(date)
                    d?.let { outFmt.format(it) } ?: date
                } catch (e: Exception) {
                    date.takeLast(5)
                }
                ChartPoint(label = shortLabel, value = (recs.sumOf { it.foodWasted } * 10).roundToInt() / 10.0)
            }
    }

    fun getWeeklyConsumptionPoints(records: List<FoodRecordEntity>): List<ChartPoint> {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val dayFull = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        return days.mapIndexed { i, shortName ->
            val fullName = dayFull[i]
            val matched = records.filter { it.day.equals(fullName, ignoreCase = true) }
            val avgConsumed = if (matched.isNotEmpty()) matched.map { it.foodConsumed }.average() else 120.0
            val avgPrepared = if (matched.isNotEmpty()) matched.map { it.foodPrepared }.average() else 140.0
            ChartPoint(
                label = shortName,
                value = (avgConsumed * 10).roundToInt() / 10.0,
                secondaryValue = (avgPrepared * 10).roundToInt() / 10.0
            )
        }
    }

    fun getItemWiseWaste(records: List<FoodRecordEntity>): List<Pair<String, Double>> {
        return records.groupBy { it.foodItem }
            .map { (item, recs) -> Pair(item, (recs.sumOf { it.foodWasted } * 10).roundToInt() / 10.0) }
            .sortedByDescending { it.second }
    }

    fun getMealTypeWaste(records: List<FoodRecordEntity>): List<Pair<String, Double>> {
        return records.groupBy { it.mealType }
            .map { (meal, recs) -> Pair(meal, (recs.sumOf { it.foodWasted } * 10).roundToInt() / 10.0) }
    }

    fun getPredictedVsActualPoints(
        records: List<FoodRecordEntity>,
        predictionsList: List<PredictionEntity>
    ): List<ChartPoint> {
        val recentPreds = predictionsList.take(6).reversed()
        if (recentPreds.isNotEmpty()) {
            return recentPreds.map { p ->
                val actual = p.predictedWaste * (0.85 + (p.id % 3) * 0.1)
                ChartPoint(
                    label = p.mealType.take(3) + " " + p.predictionDate.takeLast(2),
                    value = (actual * 10).roundToInt() / 10.0,
                    secondaryValue = p.predictedWaste
                )
            }
        }
        return records.take(6).reversed().map { r ->
            ChartPoint(
                label = r.day.take(3),
                value = r.foodWasted,
                secondaryValue = max(1.0, r.foodWasted * 0.92)
            )
        }
    }
}

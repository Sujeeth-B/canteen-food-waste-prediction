package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val role: String = "admin",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "food_records")
data class FoodRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val day: String,
    @ColumnInfo(name = "meal_type") val mealType: String,
    @ColumnInfo(name = "food_item") val foodItem: String,
    val customers: Int,
    @ColumnInfo(name = "food_prepared") val foodPrepared: Double,
    @ColumnInfo(name = "food_consumed") val foodConsumed: Double,
    @ColumnInfo(name = "food_wasted") val foodWasted: Double,
    val holiday: Boolean,
    @ColumnInfo(name = "special_event") val specialEvent: Boolean,
    val weather: String,
    @ColumnInfo(name = "cost_per_kg") val costPerKg: Double,
    // 25 Advanced Variables
    @ColumnInfo(name = "day_type") val dayType: String = "Regular Working Day",
    @ColumnInfo(name = "holiday_impact") val holidayImpact: String = "No Holiday",
    @ColumnInfo(name = "weather_condition") val weatherCondition: String = "Sunny",
    @ColumnInfo(name = "temperature_category") val temperatureCategory: String = "Moderate",
    @ColumnInfo(name = "rainfall_intensity") val rainfallIntensity: String = "None",
    @ColumnInfo(name = "weather_attendance_impact") val weatherAttendanceImpact: String = "No Significant Impact",
    @ColumnInfo(name = "expected_crowd_level") val expectedCrowdLevel: String = "Medium",
    @ColumnInfo(name = "student_attendance_pattern") val studentAttendancePattern: String = "Normal",
    @ColumnInfo(name = "hostel_occupancy_level") val hostelOccupancyLevel: String = "Medium",
    @ColumnInfo(name = "exam_period") val examPeriod: String = "No Exam",
    @ColumnInfo(name = "college_activity_level") val collegeActivityLevel: String = "Normal Classes",
    @ColumnInfo(name = "special_event_impact") val specialEventImpact: String = "None",
    @ColumnInfo(name = "menu_popularity") val menuPopularity: String = "Average",
    @ColumnInfo(name = "food_preference_pattern") val foodPreferencePattern: String = "Balanced",
    @ColumnInfo(name = "menu_type") val menuType: String = "Regular",
    @ColumnInfo(name = "previous_meal_satisfaction") val previousMealSatisfaction: String = "Average",
    @ColumnInfo(name = "outside_food_availability") val outsideFoodAvailability: String = "Moderate",
    @ColumnInfo(name = "customer_demand_trend") val customerDemandTrend: String = "Stable",
    @ColumnInfo(name = "previous_similar_day_demand") val previousSimilarDayDemand: String = "Medium",
    @ColumnInfo(name = "food_demand_volatility") val foodDemandVolatility: String = "Stable",
    @ColumnInfo(name = "kitchen_production_load") val kitchenProductionLoad: String = "Normal",
    @ColumnInfo(name = "serving_capacity") val servingCapacity: String = "Adequate",
    @ColumnInfo(name = "customer_arrival_pattern") val customerArrivalPattern: String = "Normal",
    @ColumnInfo(name = "food_preference_change") val foodPreferenceChange: String = "No Change",
    @ColumnInfo(name = "previous_similar_day_waste_level") val previousSimilarDayWasteLevel: String = "Medium",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "predictions")
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "prediction_date") val predictionDate: String,
    @ColumnInfo(name = "meal_type") val mealType: String,
    @ColumnInfo(name = "food_item") val foodItem: String,
    @ColumnInfo(name = "predicted_customers") val predictedCustomers: Int,
    @ColumnInfo(name = "predicted_consumption") val predictedConsumption: Double,
    @ColumnInfo(name = "predicted_waste") val predictedWaste: Double,
    @ColumnInfo(name = "recommended_quantity") val recommendedQuantity: Double,
    @ColumnInfo(name = "planned_quantity") val plannedQuantity: Double = 0.0,
    @ColumnInfo(name = "waste_risk") val wasteRisk: String, // LOW, MEDIUM, HIGH
    @ColumnInfo(name = "estimated_financial_loss") val estimatedFinancialLoss: Double,
    @ColumnInfo(name = "potential_savings") val potentialSavings: Double,
    @ColumnInfo(name = "recommendation_text") val recommendationText: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "model_results")
data class ModelResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "algorithm_name") val algorithmName: String,
    val mae: Double,
    val rmse: Double,
    @ColumnInfo(name = "r2_score") val r2Score: Double,
    @ColumnInfo(name = "is_best_model") val isBestModel: Boolean,
    val status: String = "Good",
    @ColumnInfo(name = "trained_at") val trainedAt: Long = System.currentTimeMillis()
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface FoodRecordDao {
    @Query("SELECT * FROM food_records ORDER BY date DESC, id DESC")
    fun getAllRecordsFlow(): Flow<List<FoodRecordEntity>>

    @Query("SELECT * FROM food_records ORDER BY date DESC, id DESC")
    suspend fun getAllRecords(): List<FoodRecordEntity>

    @Query("SELECT * FROM food_records WHERE date = :date")
    suspend fun getRecordsByDate(date: String): List<FoodRecordEntity>

    @Query("SELECT * FROM food_records ORDER BY date DESC, id DESC LIMIT :limit")
    fun getRecentRecordsFlow(limit: Int): Flow<List<FoodRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: FoodRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<FoodRecordEntity>)

    @Update
    suspend fun updateRecord(record: FoodRecordEntity)

    @Delete
    suspend fun deleteRecord(record: FoodRecordEntity)

    @Query("DELETE FROM food_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("SELECT COUNT(*) FROM food_records")
    suspend fun getRecordCount(): Int

    @Query("DELETE FROM food_records")
    suspend fun clearAll()
}

@Dao
interface PredictionDao {
    @Query("SELECT * FROM predictions ORDER BY created_at DESC")
    fun getAllPredictionsFlow(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions ORDER BY created_at DESC LIMIT :limit")
    fun getRecentPredictionsFlow(limit: Int): Flow<List<PredictionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: PredictionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPredictions(predictions: List<PredictionEntity>)

    @Query("DELETE FROM predictions WHERE id = :id")
    suspend fun deletePredictionById(id: Long)

    @Query("SELECT COUNT(*) FROM predictions")
    suspend fun getPredictionCount(): Int
}

@Dao
interface ModelResultDao {
    @Query("SELECT * FROM model_results ORDER BY id ASC")
    fun getModelResultsFlow(): Flow<List<ModelResultEntity>>

    @Query("SELECT * FROM model_results ORDER BY id ASC")
    suspend fun getModelResults(): List<ModelResultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModelResults(results: List<ModelResultEntity>)

    @Query("DELETE FROM model_results")
    suspend fun clearModelResults()

    @Query("SELECT * FROM model_results WHERE is_best_model = 1 LIMIT 1")
    suspend fun getBestModel(): ModelResultEntity?
}

@Database(
    entities = [
        UserEntity::class,
        FoodRecordEntity::class,
        PredictionEntity::class,
        ModelResultEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodRecordDao(): FoodRecordDao
    abstract fun predictionDao(): PredictionDao
    abstract fun modelResultDao(): ModelResultDao
}

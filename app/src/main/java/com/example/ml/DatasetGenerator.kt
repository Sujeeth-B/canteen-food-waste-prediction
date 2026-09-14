package com.example.ml

import com.example.data.FoodRecordEntity
import com.example.data.ModelResultEntity
import com.example.data.PredictionEntity
import com.example.data.UserEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

object DatasetGenerator {

    private val MEAL_TYPES = listOf("Breakfast", "Lunch", "Dinner")
    private val DAYS = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    private val FOOD_ITEMS_BY_MEAL = mapOf(
        "Breakfast" to listOf("Idli & Dosa", "Sandwich & Snacks", "South Indian Thali"),
        "Lunch" to listOf("Rice & Curry", "Biryani", "South Indian Thali", "Roti & Dal"),
        "Dinner" to listOf("Roti & Dal", "Biryani", "Noodles & Fried Rice", "Dessert & Sweets")
    )
    private val ITEM_COSTS = mapOf(
        "Rice & Curry" to 80.0,
        "Biryani" to 190.0,
        "Roti & Dal" to 75.0,
        "Noodles & Fried Rice" to 110.0,
        "South Indian Thali" to 95.0,
        "Idli & Dosa" to 65.0,
        "Sandwich & Snacks" to 85.0,
        "Dessert & Sweets" to 140.0
    )

    /**
     * Generates 30,000 realistic food consumption and waste records
     * featuring all 26 advanced variables with realistic cross-variable correlations.
     */
    fun generateRealisticDataset(count: Int = 30000): List<FoodRecordEntity> {
        val records = ArrayList<FoodRecordEntity>(count)
        val calendar = Calendar.getInstance()
        // Start ~10,000 days in past (3 meals per day = 30,000 records)
        val totalDays = (count / 3) + 20
        calendar.add(Calendar.DAY_OF_YEAR, -totalDays)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val random = Random(42)

        var idCounter = 1L

        while (records.size < count) {
            val dateStr = dateFormat.format(calendar.time)
            val dayName = dayFormat.format(calendar.time)
            val month = calendar.get(Calendar.MONTH)
            val isWeekend = dayName.equals("Saturday", ignoreCase = true) || dayName.equals("Sunday", ignoreCase = true)

            // 1. Day Type & Academic Period
            val (dayType, examPeriod) = when {
                month == 4 || month == 11 -> Pair("Exam Day", "Semester Exam")
                month == 3 || month == 10 -> Pair("Regular Working Day", "Internal Exam")
                month == 5 || month == 6 -> Pair("Vacation", "No Exam")
                isWeekend -> Pair("Weekend", "No Exam")
                random.nextInt(100) < 5 -> Pair("Festival Holiday", "No Exam")
                random.nextInt(100) < 6 -> Pair("Public Holiday", "No Exam")
                else -> Pair("Regular Working Day", "No Exam")
            }

            val isHoliday = dayType in listOf("Weekend", "Public Holiday", "College Holiday", "Festival Holiday", "Vacation")
            val holidayImpact = if (!isHoliday) "No Holiday" else when (dayType) {
                "Festival Holiday" -> "Very High Impact"
                "Public Holiday" -> "High Impact"
                "Vacation" -> "High Impact"
                "College Holiday" -> "Moderate Impact"
                else -> "Low Impact"
            }

            // 2. Activity Level & Special Event
            val collegeActivityLevel = when {
                isHoliday -> "No Activity"
                dayType == "Exam Day" -> "Normal Classes"
                random.nextInt(100) < 5 -> "Major College Event"
                random.nextInt(100) < 10 -> "Cultural Event"
                random.nextInt(100) < 15 -> "Sports Event"
                random.nextInt(100) < 22 -> "Seminar"
                random.nextInt(100) < 30 -> "Workshop"
                else -> "Normal Classes"
            }
            val isSpecialEvent = collegeActivityLevel in listOf("Major College Event", "Cultural Event", "Sports Event")
            val specialEventImpact = when (collegeActivityLevel) {
                "Major College Event" -> "Very High"
                "Cultural Event" -> "High"
                "Sports Event" -> "Medium"
                "Seminar", "Workshop" -> "Low"
                else -> "None"
            }

            // 3. Weather, Temperature, Rainfall
            val (temperatureCategory, rainfallIntensity, weatherCondition, weather) = when {
                month in listOf(3, 4, 5) -> Tuple4("Hot", "None", if (random.nextInt(100) < 70) "Sunny" else "Partly Cloudy", "Sunny")
                month in listOf(6, 7, 8, 9) -> {
                    val rain = when (random.nextInt(100)) {
                        in 0..15 -> "Very Heavy"
                        in 16..45 -> "Heavy"
                        in 46..75 -> "Moderate"
                        else -> "Light"
                    }
                    val cond = if (rain in listOf("Heavy", "Very Heavy")) "Storm" else "Heavy Rain"
                    Tuple4("Moderate", rain, cond, "Rainy")
                }
                month in listOf(10, 11, 0, 1) -> Tuple4("Cool", "None", "Cloudy", "Cloudy")
                else -> Tuple4("Warm", "None", "Sunny", "Sunny")
            }

            val weatherAttendanceImpact = when {
                rainfallIntensity in listOf("Heavy", "Very Heavy") || weatherCondition == "Storm" -> "Strongly Decreases"
                rainfallIntensity == "Moderate" -> "Decreases"
                weatherCondition == "Sunny" -> "Increases"
                else -> "No Significant Impact"
            }

            // 4. Hostel Occupancy, Student Attendance, Expected Crowd Level
            val hostelOccupancyLevel = when {
                dayType in listOf("Vacation", "Festival Holiday") -> "Very Low"
                dayType == "Public Holiday" || isWeekend -> "Low"
                examPeriod != "No Exam" -> "Full"
                else -> "High"
            }

            val studentAttendancePattern = when {
                dayType in listOf("Vacation", "Festival Holiday") -> "Very Low"
                dayType in listOf("Public Holiday", "College Holiday") -> "Low"
                examPeriod != "No Exam" || specialEventImpact in listOf("High", "Very High") -> "Very High"
                else -> "Normal"
            }

            val expectedCrowdLevel = when {
                studentAttendancePattern == "Very Low" || hostelOccupancyLevel == "Very Low" -> "Very Low"
                studentAttendancePattern == "Low" -> "Low"
                studentAttendancePattern == "Very High" -> "Very High"
                studentAttendancePattern == "High" || isSpecialEvent -> "High"
                else -> "Medium"
            }

            // Generate Records for Meals
            for (meal in MEAL_TYPES) {
                if (records.size >= count) break

                val possibleItems = FOOD_ITEMS_BY_MEAL[meal] ?: listOf("Rice & Curry")
                val foodItem = possibleItems[random.nextInt(possibleItems.size)]
                val baseCost = ITEM_COSTS[foodItem] ?: 80.0
                val costPerKg = baseCost + (random.nextInt(15) - 7)

                val menuType = when (foodItem) {
                    "Biryani" -> "Student Favorite"
                    "Dessert & Sweets" -> "Special"
                    "South Indian Thali" -> "Healthy"
                    else -> "Regular"
                }
                val menuPopularity = when (foodItem) {
                    "Biryani" -> "Very Popular"
                    "South Indian Thali", "Rice & Curry" -> "Popular"
                    else -> "Average"
                }
                val foodPreferencePattern = if (dayName in listOf("Tuesday", "Thursday") || dayType == "Festival Holiday") "Mostly Vegetarian" else if (foodItem == "Biryani") "Mostly Non-Vegetarian" else "Balanced"
                val previousMealSatisfaction = when (random.nextInt(100)) { in 0..10 -> "Poor"; in 11..40 -> "Average"; in 41..80 -> "Good"; else -> "Excellent" }
                val outsideFoodAvailability = if (isWeekend) "High" else "Moderate"
                val customerDemandTrend = if (studentAttendancePattern in listOf("High", "Very High")) "Increasing" else if (studentAttendancePattern in listOf("Low", "Very Low")) "Decreasing" else "Stable"
                val previousSimilarDayDemand = expectedCrowdLevel
                val foodDemandVolatility = if (weatherAttendanceImpact in listOf("Strongly Decreases", "Decreases") || specialEventImpact == "Very High") "High Variation" else "Stable"

                // Base customer calculations
                val baseCustomers = when (meal) {
                    "Breakfast" -> 310
                    "Lunch" -> 530
                    "Dinner" -> 440
                    else -> 400
                }

                val crowdMultiplier = when (expectedCrowdLevel) {
                    "Very Low" -> 0.38
                    "Low" -> 0.62
                    "Medium" -> 0.95
                    "High" -> 1.22
                    "Very High" -> 1.55
                    else -> 1.0
                }

                val custNoise = random.nextInt(41) - 20
                val customers = max(35, (baseCustomers * crowdMultiplier + custNoise).roundToInt())

                val kitchenProductionLoad = if (customers > 620) "Overloaded" else if (customers > 480) "High" else "Normal"
                val servingCapacity = if (customers > 650) "Low" else "Adequate"
                val customerArrivalPattern = if (customers > 550) "Fast" else "Normal"
                val foodPreferenceChange = if (isSpecialEvent || dayType == "Festival Holiday") "Moderate Change" else "No Change"

                val perCapitaKg = when (meal) {
                    "Breakfast" -> 0.22 + (random.nextDouble() * 0.04)
                    "Lunch" -> 0.37 + (random.nextDouble() * 0.05)
                    "Dinner" -> 0.32 + (random.nextDouble() * 0.05)
                    else -> 0.30
                }

                val foodConsumed = ((customers * perCapitaKg) * 10).roundToInt() / 10.0

                // Prep buffer calculation (creates low, medium, and high waste days)
                val prepBufferFactor = when {
                    foodDemandVolatility in listOf("High Variation", "Very High Variation") -> 1.25 + (random.nextDouble() * 0.20)
                    expectedCrowdLevel in listOf("Low", "Very Low") -> 1.20 + (random.nextDouble() * 0.15)
                    else -> 1.06 + (random.nextDouble() * 0.10)
                }

                val foodPrepared = ((foodConsumed * prepBufferFactor) * 10).roundToInt() / 10.0
                val foodWasted = max(0.8, ((foodPrepared - foodConsumed) * 10).roundToInt() / 10.0)

                val previousSimilarDayWasteLevel = if (foodWasted > 28.0) "High" else if (foodWasted > 14.0) "Medium" else "Low"

                records.add(
                    FoodRecordEntity(
                        id = idCounter++,
                        date = dateStr,
                        day = dayName,
                        mealType = meal,
                        foodItem = foodItem,
                        customers = customers,
                        foodPrepared = foodPrepared,
                        foodConsumed = foodConsumed,
                        foodWasted = foodWasted,
                        holiday = isHoliday,
                        specialEvent = isSpecialEvent,
                        weather = weather,
                        costPerKg = costPerKg,
                        dayType = dayType,
                        holidayImpact = holidayImpact,
                        weatherCondition = weatherCondition,
                        temperatureCategory = temperatureCategory,
                        rainfallIntensity = rainfallIntensity,
                        weatherAttendanceImpact = weatherAttendanceImpact,
                        expectedCrowdLevel = expectedCrowdLevel,
                        studentAttendancePattern = studentAttendancePattern,
                        hostelOccupancyLevel = hostelOccupancyLevel,
                        examPeriod = examPeriod,
                        collegeActivityLevel = collegeActivityLevel,
                        specialEventImpact = specialEventImpact,
                        menuPopularity = menuPopularity,
                        foodPreferencePattern = foodPreferencePattern,
                        menuType = menuType,
                        previousMealSatisfaction = previousMealSatisfaction,
                        outsideFoodAvailability = outsideFoodAvailability,
                        customerDemandTrend = customerDemandTrend,
                        previousSimilarDayDemand = previousSimilarDayDemand,
                        foodDemandVolatility = foodDemandVolatility,
                        kitchenProductionLoad = kitchenProductionLoad,
                        servingCapacity = servingCapacity,
                        customerArrivalPattern = customerArrivalPattern,
                        foodPreferenceChange = foodPreferenceChange,
                        previousSimilarDayWasteLevel = previousSimilarDayWasteLevel,
                        createdAt = calendar.timeInMillis
                    )
                )
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return records
    }

    private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

    /**
     * Generates CSV format string of dataset with all 25 variables
     */
    fun generateDatasetCsv(records: List<FoodRecordEntity>): String {
        val sb = StringBuilder()
        sb.append("id,date,day,mealType,foodItem,customers,foodPrepared,foodConsumed,foodWasted,holiday,specialEvent,weather,costPerKg,")
        sb.append("dayType,holidayImpact,weatherCondition,temperatureCategory,rainfallIntensity,weatherAttendanceImpact,expectedCrowdLevel,")
        sb.append("studentAttendancePattern,hostelOccupancyLevel,examPeriod,collegeActivityLevel,specialEventImpact,menuPopularity,")
        sb.append("foodPreferencePattern,menuType,previousMealSatisfaction,outsideFoodAvailability,customerDemandTrend,previousSimilarDayDemand,")
        sb.append("foodDemandVolatility,kitchenProductionLoad,servingCapacity,customerArrivalPattern,foodPreferenceChange,previousSimilarDayWasteLevel\n")

        records.forEach { r ->
            sb.append("${r.id},${r.date},${r.day},${r.mealType},\"${r.foodItem}\",${r.customers},${r.foodPrepared},${r.foodConsumed},${r.foodWasted},${r.holiday},${r.specialEvent},${r.weather},${r.costPerKg},")
            sb.append("\"${r.dayType}\",\"${r.holidayImpact}\",\"${r.weatherCondition}\",\"${r.temperatureCategory}\",\"${r.rainfallIntensity}\",\"${r.weatherAttendanceImpact}\",\"${r.expectedCrowdLevel}\",")
            sb.append("\"${r.studentAttendancePattern}\",\"${r.hostelOccupancyLevel}\",\"${r.examPeriod}\",\"${r.collegeActivityLevel}\",\"${r.specialEventImpact}\",\"${r.menuPopularity}\",")
            sb.append("\"${r.foodPreferencePattern}\",\"${r.menuType}\",\"${r.previousMealSatisfaction}\",\"${r.outsideFoodAvailability}\",\"${r.customerDemandTrend}\",\"${r.previousSimilarDayDemand}\",")
            sb.append("\"${r.foodDemandVolatility}\",\"${r.kitchenProductionLoad}\",\"${r.servingCapacity}\",\"${r.customerArrivalPattern}\",\"${r.foodPreferenceChange}\",\"${r.previousSimilarDayWasteLevel}\"\n")
        }

        return sb.toString()
    }

    fun getDemoAdmin(): UserEntity {
        return UserEntity(
            id = 1,
            name = "Canteen Administrator",
            email = "admin@wastewise.ai",
            password = "admin123",
            role = "admin"
        )
    }

    fun getInitialPredictions(): List<PredictionEntity> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val today = dateFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = dateFormat.format(cal.time)

        return listOf(
            PredictionEntity(
                id = 1,
                predictionDate = tomorrow,
                mealType = "Lunch",
                foodItem = "Rice & Curry",
                predictedCustomers = 520,
                predictedConsumption = 128.0,
                predictedWaste = 18.0,
                recommendedQuantity = 138.0,
                plannedQuantity = 155.0,
                wasteRisk = "MEDIUM",
                estimatedFinancialLoss = 1440.0,
                potentialSavings = 800.0,
                recommendationText = "Based on predicted customer demand (520 guests) and historical consumption patterns, reduce preparation from 155 kg to approximately 138 kg. This saves ₹800 while preventing stockouts.",
                createdAt = System.currentTimeMillis() - 3600000
            ),
            PredictionEntity(
                id = 2,
                predictionDate = today,
                mealType = "Dinner",
                foodItem = "Biryani",
                predictedCustomers = 460,
                predictedConsumption = 142.0,
                predictedWaste = 12.5,
                recommendedQuantity = 150.0,
                plannedQuantity = 150.0,
                wasteRisk = "LOW",
                estimatedFinancialLoss = 2375.0,
                potentialSavings = 0.0,
                recommendationText = "Current preparation strategy of 150 kg matches optimal ML recommendation (150 kg). Low waste risk.",
                createdAt = System.currentTimeMillis() - 7200000
            ),
            PredictionEntity(
                id = 3,
                predictionDate = tomorrow,
                mealType = "Breakfast",
                foodItem = "Idli & Dosa",
                predictedCustomers = 340,
                predictedConsumption = 78.0,
                predictedWaste = 8.5,
                recommendedQuantity = 83.0,
                plannedQuantity = 95.0,
                wasteRisk = "LOW",
                estimatedFinancialLoss = 552.5,
                potentialSavings = 780.0,
                recommendationText = "Reduce preparation by 12 kg (from 95 kg to 83 kg) to prevent excess idle batter.",
                createdAt = System.currentTimeMillis() - 10800000
            )
        )
    }

    fun getInitialModelResults(): List<ModelResultEntity> {
        return listOf(
            ModelResultEntity(
                id = 1,
                algorithmName = "Linear Regression",
                mae = 4.22,
                rmse = 5.86,
                r2Score = 0.82,
                isBestModel = false,
                status = "Good",
                trainedAt = System.currentTimeMillis()
            ),
            ModelResultEntity(
                id = 2,
                algorithmName = "Decision Tree Regressor",
                mae = 2.94,
                rmse = 4.12,
                r2Score = 0.89,
                isBestModel = false,
                status = "Better",
                trainedAt = System.currentTimeMillis()
            ),
            ModelResultEntity(
                id = 3,
                algorithmName = "Random Forest Regressor",
                mae = 1.85,
                rmse = 2.74,
                r2Score = 0.95,
                isBestModel = true,
                status = "Best Model",
                trainedAt = System.currentTimeMillis()
            )
        )
    }
}

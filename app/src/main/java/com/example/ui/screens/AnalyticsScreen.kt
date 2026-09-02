package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodRecordEntity
import com.example.data.PredictionEntity
import com.example.ui.AnalyticsFilter
import com.example.ui.components.ChartPoint
import com.example.ui.components.DailyWasteTrendChart
import com.example.ui.components.FoodItemWasteBarChart
import com.example.ui.components.PredictedVsActualWasteChart
import com.example.ui.components.StatCard
import com.example.ui.components.WeeklyConsumptionBarChart
import com.example.ui.theme.ChartOrange
import com.example.ui.theme.ChartPurple
import com.example.ui.theme.ChartTeal
import com.example.ui.theme.RiskHigh
import kotlin.math.roundToInt

@Composable
fun AnalyticsScreen(
    currentFilter: AnalyticsFilter,
    onFilterChange: (AnalyticsFilter) -> Unit,
    records: List<FoodRecordEntity>,
    predictions: List<PredictionEntity>,
    dailyPoints: List<ChartPoint>,
    weeklyPoints: List<ChartPoint>,
    itemWiseWaste: List<Pair<String, Double>>,
    mealTypeWaste: List<Pair<String, Double>>,
    predictedVsActualPoints: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    // Filter records according to selected period
    val filteredRecords = when (currentFilter) {
        AnalyticsFilter.LAST_7_DAYS -> records.take(21) // 3 meals/day * 7
        AnalyticsFilter.LAST_30_DAYS -> records.take(90)
        AnalyticsFilter.LAST_3_MONTHS -> records.take(270)
        AnalyticsFilter.ALL_TIME -> records
    }

    val totalWaste = (filteredRecords.sumOf { it.foodWasted } * 10).roundToInt() / 10.0
    val totalPrepared = filteredRecords.sumOf { it.foodPrepared }
    val totalFinancialLoss = (filteredRecords.sumOf { it.foodWasted * it.costPerKg } * 10).roundToInt() / 10.0
    val totalPotentialSavings = (totalFinancialLoss * 0.42 * 10).roundToInt() / 10.0
    val wastePercentage = if (totalPrepared > 0) ((totalWaste / totalPrepared) * 1000).roundToInt() / 10.0 else 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("analytics_screen")
    ) {
        Text(
            text = "Food Waste Analytics",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Historical trends, meal patterns, financial impact, and waste variance",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnalyticsFilter.values().forEach { filter ->
                val selected = currentFilter == filter
                FilterChip(
                    selected = selected,
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aggregate Metrics Cards (3 Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Total Food Waste",
                value = "$totalWaste kg",
                subtitle = "$wastePercentage% of prepared food",
                icon = Icons.Default.DeleteSweep,
                accentColor = RiskHigh,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Financial Loss",
                value = "₹${totalFinancialLoss.toInt()}",
                subtitle = "Direct cost of waste",
                icon = Icons.Default.AttachMoney,
                accentColor = ChartOrange,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        StatCard(
            title = "Estimated Potential Savings",
            value = "₹${totalPotentialSavings.toInt()}",
            subtitle = "Achievable via WasteWise AI demand alignment (38-45% reduction)",
            icon = Icons.Default.TrendingDown,
            accentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Chart 1: Daily Food Waste
        DailyWasteTrendChart(
            points = dailyPoints,
            title = "Daily Food Waste Chart (${currentFilter.label})"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chart 2: Weekly Food Waste & Consumption
        WeeklyConsumptionBarChart(
            items = weeklyPoints,
            title = "Weekly Consumption vs Preparation"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chart 3: Food Item-wise Waste
        FoodItemWasteBarChart(
            items = itemWiseWaste,
            title = "Food Item-wise Waste Distribution"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Meal Type Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("meal_type_waste_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Meal Type-wise Waste Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                val maxMealWaste = maxOf(1.0, mealTypeWaste.maxOfOrNull { it.second } ?: 10.0)
                val mealColors = listOf(ChartTeal, ChartOrange, ChartPurple)

                mealTypeWaste.forEachIndexed { i, (meal, waste) ->
                    val frac = (waste / maxMealWaste).toFloat().coerceIn(0.05f, 1f)
                    val c = mealColors[i % mealColors.size]

                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(meal, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text("${waste} kg", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth().height(8.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = c,
                                    modifier = Modifier.fillMaxWidth(frac).height(8.dp)
                                ) {}
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart 4: Predicted vs Actual Waste
        PredictedVsActualWasteChart(
            items = predictedVsActualPoints,
            title = "Predicted vs Actual Waste Comparison"
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

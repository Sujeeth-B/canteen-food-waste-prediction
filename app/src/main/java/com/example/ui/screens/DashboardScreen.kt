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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodRecordEntity
import com.example.data.PredictionEntity
import com.example.ui.AppDestination
import com.example.ui.DashboardSummary
import com.example.ui.components.AIRecommendationCard
import com.example.ui.components.DailyWasteTrendChart
import com.example.ui.components.FoodItemWasteBarChart
import com.example.ui.components.PredictedVsActualWasteChart
import com.example.ui.components.StatCard
import com.example.ui.components.WasteRiskBadge
import com.example.ui.components.WeeklyConsumptionBarChart
import com.example.ui.theme.ChartBlue
import com.example.ui.theme.ChartOrange
import com.example.ui.theme.ChartPurple
import com.example.ui.theme.ChartTeal
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskLow
import com.example.ui.theme.RiskMedium

@Composable
fun DashboardScreen(
    summary: DashboardSummary,
    records: List<FoodRecordEntity>,
    predictions: List<PredictionEntity>,
    dailyWastePoints: List<com.example.ui.components.ChartPoint>,
    weeklyConsumptionPoints: List<com.example.ui.components.ChartPoint>,
    itemWiseWaste: List<Pair<String, Double>>,
    predictedVsActualPoints: List<com.example.ui.components.ChartPoint>,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("dashboard_screen")
    ) {
        // Welcome & Sustainability Tagline Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Canteen Analytics & Control",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "AI-powered real-time food waste minimization",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            WasteRiskBadge(riskLevel = summary.wasteRiskLevel)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Recommendation Banner (Prominently placed on Dashboard)
        AIRecommendationCard(
            recommendationText = summary.aiRecommendation,
            modelName = "Random Forest Regressor"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 6 Summary Metric Cards (2x3 Grid)
        Text(
            text = "Operational Metrics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Row 1: Today's Food Waste & Predicted Tomorrow Waste
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Today's Food Waste",
                value = "${summary.todayWaste} kg",
                subtitle = "Logged in kitchen records",
                icon = Icons.Default.DeleteSweep,
                accentColor = RiskHigh,
                modifier = Modifier.weight(1f),
                testTag = "card_today_waste"
            )
            StatCard(
                title = "Predicted Tomorrow Waste",
                value = "${summary.tomorrowPredictedWaste} kg",
                subtitle = "Ensemble ML Forecast",
                icon = Icons.Default.TrendingDown,
                accentColor = ChartTeal,
                modifier = Modifier.weight(1f),
                testTag = "card_tomorrow_waste"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Potential Cost Savings & Waste Risk Level
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Potential Cost Savings",
                value = "₹${summary.potentialSavings.toInt()}",
                subtitle = "Via quantity optimization",
                icon = Icons.Default.AttachMoney,
                accentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                testTag = "card_cost_savings"
            )
            StatCard(
                title = "Waste Risk Level",
                value = summary.wasteRiskLevel,
                subtitle = when (summary.wasteRiskLevel) {
                    "HIGH" -> ">30 kg critical threshold"
                    "MEDIUM" -> "15-30 kg moderate loss"
                    else -> "<15 kg optimal prep"
                },
                icon = Icons.Default.Warning,
                accentColor = when (summary.wasteRiskLevel) {
                    "HIGH" -> RiskHigh
                    "MEDIUM" -> RiskMedium
                    else -> RiskLow
                },
                modifier = Modifier.weight(1f),
                testTag = "card_risk_level"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 3: Expected Customers & Recommended Food Quantity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Expected Customers",
                value = "${summary.expectedCustomers}",
                subtitle = "Estimated headcount",
                icon = Icons.Default.Groups,
                accentColor = ChartBlue,
                modifier = Modifier.weight(1f),
                testTag = "card_expected_customers"
            )
            StatCard(
                title = "Recommended Quantity",
                value = "${summary.recommendedFoodQuantity} kg",
                subtitle = "Calculated target prep",
                icon = Icons.Default.Restaurant,
                accentColor = ChartPurple,
                modifier = Modifier.weight(1f),
                testTag = "card_recommended_quantity"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Action Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onNavigate(AppDestination.PREDICTION) },
                modifier = Modifier.weight(1f).height(46.dp).testTag("btn_quick_prediction"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Generate AI Prediction", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = { onNavigate(AppDestination.ADD_DATA) },
                modifier = Modifier.weight(1f).height(46.dp).testTag("btn_quick_add_record"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Food Data", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Analytics Charts
        Text(
            text = "Food Waste Analytics & Trends",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Chart 1: Daily Food Waste Trend
        DailyWasteTrendChart(points = dailyWastePoints)

        Spacer(modifier = Modifier.height(16.dp))

        // Chart 2: Weekly Food Consumption
        WeeklyConsumptionBarChart(items = weeklyConsumptionPoints)

        Spacer(modifier = Modifier.height(16.dp))

        // Chart 3: Food Item-wise Waste
        FoodItemWasteBarChart(items = itemWiseWaste)

        Spacer(modifier = Modifier.height(16.dp))

        // Chart 4: Predicted vs Actual Waste
        PredictedVsActualWasteChart(items = predictedVsActualPoints)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

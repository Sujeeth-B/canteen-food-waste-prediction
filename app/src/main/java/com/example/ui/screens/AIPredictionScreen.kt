package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PredictionEntity
import com.example.ml.AIPredictionResult
import com.example.ml.FeaturePipeline
import com.example.ui.components.AIRecommendationCard
import com.example.ui.components.StatCard
import com.example.ui.components.WasteRiskBadge
import com.example.ui.theme.ChartBlue
import com.example.ui.theme.ChartGreen
import com.example.ui.theme.ChartOrange
import com.example.ui.theme.ChartPurple
import com.example.ui.theme.ChartTeal
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskLow
import com.example.ui.theme.RiskMedium
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AIPredictionScreen(
    onGeneratePrediction: (
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
    ) -> Unit,
    predictionResult: AIPredictionResult?,
    isPredicting: Boolean,
    recentPredictions: List<PredictionEntity>,
    onDeletePrediction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dayFormat = remember { SimpleDateFormat("EEEE", Locale.getDefault()) }
    val cal = remember {
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    }

    var predictionDate by remember { mutableStateOf(dateFormat.format(cal.time)) }
    var day by remember { mutableStateOf(dayFormat.format(cal.time)) }
    var mealType by remember { mutableStateOf("Lunch") }
    var foodItem by remember { mutableStateOf("Rice & Curry") }
    var expectedCustomersText by remember { mutableStateOf("520") }
    var plannedQuantityText by remember { mutableStateOf("155.0") }
    var isHoliday by remember { mutableStateOf(false) }
    var isSpecialEvent by remember { mutableStateOf(false) }
    var weather by remember { mutableStateOf("Sunny") }
    var costPerKgText by remember { mutableStateOf("80.0") }

    var dayExpanded by remember { mutableStateOf(false) }
    var foodItemExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("ai_prediction_screen")
    ) {
        Text(
            text = "AI Demand & Waste Prediction",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Infer expected customer turnout, food consumption, waste risk, and optimal preparation quantity",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Card for Prediction
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Prediction Parameters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date & Day
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = predictionDate,
                        onValueChange = { predictionDate = it },
                        label = { Text("Prediction Date") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.weight(1f).testTag("predict_date_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    ExposedDropdownMenuBox(
                        expanded = dayExpanded,
                        onExpandedChange = { dayExpanded = !dayExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = day,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Day of Week") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = dayExpanded,
                            onDismissRequest = { dayExpanded = false }
                        ) {
                            FeaturePipeline.DAYS.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d) },
                                    onClick = {
                                        day = d
                                        dayExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Meal Type Chips
                Text(
                    text = "Target Meal",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeaturePipeline.MEAL_TYPES.forEach { m ->
                        val isSelected = mealType.equals(m, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { mealType = m },
                            label = { Text(m) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Food Item
                ExposedDropdownMenuBox(
                    expanded = foodItemExpanded,
                    onExpandedChange = { foodItemExpanded = !foodItemExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = foodItem,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Food Item") },
                        leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = foodItemExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = foodItemExpanded,
                        onDismissRequest = { foodItemExpanded = false }
                    ) {
                        FeaturePipeline.FOOD_ITEMS.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    foodItem = item
                                    foodItemExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Customers & Planned Prep & Cost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = expectedCustomersText,
                        onValueChange = { expectedCustomersText = it },
                        label = { Text("Expected Guests") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("predict_customers_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = plannedQuantityText,
                        onValueChange = { plannedQuantityText = it },
                        label = { Text("Planned Prep (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("predict_planned_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = costPerKgText,
                    onValueChange = { costPerKgText = it },
                    label = { Text("Cost Per KG (₹)") },
                    leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Conditions
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isHoliday,
                        onClick = { isHoliday = !isHoliday },
                        label = { Text(if (isHoliday) "Holiday: Yes" else "Holiday: No") }
                    )
                    FilterChip(
                        selected = isSpecialEvent,
                        onClick = { isSpecialEvent = !isSpecialEvent },
                        label = { Text(if (isSpecialEvent) "Special Event: Yes" else "Special Event: No") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Weather Condition", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FeaturePipeline.WEATHERS.forEach { w ->
                        FilterChip(
                            selected = weather.equals(w, ignoreCase = true),
                            onClick = { weather = w },
                            label = { Text(w) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Generate AI Prediction Button
                Button(
                    onClick = {
                        val cust = expectedCustomersText.toIntOrNull() ?: 500
                        val planned = plannedQuantityText.toDoubleOrNull() ?: 150.0
                        val cost = costPerKgText.toDoubleOrNull() ?: 80.0
                        onGeneratePrediction(
                            predictionDate,
                            day,
                            mealType,
                            foodItem,
                            weather,
                            isHoliday,
                            isSpecialEvent,
                            cust,
                            planned,
                            cost
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_generate_prediction"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isPredicting
                ) {
                    if (isPredicting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Running Machine Learning Models...")
                    } else {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate AI Prediction", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // AI PREDICTION RESULT SECTION
        if (predictionResult != null) {
            Text(
                text = "AI Prediction Result",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth().testTag("prediction_result_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    when (predictionResult.wasteRisk) {
                        "HIGH" -> RiskHigh.copy(alpha = 0.5f)
                        "MEDIUM" -> RiskMedium.copy(alpha = 0.5f)
                        else -> RiskLow.copy(alpha = 0.5f)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header with Risk Level Badge & Model Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$mealType - $foodItem",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Date: $predictionDate | Day: $day",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        WasteRiskBadge(riskLevel = predictionResult.wasteRisk)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Output Grid (4 Metrics)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Predicted Customers",
                            value = "${predictionResult.predictedCustomers}",
                            icon = Icons.Default.Groups,
                            accentColor = ChartBlue,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Predicted Consumption",
                            value = "${predictionResult.predictedConsumption} kg",
                            icon = Icons.Default.Restaurant,
                            accentColor = ChartGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Predicted Food Waste",
                            value = "${predictionResult.predictedWaste} kg",
                            icon = Icons.Default.DeleteSweep,
                            accentColor = when (predictionResult.wasteRisk) {
                                "HIGH" -> RiskHigh
                                "MEDIUM" -> RiskMedium
                                else -> RiskLow
                            },
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Recommended Prep",
                            value = "${predictionResult.recommendedPreparation} kg",
                            icon = Icons.Default.AutoAwesome,
                            accentColor = ChartTeal,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Financial Loss & Savings Strip
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Estimated Financial Loss", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${predictionResult.estimatedFinancialLoss.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RiskHigh)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Potential Savings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${predictionResult.potentialSavings.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Comparison: Planned vs Recommended Preparation
                    val diff = predictionResult.plannedQuantity - predictionResult.recommendedPreparation
                    val absDiff = (abs(diff) * 10).toInt() / 10.0

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (diff > 0) RiskMedium.copy(alpha = 0.12f) else if (diff < 0) RiskHigh.copy(alpha = 0.12f) else RiskLow.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (diff > 0) Icons.Default.ArrowDownward else if (diff < 0) Icons.Default.ArrowUpward else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (diff > 0) RiskMedium else if (diff < 0) RiskHigh else RiskLow
                            )
                            Text(
                                text = if (diff > 0) "Action: Reduce food preparation by $absDiff kg."
                                else if (diff < 0) "Warning: Increase food preparation by $absDiff kg to avoid shortage."
                                else "Optimal: Current planned quantity matches recommendation perfectly.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recommendation Text Card
                    AIRecommendationCard(
                        recommendationText = predictionResult.recommendationText,
                        modelName = predictionResult.modelUsed
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Prediction History Table
        if (recentPredictions.isNotEmpty()) {
            Text(
                text = "Recent AI Predictions History (${recentPredictions.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            recentPredictions.take(8).forEach { pred ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${pred.predictionDate} (${pred.mealType})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                WasteRiskBadge(riskLevel = pred.wasteRisk)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${pred.foodItem} - Recommended: ${pred.recommendedQuantity} kg",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Waste: ${pred.predictedWaste} kg | Potential Savings: ₹${pred.potentialSavings.toInt()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { onDeletePrediction(pred.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete prediction",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

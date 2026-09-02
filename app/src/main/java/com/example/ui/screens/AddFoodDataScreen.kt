package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodRecordEntity
import com.example.ml.FeaturePipeline
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddFoodDataScreen(
    onSaveRecord: (
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
        costPerKg: Double
    ) -> Unit,
    recentRecords: List<FoodRecordEntity>,
    onDeleteRecord: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dayFormat = remember { SimpleDateFormat("EEEE", Locale.getDefault()) }
    val now = remember { Date() }

    var date by remember { mutableStateOf(dateFormat.format(now)) }
    var day by remember { mutableStateOf(dayFormat.format(now)) }
    var mealType by remember { mutableStateOf("Lunch") }
    var foodItem by remember { mutableStateOf("Rice & Curry") }
    var customersText by remember { mutableStateOf("450") }
    var foodPreparedText by remember { mutableStateOf("160.0") }
    var foodConsumedText by remember { mutableStateOf("142.0") }
    var foodWastedText by remember { mutableStateOf("18.0") }
    var isHoliday by remember { mutableStateOf(false) }
    var isSpecialEvent by remember { mutableStateOf(false) }
    var weather by remember { mutableStateOf("Sunny") }
    var costPerKgText by remember { mutableStateOf("80.0") }

    var formMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    var dayExpanded by remember { mutableStateOf(false) }
    var foodItemExpanded by remember { mutableStateOf(false) }

    fun resetForm() {
        val today = Date()
        date = dateFormat.format(today)
        day = dayFormat.format(today)
        mealType = "Lunch"
        foodItem = "Rice & Curry"
        customersText = "450"
        foodPreparedText = "160.0"
        foodConsumedText = "142.0"
        foodWastedText = "18.0"
        isHoliday = false
        isSpecialEvent = false
        weather = "Sunny"
        costPerKgText = "80.0"
        formMessage = null
    }

    // Auto-calculate wasted quantity when prepared or consumed changes
    fun updateAutoWaste(prep: String, cons: String) {
        val p = prep.toDoubleOrNull()
        val c = cons.toDoubleOrNull()
        if (p != null && c != null && p >= c) {
            val diff = (p - c)
            foodWastedText = String.format(Locale.US, "%.1f", diff)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("add_food_data_screen")
    ) {
        Text(
            text = "Add Daily Food Record",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Log canteen preparation, customer turnout, and wastage data into SQLite database",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (formMessage != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formMessage ?: "",
                    color = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

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
                // Section 1: Date & Day
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.weight(1f).testTag("input_date"),
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
                            label = { Text("Day") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                            modifier = Modifier.menuAnchor().testTag("dropdown_day"),
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

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Meal Type Selector Chips
                Text(
                    text = "Meal Type",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeaturePipeline.MEAL_TYPES.forEach { meal ->
                        val selected = mealType.equals(meal, ignoreCase = true)
                        FilterChip(
                            selected = selected,
                            onClick = { mealType = meal },
                            label = { Text(meal) },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Food Item Dropdown
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
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("dropdown_food_item"),
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

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: Quantitative Metrics (Customers & Cost)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = customersText,
                        onValueChange = { customersText = it },
                        label = { Text("Number of Customers") },
                        leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_customers"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = costPerKgText,
                        onValueChange = { costPerKgText = it },
                        label = { Text("Cost Per KG (₹)") },
                        leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("input_cost"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 5: Food Quantities (Prepared, Consumed, Wasted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = foodPreparedText,
                        onValueChange = {
                            foodPreparedText = it
                            updateAutoWaste(it, foodConsumedText)
                        },
                        label = { Text("Prepared (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("input_prepared"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = foodConsumedText,
                        onValueChange = {
                            foodConsumedText = it
                            updateAutoWaste(foodPreparedText, it)
                        },
                        label = { Text("Consumed (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("input_consumed"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = foodWastedText,
                        onValueChange = { foodWastedText = it },
                        label = { Text("Wasted (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("input_wasted"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 6: Environmental & Operational Factors
                Text(
                    text = "Operational Conditions",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isHoliday,
                        onClick = { isHoliday = !isHoliday },
                        label = { Text(if (isHoliday) "Holiday: Yes" else "Holiday: No") },
                        leadingIcon = if (isHoliday) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )

                    FilterChip(
                        selected = isSpecialEvent,
                        onClick = { isSpecialEvent = !isSpecialEvent },
                        label = { Text(if (isSpecialEvent) "Special Event: Yes" else "Special Event: No") },
                        leadingIcon = if (isSpecialEvent) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Weather Condition",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons: Save & Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val cust = customersText.toIntOrNull()
                            val prep = foodPreparedText.toDoubleOrNull()
                            val cons = foodConsumedText.toDoubleOrNull()
                            val waste = foodWastedText.toDoubleOrNull()
                            val cost = costPerKgText.toDoubleOrNull()

                            if (date.isBlank() || cust == null || prep == null || cons == null || waste == null || cost == null) {
                                formMessage = "Please ensure all numeric fields are correctly filled."
                                isSuccess = false
                            } else if (prep < 0 || cons < 0 || waste < 0 || cust <= 0 || cost <= 0) {
                                formMessage = "Values cannot be negative or zero for customers/cost."
                                isSuccess = false
                            } else {
                                onSaveRecord(
                                    date,
                                    day,
                                    mealType,
                                    foodItem,
                                    cust,
                                    prep,
                                    cons,
                                    waste,
                                    isHoliday,
                                    isSpecialEvent,
                                    weather,
                                    cost
                                )
                                formMessage = "Record saved successfully to SQLite database!"
                                isSuccess = true
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp).testTag("btn_save_record"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Record", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { resetForm() },
                        modifier = Modifier.weight(1f).height(50.dp).testTag("btn_reset_form"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset Form", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Entries List
        Text(
            text = "Recent Kitchen Logs (${recentRecords.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        recentRecords.take(10).forEach { record ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${record.date} (${record.day.take(3)})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = record.mealType,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${record.foodItem} - ${record.customers} customers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Prep: ${record.foodPrepared} kg | Consumed: ${record.foodConsumed} kg | Waste: ${record.foodWasted} kg",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { onDeleteRecord(record.id) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete record",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

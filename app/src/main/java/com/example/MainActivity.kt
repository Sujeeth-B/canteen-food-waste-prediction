package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppDestination
import com.example.ui.WasteWiseViewModel
import com.example.ui.screens.AIPredictionScreen
import com.example.ui.screens.AddFoodDataScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FoodRecordsScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ModelPerformanceScreen
import com.example.ui.theme.WasteWiseTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WasteWiseViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            WasteWiseTheme(darkTheme = isDarkTheme) {
                val currentUser by viewModel.currentUser.collectAsState()
                val currentDestination by viewModel.currentDestination.collectAsState()
                val authError by viewModel.authError.collectAsState()

                val records by viewModel.foodRecords.collectAsState()
                val predictions by viewModel.predictions.collectAsState()
                val modelResults by viewModel.modelResults.collectAsState()
                val latestPrediction by viewModel.latestPredictionResult.collectAsState()
                val isPredicting by viewModel.isPredicting.collectAsState()
                val isTraining by viewModel.isTraining.collectAsState()
                val analyticsFilter by viewModel.analyticsFilter.collectAsState()

                if (currentUser == null) {
                    LoginScreen(
                        onLoginClick = { email, pass ->
                            viewModel.login(email, pass) {}
                        },
                        onDemoLoginClick = {
                            viewModel.loginDemoAdmin {}
                        },
                        errorMessage = authError,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { viewModel.toggleDarkTheme() }
                    )
                } else {
                    val summary = viewModel.getDashboardSummary(records, predictions)
                    val dailyPoints = viewModel.getDailyWastePoints(records, 7)
                    val weeklyPoints = viewModel.getWeeklyConsumptionPoints(records)
                    val itemWiseWaste = viewModel.getItemWiseWaste(records)
                    val mealTypeWaste = viewModel.getMealTypeWaste(records)
                    val predictedVsActual = viewModel.getPredictedVsActualPoints(records, predictions)

                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Eco,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Text(
                                            text = "WasteWise AI",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = { viewModel.toggleDarkTheme() },
                                        modifier = Modifier.testTag("btn_theme_toggle")
                                    ) {
                                        Icon(
                                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = "Toggle Theme",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Text(
                                            text = currentUser?.role?.uppercase() ?: "ADMIN",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.logout() },
                                        modifier = Modifier.testTag("btn_logout")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ExitToApp,
                                            contentDescription = "Logout",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                val destinations = listOf(
                                    AppDestination.DASHBOARD,
                                    AppDestination.PREDICTION,
                                    AppDestination.RECORDS_HISTORY,
                                    AppDestination.ADD_DATA,
                                    AppDestination.ANALYTICS,
                                    AppDestination.MODEL_PERFORMANCE
                                )

                                destinations.forEach { dest ->
                                    val isSelected = currentDestination == dest
                                    val icon = when (dest) {
                                        AppDestination.DASHBOARD -> Icons.Default.Dashboard
                                        AppDestination.PREDICTION -> Icons.Default.Psychology
                                        AppDestination.ADD_DATA -> Icons.Default.AddCircle
                                        AppDestination.ANALYTICS -> Icons.Default.Analytics
                                        AppDestination.MODEL_PERFORMANCE -> Icons.Default.QueryStats
                                        AppDestination.RECORDS_HISTORY -> Icons.Default.TableChart
                                    }

                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.navigateTo(dest) },
                                        icon = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = dest.title,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = dest.title,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        modifier = Modifier.testTag("nav_item_${dest.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            AnimatedContent(
                                targetState = currentDestination,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "screen_transition"
                            ) { destination ->
                                when (destination) {
                                    AppDestination.DASHBOARD -> DashboardScreen(
                                        summary = summary,
                                        records = records,
                                        predictions = predictions,
                                        dailyWastePoints = dailyPoints,
                                        weeklyConsumptionPoints = weeklyPoints,
                                        itemWiseWaste = itemWiseWaste,
                                        predictedVsActualPoints = predictedVsActual,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                    AppDestination.PREDICTION -> AIPredictionScreen(
                                        onGeneratePrediction = { dt, dy, meal, item, wth, hol, evt, cust, pln, cost ->
                                            viewModel.runPrediction(dt, dy, meal, item, wth, hol, evt, cust, pln, cost)
                                        },
                                        predictionResult = latestPrediction,
                                        isPredicting = isPredicting,
                                        recentPredictions = predictions,
                                        onDeletePrediction = { id -> viewModel.deleteRecord(id) }
                                    )
                                    AppDestination.ADD_DATA -> AddFoodDataScreen(
                                        onSaveRecord = { dt, dy, meal, item, cust, prep, cons, waste, hol, evt, wth, cost ->
                                            viewModel.saveFoodRecord(dt, dy, meal, item, cust, prep, cons, waste, hol, evt, wth, cost) {}
                                        },
                                        recentRecords = records,
                                        onDeleteRecord = { id -> viewModel.deleteRecord(id) }
                                    )
                                    AppDestination.ANALYTICS -> AnalyticsScreen(
                                        currentFilter = analyticsFilter,
                                        onFilterChange = { viewModel.setAnalyticsFilter(it) },
                                        records = records,
                                        predictions = predictions,
                                        dailyPoints = dailyPoints,
                                        weeklyPoints = weeklyPoints,
                                        itemWiseWaste = itemWiseWaste,
                                        mealTypeWaste = mealTypeWaste,
                                        predictedVsActualPoints = predictedVsActual
                                    )
                                    AppDestination.MODEL_PERFORMANCE -> ModelPerformanceScreen(
                                        models = modelResults,
                                        isTraining = isTraining,
                                        onRetrainModels = { viewModel.retrainModels() },
                                        totalRecords = records.size
                                    )
                                    AppDestination.RECORDS_HISTORY -> FoodRecordsScreen(
                                        records = records,
                                        onDeleteRecord = { id -> viewModel.deleteRecord(id) },
                                        onNavigateToAdd = { viewModel.navigateTo(AppDestination.ADD_DATA) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

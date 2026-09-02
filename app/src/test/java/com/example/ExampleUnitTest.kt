package com.example

import com.example.ml.DecisionTreeRegressor
import com.example.ml.FeaturePipeline
import com.example.ml.LinearRegressionModel
import com.example.ml.MLEngine
import com.example.ml.RandomForestRegressor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ExampleUnitTest {

    @Test
    fun testFeaturePipelineEncoding() {
        val pipeline = FeaturePipeline()
        val vector = pipeline.extractFeatures(
            day = "Monday",
            mealType = "Lunch",
            foodItem = "Rice & Curry",
            weather = "Sunny",
            holiday = false,
            specialEvent = false,
            customers = 500,
            costPerKg = 80.0
        )
        // 7 days + 3 meal types + 8 food items + 3 weathers + 3 binary flags + 2 numerical = 26 features
        assertEquals(26, vector.size)
        // Check continuous features normalized
        assertEquals(0.5, vector[24], 0.001) // 500 / 1000
        assertEquals(0.4, vector[25], 0.001) // 80 / 200
    }

    @Test
    fun testLinearRegressionTrainingAndPrediction() {
        val model = LinearRegressionModel()
        // Simple synthetic dataset: y = 2*x0 + 3*x1
        val X = arrayOf(
            doubleArrayOf(1.0, 1.0),
            doubleArrayOf(2.0, 1.0),
            doubleArrayOf(1.0, 2.0),
            doubleArrayOf(3.0, 2.0),
            doubleArrayOf(2.0, 3.0),
            doubleArrayOf(4.0, 3.0)
        )
        val y = doubleArrayOf(5.0, 7.0, 8.0, 12.0, 13.0, 17.0)

        model.train(X, y)
        val pred = model.predict(doubleArrayOf(2.0, 2.0))
        // Expected ~ 10.0
        assertTrue(pred > 7.0 && pred < 13.0)
    }

    @Test
    fun testDecisionTreeAndRandomForest() {
        val X = arrayOf(
            doubleArrayOf(100.0, 1.0),
            doubleArrayOf(200.0, 1.0),
            doubleArrayOf(300.0, 2.0),
            doubleArrayOf(400.0, 2.0),
            doubleArrayOf(500.0, 3.0),
            doubleArrayOf(600.0, 3.0)
        )
        val y = doubleArrayOf(10.0, 18.0, 28.0, 39.0, 48.0, 61.0)

        val dt = DecisionTreeRegressor(maxDepth = 3)
        dt.train(X, y)
        val predDt = dt.predict(doubleArrayOf(250.0, 1.5))
        assertTrue(predDt > 5.0 && predDt < 45.0)

        val rf = RandomForestRegressor(numTrees = 6, maxDepth = 3)
        rf.train(X, y)
        val predRf = rf.predict(doubleArrayOf(350.0, 2.0))
        assertTrue(predRf > 15.0 && predRf < 55.0)
    }

    @Test
    fun testMLEngineEvaluationMetrics() {
        val X = arrayOf(
            doubleArrayOf(1.0, 0.5),
            doubleArrayOf(2.0, 0.5),
            doubleArrayOf(3.0, 0.8),
            doubleArrayOf(4.0, 0.8),
            doubleArrayOf(5.0, 1.0)
        )
        val y = doubleArrayOf(10.0, 20.0, 30.0, 40.0, 50.0)

        val engine = MLEngine()
        val metrics = engine.trainAndEvaluate(X, y, X, y)
        assertEquals(3, metrics.size)
        metrics.forEach { metric ->
            assertTrue(metric.mae >= 0.0)
            assertTrue(metric.rmse >= 0.0)
            assertTrue(metric.r2Score <= 1.0)
        }
    }
}

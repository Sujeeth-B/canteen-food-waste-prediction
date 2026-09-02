package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FoodRecordEntity
import com.example.data.UserEntity
import com.example.data.WasteWiseDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: WasteWiseDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WasteWiseDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun readAppNameString() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WasteWise AI", appName)
    }

    @Test
    fun testUserDaoOperations() = runBlocking {
        val userDao = db.userDao()
        val user = UserEntity(
            name = "Admin User",
            email = "admin@wastewise.ai",
            password = "password123",
            role = "admin"
        )
        userDao.insertUser(user)

        val loggedIn = userDao.login("admin@wastewise.ai", "password123")
        assertNotNull(loggedIn)
        assertEquals("Admin User", loggedIn?.name)
    }

    @Test
    fun testFoodRecordDaoOperations() = runBlocking {
        val recordDao = db.foodRecordDao()
        val record = FoodRecordEntity(
            date = "2026-03-01",
            day = "Monday",
            mealType = "Lunch",
            foodItem = "Rice & Curry",
            customers = 450,
            foodPrepared = 150.0,
            foodConsumed = 135.0,
            foodWasted = 15.0,
            holiday = false,
            specialEvent = false,
            weather = "Sunny",
            costPerKg = 80.0
        )
        recordDao.insertRecord(record)

        val count = recordDao.getCount()
        assertEquals(1, count)

        val all = recordDao.getAllRecordsSync()
        assertEquals(1, all.size)
        assertEquals(15.0, all[0].foodWasted, 0.001)
    }
}

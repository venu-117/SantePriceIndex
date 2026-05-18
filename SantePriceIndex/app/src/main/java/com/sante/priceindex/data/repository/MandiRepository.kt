package com.sante.priceindex.data.repository

import com.sante.priceindex.data.model.MandiPrice
import com.sante.priceindex.data.model.PriceTrend
import com.sante.priceindex.data.model.TrendDataPoint
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.random.Random

class MandiRepository {

    // Simulated network fetch with dynamic data
    suspend fun fetchTodayPrices(): List<MandiPrice> {
        delay(800) // Simulate network latency
        
        // Randomize prices slightly to simulate "Live" updates
        return MOCK_PRICES.map { original ->
            val randomChange = Random.nextDouble(-1.5, 1.5)
            val newPrice = (original.mandiPrice + randomChange).coerceAtLeast(5.0)
            
            val newTrend = when {
                randomChange > 0.5 -> PriceTrend.RISING
                randomChange < -0.5 -> PriceTrend.FALLING
                else -> PriceTrend.STABLE
            }

            original.copy(
                mandiPrice = String.format(Locale.US, "%.2f", newPrice).toDouble(),
                trend = newTrend,
                lastUpdated = "Updated just now"
            )
        }
    }

    suspend fun fetchTrendData(vegetableId: String): List<TrendDataPoint> {
        delay(400)
        return MOCK_TRENDS[vegetableId] ?: emptyList()
    }

    companion object {
        val MOCK_PRICES = listOf(
            MandiPrice(
                id = "onion",
                vegetableName = "Onion",
                vegetableNameHindi = "प्याज",
                mandiPrice = 18.0,
                unit = "kg",
                category = "Vegetables",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.RISING,
                emoji = "🧅"
            ),
            MandiPrice(
                id = "tomato",
                vegetableName = "Tomato",
                vegetableNameHindi = "टमाटर",
                mandiPrice = 25.0,
                unit = "kg",
                category = "Vegetables",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.FALLING,
                emoji = "🍅"
            ),
            MandiPrice(
                id = "potato",
                vegetableName = "Potato",
                vegetableNameHindi = "आलू",
                mandiPrice = 14.0,
                unit = "kg",
                category = "Vegetables",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.STABLE,
                emoji = "🥔"
            ),
            MandiPrice(
                id = "garlic",
                vegetableName = "Garlic",
                vegetableNameHindi = "लहसुन",
                mandiPrice = 120.0,
                unit = "kg",
                category = "Spices",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.RISING,
                emoji = "🧄"
            ),
            MandiPrice(
                id = "ginger",
                vegetableName = "Ginger",
                vegetableNameHindi = "अदरक",
                mandiPrice = 80.0,
                unit = "kg",
                category = "Spices",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.STABLE,
                emoji = "🫚"
            ),
            MandiPrice(
                id = "spinach",
                vegetableName = "Spinach",
                vegetableNameHindi = "पालक",
                mandiPrice = 20.0,
                unit = "kg",
                category = "Leafy",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.FALLING,
                emoji = "🥬"
            ),
            MandiPrice(
                id = "cauliflower",
                vegetableName = "Cauliflower",
                vegetableNameHindi = "फूलगोभी",
                mandiPrice = 22.0,
                unit = "kg",
                category = "Vegetables",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.STABLE,
                emoji = "🥦"
            ),
            MandiPrice(
                id = "chilli",
                vegetableName = "Green Chilli",
                vegetableNameHindi = "हरी मिर्च",
                mandiPrice = 45.0,
                unit = "kg",
                category = "Spices",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.RISING,
                emoji = "🌶️"
            ),
            MandiPrice(
                id = "lemon",
                vegetableName = "Lemon",
                vegetableNameHindi = "नींबू",
                mandiPrice = 60.0,
                unit = "dozen",
                category = "Fruits",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.STABLE,
                emoji = "🍋"
            ),
            MandiPrice(
                id = "banana",
                vegetableName = "Banana",
                vegetableNameHindi = "केला",
                mandiPrice = 30.0,
                unit = "dozen",
                category = "Fruits",
                lastUpdated = "Today 6:00 AM",
                trend = PriceTrend.FALLING,
                emoji = "🍌"
            )
        )

        val MOCK_TRENDS = mapOf(
            "onion" to listOf(
                TrendDataPoint("Mon", 15.0), TrendDataPoint("Tue", 16.0),
                TrendDataPoint("Wed", 16.5), TrendDataPoint("Thu", 17.0),
                TrendDataPoint("Fri", 18.0), TrendDataPoint("Sat", 18.0),
                TrendDataPoint("Sun", 19.0)
            ),
            "tomato" to listOf(
                TrendDataPoint("Mon", 35.0), TrendDataPoint("Tue", 32.0),
                TrendDataPoint("Wed", 30.0), TrendDataPoint("Thu", 28.0),
                TrendDataPoint("Fri", 25.0), TrendDataPoint("Sat", 24.0),
                TrendDataPoint("Sun", 23.0)
            ),
            "potato" to listOf(
                TrendDataPoint("Mon", 14.0), TrendDataPoint("Tue", 13.5),
                TrendDataPoint("Wed", 14.0), TrendDataPoint("Thu", 14.5),
                TrendDataPoint("Fri", 14.0), TrendDataPoint("Sat", 14.0),
                TrendDataPoint("Sun", 14.0)
            ),
            "garlic" to listOf(
                TrendDataPoint("Mon", 100.0), TrendDataPoint("Tue", 105.0),
                TrendDataPoint("Wed", 110.0), TrendDataPoint("Thu", 115.0),
                TrendDataPoint("Fri", 118.0), TrendDataPoint("Sat", 120.0),
                TrendDataPoint("Sun", 122.0)
            )
        )
    }
}

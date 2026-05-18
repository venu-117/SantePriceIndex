package com.sante.priceindex.util

import com.sante.priceindex.data.model.VendorCalculation
import java.util.Locale
import kotlin.math.roundToInt

object PricingEngine {

    /**
     * Core cost-plus pricing algorithm.
     */
    fun calculate(
        mandiPrice: Double,
        transportCostPerKg: Double,
        wastagePercent: Double,
        profitMarginPercent: Double,
        quantityKg: Double
    ): VendorCalculation {
        return VendorCalculation(
            mandiPrice = mandiPrice,
            transportCostPerKg = transportCostPerKg,
            wastagePercent = wastagePercent,
            profitMarginPercent = profitMarginPercent,
            quantityKg = quantityKg
        )
    }

    /**
     * Round price to nearest 50 paise
     */
    fun roundToNearestPaise(price: Double): Double {
        return (price * 2).roundToInt() / 2.0
    }

    /**
     * Suggest a "market friendly" price ending in 0 or 5
     */
    fun suggestMarketPrice(calculatedPrice: Double): Double {
        val base = calculatedPrice.toInt()
        val remainder = base % 5
        return if (remainder <= 2) {
            (base - remainder).toDouble()
        } else {
            (base + (5 - remainder)).toDouble()
        }
    }

    fun formatPrice(price: Double): String {
        return String.format(Locale.US, "₹%.2f", price)
    }

    fun getTrendMessage(vegName: String, trend: String): String {
        return when (trend) {
            "RISING" -> "📈 $vegName prices are rising!"
            "FALLING" -> "📉 $vegName prices are falling."
            else -> "➡️ $vegName prices are stable."
        }
    }
}

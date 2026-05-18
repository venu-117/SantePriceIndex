package com.sante.priceindex.data.model

data class MandiPrice(
    val id: String = "",
    val vegetableName: String = "",
    val vegetableNameHindi: String = "",
    val mandiPrice: Double = 0.0,      // Price per kg at Mandi (wholesale)
    val unit: String = "kg",
    val category: String = "",
    val lastUpdated: String = "",
    val trend: PriceTrend = PriceTrend.STABLE,
    val emoji: String = "🥬"
)

enum class PriceTrend {
    RISING, FALLING, STABLE
}

data class VendorCalculation(
    val mandiPrice: Double,
    val transportCostPerKg: Double,
    val wastagePercent: Double,
    val profitMarginPercent: Double,
    val quantityKg: Double
) {
    val totalCostPerKg: Double
        get() = mandiPrice + transportCostPerKg + (mandiPrice * wastagePercent / 100)

    val recommendedRetailPrice: Double
        get() = totalCostPerKg * (1 + profitMarginPercent / 100)

    val totalInvestment: Double
        get() = totalCostPerKg * quantityKg

    val expectedRevenue: Double
        get() = recommendedRetailPrice * quantityKg * (1 - wastagePercent / 100)

    val expectedNetProfit: Double
        get() = expectedRevenue - totalInvestment

    val profitPercentage: Double
        get() = if (totalInvestment > 0) (expectedNetProfit / totalInvestment) * 100 else 0.0
}

data class TrendDataPoint(
    val day: String,
    val price: Double
)

package com.example.trafficoin.ui.coin

/**
 * @author Jack
 */
object MockCoinData {
    fun getPreviewItems(): List<ExchangeItem> {
        return listOf(
            ExchangeItem("EUR", "0.850"),
            ExchangeItem("USD", "1"),
            ExchangeItem("JPY", "156.646"),
        )
    }
}
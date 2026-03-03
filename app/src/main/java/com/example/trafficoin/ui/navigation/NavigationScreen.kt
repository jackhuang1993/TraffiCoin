package com.example.trafficoin.ui.navigation

import androidx.annotation.DrawableRes
import com.example.trafficoin.R

/**
 * @author Jack
 */
sealed class NavigationScreen(
    val route: String,
    val title: String,
    @param:DrawableRes val iconRes: Int
) {
    object Flight : NavigationScreen(
        route = "Flight",
        title = "航班",
        iconRes = R.drawable.baseline_airport
    )

    object Coin : NavigationScreen(
        route = "coin",
        title = "匯率",
        iconRes = R.drawable.outline_balance
    )

    companion object {
        val items = listOf(Flight, Coin)
    }
}
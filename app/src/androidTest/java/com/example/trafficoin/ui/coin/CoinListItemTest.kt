package com.example.trafficoin.ui.coin

import android.annotation.SuppressLint
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

/**
 * @author Jack
 */
class CoinListItemTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockItem = ExchangeItem("JPY", "156.646")

    @Test
    fun coinListItem_displays() {
        composeTestRule.setContent {
            CoinListItem(
                item = mockItem,
                isSelected = false,
                isHighlighted = false,
                onClick = {},
                onLongClick = {}
            )
        }

        composeTestRule.onNodeWithText("JPY").assertIsDisplayed()
        composeTestRule.onNodeWithText("156.646").assertIsDisplayed()
    }

    @Test
    fun coinListItem_click() {
        var clicked = false

        composeTestRule.setContent {
            CoinListItem(
                item = mockItem,
                isSelected = false,
                isHighlighted = false,
                onClick = { clicked = true },
                onLongClick = {}
            )
        }

        composeTestRule.onNodeWithTag("coin_item_JPY").performClick()
        assert(clicked)
    }

    @SuppressLint("CheckResult")
    @Test
    fun coinListItem_longClick_triggersReset() {
        var resetCalled = false
        composeTestRule.setContent {
            CoinListItem(
                item = mockItem,
                isHighlighted = true,
                isSelected = false,
                onClick = {},
                onLongClick = { resetCalled = true }
            )
        }

        composeTestRule.onNodeWithTag("coin_item_JPY").performTouchInput {
            down(center)
            advanceEventTime(800)
            up()
        }
        assert(resetCalled)
    }
}
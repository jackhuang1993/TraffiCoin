package com.example.trafficoin.ui.coin

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

/**
 * @author Jack
 */
class CoinScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val lastUpdate = "2026/03/02 10:00:00"

    private val mockItems = listOf(
        ExchangeItem("EUR", "0.850"),
        ExchangeItem("USD", "1"),
        ExchangeItem("JPY", "156.646"),
    )

    @Test
    fun loadingState_showsProgressIndicator() {
        composeTestRule.setContent {
            CoinScreen(
                uiState = CoinUiState(listOf(), isLoading = true),
                onReset = {},
                onRefresh = {},
                onInputConfirm = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        composeTestRule.setContent {
            CoinScreen(
                uiState = CoinUiState(listOf(), errorMsg = "500 Internal Server Error"),
                onReset = {},
                onRefresh = {},
                onInputConfirm = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("更新失敗").assertIsDisplayed()
    }

    @Test
    fun successState_showsListAndHeader() {
        composeTestRule.setContent {
            CoinScreen(
                uiState = CoinUiState(mockItems, lastUpdate),
                onReset = {},
                onRefresh = {},
                onInputConfirm = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText(lastUpdate).assertIsDisplayed()
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("156.646").assertIsDisplayed()
    }

    @Test
    fun clickRefresh_triggersCallback() {
        var refreshCalled = false
        composeTestRule.setContent {
            CoinScreen(
                uiState = CoinUiState(mockItems, lastUpdate),
                onReset = {},
                onRefresh = { refreshCalled = true },
                onInputConfirm = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithTag("refresh_button").performClick()
        assert(refreshCalled)
    }

    @Test
    fun clickItem_opensInputDialog() {
        composeTestRule.setContent {
            CoinScreen(
                uiState = CoinUiState(mockItems, lastUpdate),
                onReset = {},
                onRefresh = {},
                onInputConfirm = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithTag("coin_item_JPY").performClick()

        composeTestRule.onNodeWithText("輸入 JPY 金額", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("確認", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun longClickHighlightedItem_triggersReset() {
        var resetCalled = false
        val baseCurrency = "USD"

        composeTestRule.setContent {
            CoinScreen(
                uiState = CoinUiState(mockItems, lastUpdate, baseCurrency = baseCurrency),
                onRefresh = {},
                onReset = { resetCalled = true },
                onInputConfirm = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithTag("coin_item_$baseCurrency").performTouchInput {
            down(center)
            advanceEventTime(800)
            up()
        }

        assert(resetCalled)
    }

    @Test
    fun confirmInputDialog_passesCorrectDataToCallback() {
        var confirmedAmount = ""
        var confirmedCurrency = ""

        composeTestRule.setContent {
            CoinScreen(
                uiState = CoinUiState(mockItems, lastUpdate),
                onRefresh = {},
                onReset = {},
                onInputConfirm = { amount, code ->
                    confirmedAmount = amount
                    confirmedCurrency = code
                }
            )
        }

        composeTestRule.onNodeWithText("JPY").performClick()

        // 在 Dialog 內輸入並確認
        composeTestRule.onNodeWithTag("input_field_amount").performTextInput("500")
        composeTestRule.onNodeWithTag("btn_confirm").performClick()

        assert(confirmedAmount == "500")
        assert(confirmedCurrency == "JPY")
    }
}
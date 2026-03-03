package com.example.trafficoin.ui.coin

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import org.junit.Rule
import org.junit.Test

/**
 * @author Jack
 */
class ExchangeInputDialogTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dialog_initialValue() {
        composeTestRule.setContent {
            ExchangeInputDialog(
                currencyCode = "JPY",
                initialValue = "3133",
                onConfirm = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("輸入 JPY 金額").assertIsDisplayed()
        composeTestRule.onNodeWithTag("input_amount").assertTextContains("3133")
    }

    @Test
    fun customKeyboard_inputSequence_isCorrect() {
        composeTestRule.setContent {
            ExchangeInputDialog(
                currencyCode = "USD",
                initialValue = "",
                onConfirm = {},
                onDismiss = {}
            )
        }

        val inputNode = composeTestRule.onNodeWithTag("input_amount")

        composeTestRule.onNodeWithTag("btn_1").performClick()
        composeTestRule.onNodeWithTag("btn_2").performClick()
        composeTestRule.onNodeWithTag("btn_3").performClick()
        composeTestRule.onNodeWithTag("btn_4").performClick()
        inputNode.assertTextContains("1234")

        composeTestRule.onNodeWithTag("btn_⌫").performClick()
        composeTestRule.onNodeWithTag("btn_5").performClick()
        composeTestRule.onNodeWithTag("btn_6").performClick()
        composeTestRule.onNodeWithTag("btn_7").performClick()
        inputNode.assertTextContains("123567")

        composeTestRule.onNodeWithTag("btn_C").performClick()
        composeTestRule.onNodeWithTag("btn_8").performClick()
        composeTestRule.onNodeWithTag("btn_9").performClick()
        composeTestRule.onNodeWithTag("btn_0").performClick()
        inputNode.assertTextContains("890")
    }

    @Test
    fun clickingConfirm_triggersCallbackWithNewValue() {
        var confirmedValue = ""
        composeTestRule.setContent {
            ExchangeInputDialog(
                currencyCode = "JPY",
                initialValue = "",
                onConfirm = { confirmedValue = it },
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("btn_1").performClick()
        composeTestRule.onNodeWithTag("btn_2").performClick()
        composeTestRule.onNodeWithTag("btn_3").performClick()
        composeTestRule.onNodeWithTag("btn_confirm").performClick()
        assert(confirmedValue == "123")
    }

    @Test
    fun pressBack_triggersDismissCallback() {
        var dismissCalled = false
        composeTestRule.setContent {
            ExchangeInputDialog(
                currencyCode = "USD",
                initialValue = "100",
                onConfirm = {},
                onDismiss = { dismissCalled = true }
            )
        }

        Espresso.pressBack()
        assert(dismissCalled)
    }
}
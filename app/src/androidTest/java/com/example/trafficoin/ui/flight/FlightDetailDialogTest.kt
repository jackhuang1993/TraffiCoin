package com.example.trafficoin.ui.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.trafficoin.data.flight.Flight
import org.junit.Rule
import org.junit.Test

/**
 * @author Jack
 */
class FlightDetailDialogTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val detailFlight = Flight(
        airline = "UIA",
        airName = "立榮航空",
        flightNo = "B78690",
        aircraftType = "AT76",
        logoUrl = "https://www.kia.gov.tw/images/ALL-square/B7.png",
        airportCode = "MZG",
        airportName = "澎湖",
        scheduledTime = "09:15",
        actualTime = "09:04",
        status = "抵達",
        gate = "36",
        delayReason = "",
        localFile = null
    )

    @Test
    fun showsFullDetails() {
        composeTestRule.setContent {
            FlightDetailDialog(flight = detailFlight, onDismiss = {})
        }

        composeTestRule.onNodeWithText("立榮航空(UIA) B78690").assertIsDisplayed()
        composeTestRule.onNodeWithText("AT76").assertIsDisplayed()
        composeTestRule.onNodeWithText("澎湖 (MZG)").assertIsDisplayed()
        composeTestRule.onNodeWithText("09:15").assertIsDisplayed()
        composeTestRule.onNodeWithText("09:04").assertIsDisplayed()
        composeTestRule.onNodeWithText("36").assertIsDisplayed()
    }

    @Test
    fun showsFullDetailsDelay() {
        val flight = detailFlight.copy(
            actualTime = "10:10",
            gate = "36",
            delayReason = "天候影響",
        )
        composeTestRule.setContent {
            FlightDetailDialog(flight = flight, onDismiss = {})
        }

        composeTestRule.onNodeWithText("10:10").assertIsDisplayed()
        composeTestRule.onNodeWithText("天候影響").assertIsDisplayed()
    }

    @Test
    fun dismiss() {
        var dismissed = false
        composeTestRule.setContent {
            FlightDetailDialog(flight = detailFlight, onDismiss = { dismissed = true })
        }

        composeTestRule.onNodeWithTag("dialog_close_button").performClick()

        assert(dismissed)
    }
}
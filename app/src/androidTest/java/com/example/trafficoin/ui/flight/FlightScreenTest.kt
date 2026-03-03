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
class FlightScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val lastUpdate = "2026/03/02 10:00:00"

    private val mockFlights = listOf(
        FlightTest("UIA", "B78690", "09:15", "09:04", "抵達").toFlight(),
        FlightTest("MDA", "AE332", "09:40", "09:56", "").toFlight()
    )


    @Test
    fun loadingState_showsProgressIndicator() {
        composeTestRule.setContent {
            FlightScreen(
                uiState = FlightUiState(listOf(), isLoading = true),
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        composeTestRule.setContent {
            FlightScreen(
                uiState = FlightUiState(listOf(), errorMsg = "500 Internal Server Error"),
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText("更新失敗").assertIsDisplayed()
    }

    @Test
    fun successState_showsListAndHeader() {
        composeTestRule.setContent {
            FlightScreen(
                uiState = FlightUiState(mockFlights, lastUpdate),
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText(lastUpdate).assertIsDisplayed()
        composeTestRule.onNodeWithText("UIA - B78690").assertIsDisplayed()
        composeTestRule.onNodeWithText("MDA - AE332").assertIsDisplayed()
    }

    @Test
    fun clickRefresh_triggersCallback() {
        var refreshCalled = false
        composeTestRule.setContent {
            FlightScreen(
                uiState = FlightUiState(mockFlights, lastUpdate),
                onRefresh = { refreshCalled = true }
            )
        }

        composeTestRule.onNodeWithTag("refresh_button").performClick()
        assert(refreshCalled)
    }

    @Test
    fun clickItem_opensDetailDialog() {
        val flights = listOf(
            Flight(
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
        )
        composeTestRule.setContent {
            FlightScreen(
                uiState = FlightUiState(flights, lastUpdate),
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithTag("flight_item_B78690").performClick()

        composeTestRule.onNodeWithText("立榮航空(UIA) B78690", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("機型", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("登機門", useUnmergedTree = true).assertIsDisplayed()
    }

    private class FlightTest(
        val airline: String,
        val flightNo: String,
        val scheduledTime: String,
        val actualTime: String,
        val status: String,
        val airName: String = "",
        val aircraftType: String = "",
        val logoUrl: String = "",
        val airportCode: String = "",
        val airportName: String? = "",
        val gate: String? = "",
        val delayReason: String? = "",
    ) {
        fun toFlight(): Flight = Flight(
            airline,
            airName,
            flightNo,
            aircraftType,
            logoUrl,
            airportCode,
            airportName,
            scheduledTime,
            actualTime,
            status,
            gate,
            delayReason
        )
    }

}
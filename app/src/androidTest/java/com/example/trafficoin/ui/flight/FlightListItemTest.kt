package com.example.trafficoin.ui.flight

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.trafficoin.data.flight.Flight
import org.junit.Rule
import org.junit.Test

/**
 * @author Jack
 */
class FlightListItemTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockFlight = Flight(
        airline = "UIA",
        airName = "",
        flightNo = "B78690",
        aircraftType = "",
        logoUrl = "",
        airportCode = "",
        airportName = "",
        scheduledTime = "09:15",
        actualTime = "09:04",
        status = "抵達",
        gate = "",
        delayReason = "",
        localFile = null
    )

    @Test
    fun flightListItem_displays() {
        composeTestRule.setContent {
            FlightListItem(flight = mockFlight, onClick = {})
        }

        composeTestRule.onNodeWithText("UIA - B78690").assertIsDisplayed()
        composeTestRule.onNodeWithText("預計時間：09:15").assertIsDisplayed()
        composeTestRule.onNodeWithText("抵達").assertIsDisplayed()
    }

    @Test
    fun flightListItem_displays_delay() {
        val flight = mockFlight.copy(actualTime = "10:25", status = "")

        composeTestRule.setContent {
            FlightListItem(flight = flight, onClick = {})
        }

        composeTestRule.onNodeWithText("10:25").assertIsDisplayed()
        composeTestRule.onNodeWithText("延誤").assertIsDisplayed()
    }

    @Test
    fun flightListItem_click() {
        var clicked = false
        composeTestRule.setContent {
            FlightListItem(flight = mockFlight, onClick = { clicked = true })
        }

        composeTestRule.onNodeWithTag("flight_item_B78690").performClick()
        assert(clicked)
    }
}
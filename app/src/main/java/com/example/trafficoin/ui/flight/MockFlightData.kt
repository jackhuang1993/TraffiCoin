package com.example.trafficoin.ui.flight

import com.example.trafficoin.data.flight.Flight

/**
 * @author Jack
 */
object MockFlightData {
    fun getPreviewFlight() = Flight(
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

    fun getPreviewFlights(): List<Flight> {
        val flight = getPreviewFlight()
        return List(5) { flight.copy(flightNo = "${flight.flightNo}-${it}") }
    }
}
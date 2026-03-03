package com.example.trafficoin.data.flight

import com.example.trafficoin.network.flight.FlightApi
import com.example.trafficoin.network.flight.FlightDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * @author Jack
 */
class FlightRepositoryTest {
    private val mockApi: FlightApi = mockk()
    private lateinit var repository: FlightRepository

    @Before
    fun setup() {
        repository = FlightRepositoryImp(mockApi)
    }

    @Test
    fun getTrafficDataSuccess() = runTest {
        val apiResponse = listOf(
            FlightDto(
                expectTime = "21:30",
                realTime = "21:31",
                airLineName = "立榮航空",
                airLineCode = "UIA",
                airLineLogo = "https://www.kia.gov.tw/images/ALL- = /B7.png",
                airLineUrl = "https://www.kia.gov.tw/contact.html#立榮航空",
                airLineNum = "B79170",
                originAirportCode = "MZG",
                originAirportName = "澎湖",
                airPlaneType = "AT76",
                boardingGate = null,
                flyStatus = "抵達",
                delayCause = ""
            )
        )
        coEvery { mockApi.queryFlights() } returns apiResponse

        val result = repository.getFlightData()
        assert(result.size == 1)
        assert(result[0].airportCode == "MZG")
        assert(result[0].status == "抵達")

        coVerify(exactly = 1) { mockApi.queryFlights() }
    }

    @Test(expected = Exception::class)
    fun getTrafficDataError() = runTest {
        coEvery { mockApi.queryFlights() } throws IOException("Network Error")

        repository.getFlightData()
    }
}
package com.example.trafficoin.ui.flight

import com.example.trafficoin.MainDispatcherRule
import com.example.trafficoin.data.flight.Flight
import com.example.trafficoin.data.flight.FlightRepository
import com.example.trafficoin.manager.media.ImageDownloader
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.*
import java.io.File

/**
 * @author Jack
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FlightViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockRepo: FlightRepository = mockk()
    private val mockDownloader: ImageDownloader = mockk()
    private lateinit var viewModel: FlightViewModel

    @Before
    fun setup() {
        coEvery { mockDownloader.download(any()) } returns File("fake/path")
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `init - should fetch data and update state`() = runTest {
        val mockData = listOf(fetchFlight())
        coEvery { mockRepo.getFlightData() } returns mockData

        collectViewModel()
        runCurrent()

        coVerify(exactly = 1) { mockRepo.getFlightData() }
        val state = viewModel.uiState.value
        assert(state.data.resetFile() == mockData)
        assert(state.errorMsg == null)
        assert(state.lastUpdate.isNotEmpty())
    }

    @Test
    fun `auto refresh - should fetch data every 10 seconds`() = runTest {
        collectViewModel()
        runCurrent()    // 第 1 次

        advanceTimeBy(10000)
        runCurrent()    // 第 2 次

        advanceTimeBy(10000)
        runCurrent()    // 第 3 次
        coVerify(exactly = 3) { mockRepo.getFlightData() }
    }

    @Test
    fun `error handling - should show error and empty flights`() = runTest {
        val errorReason = "API Error"
        coEvery { mockRepo.getFlightData() } throws Exception(errorReason)

        collectViewModel()
        runCurrent()

        val state = viewModel.uiState.value
        assert(state.data.isEmpty())
        assert(state.errorMsg == errorReason)
        assert(state.isInitialError)
    }

    @Test
    fun `refresh error with existing data`() = runTest {
        val firstData = listOf(fetchFlight())
        coEvery { mockRepo.getFlightData() } returns firstData

        collectViewModel()
        runCurrent()
        val firstUpdateTime = viewModel.uiState.value.lastUpdate

        val errorReason = "500 Internal Server Error"
        coEvery { mockRepo.getFlightData() } throws Exception(errorReason)

        advanceTimeBy(10001)
        runCurrent()

        val state = viewModel.uiState.value
        assert(state.data.resetFile() == firstData)
        assert(state.lastUpdate == firstUpdateTime)
        assert(state.errorMsg == errorReason)
        assert(!state.isInitialError)
    }

    @Test
    fun manualRefresh() = runTest {
        collectViewModel()
        runCurrent()
        coVerify(exactly = 1) { mockRepo.getFlightData() }

        advanceTimeBy(5000)
        runCurrent()

        viewModel.manualRefresh()
        runCurrent()
        coVerify(exactly = 2) { mockRepo.getFlightData() }

        advanceTimeBy(6000)
        runCurrent()
        coVerify(exactly = 2) { mockRepo.getFlightData() }

        advanceTimeBy(4000)
        runCurrent()
        coVerify(exactly = 3) { mockRepo.getFlightData() }
    }

    @Test(expected = Exception::class)
    fun `image download failure - should still maintain success state with original data`() =
        runTest {
            coEvery { mockDownloader.download(any()) } throws Exception("404 Not Found")

            collectViewModel()
            runCurrent()

            val state = viewModel.uiState.value
            assert(state.data.first().localFile == null)
            coVerify { mockRepo.getFlightData() }
        }


    private fun fetchFlight(): Flight {
        return Flight(
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
    }

    private fun TestScope.collectViewModel() {
        viewModel = FlightViewModel(mockRepo, mockDownloader)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
    }

    private fun List<Flight>.resetFile(): List<Flight> {
        return map { it.copy(localFile = null) }
    }
}
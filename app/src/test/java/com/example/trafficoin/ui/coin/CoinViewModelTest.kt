package com.example.trafficoin.ui.coin

import com.example.trafficoin.MainDispatcherRule
import com.example.trafficoin.data.coin.CoinRepository
import com.example.trafficoin.data.coin.ExchangeRate
import com.example.trafficoin.data.datastore.CoinPreferenceRepository
import com.example.trafficoin.data.datastore.CoinPreferences
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * @author Jack
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoinViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockRepo: CoinRepository = mockk()
    private val mockPrefsRepo: CoinPreferenceRepository = mockk()
    private lateinit var viewModel: CoinViewModel

    private val fakePrefsFlow = MutableStateFlow(CoinPreferences("USD", 100))

    @Before
    fun setup() {
        every { mockPrefsRepo.preferencesFlow } returns fakePrefsFlow
        coEvery { mockRepo.getRates() } returns fetchRates()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `init - should fetch data and update state`() = runTest {
        collectViewModel()
        runCurrent()

        val state = viewModel.uiState.value
        val item = state.data.find { it.code == "JPY" }
        assert(item?.amount == "15664.6")
        assert(state.baseCurrency == "USD")
    }

    @Test
    fun `auto refresh - should fetch data every 10 seconds`() = runTest {
        collectViewModel()
        runCurrent()    // 第 1 次

        advanceTimeBy(10000)
        runCurrent()    // 第 2 次

        advanceTimeBy(10000)
        runCurrent()    // 第 3 次
        coVerify(exactly = 3) { mockRepo.getRates() }
    }

    @Test
    fun `error handling - should show error and empty rates`() = runTest {
        val errorReason = "API Error"
        coEvery { mockRepo.getRates() } throws Exception(errorReason)

        collectViewModel()
        runCurrent()

        val state = viewModel.uiState.value
        assert(state.data.isEmpty())
        assert(state.errorMsg == errorReason)
        assert(state.isInitialError)
    }

    @Test
    fun `refresh error with existing data`() = runTest {
        collectViewModel()
        runCurrent()
        val firstData = viewModel.uiState.value.data
        val firstUpdateTime = viewModel.uiState.value.lastUpdate

        val errorReason = "500 Internal Server Error"
        coEvery { mockRepo.getRates() } throws Exception(errorReason)

        advanceTimeBy(10001)
        runCurrent()

        val state = viewModel.uiState.value
        assert(state.data == firstData)
        assert(state.lastUpdate == firstUpdateTime)
        assert(state.errorMsg == errorReason)
        assert(!state.isInitialError)
    }

    @Test
    fun manualRefresh() = runTest {
        collectViewModel()
        runCurrent()
        coVerify(exactly = 1) { mockRepo.getRates() }

        advanceTimeBy(5000)
        runCurrent()

        viewModel.manualRefresh()
        runCurrent()
        coVerify(exactly = 2) { mockRepo.getRates() }

        advanceTimeBy(6000)
        runCurrent()
        coVerify(exactly = 2) { mockRepo.getRates() }

        advanceTimeBy(4000)
        runCurrent()
        coVerify(exactly = 3) { mockRepo.getRates() }
    }

    @Test
    fun `onInputChanged - should call save and reflect in UI`() = runTest {
        coEvery { mockPrefsRepo.savUserInput(any(), any()) } just Runs

        collectViewModel()
        viewModel.onInputChanged("JPY", "3133")
        coVerify { mockPrefsRepo.savUserInput("JPY", 3133) }

        fakePrefsFlow.value = CoinPreferences("JPY", 3133)
        runCurrent()

        val usdItem = viewModel.uiState.value.data.find { it.code == "USD" }
        assert(usdItem?.amount == "20.001")
        assert(viewModel.uiState.value.baseCurrency == "JPY")
    }

    @Test
    fun `calculate - when fromCurrency is missing`() = runTest {
        fakePrefsFlow.value = CoinPreferences("GBP", 100)

        collectViewModel()
        runCurrent()

        val data = viewModel.uiState.value.data
        val jpyItem = data.find { it.code == "JPY" }
        assert(jpyItem?.amount == "156.646")

        val usdItem = data.find { it.code == "USD" }
        assert(usdItem?.amount == "1")
    }

    @Test
    fun `onReset - should clear preferences and restore defaults`() = runTest {
        coEvery { mockPrefsRepo.reset() } just Runs

        collectViewModel()
        viewModel.onReset()
        coVerify { mockPrefsRepo.reset() }

        fakePrefsFlow.value = CoinPreferences("USD", 1)
        runCurrent()

        assert(viewModel.uiState.value.baseCurrency == "USD")
    }


    private fun TestScope.collectViewModel() {
        viewModel = CoinViewModel(mockRepo, mockPrefsRepo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
    }

    private fun fetchRates(): List<ExchangeRate> {
        return listOf(
            ExchangeRate("EUR",0.850),
            ExchangeRate("USD",1.0),
            ExchangeRate("JPY",156.646),
        )
    }
}
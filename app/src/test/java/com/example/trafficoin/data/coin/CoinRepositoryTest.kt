package com.example.trafficoin.data.coin

import com.example.trafficoin.network.coin.CoinApi
import com.example.trafficoin.network.coin.CoinDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * @author Jack
 */
class CoinRepositoryTest {
    private val mockApi: CoinApi = mockk()
    private lateinit var repository: CoinRepository

    @Before
    fun setup() {
        repository = CoinRepositoryImp(mockApi)
    }

    @Test
    fun `getRates - when key exists and api success`() = runTest {
        System.setProperty("COIN_API_KEY", "test_actual_key")
        val apiResponse = CoinDto(
            mapOf(
                "EUR" to 0.8504161521,
                "JPY" to 156.6460267996,
                "USD" to 1.0,
            )
        )

        coEvery { mockApi.queryExchangeRates("test_actual_key") } returns apiResponse

        val result = repository.getRates()
        assert(result.size == 3)
        assert(result.any { it.currency == "EUR" && it.rate == 0.8504161521 })
        assert(result.any { it.currency == "JPY" && it.rate == 156.6460267996 })
    }

    @Test(expected = Exception::class)
    fun `getRates - when has exception`() = runTest {
        coEvery { mockApi.queryExchangeRates(any()) } throws IOException("Network Error")

        repository.getRates()
    }

}
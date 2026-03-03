package com.example.trafficoin.network

import android.util.Log
import com.example.trafficoin.MainDispatcherRule
import com.example.trafficoin.di.AppModule
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.*

/**
 * @author Jack
 */
class ApiTest {
    // 處理 Coroutines (必備)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var httpClient: OkHttpClient

    @Before
    fun setup() {
        // mock android.util.Log 後 OkHttpClient 才能正常運作
        mockkStatic(Log::class)
        every { Log.isLoggable(any<String>(), any<Int>()) } returns true
        every { Log.println(any<Int>(), any<String>(), any<String>()) } returns 0

        httpClient = AppModule.provideOkHttpClient()
    }

    @Test
    fun testRealFlightApi() = runTest {
        val builder = AppModule.provideRetrofitBuilder(httpClient)
        val api = AppModule.provideFlightApi(builder)

        val flights = api.queryFlights()
        println("response: [\n\t${flights.joinToString(separator = ",\n\t")}\n]")
        assert(flights.isNotEmpty())
    }

    @Test
    fun testRealCoinApi() = runTest {
        val builder = AppModule.provideRetrofitBuilder(httpClient)
        val api = AppModule.provideCoinApi(builder)

        val key = System.getProperty("COIN_API_KEY") ?: ""
        val response = api.queryExchangeRates(key)
        val data = response.data
        println("data: $data")
        assert(data.isNotEmpty())
    }
}
package com.example.trafficoin.data.coin

import com.example.trafficoin.BuildConfig
import com.example.trafficoin.network.coin.CoinApi

data class ExchangeRate(val currency: String, val rate: Double)

/**
 * @author Jack
 */
interface CoinRepository {
    @Throws(Exception::class)
    suspend fun getRates(): List<ExchangeRate>
}

class CoinRepositoryImp(private val api: CoinApi) : CoinRepository {
    override suspend fun getRates(): List<ExchangeRate> {
        val key = BuildConfig.COIN_API_KEY
        return api.queryExchangeRates(key).data.map {
            ExchangeRate(it.key, it.value)
        }
    }
}
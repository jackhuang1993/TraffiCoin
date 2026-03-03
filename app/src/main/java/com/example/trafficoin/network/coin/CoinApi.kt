package com.example.trafficoin.network.coin

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Jack
 */
interface CoinApi {
    @GET("/v1/latest")
    suspend fun queryExchangeRates(
        @Query("apikey") key: String
    ): CoinDto
}
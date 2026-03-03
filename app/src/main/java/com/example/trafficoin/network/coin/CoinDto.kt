package com.example.trafficoin.network.coin

import kotlinx.serialization.Serializable

/**
 * @author Jack
 */
@Serializable
data class CoinDto(val data: Map<String, Double>)
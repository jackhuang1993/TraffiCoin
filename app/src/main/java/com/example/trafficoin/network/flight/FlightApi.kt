package com.example.trafficoin.network.flight

import retrofit2.http.GET

/**
 * @author Jack
 */
interface FlightApi {
    //    https://www.kia.gov.tw/API/InstantSchedule.ashx?AirFlyLine=2&AirFlyIO=2
    @GET("/Announce/NewsArea/InstantSchedule_DOMARR.json")
    suspend fun queryFlights(): List<FlightDto>
}
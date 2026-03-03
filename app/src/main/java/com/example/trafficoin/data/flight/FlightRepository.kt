package com.example.trafficoin.data.flight

import android.os.Parcelable
import com.example.trafficoin.network.flight.FlightApi
import com.example.trafficoin.network.flight.FlightDto
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class Flight(
    val airline: String,        // 航空公司代碼
    val airName: String,        // 航空公司名稱
    val flightNo: String,       // 航班編號
    val aircraftType: String,   // 機型
    val logoUrl: String,        // Logo 圖片連結
    val airportCode: String,    // 起飛機場代碼
    val airportName: String?,   // 起飛機場名稱
    val scheduledTime: String,  // 預計時間
    val actualTime: String,     // 實際時間
    val status: String,         // 狀態
    val gate: String?,          // 登機門
    val delayReason: String?,   // 延誤原因
    val localFile: File? = null,// logo 檔案
) : Parcelable {
    val isDelay: Boolean
        get() = status.isBlank()
}

/**
 * @author Jack
 */
interface FlightRepository {
    @Throws(Exception::class)
    suspend fun getFlightData(): List<Flight>
}

class FlightRepositoryImp(private val api: FlightApi) : FlightRepository {
    override suspend fun getFlightData(): List<Flight> {
        return api.queryFlights().map { it.toFlight() }
    }

    private fun FlightDto.toFlight(): Flight {
        return Flight(
            airLineCode,
            airLineName ?: "",
             airLineNum,
            airPlaneType ?: "",
            airLineLogo,
            originAirportCode,
            originAirportName,
            expectTime,
            realTime,
            flyStatus,
            boardingGate,
            delayCause
        )
    }
}
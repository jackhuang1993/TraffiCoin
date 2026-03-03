package com.example.trafficoin.network.flight

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Jack
 */
@Serializable
data class FlightDto(
    @SerialName("expectTime")
    val expectTime: String, // 預計時間

    @SerialName("realTime")
    val realTime: String,   // 實際時間

    @SerialName("airLineName")
    val airLineName: String?,   // 航空公司名稱

    @SerialName("airLineCode")
    val airLineCode: String,    // 航空公司代碼

    @SerialName("airLineLogo")
    val airLineLogo: String,    // Logo 圖片連結

    @SerialName("airLineUrl")
    val airLineUrl: String?,    // 航空公司官網

    @SerialName("airLineNum")
    val airLineNum: String,     // 航班編號

    @SerialName("upAirportCode")
    val originAirportCode: String,  // 起飛機場代碼

    @SerialName("upAirportName")
    val originAirportName: String?, // 起飛機場名稱

    @SerialName("airPlaneType")
    val airPlaneType: String?, // 機型

    @SerialName("airBoardingGate")
    val boardingGate: String?, // 登機門

    @SerialName("airFlyStatus")
    val flyStatus: String ,   // 飛行狀態

    @SerialName("airFlyDelayCause")
    val delayCause: String?    // 延誤原因
)
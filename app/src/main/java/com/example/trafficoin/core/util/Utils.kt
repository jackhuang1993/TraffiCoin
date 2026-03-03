package com.example.trafficoin.core.util

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Jack
 */
object Utils {
    fun getCurrentDate(): String {
        return ZonedDateTime.now(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }
}
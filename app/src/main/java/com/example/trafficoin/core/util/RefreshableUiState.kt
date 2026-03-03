package com.example.trafficoin.core.util

import retrofit2.HttpException
import java.io.IOException


/**
 * @author Jack
 */
interface RefreshableUiState<T> {
    val data: List<T>
    val lastUpdate: String
    val isLoading: Boolean
    val errorMsg: String?

    val isInitialError: Boolean get() = data.isEmpty() && errorMsg != null
}

fun Throwable.toUiStateErrorMessage(): String {
    return when (this) {
        is HttpException -> "伺服器連線異常 (${code()})"
        is IOException -> "請檢查網路連線"
        else -> "發生未知錯誤"
    }
}
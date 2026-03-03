package com.example.trafficoin.core.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

/**
 * @author Jack
 */
class AutoRefreshTicker(private val interval: Long = 10000L) {
    private val _manualTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> flowWithTicker(fetcher: suspend () -> T): Flow<T> {
        return _manualTrigger.flatMapLatest {
            flow {
                while (true) {
                    emit(fetcher())
                    delay(interval)
                }
            }
        }
    }

    fun trigger() {
        _manualTrigger.tryEmit(Unit)
    }
}
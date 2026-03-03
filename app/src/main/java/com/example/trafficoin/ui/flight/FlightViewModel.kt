package com.example.trafficoin.ui.flight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trafficoin.core.util.AutoRefreshTicker
import com.example.trafficoin.core.util.RefreshableUiState
import com.example.trafficoin.core.util.Utils
import com.example.trafficoin.core.util.toUiStateErrorMessage
import com.example.trafficoin.data.flight.Flight
import com.example.trafficoin.data.flight.FlightRepository
import com.example.trafficoin.manager.media.ImageDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import javax.inject.Inject


data class FlightUiState(
    override val data: List<Flight> = emptyList(),
    override val lastUpdate: String = "--:--:--",
    override val isLoading: Boolean = false,
    override val errorMsg: String? = null
) : RefreshableUiState<Flight>

/**
 * @author Jack
 */
@HiltViewModel
class FlightViewModel @Inject constructor(
    private val flightRepo: FlightRepository,
    private val imageDownloader: ImageDownloader
) : ViewModel() {
    private var lastFlights = emptyList<Flight>()
    private var lastTime = "--:--:--"

    private val ticker = AutoRefreshTicker()
    private val downloadSemaphore = Semaphore(3)
    private val downloadedImageMap = MutableStateFlow<Map<String, File>>(emptyMap())

    private val apiDataFlow = ticker.flowWithTicker { fetchFromApi() }

    val uiState: StateFlow<FlightUiState> =
        combine(apiDataFlow, downloadedImageMap) { state, imageMap ->
            val patchedFlights = state.data.map { flight ->
                imageMap[flight.flightNo]?.let { flight.copy(localFile = it) } ?: flight
            }
            state.copy(data = patchedFlights)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FlightUiState(isLoading = true)
        )


    fun manualRefresh() {
        ticker.trigger()
    }

    private suspend fun fetchFromApi(): FlightUiState {
        return try {
            val data = flightRepo.getFlightData()
            downloadImages(data)
            lastFlights = data
            lastTime = Utils.getCurrentDate()
            FlightUiState(data, lastTime, isLoading = false)
        } catch (e: Exception) {
            FlightUiState(lastFlights, lastTime, errorMsg = e.toUiStateErrorMessage())
        }
    }

    private fun downloadImages(flights: List<Flight>) {
        viewModelScope.launch(Dispatchers.IO) {
            flights.forEach { flight ->
                if (downloadedImageMap.value.containsKey(flight.flightNo)) return@forEach

                launch {
                    downloadSemaphore.withPermit {
                        val file = imageDownloader.download(flight.logoUrl)
                        if (file != null)
                            downloadedImageMap.update { it + (flight.flightNo to file) }
                    }
                }
            }
        }
    }
}
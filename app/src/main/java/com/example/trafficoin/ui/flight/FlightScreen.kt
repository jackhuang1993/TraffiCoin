package com.example.trafficoin.ui.flight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.trafficoin.data.flight.Flight
import com.example.trafficoin.ui.common.HeaderSection

@Composable
fun FlightRoot(viewModel: FlightViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    FlightScreen(
        uiState = uiState,
        onRefresh = viewModel::manualRefresh
    )
}

/**
 * @author Jack
 */
@Composable
fun FlightScreen(
    uiState: FlightUiState,
    onRefresh: () -> Unit
) {
    var selectedFlight by rememberSaveable { mutableStateOf<Flight?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HeaderSection(uiState, onRefresh)

        Box(modifier = Modifier.weight(1f)) {
            if (uiState.data.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("flight_list")
                ) {
                    items(uiState.data, key = { it.flightNo }) { flight ->
                        FlightListItem(flight, onClick = { selectedFlight = flight })
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                }
            } else if (uiState.isInitialError) {
                Text(
                    text = "資料更新失敗",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            } else if (uiState.isLoading) {
                CircularProgressIndicator(
                    Modifier
                        .align(Alignment.Center)
                        .testTag("loading_indicator")
                )
            }
        }
    }

    selectedFlight?.let { flight ->
        FlightDetailDialog(flight = flight, onDismiss = { selectedFlight = null })
    }
}

@Preview(showBackground = true, name = "Success")
@Composable
fun PreviewFlightScreenSuccess() {
    MaterialTheme {
        FlightScreen(
            uiState = FlightUiState(
                data = MockFlightData.getPreviewFlights(),
                lastUpdate = "2026/03/01 16:00:00"
            ),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true, name = "Error Empty")
@Composable
fun PreviewFlightScreenErrorEmpty() {
    MaterialTheme {
        FlightScreen(
            uiState = FlightUiState(listOf(), errorMsg = "Error"),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
fun PreviewFlightScreenError() {
    MaterialTheme {
        FlightScreen(
            uiState = FlightUiState(
                data = MockFlightData.getPreviewFlights(),
                lastUpdate = "2026/03/01 16:00:00", errorMsg = "Error"
            ),
            onRefresh = {}
        )
    }
}

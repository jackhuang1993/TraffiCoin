package com.example.trafficoin.ui.flight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trafficoin.data.flight.Flight

/**
 * @author Jack
 */
@Composable
fun FlightDetailDialog(flight: Flight, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { LocalLogo(file = flight.localFile) },
        title = {
            Text(
                "${flight.airName}(${flight.airline}) ${flight.flightNo}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailRow("機型", flight.aircraftType)
                DetailRow("起飛機場", "${flight.airportName} (${flight.airportCode})")
                DetailRow("預計時間", flight.scheduledTime)
                DetailRow("實際時間", flight.actualTime)
                DetailRow("登機門", flight.gate ?: "")
                if (!flight.delayReason.isNullOrBlank()) {
                    DetailRow("延誤原因", flight.delayReason, isError = true)
                }
            }
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.testTag("dialog_close_button"),
                onClick = onDismiss
            ) { Text("關閉") }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String, isError: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label, color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            value,
            color = if (isError) Color.Red else Color.Unspecified,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true, name = "Normal")
@Composable
private fun PreviewFlightDetailDialogNormal() {
    MaterialTheme {
        FlightDetailDialog(
            flight = MockFlightData.getPreviewFlight(),
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "Delay")
@Composable
private fun PreviewFlightDetailDialogDelay() {
    MaterialTheme {
        FlightDetailDialog(
            flight = MockFlightData.getPreviewFlight().copy(
                status = "",
                actualTime = "106:30",
                delayReason = "風速過大"
            ),
            onDismiss = {}
        )
    }
}
package com.example.trafficoin.ui.flight

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trafficoin.data.flight.Flight

@Composable
fun FlightListItem(flight: Flight, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("flight_item_${flight.flightNo}")
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LocalLogo(file = flight.localFile, modifier = Modifier.size(40.dp))

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${flight.airline} - ${flight.flightNo}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "預計時間：${flight.scheduledTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = flight.actualTime,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = flight.takeUnless { it.isDelay }?.status ?: "延誤",
                    color = if (flight.isDelay) Color.Red else Color(0xFF2E7D32),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Normal")
@Composable
private fun PreviewFlightListItemNormal() {
    MaterialTheme {
        FlightListItem(
            flight = MockFlightData.getPreviewFlight(),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Delay")
@Composable
fun PreviewFlightListItemDelay() {
    MaterialTheme {
        FlightListItem(
            flight = MockFlightData.getPreviewFlight().copy(
                status = "",
                actualTime = "106:30"
            ),
            onClick = {}
        )
    }
}
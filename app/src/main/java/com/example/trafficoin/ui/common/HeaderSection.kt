package com.example.trafficoin.ui.common

import android.app.Activity
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trafficoin.R
import com.example.trafficoin.core.util.RefreshableUiState
import com.example.trafficoin.ui.flight.FlightUiState

@Composable
fun <S, T> HeaderSection(uiState: S, onRefresh: () -> Unit) where S : RefreshableUiState<T> {
    val context = LocalContext.current
    val exitInteractionSource = remember { MutableInteractionSource() }
    val isExitPressed by exitInteractionSource.collectIsPressedAsState()
    val refreshInteractionSource = remember { MutableInteractionSource() }
    val isRefreshPressed by refreshInteractionSource.collectIsPressedAsState()

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.graphicsLayer {
                        alpha = if (isExitPressed) 0.5f else 1f
                    },
                    interactionSource = exitInteractionSource,
                    onClick = { (context as? Activity)?.finish() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_left),
                        contentDescription = "Exit App"
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    val updateTime = uiState.lastUpdate
                    Text(
                        "刷新時間",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        updateTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    modifier = Modifier
                        .testTag("refresh_button")
                        .graphicsLayer {
                            alpha = if (isRefreshPressed) 0.5f else 1f
                        },
                    interactionSource = refreshInteractionSource,
                    onClick = onRefresh
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_refresh),
                        contentDescription = "Refresh",
                    )
                }
            }

            if (uiState.errorMsg != null) {
                Text(
                    text = "更新失敗，${uiState.errorMsg}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .testTag("error_message")
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "Success")
@Composable
fun PreviewHeaderSectionSuccess() {
    MaterialTheme {
        HeaderSection(
            uiState = FlightUiState(
                data = listOf(),
                lastUpdate = "2026/03/01 16:00:00"
            ),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
fun PreviewHeaderSectionError() {
    MaterialTheme {
        HeaderSection(
            uiState = FlightUiState(data = listOf(), errorMsg = "發生未知錯誤"),
            onRefresh = {}
        )
    }
}
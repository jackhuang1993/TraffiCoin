package com.example.trafficoin.ui.coin

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
import com.example.trafficoin.ui.common.HeaderSection

@Composable
fun CoinRoot(viewModel: CoinViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    CoinScreen(
        uiState = uiState,
        onReset = viewModel::onReset,
        onRefresh = viewModel::manualRefresh,
        onInputConfirm = { amount, currency ->
            viewModel.onInputChanged(currency, amount)
        }
    )
}

/**
 * @author Jack
 */
@Composable
fun CoinScreen(
    uiState: CoinUiState,
    onRefresh: () -> Unit,
    onReset: () -> Unit,
    onInputConfirm: (String, String) -> Unit
) {
    var editingItem by rememberSaveable { mutableStateOf<ExchangeItem?>(null) }

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
                    items(uiState.data, key = { it.code }) { item ->
                        CoinListItem(
                            item,
                            isSelected = item.code == editingItem?.code,
                            isHighlighted = item.code == uiState.baseCurrency,
                            onClick = { editingItem = item },
                            onLongClick = onReset
                        )
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

    editingItem?.let { item ->
        ExchangeInputDialog(
            currencyCode = item.code,
            initialValue = if (item.code == uiState.baseCurrency) item.amount else "",
            onConfirm = { amount ->
                onInputConfirm(amount, item.code)
                editingItem = null
            },
            onDismiss = { editingItem = null }
        )
    }
}


@Preview(showBackground = true, name = "Success")
@Composable
fun PreviewCoinScreenSuccess() {
    MaterialTheme {
        CoinScreen(
            uiState = CoinUiState(
                data = MockCoinData.getPreviewItems(),
                lastUpdate = "2026/03/01 16:00:00"
            ),
            onReset = {},
            onRefresh = {},
            onInputConfirm = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Error Empty")
@Composable
fun PreviewCoinScreenErrorEmpty() {
    MaterialTheme {
        CoinScreen(
            uiState = CoinUiState(listOf(), errorMsg = "Error"),
            onReset = {},
            onRefresh = {},
            onInputConfirm = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
fun PreviewCoinScreenError() {
    MaterialTheme {
        CoinScreen(
            uiState = CoinUiState(
                data = MockCoinData.getPreviewItems(),
                lastUpdate = "2026/03/01 16:00:00", errorMsg = "Error"
            ),
            onReset = {},
            onRefresh = {},
            onInputConfirm = { _, _ -> }
        )
    }
}
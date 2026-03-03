package com.example.trafficoin.ui.coin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @author Jack
 */
@Composable
fun CoinListItem(
    item: ExchangeItem,
    isSelected: Boolean,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current // 觸覺回饋提升手感

    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        isHighlighted -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when {
        isHighlighted -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("coin_item_${item.code}")
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    if (isHighlighted) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                }
            ),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = borderColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.code,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.tertiary
                    isHighlighted -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Text(
                text = item.amount,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (isSelected || isHighlighted) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true, name = "Normal")
@Composable
private fun PreviewCoinListItem() {
    MaterialTheme {
        CoinListItem(
            item = ExchangeItem("JPY", "156.646"),
            isSelected = false,
            isHighlighted = false,
            onClick = {},
            onLongClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Selected")
@Composable
private fun PreviewCoinListItemSelected() {
    MaterialTheme {
        CoinListItem(
            item = ExchangeItem("USD", "1"),
            isSelected = true,
            isHighlighted = false,
            onClick = {},
            onLongClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Highlighted")
@Composable
private fun PreviewCoinListItemHighlighted() {
    MaterialTheme {
        CoinListItem(
            item = ExchangeItem("USD", "1"),
            isSelected = false,
            isHighlighted = true,
            onClick = {},
            onLongClick = {}
        )
    }
}
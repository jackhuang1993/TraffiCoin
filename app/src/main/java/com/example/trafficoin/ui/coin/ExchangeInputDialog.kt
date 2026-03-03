package com.example.trafficoin.ui.coin

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * @author Jack
 */
@Composable
fun ExchangeInputDialog(
    currencyCode: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

//    var textState by remember { mutableStateOf(initialValue) }
    var textState by rememberSaveable { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("輸入 $currencyCode 金額") },
        confirmButton = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false     // 解除寬度限制
        ),
        text = {
            if (isLandscape) {  //橫向模式
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        DisplaySection(textState, 10.dp)
                        ActionButtons { onConfirm(textState.ifEmpty { "0" }) }
                    }

                    Column(modifier = Modifier.weight(1.2f)) {
                        NumpadGrid(
                            currentValue = textState, isLandscape = true,
                            onValueChange = { value -> textState = value }
                        )
                    }
                }
            } else {    // 縱向模式
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DisplaySection(textState, 16.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        NumpadGrid(
                            currentValue = textState,
                            onValueChange = { value -> textState = value }
                        )
                        ActionButtons { onConfirm(textState.ifEmpty { "0" }) }
                    }
                }
            }
        }
    )
}

@Composable
fun DisplaySection(amount: String, padding: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .padding(padding),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = amount.ifEmpty { "0" },
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("input_amount")
        )
    }
}

@Composable
fun NumpadGrid(
    currentValue: String,
    isLandscape: Boolean = false,
    onValueChange: (String) -> Unit
) {
    var value by remember { mutableStateOf(currentValue) }

    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "⌫")
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        buttons.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { label ->
                    CalcButton(
                        text = label,
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isLandscape) 44.dp else Dp.Unspecified)
                            .testTag("btn_$label"),
                        onClick = {
                            when (label) {
                                "C" -> value = ""
                                "⌫" -> if (value.isNotEmpty()) value =
                                    value.dropLast(1)

                                else -> {
                                    if (value.length < 9) {
                                        if (value == "0") value = label
                                        else value += label
                                    }
                                }
                            }
                            onValueChange(value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CalcButton(text: String, modifier: Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed)
        MaterialTheme.colorScheme.secondaryContainer
    else
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)

    TextButton(
        onClick = onClick,
        modifier = modifier
            .padding(6.dp)
            .aspectRatio(1.5f),
        shape = RoundedCornerShape(8.dp),
        interactionSource = interactionSource,
        colors = ButtonDefaults.textButtonColors(
            containerColor = backgroundColor,
        )
    ) {
        Text(text, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun ActionButtons(onConfirm: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onConfirm,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .testTag("btn_confirm")
            .graphicsLayer { this.alpha = if (isPressed) 0.8f else 1f },
        shape = RoundedCornerShape(8.dp),
        interactionSource = interactionSource
    ) {
        Text("開始轉換", style = MaterialTheme.typography.titleMedium)
    }
}


@Preview(showBackground = true, name = "Normal")
@Composable
private fun PreviewExchangeInputDialog() {
    MaterialTheme {
        ExchangeInputDialog(
            currencyCode = "USD",
            initialValue = "",
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(
    showBackground = true,
    name = "Value",
    device = "spec:width=411dp,height=891dp,orientation=landscape"
)
@Composable
private fun PreviewExchangeInputDialogValue() {
    MaterialTheme {
        ExchangeInputDialog(
            currencyCode = "JYP",
            initialValue = "123456789",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
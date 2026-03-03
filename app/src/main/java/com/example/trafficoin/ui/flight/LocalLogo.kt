package com.example.trafficoin.ui.flight

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.trafficoin.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun LocalLogo(file: File?, modifier: Modifier = Modifier) {
    val bitmapState = produceState<ImageBitmap?>(initialValue = null, key1 = file) {
        value = if (file != null && file.exists()) {
            withContext(Dispatchers.IO) {
                runCatching {
                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                }.getOrNull()
            }
        } else null
    }

    if (bitmapState.value != null) {
        Image(
            bitmap = bitmapState.value!!,
            contentDescription = "airline_logo_container",
            modifier = modifier.clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = modifier.background(Color.LightGray, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.airplane_ticket),
                contentDescription = "default_logo"
            )
        }
    }
}
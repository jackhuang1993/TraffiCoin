package com.example.trafficoin.ui.flight

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

/**
 * @author Jack
 */
class LocalLogoTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsDefaultTicket() {
        composeTestRule.setContent {
            LocalLogo(file = null)
        }

        composeTestRule.onNodeWithContentDescription("default_logo").assertIsDisplayed()
    }

    @Test
    fun localLogo_whenFileExists_showsActualLogo() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val tempFile = File(context.cacheDir, "test_logo.png")

        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.BLUE)
        FileOutputStream(tempFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        composeTestRule.setContent {
            LocalLogo(file = tempFile)
        }

        composeTestRule.onNodeWithContentDescription("airline_logo_container").assertExists()

        tempFile.delete()
    }
}
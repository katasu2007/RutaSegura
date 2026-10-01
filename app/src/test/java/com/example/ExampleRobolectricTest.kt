package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.RouteReport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Ruta Segura", appName)
    }

    @Test
    fun `route report model holds safe and risk attributes properly`() {
        val safePoint = RouteReport(
            id = 1,
            placeName = "Acceso INDEL",
            isSafe = true,
            recommendedHours = "07:00 a 20:00",
            notes = "Buena iluminación",
            xRatio = 0.1f,
            yRatio = -0.1f
        )
        assertTrue(safePoint.isSafe)
        assertEquals("Acceso INDEL", safePoint.placeName)

        val riskPoint = RouteReport(
            id = 2,
            placeName = "Pasaje Oscuro",
            isSafe = false,
            recommendedHours = "Evitar noche",
            notes = "Sin luz",
            xRatio = -0.5f,
            yRatio = 0.5f
        )
        assertFalse(riskPoint.isSafe)
        assertEquals("Pasaje Oscuro", riskPoint.placeName)
    }
}

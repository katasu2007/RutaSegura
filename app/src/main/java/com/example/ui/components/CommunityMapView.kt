package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RouteReport
import com.example.ui.theme.LightGold
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RiskRed
import com.example.ui.theme.RiskRedLight
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenLight

/**
 * Mapa esquemático interactivo de los trayectos peatonales alrededor del Instituto INDEL.
 * Permite visualizar geográficamente las zonas seguras/iluminadas y los puntos de riesgo reportados.
 */
@Composable
fun CommunityMapView(
    reports: List<RouteReport>,
    selectedReport: RouteReport?,
    onSelectReport: (RouteReport?) -> Unit,
    onAddNewAtPosition: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Fondo nocturno / satelital urbano
            .testTag("community_map_view")
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val centerX = widthPx / 2f
        val centerY = heightPx / 2f
        val mapScale = (minOf(widthPx, heightPx) / 2.5f)

        // Detección de toques en el mapa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(reports) {
                    detectTapGestures { tapOffset ->
                        // Verificar si se tocó cerca de algún reporte
                        val touchTolerancePx = 40.dp.toPx()
                        val hitReport = reports.firstOrNull { report ->
                            val reportX = centerX + (report.xRatio * mapScale)
                            val reportY = centerY + (report.yRatio * mapScale)
                            val distance = kotlin.math.hypot(tapOffset.x - reportX, tapOffset.y - reportY)
                            distance <= touchTolerancePx
                        }

                        if (hitReport != null) {
                            onSelectReport(hitReport)
                        } else {
                            // Si se tocó en espacio vacío, deseleccionar
                            onSelectReport(null)
                        }
                    }
                }
        ) {
            // Dibujado del mapa esquemático urbano
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Cuadrícula y calles principales hacia INDEL
                val gridColor = Color(0xFF1E293B)
                val streetColor = Color(0xFF334155)
                val mainAvenueColor = Color(0xFF475569)

                // Círculos de radio peatonal desde INDEL (100m, 250m, 500m)
                val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                drawCircle(
                    color = Color(0xFF1E3A8A).copy(alpha = 0.35f),
                    radius = mapScale * 0.45f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = dashedEffect)
                )
                drawCircle(
                    color = Color(0xFF1E3A8A).copy(alpha = 0.25f),
                    radius = mapScale * 0.95f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = dashedEffect)
                )

                // Calles secundarias (grilla urbana)
                val step = mapScale * 0.35f
                for (i in -4..4) {
                    val y = centerY + i * step
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(widthPx, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                for (i in -4..4) {
                    val x = centerX + i * step
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, heightPx),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Avenidas peatonales principales
                // Av. Belgrano (Diagonal / Cruce principal)
                drawLine(
                    color = mainAvenueColor,
                    start = Offset(centerX - mapScale * 1.3f, centerY + mapScale * 0.9f),
                    end = Offset(centerX + mapScale * 1.3f, centerY - mapScale * 0.9f),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Calle de los Estudiantes (Vertical frente a INDEL)
                drawLine(
                    color = streetColor,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, heightPx),
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Bv. Talleres (Horizontal)
                drawLine(
                    color = streetColor,
                    start = Offset(0f, centerY),
                    end = Offset(widthPx, centerY),
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Halo del Instituto INDEL en el centro
                drawCircle(
                    color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                    radius = 28.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color(0xFF1E3A8A),
                    radius = 16.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = Offset(centerX, centerY)
                )

                // Dibujar pines de los reportes comunitarios
                reports.forEach { report ->
                    val isSelected = selectedReport?.id == report.id
                    val pinX = centerX + (report.xRatio * mapScale)
                    val pinY = centerY + (report.yRatio * mapScale)
                    val pinColor = if (report.isSafe) SafeGreen else RiskRed

                    // Halo exterior si está seleccionado
                    if (isSelected) {
                        drawCircle(
                            color = pinColor.copy(alpha = 0.4f),
                            radius = 24.dp.toPx(),
                            center = Offset(pinX, pinY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 17.dp.toPx(),
                            center = Offset(pinX, pinY),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    } else {
                        // Halo sutil constante
                        drawCircle(
                            color = pinColor.copy(alpha = 0.25f),
                            radius = 14.dp.toPx(),
                            center = Offset(pinX, pinY)
                        )
                    }

                    // Círculo base del pin
                    drawCircle(
                        color = pinColor,
                        radius = if (isSelected) 12.dp.toPx() else 9.dp.toPx(),
                        center = Offset(pinX, pinY)
                    )

                    // Centro brillante
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                        center = Offset(pinX, pinY)
                    )
                }
            }
        }

        // Etiqueta del Instituto INDEL en el centro
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "INSTITUTO INDEL",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Panel de Leyenda en la parte superior izquierda
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            color = Color(0xFF0F172A).copy(alpha = 0.9f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = "Plano Estudiantil INDEL",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SafeGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Zona segura / iluminada",
                        color = Color(0xFFCBD5E1),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(RiskRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Punto de riesgo",
                        color = Color(0xFFCBD5E1),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // Botón rápido para reportar en el centro o lugar libre
        Button(
            onClick = { onAddNewAtPosition(0.2f, 0.2f) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .testTag("map_quick_add_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = NavyLight
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AddLocationAlt,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reportar punto", fontSize = 12.sp)
        }

        // Tarjeta flotante inferior cuando se toca un reporte en el mapa
        AnimatedVisibility(
            visible = selectedReport != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            selectedReport?.let { report ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_report_detail_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (report.isSafe) SafeGreenLight else RiskRedLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (report.isSafe) Icons.Default.Lightbulb else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (report.isSafe) LightGold else RiskRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (report.isSafe) "ZONA SEGURA" else "PUNTO DE RIESGO",
                                    color = if (report.isSafe) SafeGreen else RiskRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Botón cerrar detalle
                            IconButton(
                                onClick = { onSelectReport(null) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = report.placeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "🕒 ${report.recommendedHours}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (report.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = report.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

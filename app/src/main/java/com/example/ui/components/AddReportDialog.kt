package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LightGold
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RiskRed
import com.example.ui.theme.RiskRedLight
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenLight

/**
 * Sector aproximado en relación al Instituto INDEL para ubicar el reporte en el mapa.
 */
enum class ZoneSector(val label: String, val xRatio: Float, val yRatio: Float) {
    ENTRANCE("Entrada INDEL", 0.05f, -0.08f),
    NORTH("Norte (Estación)", 0.35f, -0.55f),
    SOUTH("Sur (Parada Buses)", 0.40f, 0.45f),
    WEST("Oeste (Plaza)", -0.45f, 0.35f),
    EAST("Este (Talleres)", -0.42f, -0.42f)
}

/**
 * Diálogo / BottomSheet para registrar rápidamente una zona o tramo seguro o de riesgo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportSheet(
    onDismiss: () -> Unit,
    onSubmit: (
        placeName: String,
        isSafe: Boolean,
        recommendedHours: String,
        notes: String,
        xRatio: Float,
        yRatio: Float
    ) -> Unit,
    initialX: Float = 0f,
    initialY: Float = 0f,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isSafe by remember { mutableStateOf(true) }
    var placeName by remember { mutableStateOf("") }
    var recommendedHours by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedSector by remember { mutableStateOf(ZoneSector.ENTRANCE) }
    var showError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier.testTag("add_report_bottom_sheet"),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera del formulario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reportar zona o tramo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Comunidad Estudiantil INDEL",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_add_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Selector de Seguridad (🟢 Seguro / Iluminado vs 🔴 Peligro / Riesgo)
            Text(
                text = "1. Estado de seguridad *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Opción Seguro / Iluminado
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSafe) SafeGreenLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(
                            width = if (isSafe) 2.dp else 1.dp,
                            color = if (isSafe) SafeGreen else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { isSafe = true }
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                        .testTag("select_safe_option"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isSafe) SafeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Seguro / Iluminado",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSafe) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSafe) SafeGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Opción Peligro / Riesgo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isSafe) RiskRedLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(
                            width = if (!isSafe) 2.dp else 1.dp,
                            color = if (!isSafe) RiskRed else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { isSafe = false }
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                        .testTag("select_risk_option"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (!isSafe) RiskRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Peligro / Riesgo",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (!isSafe) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isSafe) RiskRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 2: Nombre del lugar o tramo
            Text(
                text = "2. Nombre del lugar o tramo *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = placeName,
                onValueChange = {
                    placeName = it
                    if (it.isNotBlank()) showError = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_place_name"),
                placeholder = { Text("Ej. Parada de colectivo, Av. Belgrano y Calle 3...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                isError = showError && placeName.isBlank(),
                shape = RoundedCornerShape(12.dp)
            )

            // Sugerencias rápidas para el lugar
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "Frente al INDEL",
                    "Parada Colectivo",
                    "Sendero Plaza",
                    "Cruce Vías",
                    "Pasaje Lateral"
                ).forEach { suggestion ->
                    SuggestionChip(
                        onClick = {
                            placeName = suggestion
                            showError = false
                        },
                        label = { Text(suggestion, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 3: Hora recomendada / Horario crítico
            Text(
                text = "3. Hora recomendada o franja crítica *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = recommendedHours,
                onValueChange = {
                    recommendedHours = it
                    if (it.isNotBlank()) showError = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_recommended_hours"),
                placeholder = {
                    Text(if (isSafe) "Ej. 07:00 a 20:00 hs, Todo el día" else "Ej. Evitar luego de las 19:00 hs")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                isError = showError && recommendedHours.isBlank(),
                shape = RoundedCornerShape(12.dp)
            )

            // Sugerencias rápidas para horario
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val hourSuggestions = if (isSafe) {
                    listOf("Todo el día", "07:00 a 19:00 hs", "06:30 a 22:30 hs", "Turno Mañana y Tarde")
                } else {
                    listOf("Evitar de noche (20:00+)", "Salida nocturna (22 hs)", "Todo horario sin luz", "Horas solitarias")
                }
                hourSuggestions.forEach { suggestion ->
                    SuggestionChip(
                        onClick = {
                            recommendedHours = suggestion
                            showError = false
                        },
                        label = { Text(suggestion, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 4: Sector respecto a INDEL (para ubicar en el mapa esquemático)
            Text(
                text = "4. Sector aproximado respecto a INDEL",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ZoneSector.values().forEach { sector ->
                    val isSelected = selectedSector == sector
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedSector = sector }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = sector.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 5: Notas u observaciones adicionales
            Text(
                text = "5. Observaciones adicionales (opcional)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_notes"),
                placeholder = {
                    Text(if (isSafe) "Ej. Buena iluminación LED, cámaras de seguridad, quiosco abierto..." else "Ej. Falta luminaria, tramo solitario, cruzar con precaución...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            // Mensaje de validación si faltan campos obligatorios
            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Por favor completá el nombre del lugar y el horario recomendado.",
                    color = RiskRed,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción (Guardar y Cancelar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cancel_report_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        if (placeName.isBlank() || recommendedHours.isBlank()) {
                            showError = true
                        } else {
                            val finalX = if (initialX != 0f) initialX else selectedSector.xRatio
                            val finalY = if (initialY != 0f) initialY else selectedSector.yRatio
                            onSubmit(placeName, isSafe, recommendedHours, notes, finalX, finalY)
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("submit_report_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSafe) SafeGreen else RiskRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (isSafe) Icons.Default.Shield else Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSafe) "Registrar Zona Segura" else "Reportar Riesgo",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.SecurityFilter
import com.example.ui.theme.RiskRed
import com.example.ui.theme.RiskRedLight
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenLight

/**
 * Barra superior de búsqueda y filtros de seguridad por nivel.
 * Permite filtrar por "Todos", "Solo zonas bien iluminadas" y "Puntos de riesgo".
 */
@Composable
fun FilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: SecurityFilter,
    onFilterSelected: (SecurityFilter) -> Unit,
    totalCount: Int,
    safeCount: Int,
    riskCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Campo de búsqueda de texto
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_field"),
            placeholder = {
                Text(
                    text = "Buscar por calle, tramo u horario...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Chips deslizables para filtrar por nivel de seguridad
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filtro: Todos
            FilterChip(
                selected = selectedFilter == SecurityFilter.ALL,
                onClick = { onFilterSelected(SecurityFilter.ALL) },
                label = {
                    Text("Todos ($totalCount)")
                },
                modifier = Modifier.testTag("filter_chip_all")
            )

            // Filtro: Solo zonas bien iluminadas / seguras
            FilterChip(
                selected = selectedFilter == SecurityFilter.SAFE_ONLY,
                onClick = { onFilterSelected(SecurityFilter.SAFE_ONLY) },
                label = {
                    Text("Solo iluminadas ($safeCount)")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (selectedFilter == SecurityFilter.SAFE_ONLY) SafeGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SafeGreenLight,
                    selectedLabelColor = SafeGreen
                ),
                modifier = Modifier.testTag("filter_chip_safe_only")
            )

            // Filtro: Puntos de riesgo
            FilterChip(
                selected = selectedFilter == SecurityFilter.RISK_ONLY,
                onClick = { onFilterSelected(SecurityFilter.RISK_ONLY) },
                label = {
                    Text("Puntos de riesgo ($riskCount)")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (selectedFilter == SecurityFilter.RISK_ONLY) RiskRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RiskRedLight,
                    selectedLabelColor = RiskRed
                ),
                modifier = Modifier.testTag("filter_chip_risk_only")
            )
        }
    }
}

package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RouteReport
import com.example.ui.components.AddReportSheet
import com.example.ui.components.CommunityMapView
import com.example.ui.components.FilterBar
import com.example.ui.components.ReportCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RiskRed
import com.example.ui.theme.SafeGreen

/**
 * Pantalla principal de la aplicación RUTA SEGURA para estudiantes del INDEL.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaSeguraApp(
    viewModel: RouteViewModel,
    modifier: Modifier = Modifier
) {
    val allReports by viewModel.allReports.collectAsState()
    val filteredReports by viewModel.filteredReports.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedReport by viewModel.selectedReport.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val safeCount by viewModel.safeCount.collectAsState()
    val riskCount by viewModel.riskCount.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Estado para confirmación de eliminación
    var reportToDelete by remember { mutableStateOf<RouteReport?>(null) }

    // Posición inicial en caso de reportar tocando en el mapa
    var addInitialX by remember { mutableStateOf(0f) }
    var addInitialY by remember { mutableStateOf(0f) }

    // Mostrar snackbar cuando haya un mensaje
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SafeGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "RUTA SEGURA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF3B82F6))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "INDEL",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Comunidad Estudiantil",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NavyDark
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    addInitialX = 0f
                    addInitialY = 0f
                    viewModel.setAddDialogVisible(true)
                },
                modifier = Modifier.testTag("fab_add_report"),
                containerColor = NavyPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = "Reportar zona",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Pestañas principales (Lista vs Mapa)
            TabRow(
                selectedTabIndex = if (activeTab == AppTab.LIST) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (activeTab == AppTab.LIST) 0 else 1]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = activeTab == AppTab.LIST,
                    onClick = { viewModel.onTabSelected(AppTab.LIST) },
                    modifier = Modifier.testTag("tab_list"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatListBulleted,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lista (${filteredReports.size})",
                                fontWeight = if (activeTab == AppTab.LIST) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )

                Tab(
                    selected = activeTab == AppTab.MAP,
                    onClick = { viewModel.onTabSelected(AppTab.MAP) },
                    modifier = Modifier.testTag("tab_map"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mapa INDEL",
                                fontWeight = if (activeTab == AppTab.MAP) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )
            }

            // Barra de Filtro y Búsqueda
            FilterBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChanged,
                selectedFilter = selectedFilter,
                onFilterSelected = viewModel::onFilterSelected,
                totalCount = allReports.size,
                safeCount = safeCount,
                riskCount = riskCount
            )

            // Contenido dinámico con animación suave entre Lista y Mapa
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TabContentTransition",
                modifier = Modifier.fillMaxSize()
            ) { targetTab ->
                when (targetTab) {
                    AppTab.LIST -> {
                        // Vista 2: Lista con los reportes de la comunidad
                        if (filteredReports.isEmpty()) {
                            // Estado vacío cuando no hay resultados para el filtro/búsqueda
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterListOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No se encontraron reportes",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (searchQuery.isNotEmpty() || selectedFilter != SecurityFilter.ALL) {
                                            "Probá cambiar el filtro o borrar el texto de búsqueda."
                                        } else {
                                            "Sé el primero en reportar un punto seguro o de riesgo en los trayectos al INDEL."
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    if (searchQuery.isNotEmpty() || selectedFilter != SecurityFilter.ALL) {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.onSearchQueryChanged("")
                                                viewModel.onFilterSelected(SecurityFilter.ALL)
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Restablecer filtros")
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("reports_lazy_column"),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = filteredReports,
                                    key = { it.id }
                                ) { report ->
                                    ReportCard(
                                        report = report,
                                        onViewOnMap = { selected ->
                                            viewModel.onReportSelected(selected)
                                            viewModel.onTabSelected(AppTab.MAP)
                                        },
                                        onDelete = { selected ->
                                            reportToDelete = selected
                                        }
                                    )
                                }
                            }
                        }
                    }

                    AppTab.MAP -> {
                        // Vista 2: Mapa sencillo con los reportes de la comunidad
                        CommunityMapView(
                            reports = filteredReports,
                            selectedReport = selectedReport,
                            onSelectReport = viewModel::onReportSelected,
                            onAddNewAtPosition = { x, y ->
                                addInitialX = x
                                addInitialY = y
                                viewModel.setAddDialogVisible(true)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    // Modal de Registro Rápido (Función 1)
    if (showAddDialog) {
        AddReportSheet(
            onDismiss = { viewModel.setAddDialogVisible(false) },
            onSubmit = { placeName, isSafe, hours, notes, xRatio, yRatio ->
                viewModel.createReport(
                    placeName = placeName,
                    isSafe = isSafe,
                    recommendedHours = hours,
                    notes = notes,
                    xRatio = xRatio,
                    yRatio = yRatio
                )
            },
            initialX = addInitialX,
            initialY = addInitialY
        )
    }

    // Diálogo de confirmación para eliminar reporte
    reportToDelete?.let { report ->
        AlertDialog(
            onDismissRequest = { reportToDelete = null },
            title = { Text("¿Eliminar reporte?") },
            text = { Text("Se eliminará '${report.placeName}' de los reportes comunitarios.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteReport(report)
                        reportToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RiskRed)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { reportToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

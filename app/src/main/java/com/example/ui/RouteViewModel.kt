package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.RouteRepository
import com.example.model.RouteReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Filtro de seguridad para la búsqueda de rutas.
 */
enum class SecurityFilter(val label: String) {
    ALL("Todos"),
    SAFE_ONLY("Solo zonas bien iluminadas"),
    RISK_ONLY("Puntos de riesgo")
}

/**
 * Pestañas principales de visualización: Lista de reportes o Mapa comunitario INDEL.
 */
enum class AppTab(val title: String) {
    LIST("Lista"),
    MAP("Mapa INDEL")
}

/**
 * ViewModel que controla la lógica de negocio, reportes, filtros y persistencia local de RUTA SEGURA.
 */
class RouteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RouteRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = RouteRepository(database.routeReportDao())
    }

    // Todos los reportes provenientes de la base de datos local
    val allReports: StateFlow<List<RouteReport>> = repository.allReports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Texto de búsqueda ingresado por el usuario
    val searchQuery = MutableStateFlow("")

    // Filtro de seguridad seleccionado
    val selectedFilter = MutableStateFlow(SecurityFilter.ALL)

    // Pestaña activa (Lista o Mapa)
    val activeTab = MutableStateFlow(AppTab.LIST)

    // Reporte seleccionado para vista previa o foco en el mapa
    val selectedReport = MutableStateFlow<RouteReport?>(null)

    // Control para mostrar u ocultar la ventana modal de registro rápido
    val showAddDialog = MutableStateFlow(false)

    // Mensaje de confirmación temporal (Snackbar)
    val snackbarMessage = MutableStateFlow<String?>(null)

    /**
     * Reportes filtrados en tiempo real por búsqueda y por nivel de seguridad.
     */
    val filteredReports: StateFlow<List<RouteReport>> = combine(
        allReports,
        searchQuery,
        selectedFilter
    ) { reports, query, filter ->
        val queryTrimmed = query.trim().lowercase()
        reports.filter { report ->
            // Filtro por texto
            val matchesQuery = queryTrimmed.isEmpty() ||
                    report.placeName.lowercase().contains(queryTrimmed) ||
                    report.recommendedHours.lowercase().contains(queryTrimmed) ||
                    report.notes.lowercase().contains(queryTrimmed)

            // Filtro por nivel de seguridad
            val matchesFilter = when (filter) {
                SecurityFilter.ALL -> true
                SecurityFilter.SAFE_ONLY -> report.isSafe
                SecurityFilter.RISK_ONLY -> !report.isSafe
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Cantidades para los badges de los filtros
     */
    val safeCount: StateFlow<Int> = allReports.combine(searchQuery) { reports, _ ->
        reports.count { it.isSafe }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val riskCount: StateFlow<Int> = allReports.combine(searchQuery) { reports, _ ->
        reports.count { !it.isSafe }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /**
     * Registra un nuevo reporte comunitario.
     */
    fun createReport(
        placeName: String,
        isSafe: Boolean,
        recommendedHours: String,
        notes: String,
        xRatio: Float = 0f,
        yRatio: Float = 0f
    ) {
        viewModelScope.launch {
            val newReport = RouteReport(
                placeName = placeName.trim(),
                isSafe = isSafe,
                recommendedHours = recommendedHours.trim(),
                notes = notes.trim(),
                xRatio = xRatio,
                yRatio = yRatio,
                timestamp = System.currentTimeMillis()
            )
            repository.addReport(newReport)
            showAddDialog.value = false
            snackbarMessage.value = if (isSafe) {
                "¡Zona segura registrada con éxito!"
            } else {
                "¡Alerta de riesgo reportada a la comunidad!"
            }
        }
    }

    /**
     * Elimina un reporte comunitario.
     */
    fun deleteReport(report: RouteReport) {
        viewModelScope.launch {
            repository.removeReport(report)
            if (selectedReport.value?.id == report.id) {
                selectedReport.value = null
            }
            snackbarMessage.value = "Reporte eliminado."
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onFilterSelected(filter: SecurityFilter) {
        selectedFilter.value = filter
    }

    fun onTabSelected(tab: AppTab) {
        activeTab.value = tab
    }

    fun onReportSelected(report: RouteReport?) {
        selectedReport.value = report
    }

    fun setAddDialogVisible(visible: Boolean) {
        showAddDialog.value = visible
    }

    fun clearSnackbarMessage() {
        snackbarMessage.value = null
    }
}

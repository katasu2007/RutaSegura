package com.example.data

import com.example.model.RouteReport
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que gestiona el acceso a los reportes de seguridad para las rutas del INDEL.
 */
class RouteRepository(private val dao: RouteReportDao) {

    /**
     * Flujo de todos los reportes comunitarios ordenados por fecha.
     */
    val allReports: Flow<List<RouteReport>> = dao.getAllReports()

    /**
     * Registra un nuevo reporte en la base de datos local.
     */
    suspend fun addReport(report: RouteReport): Long {
        return dao.insertReport(report)
    }

    /**
     * Elimina un reporte comunitario.
     */
    suspend fun removeReport(report: RouteReport) {
        dao.deleteReport(report)
    }

    /**
     * Verifica si la base de datos está vacía para resembrado si fuera necesario.
     */
    suspend fun hasReports(): Boolean {
        return dao.getReportCount() > 0
    }
}

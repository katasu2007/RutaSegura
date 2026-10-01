package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.RouteReport
import kotlinx.coroutines.flow.Flow

/**
 * Objeto de acceso a datos (DAO) para operaciones locales sobre los reportes de ruta.
 */
@Dao
interface RouteReportDao {

    /**
     * Obtiene el flujo reactivo de todos los reportes ordenados del más reciente al más antiguo.
     */
    @Query("SELECT * FROM route_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<RouteReport>>

    /**
     * Inserta un nuevo reporte o reemplaza en caso de conflicto de clave primaria.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: RouteReport): Long

    /**
     * Inserta una lista inicial de reportes (útil para la carga de datos iniciales comunitarios).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(reports: List<RouteReport>)

    /**
     * Elimina un reporte específico.
     */
    @Delete
    suspend fun deleteReport(report: RouteReport)

    /**
     * Obtiene la cantidad de reportes almacenados.
     */
    @Query("SELECT COUNT(*) FROM route_reports")
    suspend fun getReportCount(): Int
}

package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa el reporte comunitario de un tramo o punto en el camino al Instituto INDEL.
 *
 * @param id Identificador único autogenerado en la base de datos local.
 * @param placeName Nombre del lugar, calle o tramo (ej. "Av. Belgrano y Calle 14").
 * @param isSafe Indica si es una zona segura/iluminada (true) o si presenta peligro/riesgo (false).
 * @param recommendedHours Horario recomendado o franja crítica (ej. "07:00 a 19:30 hs", "Evitar de noche").
 * @param notes Observaciones, detalles sobre luminarias, presencia policial o motivos de alerta.
 * @param xRatio Coordenada horizontal relativa (-1.0f a 1.0f) respecto al Instituto INDEL para el mapa comunitario.
 * @param yRatio Coordenada vertical relativa (-1.0f a 1.0f) respecto al Instituto INDEL para el mapa comunitario.
 * @param timestamp Fecha y hora de creación en milisegundos.
 */
@Entity(tableName = "route_reports")
data class RouteReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val placeName: String,
    val isSafe: Boolean,
    val recommendedHours: String,
    val notes: String = "",
    val xRatio: Float = 0f,
    val yRatio: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

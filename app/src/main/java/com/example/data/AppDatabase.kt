package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.RouteReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos local Room de la aplicación RUTA SEGURA.
 * Funciona de manera 100% offline y local sin necesidad de servidor ni registro previo.
 */
@Database(entities = [RouteReport::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun routeReportDao(): RouteReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ruta_segura_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback para precargar reportes comunitarios de ejemplo alrededor del Instituto INDEL
     * cuando la base de datos se crea por primera vez.
     */
    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialReports(database.routeReportDao())
                }
            }
        }

        private suspend fun populateInitialReports(dao: RouteReportDao) {
            val initialReports = listOf(
                RouteReport(
                    placeName = "Acceso Principal INDEL (Av. Belgrano)",
                    isSafe = true,
                    recommendedHours = "06:30 a 22:30 hs",
                    notes = "Luminarias LED nuevas, cámaras de seguridad municipal y presencia de personal de recepción.",
                    xRatio = 0.05f,
                    yRatio = -0.1f,
                    timestamp = System.currentTimeMillis() - 3600000 * 2
                ),
                RouteReport(
                    placeName = "Parada de Colectivos (Líneas 10 y 24)",
                    isSafe = true,
                    recommendedHours = "07:00 a 20:30 hs",
                    notes = "Parada concurrida por estudiantes, quiosco con luz permanente y buen flujo de transeúntes.",
                    xRatio = 0.45f,
                    yRatio = 0.35f,
                    timestamp = System.currentTimeMillis() - 3600000 * 5
                ),
                RouteReport(
                    placeName = "Pasaje Los Talleres (Detrás de aulas técnicas)",
                    isSafe = false,
                    recommendedHours = "Evitar luego de las 18:30 hs",
                    notes = "Falta luminaria pública, vereda rota y tramo solitario al atardecer. Conviene rodear por Av. Belgrano.",
                    xRatio = -0.4f,
                    yRatio = -0.5f,
                    timestamp = System.currentTimeMillis() - 3600000 * 8
                ),
                RouteReport(
                    placeName = "Plaza de la Juventud (Sendero Iluminado)",
                    isSafe = true,
                    recommendedHours = "07:00 a 21:00 hs",
                    notes = "Farolas en funcionamiento en el sendero central y presencia de patrulla comunitaria.",
                    xRatio = -0.5f,
                    yRatio = 0.4f,
                    timestamp = System.currentTimeMillis() - 3600000 * 12
                ),
                RouteReport(
                    placeName = "Cruce Peatonal Vías del Tren (Calle 4)",
                    isSafe = false,
                    recommendedHours = "Peligro alto de 19:00 a 23:00 hs",
                    notes = "Escasa visibilidad nocturna, paso a nivel oscuro sin reflector. Cruzar siempre en grupo.",
                    xRatio = 0.6f,
                    yRatio = -0.6f,
                    timestamp = System.currentTimeMillis() - 3600000 * 18
                )
            )
            dao.insertAll(initialReports)
        }
    }
}

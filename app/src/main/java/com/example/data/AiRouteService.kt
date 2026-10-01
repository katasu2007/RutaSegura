package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.example.model.AiRouteRecommendation
import com.example.model.RouteReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Servicio del Asistente de Ruta IA con Google Gemini API y motor de respaldo local.
 */
class AiRouteService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val modelName = "gemini-2.5-flash"

    /**
     * Analiza el trayecto del estudiante considerando los reportes locales y el destino.
     */
    suspend fun analyzeRoute(
        destination: String,
        reports: List<RouteReport>
    ): AiRouteRecommendation = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // Si no hay API key o es placeholder, usar el fallback inteligente
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("AiRouteService", "API Key ausente o por defecto. Activando fallback local.")
            return@withContext getFallbackRecommendation(destination, reports)
        }

        try {
            val contextReports = reports.joinToString("\n") { r ->
                val estado = if (r.isSafe) "SEGURO/ILUMINADO" else "PELIGRO/RIESGO"
                "- [$estado] Lugar: ${r.placeName}. Horario: ${r.recommendedHours}. Notas: ${r.notes.ifBlank { "Sin notas" }}"
            }

            val prompt = """
Eres el Asistente de Seguridad Estudiantil del Instituto INDEL.
Analiza los siguientes reportes comunitarios de caminata de los estudiantes:
$contextReports

El estudiante se dirige caminando a: "$destination".

Genera una recomendación de caminata segura. Responde STRICTLY en formato JSON válido con la siguiente estructura y sin texto adicional:
{
  "nivel_seguridad": "Alto" | "Medio" | "Bajo",
  "ruta_sugerida": "Descripción breve y clara del trayecto recomendado",
  "advertencias": ["Punto de riesgo 1 a evitar", "Punto de riesgo 2 a evitar"],
  "consejo_estudiantil": "Un consejo práctico para la caminata escolar"
}
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                }
                put("generationConfig", genConfig)
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("AiRouteService", "Gemini HTTP error: ${response.code}. Activando fallback.")
                return@withContext getFallbackRecommendation(destination, reports)
            }

            val responseBody = response.body?.string() ?: ""
            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            parseAiJson(text)
        } catch (e: Exception) {
            Log.e("AiRouteService", "Excepción al consultar Gemini API", e)
            getFallbackRecommendation(destination, reports)
        }
    }

    /**
     * Parsea el JSON devuelto por Gemini.
     */
    private fun parseAiJson(rawText: String): AiRouteRecommendation {
        var cleanText = rawText.trim()
        if (cleanText.startsWith("```json")) {
            cleanText = cleanText.removePrefix("```json").trim()
        }
        if (cleanText.startsWith("```")) {
            cleanText = cleanText.removePrefix("```").trim()
        }
        if (cleanText.endsWith("```")) {
            cleanText = cleanText.removeSuffix("```").trim()
        }

        val json = JSONObject(cleanText)
        val nivel = json.optString("nivel_seguridad", "Medio")
        val ruta = json.optString("ruta_sugerida", "Sigue siempre avenidas transitadas e iluminadas.")
        val advertenciasArray = json.optJSONArray("advertencias")
        val advertencias = mutableListOf<String>()
        if (advertenciasArray != null) {
            for (i in 0 until advertenciasArray.length()) {
                advertencias.add(advertenciasArray.getString(i))
            }
        }
        val consejo = json.optString("consejo_estudiantil", "Coordiná la salida con compañeros del INDEL.")

        return AiRouteRecommendation(
            nivel_seguridad = nivel,
            ruta_sugerida = ruta,
            advertencias = if (advertencias.isEmpty()) listOf("Mantener atención en cruces peatonales") else advertencias,
            consejo_estudiantil = consejo,
            esFallback = false
        )
    }

    /**
     * Generador de recomendación de respaldo seguro si la red o API falla.
     */
    fun getFallbackRecommendation(destination: String, reports: List<RouteReport>): AiRouteRecommendation {
        val puntosRiesgo = reports.filter { !it.isSafe }
        val puntosSeguros = reports.filter { it.isSafe }

        val advertencias = if (puntosRiesgo.isNotEmpty()) {
            puntosRiesgo.take(3).map { "Evitar ${it.placeName} (${it.recommendedHours})" }
        } else {
            listOf("Caminar atento y evitar manipular el celular en esquinas oscuras.")
        }

        val tramoSeguro = if (puntosSeguros.isNotEmpty()) {
            puntosSeguros.first().placeName
        } else {
            "Av. Belgrano frente al INDEL"
        }

        val nivel = if (puntosRiesgo.size > 2) "Medio" else "Alto"

        return AiRouteRecommendation(
            nivel_seguridad = nivel,
            ruta_sugerida = "Para dirigirte hacia \"$destination\", avanza por el corredor iluminado de $tramoSeguro. Mantente sobre veredas transitadas y evita atajos por pasajes secundarios.",
            advertencias = advertencias,
            consejo_estudiantil = "Coordiná la caminata en grupo con compañeros del Instituto INDEL para mayor seguridad en los horarios vespertino y nocturno.",
            esFallback = true
        )
    }
}

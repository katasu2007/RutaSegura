package com.example.model

/**
 * Respuesta estructurada generada por el Asistente de Ruta IA (Gemini).
 *
 * @param nivel_seguridad Nivel evaluado: "Alto", "Medio" o "Bajo".
 * @param ruta_sugerida Descripción breve del trayecto recomendado.
 * @param advertencias Lista de puntos de riesgo y precauciones a evitar.
 * @param consejo_estudiantil Consejo práctico para la caminata escolar.
 * @param esFallback Indica si proviene del motor de respaldo local o de la API directa.
 */
data class AiRouteRecommendation(
    val nivel_seguridad: String,
    val ruta_sugerida: String,
    val advertencias: List<String>,
    val consejo_estudiantil: String,
    val esFallback: Boolean = false
)

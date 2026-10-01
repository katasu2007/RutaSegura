# Bitácora de Prompts - Ruta Segura

## Peldaño P0: Prompt Cero (Inicial)

**Fecha/Hora:** 01/10/2026 - 09:45 AM
**Plataforma / Modelo:** Google AI Studio (Gemini)

### Prompt enviado:ROL: Desarrollador Full-Stack experto en integración de APIs e Inteligencia Artificial.

CONTEXTO:
Estoy desarrollando la aplicación web "RUTA SEGURA" para estudiantes del instituto INDEL. La app ya permite registrar puntos de riesgo/seguridad, filtrar reportes, guardarlos en localStorage y cuenta con una interfaz mobile-first con validaciones. Ahora necesito integrar la función inteligente (Sello de IA) para ofrecer recomendaciones analizadas en tiempo real.

OBJETIVO (Peldaño M5):
Integrar un módulo de IA que analice los reportes almacenados en la app y genere recomendaciones personalizadas en formato JSON sobre las rutas de caminata para los estudiantes.

TAREA ESPECÍFICA:
1. Crea un módulo o sección "Asistente de Ruta IA" en la interfaz con un campo de texto ("¿Hacia dónde te diriges?") y un botón "Analizar Ruta Segura".
2. Al presionar el botón, la función debe:
   - Leer todos los reportes vigentes en `localStorage`.
   - Enviar una solicitud a la API de Gemini enviando como contexto los reportes actuales y el destino del estudiante.
   - Solicitar a la IA que responda STRICTLY (estrictamente) en formato JSON válido con la siguiente estructura:
     {
       "nivel_seguridad": "Alto | Medio | Bajo",
       "ruta_sugerida": "Descripción breve del trayecto recomendado",
       "advertencias": ["Lista de puntos de riesgo a evitar"],
       "consejo_estudiantil": "Un consejo práctico para la caminata"
     }
3. Renderiza la respuesta devuelta por la IA en una tarjeta/card de UI atractiva dentro de la app (desglosando el nivel de seguridad con colores, la ruta y las advertencias).
4. Implementa un sistema de fallback o manejo de errores: si la API Key falla, expira o no hay conexión a internet, la app debe capturar la excepción y responder con un JSON simulado por defecto para evitar que la aplicación colapse.

RESTRICCIONES TÉCNICAS Y DE SEGURIDAD:
- Manejo seguro de credenciales: La API Key debe obtenerse desde una variable de entorno o configuración configurable (nunca expuesta directamente en texto plano en repositorios públicos).
- La llamada a la API debe ser asíncrona (`async/await`) con un bloque `try/catch` riguroso.
- Mantén la aplicación 100% funcional aun cuando la llamada de IA no responda o devuelva un error.

FORMATO DE SALIDA:
Proporciona las funciones de JavaScript (`async function analizarRuta()`, el parser de JSON y el fallback) junto con la estructura HTML de la tarjeta de recomendación.

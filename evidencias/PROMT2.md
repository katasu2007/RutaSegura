# Bitácora de Prompts - Ruta Segura

## Peldaño P0: Prompt Cero (Inicial)

**Fecha/Hora:** 01/10/2026 - 09:00 AM
**Plataforma / Modelo:** Google AI Studio (Gemini)

### Prompt enviado: ROL: Desarrollador Web Senior experto en JavaScript.

CONTEXTO: 
Estoy desarrollando la aplicación "RUTA SEGURA" para estudiantes que caminan al instituto INDEL. Actualmente la aplicación permite agregar y visualizar reportes de puntos seguros y de riesgo, pero los datos se pierden al recargar o cerrar la página.

OBJETIVO (Peldaño M2):
Implementar la persistencia de datos local para que los reportes guardados por el usuario no se borren cuando la página se recargue o se cierre el navegador.

TAREA ESPECÍFICA:
1. Integra `localStorage` para almacenar la lista de reportes en formato JSON.
2. Haz que la aplicación cargue automáticamente los reportes guardados en `localStorage` al iniciar la app (`DOMContentLoaded`).
3. Cada vez que se agregue o elimine un reporte, actualiza el estado en `localStorage`.
4. Incluye datos semilla o iniciales por defecto (al menos 2 reportes de prueba en las cercanías de INDEL) si `localStorage` está completamente vacío la primera vez.

RESTRICCIONES TÉCNICAS:
- No uses librerías externas ni servidores externos; solo JavaScript nativo (Vanilla JS).
- No rompas las funciones existentes (formulario de reporte, visualización y filtros por tipo de seguridad).
- Mantén el código limpio, estructurado y bien comentado.

FORMATO DE SALIDA:
Entrega el código JavaScript modificado (o el archivo JS completo) explicando brevemente en 2 líneas qué funciones se modificaron para integrar la persistencia.

# Bitácora de Prompts - Ruta Segura

## Peldaño P0: Prompt Cero (Inicial)

**Fecha/Hora:** 01/10/2026 - 09:25 AM
**Plataforma / Modelo:** Google AI Studio (Gemini)

### Prompt enviado:ROL: UI/UX Designer y Desarrollador Frontend Mobile-First.

CONTEXTO:
Estoy desarrollando la aplicación web "RUTA SEGURA" para estudiantes que caminan hacia el instituto INDEL. La app ya cuenta con funcionalidades de registro de puntos, filtros y persistencia de datos local (localStorage). Ahora necesito optimizar completamente la interfaz para que sea perfectamente utilizable desde un dispositivo móvil (teléfono inteligente).

OBJETIVO (Peldaño M3):
Mejorar la experiencia de usuario (UX/UI) en pantallas móviles, asegurando un diseño intuitivo, accesible en exteriores y con una gestión clara de los estados iniciales (estado vacío).

TAREA ESPECÍFICA:
1. Optimizar el diseño con un enfoque Mobile-First utilizando CSS Flexbox/Grid e interfaces adaptables.
2. Asegurar áreas de toque accesibles (Touch Targets): botones y campos de texto con un tamaño mínimo de 48px de alto para facilitar el uso con una sola mano.
3. Garantizar un alto contraste cromático en textos e íconos para que la pantalla sea legible en exteriores bajo la luz del sol.
4. Diseñar e implementar el "Estado Vacío" (Empty State): cuando no exista ningún reporte en localStorage, la aplicación debe mostrar una ilustración/ícono amigable y un mensaje explicativo claro (ej. "Aún no hay reportes de rutas en tu zona. ¡Sé el primero en marcar un punto seguro!").
5. Agregar un botón de acceso rápido o acción flotante (FAB) para realizar un reporte rápido sin necesidad de desplazarse por toda la página.

RESTRICCIONES TÉCNICAS:
- Utiliza CSS nativo (o HTML/CSS estándar) sin depender de frameworks pesados que ralenticen la carga móvil.
- Mantén el diseño limpio, responsivo y sin desbordamientos horizontales en pantallas pequeñas.
- Conservar intactas la funcionalidad de reportes, los filtros y la persistencia en localStorage de las fases anteriores.

FORMATO DE SALIDA:
Proporciona el código CSS y HTML actualizado con las modificaciones explicadas brevemente.

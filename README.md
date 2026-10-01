# 🛡️ RUTA SEGURA - Instituto INDEL

> **Aplicación comunitaria y colaborativa para que los estudiantes del Instituto INDEL reporten y consulten zonas seguras y puntos de riesgo en sus trayectos a pie.**

![Android](https://img.shields.io/badge/Android-Jetpack%20Compose-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white)
![Web](https://img.shields.io/badge/Web-Mobile--First-E34F26?logo=html5&logoColor=white)
![IA](https://img.shields.io/badge/AI-Google%20Gemini%20API-4285F4?logo=google&logoColor=white)
![Licencia](https://img.shields.io/badge/Licencia-MIT-green)

---

## 📌 Contexto y Problemática

Los estudiantes que asisten a pie al **Instituto INDEL** recorren diversas arterias y pasajes urbanos en diferentes turnos (mañana, tarde y noche). Muchos trayectos presentan problemáticas como deficiente iluminación pública, esquinas solitarias o cruces ferroviarios con baja visibilidad.

**RUTA SEGURA** nace para empoderar a la comunidad estudiantil, permitiendo compartir información en tiempo real sobre tramos iluminados y advertencias sobre puntos críticos para que todos puedan desplazarse con mayor tranquilidad y seguridad.

---

## 🚀 Funcionalidades Principales

### 1. 📍 Registro y Reporte Rápido de Zonas
- **Clasificación clara en un toque:**
  - 🟢 **Zona Segura / Iluminada:** Veredas transitadas, presencia policial, buena luz LED, comercios abiertos.
  - 🔴 **Punto de Riesgo / Peligro:** Calles oscuras, falta de luminarias, pasajes solitarios.
- Ingreso de nombre del tramo o calle con sugerencias rápidas (*"Frente al INDEL"*, *"Parada Colectivo"*, *"Sendero Plaza"*, *"Cruce Vías"*).
- Franja horaria recomendada o de precaución (*"07:00 a 19:30 hs"*, *"Evitar de noche"*).
- Posicionamiento intuitivo en el mapa comunitario alrededor del campus.

### 2. 🗺️ Visualización Dual: Lista y Mapa Comunitario INDEL
- **Vista Lista:** Tarjetas con diseño accesible, insignias de estado de alto contraste, horario sugerido, observaciones detalladas y botón para ubicar en mapa.
- **Vista Mapa INDEL:** Plano esquemático urbano centrado en el Instituto INDEL con marcadores interactivos verde y rojo, anillos de proximidad y vista previa táctil.

### 3. 🔍 Filtro y Búsqueda por Nivel de Seguridad
- Búsqueda en tiempo real por texto (nombre de calle, horario o detalles).
- Chips de filtrado rápido con contador dinámico:
  - `Todos` (total de aportes comunitarios).
  - `🟢 Solo zonas bien iluminadas` (únicamente tramos seguros).
  - `🔴 Puntos de riesgo` (alertas a tener en cuenta).

### 4. ✨ Asistente de Ruta IA (Sello de IA con Google Gemini)
- Módulo inteligente que analiza los reportes comunitarios vigentes para sugerir la mejor ruta hacia un destino indicado por el estudiante.
- Generación estructurada estricta en formato **JSON**:
  ```json
  {
    "nivel_seguridad": "Alto | Medio | Bajo",
    "ruta_sugerida": "Descripción breve del trayecto recomendado",
    "advertencias": ["Lista de puntos de riesgo a evitar"],
    "consejo_estudiantil": "Un consejo práctico para la caminata"
  }
  ```
- **Resiliencia & Fallback:** En caso de fallas de conexión o ausencia de API Key, se activa automáticamente un motor de respaldo local que evalúa los reportes almacenados, garantizando que la aplicación nunca se interrumpa.

### 5. 💾 Persistencia de Datos 100% Local (Offline-First)
- **Versión Android:** Implementada con **Room Database (SQLite)**.
- **Versión Web:** Implementada con **`localStorage`** y precarga de reportes semilla comunitarios.
- No requiere cuentas, registro ni servidores externos obligatorios.

### 6. 📱 Experiencia Mobile-First & Accesibilidad (Peldaño M3)
- **Touch Targets:** Botones, chips y controles táctiles con altura mínima de **48px** para uso cómodo con una sola mano.
- **Alto Contraste Cromático:** Paleta validada para lectura en exteriores bajo la luz directa del sol.
- **Botón de Acción Flotante (FAB):** Acceso permanente en la esquina inferior para registrar reportes en cualquier momento.
- **Estado Vacío (Empty State):** Mensaje ilustrado y amigable cuando no existen reportes cargados.

---

## 🛠️ Stack Tecnológico

### Android (Nativo)
- **Lenguaje:** Kotlin 2.1.0
- **UI:** Jetpack Compose con Material Design 3 (M3)
- **Arquitectura:** MVVM (Model-View-ViewModel) + StateFlow
- **Base de Datos:** Android Jetpack Room (SQLite local)
- **Red:** OkHttp 4 / Retrofit
- **Testing:** JUnit4, Robolectric

### Web (Mobile-First)
- **Estructura:** HTML5 semántico
- **Estilos:** CSS3 nativo (CSS Grid, Flexbox, variables CSS, sin frameworks pesados)
- **Lógica:** Vanilla JavaScript (ES6+ modular)
- **Almacenamiento:** API Web `localStorage`
- **Inteligencia Artificial:** Google Gemini REST API (`gemini-2.5-flash`)

---

## 📁 Estructura del Repositorio

```text
ruta-segura-indel/
├── app/                                # Aplicación Android nativa
│   ├── src/main/java/com/example/
│   │   ├── data/                       # Room Database, DAO y AiRouteService
│   │   ├── model/                      # RouteReport y AiRouteRecommendation
│   │   ├── ui/                         # ViewModel, Screens y Composables
│   │   │   ├── components/             # Tarjetas, Mapa, Filtros y Asistente IA
│   │   │   └── theme/                  # Colores y tipografía M3
│   │   └── MainActivity.kt             # Entrada principal de la app
│   └── build.gradle.kts                # Configuración de dependencias Android
├── web/                                # Aplicación Web Mobile-First
│   ├── index.html                      # Estructura semántica con Asistente IA y FAB
│   ├── styles.css                      # Estilos de alto contraste y Touch Targets >= 48px
│   └── app.js                          # Lógica de reportes, localStorage y llamada a Gemini
├── evidencias/                         # Bitácora de Prompts y entregables de evaluación
│   ├── PROMPTS.md                      # Peldaño P0: Prompt inicial
│   ├── PROMT2.md                       # Peldaño M2: Persistencia local
│   ├── PROMT3.md                       # Peldaño M3: Mobile-First UX/UI
│   ├── PROMT4.md                       # Peldaño M5: Sello de IA con Gemini
│   └── *.png                           # Capturas de evidencia funcional
├── metadata.json                       # Metadatos del entorno AI Studio
└── README.md                           # Documentación general del proyecto
```

---

## 🚦 Guía de Instalación y Ejecución

### Opción 1: Ejecutar la App Android (Streaming Emulator / Android Studio)
1. Abrir el proyecto en **Android Studio** (versión Ladybug o superior) o en el contenedor de **Google AI Studio**.
2. Sincronizar Gradle (`Sync Project with Gradle Files`).
3. *(Opcional)* Configurar tu `GEMINI_API_KEY` en el archivo `.env` o en el panel de Secrets.
4. Ejecutar las pruebas unitarias:
   ```bash
   gradle :app:testDebugUnitTest
   ```
5. Compilar y correr en el dispositivo o emulador:
   ```bash
   gradle :app:assembleDebug
   ```

### Opción 2: Ejecutar la Versión Web
1. Navegar a la carpeta `web/`:
   ```bash
   cd web
   ```
2. Abrir `index.html` en cualquier navegador web moderno, o servirlo con cualquier servidor local ligero:
   ```bash
   # Ejemplo con Python
   python3 -m http.server 8080
   # O con Node.js
   npx serve .
   ```

---

## 👥 Bitácora de Desarrollo (Hitos y Peldaños)

- **Peldaño P0:** Generación de la primera versión funcional con las 3 funciones básicas (registro rápido, lista/mapa comunitario y filtros).
- **Peldaño M2:** Persistencia de datos local en `localStorage` con carga en `DOMContentLoaded` y reportes semilla.
- **Peldaño M3:** Optimización Mobile-First, Touch Targets de 48px, alto contraste exterior, botón flotante (FAB) y Empty State.
- **Peldaño M5:** Sello de IA con Google Gemini API (`gemini-2.5-flash`), salida estricta en JSON y fallback ante fallos de red.

---

## 📄 Licencia

Este proyecto está bajo la Licencia **MIT**. Desarrollado con fines educativos y de seguridad comunitaria para la comunidad de estudiantes del **Instituto INDEL**.

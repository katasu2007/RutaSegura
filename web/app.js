/**
 * RUTA SEGURA - Instituto INDEL
 * Gestión de reportes de zonas seguras y puntos de riesgo con persistencia en localStorage.
 * Integración con Asistente de IA (Peldaño M5 - Sello de IA con Gemini API).
 */

const STORAGE_KEY = 'ruta_segura_reportes';

// Configuración segura de la API Key (obtenida de variables de entorno o configuración global)
const GEMINI_API_KEY = (typeof window !== 'undefined' && window.GEMINI_API_KEY) || 
                       (typeof process !== 'undefined' && process.env?.GEMINI_API_KEY) || 
                       "";

// Modelo Gemini recomendado para tareas rápidas de texto
const GEMINI_MODEL = "gemini-2.5-flash";

// Datos semilla por defecto para la primera inicialización si no hay registros
const REPORTES_SEMILLA = [
    {
        id: 1,
        lugar: "Acceso Principal INDEL (Av. Belgrano)",
        esSeguro: true,
        horario: "06:30 a 22:30 hs",
        detalles: "Luminarias LED y presencia de personal en puerta.",
        fecha: new Date().toLocaleDateString('es-AR')
    },
    {
        id: 2,
        lugar: "Pasaje Los Talleres (Detrás de aulas)",
        esSeguro: false,
        horario: "Evitar luego de las 18:30 hs",
        detalles: "Poco tránsito y falta luminaria pública.",
        fecha: new Date().toLocaleDateString('es-AR')
    }
];

let reportes = [];
let filtroActual = 'todos';

/**
 * Carga los reportes desde localStorage o inserta los datos semilla si está vacío.
 */
function cargarReportes() {
    try {
        const datosGuardados = localStorage.getItem(STORAGE_KEY);
        if (datosGuardados !== null) {
            reportes = JSON.parse(datosGuardados);
        } else {
            reportes = [...REPORTES_SEMILLA];
            guardarEnStorage();
        }
    } catch (error) {
        console.error("Error al leer de localStorage:", error);
        reportes = [...REPORTES_SEMILLA];
    }
    renderizarReportes(filtroActual);
}

/**
 * Guarda el arreglo actual en localStorage.
 */
function guardarEnStorage() {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(reportes));
    } catch (error) {
        console.error("Error al guardar en localStorage:", error);
    }
}

/**
 * Agrega un nuevo reporte y actualiza la interfaz.
 */
function agregarReporte(lugar, esSeguro, horario, detalles = "") {
    const nuevoReporte = {
        id: Date.now(),
        lugar: lugar.trim(),
        esSeguro: Boolean(esSeguro),
        horario: horario.trim(),
        detalles: detalles.trim(),
        fecha: new Date().toLocaleDateString('es-AR')
    };

    reportes.unshift(nuevoReporte);
    guardarEnStorage();
    renderizarReportes(filtroActual);
}

/**
 * Elimina un reporte por su identificador.
 */
function eliminarReporte(id) {
    reportes = reportes.filter(reporte => reporte.id !== id);
    guardarEnStorage();
    renderizarReportes(filtroActual);
}

/**
 * Renderiza los reportes o el estado vacío accesible (Empty State).
 */
function renderizarReportes(filtro = 'todos') {
    filtroActual = filtro;
    const contenedor = document.getElementById('lista-reportes');
    if (!contenedor) return;

    const reportesFiltrados = reportes.filter(reporte => {
        if (filtro === 'seguros') return reporte.esSeguro;
        if (filtro === 'riesgo') return !reporte.esSeguro;
        return true;
    });

    if (reportesFiltrados.length === 0) {
        contenedor.innerHTML = `
            <div class="empty-state" role="status">
                <div class="empty-state-icono">🗺️</div>
                <h3 class="empty-state-titulo">Aún no hay reportes de rutas en tu zona</h3>
                <p class="empty-state-descripcion">
                    ¡Sé el primero en marcar un punto seguro o advertir a tus compañeros del INDEL sobre un tramo con poca luz!
                </p>
                <button class="empty-state-btn" onclick="abrirModalReporte()">
                    <span>➕</span>
                    <span>Marcar punto ahora</span>
                </button>
            </div>
        `;
        return;
    }

    contenedor.innerHTML = reportesFiltrados.map(reporte => `
        <article class="tarjeta-reporte ${reporte.esSeguro ? 'seguro' : 'peligro'}">
            <header class="tarjeta-header">
                <span class="badge ${reporte.esSeguro ? 'badge-seguro' : 'badge-peligro'}">
                    ${reporte.esSeguro ? '🟢 Zona Segura / Iluminada' : '🔴 Punto de Riesgo'}
                </span>
                <button class="btn-eliminar" onclick="eliminarReporte(${reporte.id})" aria-label="Eliminar reporte">✕</button>
            </header>
            <h3 class="tarjeta-titulo">${escapeHtml(reporte.lugar)}</h3>
            <p class="tarjeta-horario"><strong>🕒 Horario:</strong> ${escapeHtml(reporte.horario)}</p>
            ${reporte.detalles ? `<p class="tarjeta-detalles">${escapeHtml(reporte.detalles)}</p>` : ''}
            <footer class="tarjeta-footer">
                <small>Publicado: ${reporte.fecha}</small>
            </footer>
        </article>
    `).join('');
}

function escapeHtml(texto) {
    const div = document.createElement('div');
    div.textContent = texto;
    return div.innerHTML;
}

// Control del Modal / Drawer Móvil
function abrirModalReporte() {
    const modal = document.getElementById('modal-reporte');
    if (modal) modal.classList.add('abierto');
}

function cerrarModalReporte() {
    const modal = document.getElementById('modal-reporte');
    if (modal) modal.classList.remove('abierto');
}

/* ==========================================================================
   PELDAÑO M5: Asistente de Ruta IA con Gemini API
   ========================================================================== */

/**
 * Analiza los reportes vigentes y el destino deseado mediante la API de Gemini.
 * En caso de fallo de red, clave ausente o error de formato, activa el fallback simulado.
 * @param {string} destino - Punto o lugar al que se dirige el estudiante.
 */
async function analizarRuta(destino) {
    const contenedorResultado = document.getElementById('contenedor-resultado-ia');
    const botonAnalizar = document.getElementById('btn-analizar-ia');

    if (!destino || !destino.trim()) {
        alert("Por favor indica hacia dónde te diriges para analizar tu ruta.");
        return;
    }

    // Estado visual de carga (Loading state)
    if (botonAnalizar) {
        botonAnalizar.disabled = true;
        botonAnalizar.innerHTML = `<span>⏳</span> <span>Consultando con Gemini...</span>`;
    }

    try {
        // 1. Obtener reportes vigentes de localStorage para enviar como contexto
        const reportesActuales = reportes.length > 0 ? reportes : REPORTES_SEMILLA;
        const contextoReportes = reportesActuales.map(r => 
            `- [${r.esSeguro ? 'SEGURO/ILUMINADO' : 'PELIGRO/RIESGO'}] Lugar: ${r.lugar}. Horario: ${r.horario}. Detalles: ${r.detalles || 'Sin notas'}`
        ).join("\n");

        // 2. Si no hay API Key configurada, activar directamente el fallback inteligente
        if (!GEMINI_API_KEY) {
            console.warn("GEMINI_API_KEY no configurada. Activando fallback simulado por seguridad.");
            // Pequeña pausa para simular experiencia de análisis
            await new Promise(res => setTimeout(res, 800));
            const recomendacionFallback = generarRecomendacionFallback(destino, reportesActuales);
            renderizarTarjetaIA(recomendacionFallback);
            return;
        }

        // 3. Construir prompt estricto solicitando respuesta exclusivamente en JSON
        const prompt = `Eres el Asistente de Seguridad Estudiantil del Instituto INDEL.
Analiza los siguientes reportes comunitarios de caminata de los estudiantes:
${contextoReportes}

El estudiante se dirige a: "${destino.trim()}".

Genera una recomendación de caminata segura. Responde STRICTLY en formato JSON válido con la siguiente estructura y sin texto adicional:
{
  "nivel_seguridad": "Alto" | "Medio" | "Bajo",
  "ruta_sugerida": "Descripción breve y clara del trayecto recomendado",
  "advertencias": ["Punto de riesgo 1 a evitar", "Punto de riesgo 2 a evitar"],
  "consejo_estudiantil": "Un consejo práctico para la caminata escolar"
}`;

        // 4. Llamada HTTP a la API REST de Google Gemini
        const url = `https://generativelanguage.googleapis.com/v1beta/models/${GEMINI_MODEL}:generateContent?key=${encodeURIComponent(GEMINI_API_KEY)}`;
        
        const respuesta = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                contents: [{
                    parts: [{ text: prompt }]
                }],
                generationConfig: {
                    responseMimeType: "application/json",
                    temperature: 0.3
                }
            })
        });

        if (!respuesta.ok) {
            throw new Error(`Error en respuesta Gemini API: HTTP ${respuesta.status}`);
        }

        const datos = await respuesta.json();
        const textoGenerado = datos?.candidates?.[0]?.content?.parts?.[0]?.text;
        
        if (!textoGenerado) {
            throw new Error("La API de Gemini devolvió una respuesta sin contenido de texto.");
        }

        // 5. Parsear el JSON devuelto
        const resultadoIA = parsearRespuestaJSON(textoGenerado);
        renderizarTarjetaIA(resultadoIA);

    } catch (error) {
        console.error("Excepción en análisis de IA:", error);
        // Fallback robusto para evitar que la aplicación colapse
        const recomendacionFallback = generarRecomendacionFallback(destino, reportes);
        renderizarTarjetaIA(recomendacionFallback);
    } finally {
        if (botonAnalizar) {
            botonAnalizar.disabled = false;
            botonAnalizar.innerHTML = `<span>⚡</span> <span>Analizar Ruta Segura</span>`;
        }
    }
}

/**
 * Limpia y parsea de manera segura el JSON devuelto por el modelo.
 * Remueve bloques de markdown (```json ... ```) si están presentes.
 */
function parsearRespuestaJSON(texto) {
    try {
        let textoLimpio = texto.trim();
        if (textoLimpio.startsWith("```json")) {
            textoLimpio = textoLimpio.replace(/^```json\s*/, '').replace(/\s*```$/, '');
        } else if (textoLimpio.startsWith("```")) {
            textoLimpio = textoLimpio.replace(/^```\s*/, '').replace(/\s*```$/, '');
        }
        return JSON.parse(textoLimpio);
    } catch (err) {
        console.warn("Fallo al parsear JSON devuelto por IA. Utilizando extractor de regex.", err);
        const match = texto.match(/\{[\s\S]*\}/);
        if (match) {
            return JSON.parse(match[0]);
        }
        throw err;
    }
}

/**
 * Genera una recomendación local simulada en caso de error de conexión, clave o API caída.
 */
function generarRecomendacionFallback(destino, listaReportes) {
    const puntosRiesgo = (listaReportes || []).filter(r => !r.esSeguro);
    const puntosSeguros = (listaReportes || []).filter(r => r.esSeguro);

    let advertencias = puntosRiesgo.map(p => `Evitar ${p.lugar} (${p.horario})`);
    if (advertencias.length === 0) {
        advertencias = ["Caminar atento y no utilizar el celular en esquinas con poca visibilidad."];
    }

    const tramoSeguro = puntosSeguros.length > 0 
        ? puntosSeguros[0].lugar 
        : "Av. Belgrano y calles principales iluminadas";

    return {
        "nivel_seguridad": puntosRiesgo.length > 2 ? "Medio" : "Alto",
        "ruta_sugerida": `Para llegar a "${destino}", avanza por el corredor iluminado de ${tramoSeguro}. Mantente sobre veredas transitadas y evita atajos por pasajes secundarios.`,
        "advertencias": advertencias.slice(0, 3),
        "consejo_estudiantil": "Coordiná la salida con compañeros del INDEL para caminar en grupo, especialmente en los horarios vespertino y nocturno."
    };
}

/**
 * Renderiza la tarjeta con el resultado de la recomendación de IA.
 */
function renderizarTarjetaIA(datos) {
    const contenedor = document.getElementById('contenedor-resultado-ia');
    if (!contenedor) return;

    const nivelClase = (datos.nivel_seguridad || 'Medio').toLowerCase();
    const nivelTexto = datos.nivel_seguridad || 'Medio';

    const listaAdvertencias = (Array.isArray(datos.advertencias) ? datos.advertencias : [])
        .map(adv => `<li class="ia-advertencia-item">⚠️ ${escapeHtml(adv)}</li>`)
        .join('');

    contenedor.innerHTML = `
        <article class="tarjeta-ia-resultado" role="region" aria-label="Recomendación de Ruta IA">
            <header class="ia-res-header">
                <div>
                    <strong style="font-size: 0.95rem;">Análisis de Trayecto</strong>
                </div>
                <span class="badge-nivel ${nivelClase}">
                    Nivel: ${escapeHtml(nivelTexto)}
                </span>
            </header>

            <div class="ia-res-seccion">
                <h4 class="ia-res-titulo">🚶 Ruta Recomendada</h4>
                <p class="ia-res-texto">${escapeHtml(datos.ruta_sugerida || 'Sigue siempre las avenidas principales.')}</p>
            </div>

            ${listaAdvertencias ? `
                <div class="ia-res-seccion">
                    <h4 class="ia-res-titulo">🚨 Puntos de Atención a Evitar</h4>
                    <ul class="ia-advertencias-lista">
                        ${listaAdvertencias}
                    </ul>
                </div>
            ` : ''}

            <div class="ia-res-seccion" style="margin-bottom: 0;">
                <h4 class="ia-res-titulo">💡 Consejo Comunitario INDEL</h4>
                <div class="ia-consejo-box">
                    ${escapeHtml(datos.consejo_estudiantil || 'Caminar en grupo mejora la seguridad de todos.')}
                </div>
            </div>
        </article>
    `;
}

// Inicialización de Eventos DOM
document.addEventListener('DOMContentLoaded', () => {
    cargarReportes();

    // Evento del Asistente de Ruta IA
    const formAnalisisIA = document.getElementById('form-analisis-ia');
    if (formAnalisisIA) {
        formAnalisisIA.addEventListener('submit', (e) => {
            e.preventDefault();
            const inputDestino = document.getElementById('input-destino');
            if (inputDestino) {
                analizarRuta(inputDestino.value);
            }
        });
    }

    // FAB (Botón de Acción Flotante)
    const btnFab = document.getElementById('btn-fab');
    if (btnFab) {
        btnFab.addEventListener('click', abrirModalReporte);
    }

    // Botón de cerrar modal
    const btnCerrar = document.getElementById('btn-cerrar-modal');
    if (btnCerrar) {
        btnCerrar.addEventListener('click', cerrarModalReporte);
    }

    // Cerrar al tocar fuera del modal
    const modalOverlay = document.getElementById('modal-reporte');
    if (modalOverlay) {
        modalOverlay.addEventListener('click', (e) => {
            if (e.target === modalOverlay) cerrarModalReporte();
        });
    }

    // Formulario de nuevo reporte
    const formulario = document.getElementById('form-reporte');
    if (formulario) {
        formulario.addEventListener('submit', (e) => {
            e.preventDefault();
            const lugar = document.getElementById('input-lugar').value;
            const esSeguro = document.querySelector('input[name="tipo-seguridad"]:checked').value === 'seguro';
            const horario = document.getElementById('input-horario').value;
            const detalles = document.getElementById('input-detalles').value;

            if (lugar && horario) {
                agregarReporte(lugar, esSeguro, horario, detalles);
                formulario.reset();
                cerrarModalReporte();
            }
        });
    }

    // Filtros por nivel de seguridad
    const botonesFiltro = document.querySelectorAll('.btn-filtro');
    botonesFiltro.forEach(boton => {
        boton.addEventListener('click', (e) => {
            botonesFiltro.forEach(b => b.classList.remove('activo'));
            e.currentTarget.classList.add('activo');
            const filtro = e.currentTarget.dataset.filtro;
            renderizarReportes(filtro);
        });
    });
});

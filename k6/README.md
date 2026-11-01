# Pruebas de rendimiento con k6

Las pruebas usan k6 en Docker contra la API publicada en Render. No inician el backend ni una base de datos. El preflight y la verificación mínima son comprobaciones separadas de las pruebas académicas. No ejecutar solicitudes a Render sin autorización.

## Alcance, autenticación y datos

| Módulo | Endpoint | Permiso |
|---|---|---|
| Vehículos | `GET /api/vehiculos?page=0&size=20` | JWT de usuario activo con rol `CLIENTE` o `ADMINISTRADOR` |
| Gastos | `GET /api/gastos?page=0&size=20` | JWT de usuario activo con rol `CLIENTE` o `ADMINISTRADOR` |

El `size` por omisión es 20 y puede ajustarse entre 1 y 50 mediante `PAGE_SIZE`. Ambos endpoints admiten paginación Spring (`page`, `size`, `sort`). La API devuelve HTTP 200 para consultas correctas. `GET /api/gastos` responde 404 cuando la página no contiene registros.

La prueba general incorpora estos GET confirmados en los controladores:

| Módulo | Endpoint paginado | HTTP cuando no hay contenido según el controlador | Permiso |
|---|---|---|---|
| Vehículos | `/api/vehiculos?page=0&size=20` | 200 con página vacía | `CLIENTE` o `ADMINISTRADOR` |
| Gastos | `/api/gastos?page=0&size=20` | 404 | `CLIENTE` o `ADMINISTRADOR` |
| Mantenimientos | `/api/mantenimientos?page=0&size=20` | 404 | `CLIENTE` o `ADMINISTRADOR` |
| Recordatorios | `/api/recordatorios?page=0&size=20` | 404 | `CLIENTE` o `ADMINISTRADOR` |
| Registros de kilometraje | `/api/kilometrajes?page=0&size=20` | 404 | `CLIENTE` o `ADMINISTRADOR` |

Todos los listados aceptan parámetros de paginación Spring. Mantenimientos también acepta `vehiculoId`, `estado` y `activo`; kilometrajes acepta `vehiculoId` y `activo`. La prueba general omite esos filtros opcionales y solo usa `page=0&size=PAGE_SIZE`. La autorización global requiere JWT y los controladores aplican `@PreAuthorize` con los roles indicados. El preflight trata una respuesta 404 en listados marcados como vacíos como diagnóstico posible de falta de contenido y **no** como respuesta correcta HTTP 200.

El login real es `POST /api/auth/login` con `{"correo":"...","clave":"..."}` y devuelve `{"token":"..."}`. El token dura una hora. Los scripts k6 no hacen login y solo envían `GET` autenticados; no realizan operaciones POST, PUT o DELETE. El éxito HTTP 200 en ambos endpoints confirma que el token tiene permisos para esas consultas.

No usar una cuenta ni generar solicitudes sin autorización. No incluir contraseñas o JWT en archivos versionados, comandos visibles, reportes o capturas. No se conecta directamente a Aiven ni se modifican datos.

## Requisitos y preparación

- GitHub Codespaces con Docker y Docker Compose (`docker --version`, `docker compose version`).
- `curl` y `jq` para disponibilidad y login opcional.
- JWT válido de un usuario de prueba autorizado.
- Conectividad HTTPS a Render.

La imagen está fijada a `grafana/k6:0.55.0`; Docker la descargará en el primer uso. Desde la raíz:

```bash
docker --version
docker compose version
cp .env.k6.example .env.k6
```

`.env.k6` está excluido de Git. Sustituye `AUTH_TOKEN` por el JWT sin `Bearer `. Para obtener el JWT manualmente (fuera de k6), evita escribir la clave en el historial:

```bash
read -r -p "Correo: " K6_USER
read -r -s -p "Clave: " K6_PASSWORD
printf '\n'
BASE_URL=${BASE_URL:-https://controlvehicular-6s7g.onrender.com}
LOGIN_JSON=$(jq -n --arg correo "$K6_USER" --arg clave "$K6_PASSWORD" '{correo:$correo,clave:$clave}')
export AUTH_TOKEN
AUTH_TOKEN=$(curl -fsS "$BASE_URL/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d "$LOGIN_JSON" | jq -er '.token')
unset K6_USER K6_PASSWORD LOGIN_JSON
```

Ejecuta el bloque y k6 desde la misma terminal. El `AUTH_TOKEN` exportado prevalece sobre `.env.k6`. Al terminar, ejecuta `unset AUTH_TOKEN`.

## Disponibilidad

Esta comprobación opcional hace una sola consulta pública al Swagger, no valida JWT:

```bash
curl -sS -o /dev/null -w 'Swagger HTTP %{http_code}\n' \
  https://controlvehicular-6s7g.onrender.com/swagger-ui/index.html
```

## Preflight: ambos endpoints con JWT

El preflight ejecuta **una iteración con 1 VU** y dos solicitudes GET en secuencia, una a cada endpoint. Registra sus códigos HTTP, verifica que ambos sean 200 y falla el threshold de checks si alguno no lo es. Un 200 en los dos confirma que el JWT tiene permisos para los módulos. El JSON también separa autorización de HTTP 200: la autorización de Gastos se considera concedida si devuelve 200 o el 404 de página vacía, porque la protección por rol se aplica antes de que el controlador responda 404.

La ejecución sí genera solicitudes reales a Render; ejecutarla solo tras autorizar ese preflight:

```bash
REPORT_NAME=preflight-endpoints \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/preflight.js
```

Produce `k6/reports/preflight-endpoints.html` (dashboard k6) y `k6/reports/preflight-endpoints.json`. Un 404 en Gastos significa que no hay contenido en esa página, según el comportamiento del controlador; no es una respuesta HTTP 200, aunque confirma que la consulta pasó la autorización por rol. No insertar gastos ni alterar datos de producción para hacer pasar el chequeo. Solicita al responsable que confirme si existe un conjunto de prueba ya aprobado con registros; si no existe, conserva y documenta el 404 como resultado real y no reportes el endpoint como HTTP 200.

## Preflight del sistema general (cinco endpoints)

Antes de la prueba general, este comando envía exactamente una consulta GET a cada módulo (cinco solicitudes en total), con un VU y una iteración. Guarda estados HTTP/checks y permite distinguir un 404 compatible con lista vacía de respuestas 401/403/5xx:

```bash
REPORT_NAME=preflight-sistema-general \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/preflight-sistema-general.js
```

Los resultados quedan en `k6/reports/preflight-sistema-general.html` y `.json`. Un 404 en Gastos, Mantenimientos, Recordatorios o Kilometrajes indica que ese controlador devuelve 404 cuando el listado consultado está vacío; el preflight lo marca como endpoint sin HTTP 200, aunque la respuesta 404 posterior a la autorización es compatible con un JWT que pasó el filtro de rol. No se crean datos en producción para corregirlo. Detén la prueba general si un módulo no devuelve HTTP 200; documenta el resultado o confirma con el responsable si ya existe un conjunto de prueba autorizado y no vacío. Si hay 401/403, revisa el token y que su usuario esté activo y tenga rol permitido. Otros estados requieren revisar disponibilidad/ruta y no deben reinterpretarse como correctos.

## Prueba general mixta (un único HTML gráfico)

`sistema-general.js` consulta un solo módulo en cada iteración y rota determinísticamente entre los cinco módulos por VU e iteración. Cada llamada es un GET paginado, con el mismo JWT y una pausa de un segundo entre iteraciones. No ejecuta escrituras ni realiza carga masiva. Las etiquetas k6 `module`, `phase`, `phase_group` y `phase_detail` identifican cada solicitud; el JSON conserva métricas globales y desgloses por módulo y subfase.

El escenario es único y secuencial (sin escenarios concurrentes), con máximo configurado de 15 VU. La rampa y los periodos son:

| Subfase | Duración | VU objetivo |
|---|---:|---:|
| Calentamiento base | 5 s | 0 → 1 |
| Rendimiento base medido | 60 s | 1 |
| Transición moderada | 10 s | 1 → 5 |
| Carga moderada medida | 60 s | 5 |
| Transición elevada | 10 s | 5 → 10 |
| Carga elevada medida | 60 s | 10 |
| Transición a estrés y reducción a 0 VU | 10 s | 10 → 0 |
| Estrés: incremento | 20 s | 0 → 5 |
| Estrés: incremento | 20 s | 5 → 10 |
| Estrés: incremento | 20 s | 10 → 15 |
| Estrés: mantenimiento | 20 s | 15 |
| Estrés: reducción | 20 s | 15 → 0 |

La duración programada total es 315 segundos, más el cierre normal del escenario. El intervalo a 0 VU separa la carga elevada del estrés y evita convertir la transición de carga en una parte inadvertida de esa prueba. Las fases de rendimiento/carga se reportan separadas de sus transiciones. Durante cada subfase, cada VU elige su módulo por turnos; la proporción exacta observada se informa y no se presume idéntica cuando una fase se interrumpe. `summaryTrendStats` incluye `count` para que los Trend de latencia permitan calcular el total de solicitudes por módulo/fase; las fases sin muestras informan criterios `null` (no evaluables), nunca aprobados.

Cuando el preflight haya confirmado disponibilidad y tengas autorización para generar esta carga contra Render, el comando único es:

```bash
REPORT_NAME=sistema-general \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/sistema-general.js
```

Escribe el HTML gráfico Web Dashboard en `k6/reports/sistema-general.html` y el resumen agregado/desglosado en `k6/reports/sistema-general.json`. La página JSON incluye métricas HTTP, p95, tasa de solicitudes, errores, checks, VU máximo observado y planificado, conteo de solicitudes por módulo/subfase, umbrales, criterios académicos y estado de ejecución.

Para rendimiento base, carga moderada y elevada se evalúa por separado p95 `< 3.000 ms`, HTTP fallidos `< 1 %` y checks HTTP 200 exitosos `> 99 %`. Como barreras preventivas para evitar subir la carga con degradación severa, las fases de rendimiento/carga abortan a partir de una tasa de fallos de 5 % o p95 de 8.000 ms. La evaluación se programa a los 15 s para rendimiento base, 70 s para carga moderada (fase iniciada en 65 s) y 140 s para carga elevada (iniciada en 135 s). El estrés se etiqueta con `phase_group:estres_controlado` y aplica fallos `< 10 %` y p95 `< 8.000 ms`; la evaluación comienza a los 230 s, después del inicio previsto del grupo de estrés alrededor de 215 s. `delayAbortEval` es tiempo desde el inicio de la prueba para comenzar a evaluar el threshold; no exige incumplimiento continuo. Los criterios académicos siguen separados y un fallo de check HTTP 200 no se convierte en éxito por los límites preventivos.

El máximo de 15 VU es el previsto de k6; cada iteración emite una sola petición al módulo seleccionado. Aun así cada GET paginado consulta la base Aiven a través de la aplicación Render, así que no debe ejecutarse hasta revisar el preflight, tamaño de página, cadencia y aprobación del responsable. Una respuesta 404 de listados vacíos invalida los checks y debe documentarse; no se han generado resultados ni reportes de prueba general anticipadamente.

## Verificación mínima por endpoint

El perfil `verify` de `api.js` tiene 1 VU y 1 iteración (una sola solicitud), sin pausa ni umbrales de rendimiento académico. Es útil para comprobar el flujo Docker/k6/JWT/endpoint/exportación de un módulo después del preflight. Genera HTML y JSON con `runStatus`, el HTTP 200 y métricas observadas. No sustituye a la prueba académica de 60 s.

```bash
TEST_MODULE=vehiculos TEST_PROFILE=verify REPORT_NAME=vehiculos-verificacion \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/api.js
```

Para Gastos, ejecuta una verificación separada con `TEST_MODULE=gastos`. No ejecutar ahora contra Render sin autorización.

## Escenarios académicos

Las pruebas de rendimiento/carga duran 60 segundos. Cada iteración espera 1 segundo para moderar el tráfico. Carga permite 5 o 10 VU. El estrés tiene siete etapas de 30 segundos: subir a 5, mantener 5, subir a 10, mantener 10, subir a 15, mantener 15 y bajar a 0. Se recomienda una pausa de 2 segundos entre iteraciones de estrés.

Todos los comandos se lanzan desde la raíz y requieren JWT en `.env.k6` o exportado en la misma terminal. La carga y el estrés son pruebas independientes que necesitan autorización antes de cada ejecución.

### Rendimiento base (1 VU, 60 s)

```bash
TEST_MODULE=vehiculos TEST_PROFILE=baseline REPORT_NAME=vehiculos-rendimiento \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/api.js
```

```bash
TEST_MODULE=gastos TEST_PROFILE=baseline REPORT_NAME=gastos-rendimiento \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/api.js
```

### Carga (5 y 10 VU, 60 s cada una)

```bash
TEST_MODULE=vehiculos TEST_PROFILE=load LOAD_VUS=5 REPORT_NAME=vehiculos-carga-5vu \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/api.js
```

```bash
TEST_MODULE=vehiculos TEST_PROFILE=load LOAD_VUS=10 REPORT_NAME=vehiculos-carga-10vu \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/api.js
```

```bash
TEST_MODULE=gastos TEST_PROFILE=load LOAD_VUS=5 REPORT_NAME=gastos-carga-5vu \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/api.js
```

```bash
TEST_MODULE=gastos TEST_PROFILE=load LOAD_VUS=10 REPORT_NAME=gastos-carga-10vu \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/api.js
```

### Estrés controlado (máximo 15 VU)

El límite de aborto es independiente del criterio académico: abortar si fallos alcanzan 10 % o el p95 llega a 8.000 ms. `delayAbortEval: '15s'` retrasa el inicio de la evaluación del threshold; no exige que la condición se incumpla continuamente durante 15 segundos. El JSON registra todas las señales no-200 o de respuesta de al menos 3.000 ms; la primera global es el menor `exec.instance.currentTestRunDuration`, y su etapa se deriva de la rampa.

```bash
TEST_MODULE=vehiculos TEST_PROFILE=stress ITERATION_PAUSE_SECONDS=2 REPORT_NAME=vehiculos-estres-controlado \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/api.js
```

```bash
TEST_MODULE=gastos TEST_PROFILE=stress ITERATION_PAUSE_SECONDS=2 REPORT_NAME=gastos-estres-controlado \
docker compose --env-file .env.k6 -f compose.k6.yaml run --rm --service-ports k6 run /scripts/api.js
```

Se pueden ajustar `STRESS_STAGE_SECONDS` (10–120 s por etapa), `ITERATION_PAUSE_SECONDS` (mayor que 0, máximo 30), `PAGE_SIZE` (1–50) y `TEST_DURATION_SECONDS` (1–3600). `LOAD_VUS` solo admite 5 o 10. Registra los cambios al protocolo en el informe.

## Dashboard, métricas y reportes

Compose habilita el dashboard/export de k6 y establece `K6_WEB_DASHBOARD_OPEN=false`. Durante la prueba, abre **Ports** en Codespaces y visita el puerto `5665`; el dashboard deja de estar disponible al salir el contenedor. Al terminar normalmente, k6 exporta gráficos HTML y el `handleSummary` guarda el JSON. Si el proceso se interrumpe o aborta, conserva el estado parcial como cancelado/abortado/incompleto; no lo trates como ejecución completa. Un cierre forzado que impida a k6 ejecutar el resumen puede no producir archivos.

Los archivos se guardan en `k6/reports/`:

- `<REPORT_NAME>.html`: visualizaciones exportadas por k6 Web Dashboard.
- `<REPORT_NAME>.json`: solicitudes, req/s, duración, latencias, p95, fallos HTTP, checks, VU máximo observado, VU máximo configurado, thresholds y estado de ejecución.

Después de las ejecuciones, genera el comparativo académico:

```bash
node k6/scripts/create-report.mjs
```

`resumen-general.html` incluye escenarios, parámetros, métricas, cumplimiento académico, thresholds, incidencias, señales de degradación y conclusiones solo de ejecuciones completas. No inventa datos; los escenarios sin JSON aparecen pendientes. Los reportes de preflight/verificación son informes operativos aparte, no se mezclan con la comparación de rendimiento/carga/estrés. HTML/JSON están excluidos de Git.

## Interpretación

Para rendimiento y carga, thresholds académicos provisionales: p95 `< 3.000 ms`, tasa de fallos HTTP `< 1 %` y checks HTTP 200 exitosos `> 99 %`. El JSON diferencia checks fallidos, incumplimientos de thresholds y estado de ejecución. También informa si todos los checks HTTP 200 pasaron, que es un criterio más estricto.

El estrés usa sus límites preventivos de aborto (10 % de fallos y p95 de 8.000 ms) separados de los criterios académicos. La evaluación de aborto empieza después del retraso de 15 s configurado; no requiere 15 s continuos de incumplimiento. Los resultados no se ocultan ni se convierten en aprobados al interrumpir. `vusMaxObserved` proviene de la métrica k6 `vus`; `configuredVusMax` indica el máximo planificado.

La API devuelve 404 en Gastos si la página está vacía: es un resultado funcional real y no debe corregirse insertando datos de producción. Render puede estar suspendido o iniciar en frío, y la latencia incluye red desde Codespaces; anota esas condiciones y límites del servicio.

## Detener y limpiar

Interrumpe k6 con `Ctrl+C`. `run --rm` elimina el contenedor al terminar. Para comprobar y limpiar el proyecto k6:

```bash
docker compose --env-file .env.k6 -f compose.k6.yaml ps
docker compose --env-file .env.k6 -f compose.k6.yaml down
unset AUTH_TOKEN
```

No usar `down -v`; no hay volúmenes de datos. Los reportes quedan en el repositorio local y pueden borrarse manualmente cuando ya no se necesiten. Estos comandos no afectan la API ni Aiven.

import { mkdir, readFile, writeFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const reportsDirectory = path.resolve(scriptDirectory, '../reports');
const expectedReports = [
  ['vehiculos-rendimiento', 'Vehículos', 'Rendimiento base'],
  ['vehiculos-carga-5vu', 'Vehículos', 'Carga: 5 VU'],
  ['vehiculos-carga-10vu', 'Vehículos', 'Carga: 10 VU'],
  ['vehiculos-estres-controlado', 'Vehículos', 'Estrés controlado'],
  ['gastos-rendimiento', 'Gastos', 'Rendimiento base'],
  ['gastos-carga-5vu', 'Gastos', 'Carga: 5 VU'],
  ['gastos-carga-10vu', 'Gastos', 'Carga: 10 VU'],
  ['gastos-estres-controlado', 'Gastos', 'Estrés controlado'],
];

const results = await Promise.all(expectedReports.map(async ([name, module, profile]) => {
  try {
    const contents = await readFile(path.join(reportsDirectory, `${name}.json`), 'utf8');
    return { name, module, profile, result: JSON.parse(contents) };
  } catch (error) {
    if (error.code === 'ENOENT') {
      return { name, module, profile, result: null };
    }
    throw error;
  }
}));

const rows = results.map(renderScenarioRow);
const conclusions = buildConclusions(results);
const document = `<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Resumen general de pruebas k6</title>
  <style>
    body{font:15px system-ui,sans-serif;margin:2rem;color:#202124}
    h1,h2{color:#1f4e79}
    .table-wrap{overflow-x:auto;margin:1rem 0 2rem}
    table{border-collapse:collapse;min-width:1400px;width:100%}
    th,td{border:1px solid #bbb;padding:.5rem;text-align:left;vertical-align:top}
    th{background:#e8eef5}
    .pending{color:#745c00}
    .failed{color:#9c1b1b}
  </style>
</head>
<body>
  <h1>Resumen general de pruebas k6</h1>
  <p>Generado ${new Date().toISOString()}. El informe solo incorpora resúmenes JSON
  presentes en <code>k6/reports/</code>; los escenarios no ejecutados aparecen como pendientes.
  Las ejecuciones canceladas, abortadas o incompletas no se presentan como completas.</p>
  <div class="table-wrap">
    <table>
      <thead><tr>
        <th>Módulo</th><th>Escenario</th><th>Estado de ejecución</th><th>Endpoint</th>
        <th>Parámetros</th><th>Solicitudes</th><th>Req/s</th><th>Promedio / mín. / máx.</th>
        <th>p95</th><th>Fallidas</th><th>VU configurados / máximo observado</th>
        <th>Checks HTTP 200</th><th>Criterios académicos</th><th>Thresholds k6</th>
        <th>Incidencias / degradación</th>
      </tr></thead>
      <tbody>${rows.join('\n')}</tbody>
    </table>
  </div>
  <h2>Comparaciones y conclusiones basadas en ejecuciones completas</h2>
  <ul>${conclusions}</ul>
  <p>Criterios académicos iniciales para rendimiento/carga: p95 &lt; 3.000 ms,
  solicitudes fallidas &lt; 1 % y más del 99 % de checks HTTP 200.
  En estrés se aplican límites de interrupción separados y más altos; su incumplimiento
  no se confunde con los criterios académicos.</p>
</body>
</html>
`;

await mkdir(reportsDirectory, { recursive: true });
await writeFile(path.join(reportsDirectory, 'resumen-general.html'), document);
console.log(`Reporte general generado: ${path.join(reportsDirectory, 'resumen-general.html')}`);

function renderScenarioRow({ module, profile, result }) {
  if (!result) {
    return `<tr class="pending"><td>${escapeHtml(module)}</td>` +
      `<td>${escapeHtml(profile)}</td><td>Pendiente</td><td colspan="13">` +
      `No existe resumen JSON: escenario no ejecutado.</td></tr>`;
  }

  const { test, metrics, criteria, thresholds, safetyLimits } = result;
  const status = runStatusLabel(test.runStatus);
  const parameters = [
    `t=${formatDuration(test.durationMs)} / objetivo ${formatDuration(test.expectedDurationMs)}`,
    `página=${escapeHtml(test.pageSize)}`,
    `pausa=${escapeHtml(test.iterationPauseSeconds)} s`,
    `etapa estrés=${test.stressStageSeconds ? `${escapeHtml(test.stressStageSeconds)} s` : 'N/A'}`,
  ].join('<br>');
  const failures = `${format(metrics.failedRequests)} (${formatPercent(metrics.failedRate)})`;
  const vuMetrics = `${format(test.configuredVusMax)} / ${format(metrics.vusMaxObserved)}`;
  const checks = `${format(metrics.checksPassed)} OK / ${format(metrics.checksFailed)} fallidos ` +
    `(${formatPercent(metrics.checksPassRate)})`;
  const incident = buildIncidentText(test, metrics, thresholds, safetyLimits);
  const rowClass = test.runStatus === 'completed' ? '' : ' class="failed"';

  return `<tr${rowClass}><td>${escapeHtml(module)}</td><td>${escapeHtml(profile)}</td>` +
    `<td>${status}<br>thresholds: ${thresholds.failed ? 'fallidos' : 'sin fallos registrados'}</td>` +
    `<td>${escapeHtml(test.endpoint)}</td><td>${parameters}</td>` +
    `<td>${format(metrics.httpRequestsTotal)}</td><td>${format(metrics.requestsPerSecond)}</td>` +
    `<td>${format(metrics.responseAvgMs)} / ${format(metrics.responseMinMs)} / ` +
    `${format(metrics.responseMaxMs)} ms</td><td>${format(metrics.responseP95Ms)} ms</td>` +
    `<td>${failures}</td><td>${vuMetrics}</td><td>${checks}</td>` +
    `<td>${formatAcademicCriteria(criteria)}</td><td>${formatThresholds(thresholds)}</td>` +
    `<td>${incident}</td></tr>`;
}

function buildIncidentText(test, metrics, thresholds, safetyLimits) {
  const incidents = [];
  if (metrics.failedRequests > 0) {
    incidents.push(`${format(metrics.failedRequests)} HTTP fallidas`);
  }
  if (test.runStatus !== 'completed') {
    incidents.push(`Ejecución ${runStatusLabel(test.runStatus).toLowerCase()}`);
  }
  if (thresholds.failed) {
    incidents.push('uno o más thresholds fallaron');
  }
  if (metrics.degradationOnsetSeconds !== null) {
    incidents.push(
      `primera señal a ${format(metrics.degradationOnsetSeconds)} s ` +
      `(${escapeHtml(metrics.degradationStage ?? 'etapa no determinada')})`,
    );
  } else if (test.profile === 'stress' && test.runStatus === 'completed') {
    incidents.push('sin señal registrada (HTTP no-200 o respuesta >= 3.000 ms)');
  }
  if (safetyLimits) {
    incidents.push(
      `límites de aborto: ${escapeHtml(safetyLimits.failedRateAbortThreshold)}; ` +
      `${escapeHtml(safetyLimits.p95AbortThreshold)}`,
    );
  }
  return incidents.length ? incidents.join('<br>') : 'Ninguna registrada';
}

function buildConclusions(allResults) {
  const byName = new Map(allResults.map((item) => [item.name, item.result]));
  const points = [];

  for (const module of ['vehiculos', 'gastos']) {
    const five = byName.get(`${module}-carga-5vu`);
    const ten = byName.get(`${module}-carga-10vu`);
    if (!isComplete(five) || !isComplete(ten)) {
      points.push(
        `${module}: comparación 5 vs. 10 VU pendiente; se requieren ambos escenarios completos.`,
      );
      continue;
    }

    points.push(
      `${module}: de 5 a 10 VU, el p95 cambió ` +
      `${formatChange(percentChange(five.metrics.responseP95Ms, ten.metrics.responseP95Ms))} y ` +
      `las solicitudes/s cambiaron ` +
      `${formatChange(percentChange(five.metrics.requestsPerSecond, ten.metrics.requestsPerSecond))}.`,
    );
  }

  for (const module of ['vehiculos', 'gastos']) {
    const stress = byName.get(`${module}-estres-controlado`);
    if (!stress) {
      points.push(`${module}: estrés controlado pendiente; no hay conclusión.`);
    } else if (stress.test.runStatus !== 'completed') {
      points.push(
        `${module}: estrés ${runStatusLabel(stress.test.runStatus).toLowerCase()}; ` +
        `no se infiere una conclusión de ejecución completa.`,
      );
    } else if (stress.metrics.degradationOnsetSeconds === null) {
      points.push(
        `${module}: no se registró señal de degradación según la definición configurada.`,
      );
    } else {
      points.push(
        `${module}: primera señal de degradación a ` +
        `${format(stress.metrics.degradationOnsetSeconds)} s ` +
        `(${escapeHtml(stress.metrics.degradationStage ?? 'etapa no determinada')}).`,
      );
    }
  }

  return points.map((point) => `<li>${point}</li>`).join('\n');
}

function formatAcademicCriteria(criteria) {
  if (!criteria) {
    return 'No aplican a este perfil';
  }
  return [
    criteriaLabel('p95 < 3.000 ms', criteria.p95Under3000Ms),
    criteriaLabel('fallos < 1 %', criteria.failedRateUnder1Percent),
    criteriaLabel('checks > 99 %', criteria.checksPassRateOver99Percent),
    criteriaLabel('todos HTTP 200', criteria.allHttp200ChecksPassed),
  ].join('<br>');
}

function formatThresholds(thresholds) {
  const values = Object.entries(thresholds.passed ?? {});
  if (!values.length) {
    return 'Sin thresholds registrados';
  }
  return values.map(([name, passed]) =>
    `${escapeHtml(name)}: ${passed ? 'cumple' : 'falla'}`,
  ).join('<br>');
}

function criteriaLabel(name, value) {
  return `${name}: ${value === null ? 'N/D' : value ? 'cumple' : 'no cumple'}`;
}

function runStatusLabel(status) {
  return ({
    completed: 'Completa',
    cancelled: 'Cancelada',
    aborted_by_threshold: 'Abortada por threshold',
    incomplete: 'Incompleta',
  })[status] ?? `Estado desconocido (${escapeHtml(status ?? 'N/D')})`;
}

function isComplete(result) {
  return result?.test?.runStatus === 'completed';
}

function formatDuration(milliseconds) {
  return Number.isFinite(milliseconds) ? `${(milliseconds / 1000).toFixed(1)} s` : 'N/D';
}

function format(value) {
  return Number.isFinite(value) ? value.toFixed(2) : 'N/D';
}

function formatPercent(value) {
  return Number.isFinite(value) ? `${(value * 100).toFixed(2)} %` : 'N/D';
}

function percentChange(before, after) {
  if (!Number.isFinite(before) || !Number.isFinite(after) || before === 0) {
    return null;
  }
  return ((after - before) / before) * 100;
}

function formatChange(value) {
  return value === null
    ? 'no calculable (métrica ausente o base 0)'
    : `${value.toFixed(1)} %`;
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (character) => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
  })[character]);
}

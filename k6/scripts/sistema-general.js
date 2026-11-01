import http from 'k6/http';
import execution from 'k6/execution';
import { check, sleep } from 'k6';
import { Counter, Gauge, Trend } from 'k6/metrics';

const baseUrl = __ENV.BASE_URL || '';
const authToken = __ENV.AUTH_TOKEN || '';
const reportName = __ENV.REPORT_NAME || 'sistema-general';
const pageSize = parseBoundedInteger('PAGE_SIZE', 20, 1, 50);
const iterationPauseSeconds = parsePause();
const stages = [
  { duration: '5s', target: 1 },
  { duration: '60s', target: 1 },
  { duration: '10s', target: 5 },
  { duration: '60s', target: 5 },
  { duration: '10s', target: 10 },
  { duration: '60s', target: 10 },
  { duration: '10s', target: 0 },
  { duration: '20s', target: 5 },
  { duration: '20s', target: 10 },
  { duration: '20s', target: 15 },
  { duration: '20s', target: 15 },
  { duration: '20s', target: 0 },
];
const moduleDefinitions = [
  { key: 'vehiculos', path: '/api/vehiculos' },
  { key: 'gastos', path: '/api/gastos' },
  { key: 'mantenimientos', path: '/api/mantenimientos' },
  { key: 'recordatorios', path: '/api/recordatorios' },
  { key: 'kilometrajes', path: '/api/kilometrajes' },
];
const detailedPhases = [
  { key: 'base_calentamiento', phaseTag: 'rendimiento_base', label: 'Calentamiento de rendimiento base', durationSeconds: 5, configuredVus: 1, academic: false },
  { key: 'rendimiento_base', phaseTag: 'rendimiento_base', label: 'Rendimiento base', durationSeconds: 60, configuredVus: 1, academic: true },
  { key: 'transicion_carga_moderada', phaseTag: 'carga_moderada', label: 'Transición a carga moderada', durationSeconds: 10, configuredVus: 5, academic: false },
  { key: 'carga_moderada', phaseTag: 'carga_moderada', label: 'Carga moderada', durationSeconds: 60, configuredVus: 5, academic: true },
  { key: 'transicion_carga_elevada', phaseTag: 'carga_elevada', label: 'Transición a carga elevada', durationSeconds: 10, configuredVus: 10, academic: false },
  { key: 'carga_elevada', phaseTag: 'carga_elevada', label: 'Carga elevada', durationSeconds: 60, configuredVus: 10, academic: true },
  { key: 'transicion_inicio_estres', phaseTag: 'transicion_estres', label: 'Transición a estrés y reducción a 0 VU', durationSeconds: 10, configuredVus: 0, academic: false },
  { key: 'estres_incremento_5vu', phaseTag: 'estres_controlado', label: 'Estrés: incremento hasta 5 VU', durationSeconds: 20, configuredVus: 5, academic: false },
  { key: 'estres_incremento_10vu', phaseTag: 'estres_controlado', label: 'Estrés: incremento hasta 10 VU', durationSeconds: 20, configuredVus: 10, academic: false },
  { key: 'estres_incremento_15vu', phaseTag: 'estres_controlado', label: 'Estrés: incremento hasta 15 VU', durationSeconds: 20, configuredVus: 15, academic: false },
  { key: 'estres_mantenimiento_15vu', phaseTag: 'estres_controlado', label: 'Estrés: mantenimiento de 15 VU', durationSeconds: 20, configuredVus: 15, academic: false },
  { key: 'estres_reduccion', phaseTag: 'estres_controlado', label: 'Estrés: reducción de carga', durationSeconds: 20, configuredVus: 0, academic: false },
];

if (!/^https:\/\/[a-z0-9.-]+(?::\d+)?\/?$/i.test(baseUrl)) {
  throw new Error('BASE_URL debe ser el origen HTTPS de la API, sin ruta ni parámetros.');
}

if (!/^[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$/.test(authToken)) {
  throw new Error('AUTH_TOKEN debe ser un JWT compacto suministrado externamente.');
}

if (!/^[a-z0-9][a-z0-9-]*$/.test(reportName)) {
  throw new Error('REPORT_NAME solo puede contener minúsculas, números y guiones.');
}

const origin = baseUrl.replace(/\/$/, '');
const phaseOffsets = calculateOffsets(detailedPhases);
const customMetrics = createCustomMetrics();
const aggregatePhaseMetrics = createPhaseMetrics();
const academicThresholds = {};
const preventiveThresholds = {};

for (const phase of detailedPhases.filter((item) => item.academic)) {
  academicThresholds[`http_req_duration{phase:${phase.key}}`] = ['p(95)<3000'];
  academicThresholds[`http_req_failed{phase:${phase.key}}`] = ['rate<0.01'];
  academicThresholds[`checks{phase:${phase.key}}`] = ['rate>0.99'];
}

for (const guardrail of [
  { phaseGroup: 'rendimiento_base', delay: '15s' },
  { phaseGroup: 'carga_moderada', delay: '70s' },
  { phaseGroup: 'carga_elevada', delay: '140s' },
]) {
  preventiveThresholds[`http_req_failed{phase_group:${guardrail.phaseGroup}}`] = [{
    threshold: 'rate<0.05',
    abortOnFail: true,
    delayAbortEval: guardrail.delay,
  }];
  preventiveThresholds[`http_req_duration{phase_group:${guardrail.phaseGroup}}`] = [{
    threshold: 'p(95)<8000',
    abortOnFail: true,
    delayAbortEval: guardrail.delay,
  }];
}

preventiveThresholds['http_req_failed{phase_group:estres_controlado}'] = [{
  threshold: 'rate<0.10',
  abortOnFail: true,
  delayAbortEval: '230s',
}];
preventiveThresholds['http_req_duration{phase_group:estres_controlado}'] = [{
  threshold: 'p(95)<8000',
  abortOnFail: true,
  delayAbortEval: '230s',
}];

export const options = {
  discardResponseBodies: true,
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'count'],
  scenarios: {
    sistema: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages,
      gracefulRampDown: '10s',
    },
  },
  thresholds: {
    ...academicThresholds,
    ...preventiveThresholds,
  },
};

http.setResponseCallback(http.expectedStatuses(200));

export default function () {
  const elapsedSeconds = execution.instance.currentTestRunDuration / 1000;
  const phase = phaseAt(elapsedSeconds);
  const moduleIndex = (__VU + __ITER - 1) % moduleDefinitions.length;
  const module = moduleDefinitions[moduleIndex];
  const url = `${origin}${module.path}?page=0&size=${pageSize}`;
  const tags = {
    module: module.key,
    phase: phase.key,
    phase_group: phase.phaseTag,
    phase_detail: phase.key,
  };
  const response = http.get(url, {
    headers: {
      Authorization: `Bearer ${authToken}`,
      Accept: 'application/json',
    },
    timeout: '10s',
    tags,
  });
  const metricSet = customMetrics[`${module.key}:${phase.key}`];
  const phaseMetrics = aggregatePhaseMetrics[phase.key];
  const successful = response.status === 200;
  const checkPassed = check(response, {
    'respuesta HTTP 200': (res) => res.status === 200,
  }, tags);
  recordMetrics(metricSet, response, successful, checkPassed);
  recordMetrics(phaseMetrics, response, successful, checkPassed);

  sleep(iterationPauseSeconds);
}

export function handleSummary(data) {
  const metrics = {
    httpRequestsTotal: value(data, 'http_reqs', 'count') ?? 0,
    requestsPerSecond: value(data, 'http_reqs', 'rate') ?? 0,
    responseAvgMs: value(data, 'http_req_duration', 'avg') ?? null,
    responseMinMs: value(data, 'http_req_duration', 'min') ?? null,
    responseMaxMs: value(data, 'http_req_duration', 'max') ?? null,
    responseP95Ms: value(data, 'http_req_duration', 'p(95)') ?? null,
    failedRequests: value(data, 'http_req_failed', 'passes') ??
      Math.round(
        (value(data, 'http_reqs', 'count') ?? 0) *
        (value(data, 'http_req_failed', 'rate') ?? 0),
      ),
    failedRate: value(data, 'http_req_failed', 'rate') ?? 0,
    checksPassed: value(data, 'checks', 'passes') ?? 0,
    checksFailed: value(data, 'checks', 'fails') ?? 0,
    checksPassRate: value(data, 'checks', 'rate') ?? 0,
    vusMaxConfigured: 15,
    vusMaxObserved: value(data, 'vus', 'max') ?? 0,
  };
  const phases = detailedPhases.map((phase) =>
    buildPhaseSummary(data, phase, aggregatePhaseMetrics[phase.key]),
  );
  const moduleSummaries = moduleDefinitions.map((module) => ({
    module: module.key,
    endpoints: `${origin}${module.path}?page=0&size=${pageSize}`,
    phases: phases.map((phase) => ({
      phase: phase.key,
      label: phase.label,
      startSeconds: phase.startSeconds,
      durationSeconds: phase.durationSeconds,
      configuredVus: phase.configuredVus,
      metrics: summarizeMetricSet(
        data,
        customMetrics[`${module.key}:${phase.key}`],
        phase.durationSeconds,
      ),
    })),
  }));
  const thresholds = summarizeThresholds(data.metrics);
  const report = {
    test: {
      name: 'Sistema general Control Vehicular',
      reportName,
      generatedAt: new Date().toISOString(),
      runStatus: getRunStatus(data, phases, thresholds),
      pageSize,
      iterationPauseSeconds,
      moduleSelection: 'Round-robin por VU e iteración; una solicitud GET por iteración.',
      maxConcurrentVusConfigured: 15,
      totalScheduledDurationSeconds: phaseOffsets.totalDurationSeconds,
      phaseDefinitions: phases,
      httpMethods: ['GET'],
    },
    global: metrics,
    modules: moduleSummaries,
    academicCriteria: {
      appliesTo: ['rendimiento_base', 'carga_moderada', 'carga_elevada'],
      p95Under3000Ms: '< 3000 ms',
      failedRateUnder1Percent: '< 1%',
      checksPassRateOver99Percent: '> 99%',
      resultsByPhase: phases
        .filter((phase) => phase.academic)
        .map((phase) => phase.criteria),
    },
    stressSafetyLimits: {
      appliesTo: 'estres_controlado',
      failedRateAbortThreshold: 'rate < 10%',
      p95AbortThreshold: 'p(95) < 8000 ms',
      evaluationBeginsAt: '230s desde el inicio; la fase estres_controlado empieza aproximadamente en 215s',
      delayAbortEvalMeaning: '230s retrasa el inicio de la evaluación; no exige incumplimiento continuo',
    },
    preventiveSafetyLimits: {
      phases: [
        { phaseGroup: 'rendimiento_base', failedRate: '< 5%', p95: '< 8000 ms', evaluationBeginsAt: '15s' },
        { phaseGroup: 'carga_moderada', failedRate: '< 5%', p95: '< 8000 ms', evaluationBeginsAt: '70s' },
        { phaseGroup: 'carga_elevada', failedRate: '< 5%', p95: '< 8000 ms', evaluationBeginsAt: '140s' },
      ],
      meaning: 'Evaluaciones preventivas para abortar antes de aumentar la carga.',
    },
    thresholds,
  };

  return {
    stdout: JSON.stringify(report, null, 2),
    [`/reports/${reportName}.json`]: JSON.stringify(report, null, 2),
  };
}

function createCustomMetrics() {
  const result = {};
  for (const module of moduleDefinitions) {
    for (const phase of detailedPhases) {
      const suffix = `${module.key}_${phase.key}`;
      result[`${module.key}:${phase.key}`] = createMetricSet(`general_${suffix}`);
    }
  }
  return result;
}

function createPhaseMetrics() {
  return Object.fromEntries(
    detailedPhases.map((phase) => [
      phase.key,
      createMetricSet(`general_phase_${phase.key}`),
    ]),
  );
}

function createMetricSet(prefix) {
  const names = {
    failed: `${prefix}_failed`,
    checksFailed: `${prefix}_checks_failed`,
    duration: `${prefix}_duration`,
    activeVus: `${prefix}_active_vus`,
  };
  return {
    names,
    failed: new Counter(names.failed),
    checksFailed: new Counter(names.checksFailed),
    duration: new Trend(names.duration, true),
    activeVus: new Gauge(names.activeVus),
  };
}

function recordMetrics(metricSet, response, successful, checkPassed) {
  metricSet.duration.add(response.timings.duration);
  metricSet.activeVus.add(execution.instance.vusActive);
  if (!successful) {
    metricSet.failed.add(1);
  }
  if (!checkPassed) {
    metricSet.checksFailed.add(1);
  }
}

function calculateOffsets(phases) {
  let startSeconds = 0;
  return {
    phases: phases.map((phase) => {
      const interval = {
        ...phase,
        startSeconds,
        endSeconds: startSeconds + phase.durationSeconds,
      };
      startSeconds = interval.endSeconds;
      return interval;
    }),
    totalDurationSeconds: startSeconds,
  };
}

function phaseAt(elapsedSeconds) {
  return phaseOffsets.phases.find((phase) => elapsedSeconds < phase.endSeconds) ??
    phaseOffsets.phases[phaseOffsets.phases.length - 1];
}

function buildPhaseSummary(data, phase, phaseMetrics) {
  const metrics = summarizeMetricSet(
    data,
    phaseMetrics,
    phase.durationSeconds,
  );
  return {
    phase: phase.key,
    label: phase.label,
    startSeconds: phase.startSeconds,
    endSeconds: phase.endSeconds,
    durationSeconds: phase.durationSeconds,
    configuredVus: phase.configuredVus,
    academicCriteriaApplied: phase.academic,
    metrics,
    criteria: phase.academic
      ? {
          samplesAvailable: metrics.httpRequestsTotal > 0,
          p95Under3000Ms: metrics.httpRequestsTotal === 0 || metrics.responseP95Ms === null
            ? null
            : metrics.responseP95Ms < 3000,
          failedRateUnder1Percent: metrics.httpRequestsTotal === 0
            ? null
            : metrics.failedRate < 0.01,
          checksPassRateOver99Percent: metrics.httpRequestsTotal === 0
            ? null
            : metrics.checksPassRate > 0.99,
          allHttp200ChecksPassed: metrics.httpRequestsTotal === 0
            ? null
            : metrics.checksFailed === 0,
        }
      : null,
  };
}

function summarizeMetricSet(data, metricSet, phaseDurationSeconds) {
  const requests = metricValue(data, metricSet.names.duration, 'count') ?? 0;
  const failed = metricValue(data, metricSet.names.failed, 'count') ?? 0;
  const checksFailed = metricValue(data, metricSet.names.checksFailed, 'count') ?? 0;
  return {
    httpRequestsTotal: requests,
    requestsPerSecond: phaseDurationSeconds > 0 ? requests / phaseDurationSeconds : 0,
    responseAvgMs: metricValue(data, metricSet.names.duration, 'avg'),
    responseMinMs: metricValue(data, metricSet.names.duration, 'min'),
    responseMaxMs: metricValue(data, metricSet.names.duration, 'max'),
    responseP95Ms: metricValue(data, metricSet.names.duration, 'p(95)'),
    failedRequests: failed,
    failedRate: requests > 0 ? failed / requests : null,
    checksPassed: requests - checksFailed,
    checksFailed,
    checksPassRate: requests > 0 ? (requests - checksFailed) / requests : null,
    vusMaxObservedOnModuleIterations:
      metricValue(data, metricSet.names.activeVus, 'max') ?? 0,
  };
}

function metricValue(data, metricName, statistic) {
  return data.metrics[metricName]?.values?.[statistic] ?? null;
}

function value(data, metricName, statistic) {
  return data.metrics[metricName]?.values?.[statistic] ?? null;
}

function summarizeThresholds(metrics) {
  const result = {};
  for (const [metricName, metric] of Object.entries(metrics)) {
    for (const [threshold, outcome] of Object.entries(metric.thresholds ?? {})) {
      result[`${metricName}: ${threshold}`] = outcome.ok;
    }
  }
  return result;
}

function getRunStatus(data, phases, thresholds) {
  if (data.state?.isInterrupted) {
    return 'cancelled';
  }
  if (data.state?.isAborted) {
    return 'aborted_by_threshold';
  }
  const stressThresholdFailed = Object.entries(thresholds).some(
    ([name, passed]) => name.includes('phase_group:estres_controlado') && !passed,
  );
  if (stressThresholdFailed) {
    return 'aborted_by_threshold';
  }
  const expectedDuration = phaseOffsets.totalDurationSeconds;
  const durationMs = data.state?.testRunDurationMs ?? 0;
  if (durationMs < expectedDuration * 1000 * 0.95) {
    return 'incomplete';
  }
  const academicPhasesMissingRequests = phases.some(
    (phase) => phase.academicCriteriaApplied && phase.metrics.httpRequestsTotal === 0,
  );
  return academicPhasesMissingRequests ? 'incomplete' : 'completed';
}

function parseBoundedInteger(name, fallback, min, max) {
  const rawValue = __ENV[name];
  const parsedValue = rawValue === undefined || rawValue === ''
    ? fallback
    : Number(rawValue);
  if (!Number.isInteger(parsedValue) || parsedValue < min || parsedValue > max) {
    throw new Error(`${name} debe ser un entero entre ${min} y ${max}.`);
  }
  return parsedValue;
}

function parsePause() {
  const pause = Number(__ENV.ITERATION_PAUSE_SECONDS || '1');
  if (!Number.isFinite(pause) || pause <= 0 || pause > 30) {
    throw new Error('ITERATION_PAUSE_SECONDS debe ser mayor que 0 y no superar 30.');
  }
  return pause;
}

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import execution from 'k6/execution';

const moduleName = __ENV.TEST_MODULE || 'vehiculos';
const profile = __ENV.TEST_PROFILE || 'baseline';
const reportName = __ENV.REPORT_NAME || '';
const baseUrl = __ENV.BASE_URL || '';
const authToken = __ENV.AUTH_TOKEN || '';
const pageSize = parseBoundedInteger('PAGE_SIZE', 20, 1, 50);
const durationSeconds = parseBoundedInteger(
  'TEST_DURATION_SECONDS',
  60,
  1,
  3600,
);
const loadVus = parseBoundedInteger('LOAD_VUS', 5, 1, 10);
const stressStageSeconds = parseBoundedInteger(
  'STRESS_STAGE_SECONDS',
  30,
  10,
  120,
);
const iterationPauseSeconds = parsePause();
const degradationOnset = new Trend('degradation_onset_seconds', true);
const degradationSignals = new Counter('degradation_signals');

const endpoints = {
  vehiculos: '/api/vehiculos',
  gastos: '/api/gastos',
};

if (!(moduleName in endpoints)) {
  throw new Error('TEST_MODULE debe ser vehiculos o gastos.');
}

if (!['baseline', 'load', 'stress', 'verify'].includes(profile)) {
  throw new Error('TEST_PROFILE debe ser baseline, load, stress o verify.');
}

if (!/^[a-z0-9][a-z0-9-]*$/.test(reportName)) {
  throw new Error('REPORT_NAME solo puede contener minúsculas, números y guiones.');
}

if (!/^[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$/.test(authToken)) {
  throw new Error('AUTH_TOKEN debe ser un JWT compacto suministrado externamente.');
}

if (!/^https:\/\/[a-z0-9.-]+(?::\d+)?\/?$/i.test(baseUrl)) {
  throw new Error('BASE_URL debe ser el origen HTTPS de la API, sin ruta ni parámetros.');
}

const apiOrigin = baseUrl.replace(/\/$/, '');
const endpoint = `${apiOrigin}${endpoints[moduleName]}?page=0&size=${pageSize}`;
const stressStageTargets = [5, 5, 10, 10, 15, 15, 0];
const stressStages = stressStageTargets.map((target) => ({
  duration: `${stressStageSeconds}s`,
  target,
}));

const scenario = profile === 'verify'
  ? {
      executor: 'per-vu-iterations',
      vus: 1,
      iterations: 1,
      maxDuration: '30s',
    }
  : profile === 'baseline'
    ? {
        executor: 'constant-vus',
        vus: 1,
        duration: `${durationSeconds}s`,
      }
    : profile === 'load'
      ? {
        executor: 'constant-vus',
        vus: validateLoadVus(loadVus),
        duration: `${durationSeconds}s`,
      }
      : {
        executor: 'ramping-vus',
        startVUs: 0,
        stages: stressStages,
        gracefulRampDown: '10s',
      };

const thresholds = profile === 'stress'
  ? {
      http_req_failed: [{
        threshold: 'rate<0.10',
        abortOnFail: true,
        delayAbortEval: '15s',
      }],
      http_req_duration: [{
        threshold: 'p(95)<8000',
        abortOnFail: true,
        delayAbortEval: '15s',
      }],
    }
  : profile === 'verify'
      ? {
          checks: ['rate==1'],
        }
      : {
          http_req_failed: ['rate<0.01'],
          http_req_duration: ['p(95)<3000'],
          checks: ['rate>0.99'],
        };

export const options = {
  discardResponseBodies: true,
  scenarios: {
    api: scenario,
  },
  thresholds,
  tags: {
    module: moduleName,
    profile,
  },
};

http.setResponseCallback(http.expectedStatuses(200));

export default function () {
  const response = http.get(endpoint, {
    headers: {
      Authorization: `Bearer ${authToken}`,
      Accept: 'application/json',
    },
    timeout: '10s',
    tags: {
      endpoint: moduleName,
    },
  });

  check(response, {
    'respuesta HTTP 200': (res) => res.status === 200,
  });

  if (
    profile === 'stress' &&
    (response.status !== 200 || response.timings.duration >= 3000)
  ) {
    const elapsedSeconds = execution.instance.currentTestRunDuration / 1000;
    const stage = getStressStage(elapsedSeconds);
    degradationOnset.add(elapsedSeconds);
    degradationSignals.add(1, { stage });
  }

  if (profile !== 'verify') {
    sleep(iterationPauseSeconds);
  }
}

export function handleSummary(data) {
  const reqs = metricValues(data, 'http_reqs');
  const duration = metricValues(data, 'http_req_duration');
  const failures = metricValues(data, 'http_req_failed');
  const checks = metricValues(data, 'checks');
  const vus = metricValues(data, 'vus');
  const degradation = metricValues(data, 'degradation_onset_seconds');
  const requestCount = reqs.count ?? 0;
  const failureRate = failures.rate ?? 0;
  const p95 = duration['p(95)'] ?? null;
  const degradationSeconds = degradation.min ?? null;
  const durationMs = data.state?.testRunDurationMs ?? null;
  const expectedDurationMs = getExpectedDurationMs();
  const thresholdsResult = summarizeThresholds(data.metrics);
  const runStatus = getRunStatus(
    data,
    durationMs,
    expectedDurationMs,
    profile,
    Object.values(thresholdsResult).some((threshold) => threshold === false),
  );
  const configuredVusMax = getConfiguredVusMax();
  const result = {
    test: {
      module: moduleName,
      profile,
      endpoint,
      reportName,
      generatedAt: new Date().toISOString(),
      durationMs,
      expectedDurationMs,
      runStatus,
      pageSize,
      iterationPauseSeconds: profile === 'verify' ? 0 : iterationPauseSeconds,
      configuredVusMax,
      stressStageSeconds: profile === 'stress' ? stressStageSeconds : null,
    },
    metrics: {
      httpRequestsTotal: requestCount,
      requestsPerSecond: reqs.rate ?? 0,
      responseAvgMs: duration.avg ?? null,
      responseMinMs: duration.min ?? null,
      responseMaxMs: duration.max ?? null,
      responseP95Ms: p95,
      failedRequests: failures.passes ?? Math.round(requestCount * failureRate),
      failedRate: failureRate,
      vusMaxObserved: vus.max ?? 0,
      checksPassed: checks.passes ?? 0,
      checksFailed: checks.fails ?? 0,
      checksPassRate: checks.rate ?? 0,
      degradationOnsetSeconds: degradationSeconds,
      degradationStage: degradationSeconds === null
        ? null
        : getStressStage(degradationSeconds),
      degradationSignals: metricValues(data, 'degradation_signals').count ?? 0,
    },
    criteria: profile === 'baseline' || profile === 'load'
      ? {
          p95Under3000Ms: p95 === null ? null : p95 < 3000,
          failedRateUnder1Percent: failureRate < 0.01,
          checksPassRateOver99Percent: (checks.rate ?? 0) > 0.99,
          allHttp200ChecksPassed: (checks.fails ?? 0) === 0,
        }
      : null,
    safetyLimits: profile === 'stress'
      ? {
          failedRateAbortThreshold: 'rate >= 10%; abort evaluation starts after 15s',
          p95AbortThreshold: 'p95 >= 8000ms; abort evaluation starts after 15s',
          delayAbortEvalMeaning: '15s retrasa el inicio de evaluación; no exige incumplimiento continuo',
          degradationSignal: 'HTTP distinto de 200 o duración >= 3000ms',
        }
      : null,
    thresholds: {
      passed: thresholdsResult,
      failed: Object.values(thresholdsResult).some((threshold) => threshold === false),
    },
  };

  return {
    stdout: JSON.stringify(result, null, 2),
    [`/reports/${reportName}.json`]: JSON.stringify(result, null, 2),
  };
}

function parseBoundedInteger(name, fallback, min, max) {
  const rawValue = __ENV[name];
  const value = rawValue === undefined || rawValue === ''
    ? fallback
    : Number(rawValue);

  if (!Number.isInteger(value) || value < min || value > max) {
    throw new Error(`${name} debe ser un entero entre ${min} y ${max}.`);
  }

  return value;
}

function parsePause() {
  const value = Number(__ENV.ITERATION_PAUSE_SECONDS || '1');
  if (!Number.isFinite(value) || value <= 0 || value > 30) {
    throw new Error('ITERATION_PAUSE_SECONDS debe ser mayor que 0 y no superar 30.');
  }
  return value;
}

function validateLoadVus(value) {
  if (value !== 5 && value !== 10) {
    throw new Error('LOAD_VUS solo admite 5 o 10 usuarios virtuales.');
  }
  return value;
}

function getConfiguredVusMax() {
  if (profile === 'baseline' || profile === 'verify') {
    return 1;
  }
  return profile === 'load' ? loadVus : 15;
}

function getExpectedDurationMs() {
  if (profile === 'verify') {
    return null;
  }
  if (profile === 'stress') {
    return stressStageSeconds * stressStageTargets.length * 1000;
  }
  return durationSeconds * 1000;
}

function getRunStatus(data, durationMs, expectedDurationMs, testProfile, thresholdFailed) {
  if (data.state?.isInterrupted) {
    return 'cancelled';
  }
  if (testProfile === 'stress' && thresholdFailed) {
    return 'aborted_by_threshold';
  }
  const durationShort = expectedDurationMs !== null &&
    (durationMs === null || durationMs < expectedDurationMs * 0.95);
  if (data.state?.isAborted || (thresholdFailed && durationShort)) {
    return 'aborted_by_threshold';
  }
  if (durationShort) {
    return 'incomplete';
  }
  return 'completed';
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

function metricValues(data, name) {
  return data.metrics[name]?.values ?? {};
}

function getStressStage(elapsedSeconds) {
  const stageSeconds = stressStageSeconds;
  const stages = [
    'incremento 0 a 5 VU',
    'mantenimiento 5 VU',
    'incremento 5 a 10 VU',
    'mantenimiento 10 VU',
    'incremento 10 a 15 VU',
    'mantenimiento 15 VU',
    'reduccion de carga',
  ];
  return stages[Math.min(Math.floor(elapsedSeconds / stageSeconds), stages.length - 1)];
}

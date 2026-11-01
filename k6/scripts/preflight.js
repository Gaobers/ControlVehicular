import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

const baseUrl = __ENV.BASE_URL || '';
const authToken = __ENV.AUTH_TOKEN || '';
const reportName = __ENV.REPORT_NAME || 'preflight-endpoints';
const pageSize = Number(__ENV.PAGE_SIZE || '20');
const endpoints = {
  vehiculos: `${baseUrl.replace(/\/$/, '')}/api/vehiculos?page=0&size=${pageSize}`,
  gastos: `${baseUrl.replace(/\/$/, '')}/api/gastos?page=0&size=${pageSize}`,
};
const vehicleStatus = new Trend('preflight_vehiculos_http_status');
const expenseStatus = new Trend('preflight_gastos_http_status');

if (!/^https:\/\/[a-z0-9.-]+(?::\d+)?\/?$/i.test(baseUrl)) {
  throw new Error('BASE_URL debe ser el origen HTTPS de la API, sin ruta ni parámetros.');
}

if (!/^[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$/.test(authToken)) {
  throw new Error('AUTH_TOKEN debe ser un JWT compacto suministrado externamente.');
}

if (!Number.isInteger(pageSize) || pageSize < 1 || pageSize > 50) {
  throw new Error('PAGE_SIZE debe ser un entero entre 1 y 50.');
}

if (!/^[a-z0-9][a-z0-9-]*$/.test(reportName)) {
  throw new Error('REPORT_NAME solo puede contener minúsculas, números y guiones.');
}

export const options = {
  discardResponseBodies: true,
  scenarios: {
    preflight: {
      executor: 'per-vu-iterations',
      vus: 1,
      iterations: 1,
      maxDuration: '30s',
    },
  },
  thresholds: {
    checks: ['rate==1'],
  },
};

http.setResponseCallback(http.expectedStatuses(200));

export default function () {
  const headers = {
    Authorization: `Bearer ${authToken}`,
    Accept: 'application/json',
  };
  const vehicles = http.get(endpoints.vehiculos, {
    headers,
    timeout: '10s',
    tags: { endpoint: 'vehiculos', test_type: 'preflight' },
  });
  vehicleStatus.add(vehicles.status);
  check(vehicles, {
    'vehiculos HTTP 200 (JWT autorizado)': (response) => response.status === 200,
  });

  const expenses = http.get(endpoints.gastos, {
    headers,
    timeout: '10s',
    tags: { endpoint: 'gastos', test_type: 'preflight' },
  });
  expenseStatus.add(expenses.status);
  check(expenses, {
    'gastos HTTP 200 (JWT autorizado)': (response) => response.status === 200,
  });
}

export function handleSummary(data) {
  const vehicles = metricValues(data, 'preflight_vehiculos_http_status');
  const expenses = metricValues(data, 'preflight_gastos_http_status');
  const checks = metricValues(data, 'checks');
  const vehiclesStatus = vehicles.min ?? null;
  const expensesStatus = expenses.min ?? null;
  const vehiclesAuthorized = vehiclesStatus === 200;
  const expensesAuthorized = expensesStatus === 200 || expensesStatus === 404;
  const requests = metricValues(data, 'http_reqs').count ?? 0;
  const result = {
    test: {
      type: 'preflight',
      profile: 'one_vu_one_iteration',
      generatedAt: new Date().toISOString(),
      pageSize,
      configuredVusMax: 1,
      iterationsPerVu: 1,
      requestsExpected: 2,
      runStatus: data.state?.isInterrupted
        ? 'cancelled'
        : data.state?.isAborted
          ? 'aborted_by_threshold'
          : requests !== 2
            ? 'incomplete'
            : (checks.fails ?? 0) === 0
              ? 'completed'
              : 'completed_with_http_failures',
    },
    endpoints: {
      vehiculos: {
        url: endpoints.vehiculos,
        httpStatus: vehiclesStatus,
        returned200: vehiclesStatus === 200 && vehicles.max === 200,
        jwtPermissionConfirmed: vehiclesAuthorized && vehicles.max === 200,
        interpretation: vehiclesAuthorized
          ? 'HTTP 200: JWT autorizado'
          : 'No se confirmó acceso con el JWT',
      },
      gastos: {
        url: endpoints.gastos,
        httpStatus: expensesStatus,
        returned200: expensesStatus === 200 && expenses.max === 200,
        jwtPermissionConfirmed:
          expensesAuthorized && (expenses.max === 200 || expenses.max === 404),
        interpretation: expensesStatus === 200
          ? 'HTTP 200: JWT autorizado'
          : expensesStatus === 404
            ? 'HTTP 404 del controlador de Gastos: acceso concedido, página sin contenido'
            : 'No se confirmó acceso con el JWT',
      },
    },
    metrics: {
      httpRequestsTotal: requests,
      failedRate: metricValues(data, 'http_req_failed').rate ?? 0,
      checksPassed: checks.passes ?? 0,
      checksFailed: checks.fails ?? 0,
      checksPassRate: checks.rate ?? 0,
      vusMaxObserved: metricValues(data, 'vus').max ?? 0,
    },
    criteria: {
      bothEndpointsReturned200:
        vehiclesStatus === 200 &&
        vehicles.max === 200 &&
        expensesStatus === 200 &&
        expenses.max === 200,
      jwtAuthorizedForBothEndpoints:
        vehiclesAuthorized &&
        vehicles.max === 200 &&
        expensesAuthorized &&
        (expenses.max === 200 || expenses.max === 404),
    },
    thresholds: {
      passed: summarizeThresholds(data.metrics),
    },
  };

  return {
    stdout: JSON.stringify(result, null, 2),
    [`/reports/${reportName}.json`]: JSON.stringify(result, null, 2),
  };
}

function metricValues(data, name) {
  return data.metrics[name]?.values ?? {};
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

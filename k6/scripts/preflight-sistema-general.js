import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

const baseUrl = __ENV.BASE_URL || '';
const authToken = __ENV.AUTH_TOKEN || '';
const reportName = __ENV.REPORT_NAME || 'preflight-sistema-general';
const pageSize = Number(__ENV.PAGE_SIZE || '20');
const modules = [
  { key: 'vehiculos', path: '/api/vehiculos', emptyListMayReturn404: false },
  { key: 'gastos', path: '/api/gastos', emptyListMayReturn404: true },
  { key: 'mantenimientos', path: '/api/mantenimientos', emptyListMayReturn404: true },
  { key: 'recordatorios', path: '/api/recordatorios', emptyListMayReturn404: true },
  { key: 'kilometrajes', path: '/api/kilometrajes', emptyListMayReturn404: true },
];
const statusMetrics = Object.fromEntries(
  modules.map((module) => [
    module.key,
    new Trend(`preflight_general_status_${module.key}`),
  ]),
);

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
      maxDuration: '60s',
    },
  },
  thresholds: {
    checks: ['rate==1'],
  },
};

http.setResponseCallback(http.expectedStatuses(200));

export default function () {
  for (const module of modules) {
    const url = `${baseUrl.replace(/\/$/, '')}${module.path}?page=0&size=${pageSize}`;
    const response = http.get(url, {
      headers: {
        Authorization: `Bearer ${authToken}`,
        Accept: 'application/json',
      },
      timeout: '10s',
      tags: { module: module.key, phase: 'preflight' },
    });
    statusMetrics[module.key].add(response.status);
    check(response, {
      [`${module.key} HTTP 200`]: (res) => res.status === 200,
    }, { module: module.key, phase: 'preflight' });
  }
}

export function handleSummary(data) {
  const resultModules = modules.map((module) => {
    const values = data.metrics[`preflight_general_status_${module.key}`]?.values ?? {};
    const status = values.min ?? null;
    const returned200 = status === 200 && values.max === 200;
    const emptyResult404 = module.emptyListMayReturn404 &&
      status === 404 &&
      values.max === 404;
    return {
      module: module.key,
      endpoint: `${baseUrl.replace(/\/$/, '')}${module.path}?page=0&size=${pageSize}`,
      httpStatus: status,
      returned200,
      jwtPermissionConfirmed: returned200 || emptyResult404,
      emptyListMayReturn404: module.emptyListMayReturn404,
      result: returned200
        ? 'HTTP 200: endpoint accesible y lista consultable'
        : emptyResult404
          ? 'HTTP 404: probable lista vacía; no cumple el requisito HTTP 200'
          : 'Respuesta distinta de HTTP 200/404 esperado; revisar autenticación, ruta o servicio',
    };
  });
  const checks = data.metrics.checks?.values ?? {};
  const requestCount = data.metrics.http_reqs?.values?.count ?? 0;
  const report = {
    test: {
      name: 'Preflight sistema general',
      reportName,
      generatedAt: new Date().toISOString(),
      runStatus: data.state?.isInterrupted
        ? 'cancelled'
        : requestCount !== modules.length
          ? 'incomplete'
          : (checks.fails ?? 0) === 0
            ? 'completed'
            : 'completed_with_http_failures',
      configuredVus: 1,
      iterations: 1,
      expectedRequests: modules.length,
      requests: requestCount,
    },
    modules: resultModules,
    checks: {
      passed: checks.passes ?? 0,
      failed: checks.fails ?? 0,
      passRate: checks.rate ?? 0,
    },
    allJwtPermissionsConfirmed: resultModules.every(
      (module) => module.jwtPermissionConfirmed,
    ),
    allEndpointsReturned200: resultModules.every((module) => module.returned200),
  };
  return {
    stdout: JSON.stringify(report, null, 2),
    [`/reports/${reportName}.json`]: JSON.stringify(report, null, 2),
  };
}

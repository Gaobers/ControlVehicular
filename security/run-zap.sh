#!/usr/bin/env bash
set -euo pipefail

api_base="http://api:8080"

docs_ready=false
for ((attempt = 0; attempt < 60; attempt++)); do
  if curl -fsS "$api_base/v3/api-docs" -o /dev/null; then
    docs_ready=true
    break
  fi
  sleep 2
done

if [[ "$docs_ready" != true ]]; then
  printf '%s\n' 'La API local no respondió en /v3/api-docs.' >&2
  exit 1
fi

curl -sS -o /dev/null -X POST \
  -H 'Content-Type: application/json' \
  -d "{\"nombreCompleto\":\"Usuario de pruebas ZAP\",\"telefono\":\"0000000000\",\"correo\":\"${SECURITY_TEST_EMAIL}\",\"clave\":\"${SECURITY_TEST_PASSWORD}\"}" \
  "$api_base/api/auth/registro" || true

token=$(curl -fsS -X POST \
  -H 'Content-Type: application/json' \
  -d "{\"correo\":\"${SECURITY_TEST_EMAIL}\",\"clave\":\"${SECURITY_TEST_PASSWORD}\"}" \
  "$api_base/api/auth/login" \
  | sed -n 's/.*"token"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p')

if [[ -z "$token" ]]; then
  printf '%s\n' 'El login local no devolvió un JWT.' >&2
  exit 1
fi

mkdir -p /zap/wrk/reports
zap_config="-config replacer.full_list(0).description=authorization -config replacer.full_list(0).enabled=true -config replacer.full_list(0).matchtype=REQ_HEADER -config replacer.full_list(0).matchstr=Authorization -config replacer.full_list(0).regex=false -config replacer.full_list(0).replacement=Bearer\\ ${token}"

exec /zap/zap-api-scan.py \
  -t "$api_base/v3/api-docs" \
  -f openapi \
  -S \
  -r /zap/wrk/reports/zap-report.html \
  -J /zap/wrk/reports/zap-report.json \
  -z "$zap_config"

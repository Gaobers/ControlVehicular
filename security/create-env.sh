#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

if [[ -e .env.security ]]; then
  printf '%s\n' '.env.security ya existe; no se modificó.'
  exit 1
fi

command -v openssl >/dev/null || {
  printf '%s\n' 'Se requiere openssl para generar los valores locales.' >&2
  exit 1
}

database_password=$(openssl rand -hex 24)
root_password=$(openssl rand -hex 24)
jwt_secret=$(openssl rand -base64 48 | tr -d '\n')
test_password=$(openssl rand -hex 16)

umask 077
{
  printf 'SECURITY_DB_NAME=controlvehicular_security\n'
  printf 'SECURITY_DB_USER=security_app\n'
  printf 'SECURITY_DB_PASSWORD=%s\n' "$database_password"
  printf 'SECURITY_DB_ROOT_PASSWORD=%s\n' "$root_password"
  printf 'SECURITY_JWT_SECRET=%s\n' "$jwt_secret"
  printf 'SECURITY_TEST_EMAIL=zap-test@example.invalid\n'
  printf 'SECURITY_TEST_PASSWORD=%s\n' "$test_password"
} > .env.security
chmod 600 .env.security

printf '%s\n' 'Se creó .env.security con permisos locales restringidos; Git ya ignora .env.*.'
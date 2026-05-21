#!/usr/bin/env zsh

set -euo pipefail

audience="${1:-cart-service}"
subject="${2:-dev-client}"
scope="${3:-test}"
issuer="${JWT_ISSUER:-shopping-platform-local}"
secret="${JWT_SECRET:-shopping-platform-local-signing-secret-2026}"
ttl_seconds="${JWT_TTL_SECONDS:-300}"

base64url() {
  openssl base64 -A | tr '+/' '-_' | tr -d '='
}

header='{"alg":"HS256","typ":"JWT"}'
issued_at="$(date +%s)"
expires_at="$((issued_at + ttl_seconds))"
payload=$(printf '{"iss":"%s","sub":"%s","aud":["%s"],"scope":"%s","iat":%s,"exp":%s}' \
  "$issuer" "$subject" "$audience" "$scope" "$issued_at" "$expires_at")

encoded_header="$(printf '%s' "$header" | base64url)"
encoded_payload="$(printf '%s' "$payload" | base64url)"
signing_input="${encoded_header}.${encoded_payload}"
signature="$(printf '%s' "$signing_input" | openssl dgst -binary -sha256 -hmac "$secret" | base64url)"

printf '%s\n' "${signing_input}.${signature}"
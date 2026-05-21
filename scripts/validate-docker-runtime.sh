#!/usr/bin/env zsh

set -euo pipefail

for url in \
  http://localhost:18081/v3/api-docs \
  http://localhost:18082/v3/api-docs \
  http://localhost:18080/v3/api-docs
do
  echo "Checking $url"
  curl --fail --silent --show-error "$url" > /dev/null
done

echo "All microservices are reachable."
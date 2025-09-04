#!/usr/bin/env bash
set -euo pipefail

MVN="./mvnw"; [ -x "$MVN" ] || MVN="mvn"
SKIP_TESTS=${SKIP_TESTS:-true}

if [ "$SKIP_TESTS" = "true" ]; then
  "$MVN" -q -DskipTests clean package
else
  "$MVN" -q clean package
fi

# Verifica que el jar exista (lo genera el <finalName>app</finalName>)
[ -f target/app.jar ] || { echo "ERROR: target/app.jar no existe. ¿Tienes <finalName>app</finalName> en el pom?"; exit 1; }

docker compose build api
docker compose up -d

echo
echo "✅ API lista en: http://localhost:8080"
echo "📜 Logs: docker compose logs -f api"
#!/usr/bin/env bash
set -euo pipefail

# 1) Construir el JAR del backend (usa el wrapper)
cd backend/notes-backend
./mvnw -q -DskipTests clean package
cd - >/dev/null

# 2) Levantar todo con Docker Compose (DB + API + Front)
docker compose up --build

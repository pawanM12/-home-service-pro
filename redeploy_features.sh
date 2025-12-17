#!/usr/bin/env bash
set -euo pipefail

# 1. Clean Docker system (Optional, but good for a fresh start)
echo "Pruning Docker system..."
docker system prune -af

# 2. Build all services using Maven
# We build locally first to ensure the artifacts are ready for the docker build context
services=(config-server gateway-service customer-service job-service serviceprovider-service eureka-server)
for svc in "${services[@]}"; do
  echo "Building $svc..."
  (cd "$svc" && mvn clean package -DskipTests)
done

# 3. Start services using Docker Compose
echo "Starting services with Docker Compose..."
# --build ensures we pick up the changes we just built (though the dockerfile likely copies the jar)
docker compose up -d --build

echo "Waiting for services to initialize..."
# Simple wait to let things spin up
sleep 10

echo "Checking running containers..."
docker compose ps

echo "All services redeployed."
echo "Eureka Dashboard: http://localhost:8761"
echo "Gateway: http://localhost:8080"
echo "To view logs: docker compose logs -f"

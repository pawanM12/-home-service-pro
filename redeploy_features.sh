#!/usr/bin/env bash
set -euo pipefail

# 1. Clean Docker images and caches
echo "Pruning Docker system..."
docker system prune -af

# 2. Build all services
services=(config-server gateway-service customer-service job-service serviceprovider-service)
for svc in "${services[@]}"; do
  echo "Building $svc..."
  (cd "$svc" && mvn clean package -DskipTests)
  docker build -t home-service-pro-${svc}:latest "./${svc}"
done

# 3. Delete existing K8s resources (ignore errors if not present)
kubectl delete deployment,svc -l app=config-server || true
kubectl delete deployment,svc -l app=gateway-service || true
kubectl delete deployment,svc -l app=customer-service || true
kubectl delete deployment,svc -l app=job-service || true
kubectl delete deployment,svc -l app=serviceprovider-service || true
kubectl delete deployment,svc -l app=eureka-server || true
kubectl delete configmap config-repo || true

# 4. Re‑apply ConfigMap and deployments
# Note: Using platform.yaml (Config+Eureka), services.yaml (Biz Services), and gateway.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/platform.yaml

# Wait for Config Server to be ready
echo "Waiting for Config Server to be ready..."
until kubectl get pod -l app=config-server -o jsonpath='{.items[0].status.phase}' | grep -q Running; do 
  echo "Waiting for config-server..."
  sleep 5
done

kubectl apply -f k8s/services.yaml
kubectl apply -f k8s/gateway.yaml

echo "All services redeployed. Use 'kubectl port-forward svc/gateway-service 8080:8080' to access the UI."

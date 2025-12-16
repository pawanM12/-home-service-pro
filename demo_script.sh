#!/bin/bash

echo "Skipping direct Eureka check (Port 8761 not exposed in K8s)..."
# In K8s, we only port-forward the Gateway (8080).
# STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8761)


echo "\n1. Creating Service Provider..."
curl -X POST http://localhost:8080/api/providers \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice Plumber", "email": "alice@example.com", "serviceType": "PLUMBING", "hourlyRate": 50.0}'
echo "\n"

echo "\n2. Booking a Job (PLUMBING)..."
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"customerId": 1, "serviceType": "PLUMBING", "description": "Fix leaking tap", "scheduledTime": "2024-12-25T10:00:00"}'
echo "\n"

echo "\n3. Listing Pending Jobs for PLUMBING..."
curl "http://localhost:8080/api/jobs/pending/PLUMBING"
echo "\n"

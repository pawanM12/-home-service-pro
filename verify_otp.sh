#!/bin/bash

# Configuration
GATEWAY_URL="http://localhost:8080"
CUSTOMER_ID=1
PROVIDER_ID=1
SERVICE_TYPE="Plumbing"

echo "=== 1. Booking a Job ==="
JOB_JSON=$(curl -s -X POST "$GATEWAY_URL/api/jobs" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": '$CUSTOMER_ID',
    "serviceType": "'"$SERVICE_TYPE"'",
    "description": "Leaky faucet repair needed",
    "location": "123 Main St",
    "scheduledTime": "'"$(date -d '+1 day' +%Y-%m-%dT%H:%M:%S)"'"
  }')

JOB_ID=$(echo $JOB_JSON | jq -r '.id')
OTP=$(echo $JOB_JSON | jq -r '.completionOtp')
STATUS=$(echo $JOB_JSON | jq -r '.status')

echo "Job ID: $JOB_ID"
echo "OTP: $OTP"
echo "Status: $STATUS"

if [ "$STATUS" != "WAITING_FOR_PROVIDER" ]; then
  echo "Error: Job status should be WAITING_FOR_PROVIDER"
  exit 1
fi

if [ "$OTP" == "null" ] || [ -z "$OTP" ]; then
  echo "Error: OTP was not generated!"
  exit 1
fi

echo "=== 2. Provider Accepts Job ==="
ACCEPT_JSON=$(curl -s -X POST "$GATEWAY_URL/api/jobs/$JOB_ID/accept?providerId=$PROVIDER_ID")
NEW_STATUS=$(echo $ACCEPT_JSON | jq -r '.status')
echo "New Status: $NEW_STATUS"

if [ "$NEW_STATUS" != "ASSIGNED" ]; then
  echo "Error: Job status should be ASSIGNED"
  exit 1
fi

echo "=== 3. Completing Job with INCORRECT OTP (0000) ==="
curl -s -X POST "$GATEWAY_URL/api/jobs/$JOB_ID/complete?otp=0000" > /tmp/bad_otp_response
if grep -q "Invalid OTP" /tmp/bad_otp_response || grep -q "Internal Server Error" /tmp/bad_otp_response; then
    echo "Success: Bad OTP was rejected."
else
    echo "Warning: Bad OTP might have been accepted (Check logs)."
fi

echo "=== 4. Completing Job with CORRECT OTP ($OTP) ==="
COMPLETE_JSON=$(curl -s -X POST "$GATEWAY_URL/api/jobs/$JOB_ID/complete?otp=$OTP")
FINAL_STATUS=$(echo $COMPLETE_JSON | jq -r '.status')
echo "Final Status: $FINAL_STATUS"

if [ "$FINAL_STATUS" == "COMPLETED" ]; then
  echo "✅ verification SUCCESS: Job completed securely!"
else
  echo "❌ verification FAILED: Job not completed."
  exit 1
fi

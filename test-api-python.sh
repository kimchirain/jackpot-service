#!/bin/bash

echo "==================================="
echo "Jackpot Service Test Script"
echo "==================================="
echo ""

BASE_URL="http://localhost:8080/api/jackpot"

# Function to print colored output
print_step() {
    echo ""
    echo ">>> $1"
    echo "-----------------------------------"
}

# Check if service is running
print_step "1. Checking if service is running..."
curl -s ${BASE_URL}/health
echo ""

# Place multiple bets
print_step "2. Placing bets..."

echo "Bet 1 (Fixed Jackpot):"
curl -X POST ${BASE_URL}/bets \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "BET-001",
    "userId": "USER-123",
    "jackpotId": 1,
    "betAmount": 100.00
  }'
echo ""

sleep 1

echo "Bet 2 (Variable Jackpot):"
curl -X POST ${BASE_URL}/bets \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "BET-002",
    "userId": "USER-456",
    "jackpotId": 2,
    "betAmount": 150.00
  }'
echo ""

sleep 1

echo "Bet 3 (Fixed Jackpot):"
curl -X POST ${BASE_URL}/bets \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "BET-003",
    "userId": "USER-789",
    "jackpotId": 1,
    "betAmount": 200.00
  }'
echo ""

# Wait for processing
print_step "3. Waiting for bet processing..."
sleep 2

# Check rewards
print_step "4. Checking rewards..."

echo "Checking BET-001:"
curl -s ${BASE_URL}/rewards/BET-001 | python3 -m json.tool 2>/dev/null || curl -s ${BASE_URL}/rewards/BET-001
echo ""

echo "Checking BET-002:"
curl -s ${BASE_URL}/rewards/BET-002 | python3 -m json.tool 2>/dev/null || curl -s ${BASE_URL}/rewards/BET-002
echo ""

echo "Checking BET-003:"
curl -s ${BASE_URL}/rewards/BET-003 | python3 -m json.tool 2>/dev/null || curl -s ${BASE_URL}/rewards/BET-003
echo ""

print_step "5. Test complete!"
echo "Check H2 console at: http://localhost:8080/h2-console"
echo "JDBC URL: jdbc:h2:mem:jackpotdb"
echo "Username: sa"
echo "Password: (leave blank)"
echo ""

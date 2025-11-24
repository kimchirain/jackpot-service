#!/bin/bash

# Simple API test script for Jackpot Service
# No dependencies required - just bash and curl

echo "================================"
echo "Jackpot Service API Test"
echo "================================"
echo ""

BASE_URL="http://localhost:8080/api/jackpot"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Test counter
PASSED=0
FAILED=0

# Function to test endpoint
test_endpoint() {
    local name=$1
    local method=$2
    local url=$3
    local data=$4
    
    echo -n "Testing: $name... "
    
    if [ "$method" = "POST" ]; then
        response=$(curl -s -w "\n%{http_code}" -X POST "$url" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" "$url")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n-1)
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}PASSED${NC} (HTTP $http_code)"
        PASSED=$((PASSED + 1))
    else
        echo -e "${RED}FAILED${NC} (HTTP $http_code)"
        echo "Response: $body"
        FAILED=$((FAILED + 1))
    fi
}

# Wait for service to be ready
echo "Checking if service is running..."
for i in {1..10}; do
    if curl -s "$BASE_URL/health" > /dev/null 2>&1; then
        echo -e "${GREEN}Service is ready!${NC}"
        echo ""
        break
    fi
    if [ $i -eq 10 ]; then
        echo -e "${RED}Service not responding. Please start the application first.${NC}"
        echo "Run: mvn spring-boot:run"
        exit 1
    fi
    echo "Waiting for service... ($i/10)"
    sleep 2
done

# Run tests
echo "Running API tests..."
echo ""

# Test 1: Health check
test_endpoint "Health Check" "GET" "$BASE_URL/health"

# Test 2: Place bet on Fixed jackpot
test_endpoint "Place Bet (Fixed Jackpot)" "POST" "$BASE_URL/bets" \
    '{"betId":"TEST-001","userId":"USER-123","jackpotId":1,"betAmount":100.00}'

# Test 3: Place bet on Variable jackpot
test_endpoint "Place Bet (Variable Jackpot)" "POST" "$BASE_URL/bets" \
    '{"betId":"TEST-002","userId":"USER-456","jackpotId":2,"betAmount":50.00}'

# Give time for async processing
echo ""
echo "Waiting 2 seconds for async processing..."
sleep 2

# Test 4: Check reward for first bet
test_endpoint "Check Reward (TEST-001)" "GET" "$BASE_URL/rewards/TEST-001"

# Test 5: Check reward for second bet
test_endpoint "Check Reward (TEST-002)" "GET" "$BASE_URL/rewards/TEST-002"

# Test 6: Invalid bet (missing fields)
echo -n "Testing: Invalid Bet (should fail)... "
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/bets" \
    -H "Content-Type: application/json" \
    -d '{"betId":"TEST-003"}')
http_code=$(echo "$response" | tail -n1)
if [ "$http_code" = "400" ]; then
    echo -e "${GREEN}PASSED${NC} (HTTP $http_code - validation working)"
    PASSED=$((PASSED + 1))
else
    echo -e "${RED}FAILED${NC} (Expected 400, got $http_code)"
    FAILED=$((FAILED + 1))
fi

# Summary
echo ""
echo "================================"
echo "Test Summary"
echo "================================"
echo -e "Passed: ${GREEN}$PASSED${NC}"
echo -e "Failed: ${RED}$FAILED${NC}"
echo "Total: $((PASSED + FAILED))"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed! ✓${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed! ✗${NC}"
    exit 1
fi

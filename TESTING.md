# Testing Guide

## Quick Start

**Step 1:** Start the application
```bash
mvn spring-boot:run
```

**Step 2:** Run the test script for your OS

**Linux/Mac:**
```bash
chmod +x test-api.sh
./test-api.sh
```

**Windows (Command Prompt):**
```cmd
test-api.bat
```

## What Gets Tested

All scripts run 6 tests:
1. Health check
2. Place bet on Fixed jackpot
3. Place bet on Variable jackpot
4. Check if first bet won
5. Check if second bet won
6. Invalid bet (tests validation)

## Expected Result

```
Testing: Health Check... PASSED (HTTP 200)
Testing: Place Bet (Fixed Jackpot)... PASSED (HTTP 200)
Testing: Place Bet (Variable Jackpot)... PASSED (HTTP 200)
Testing: Check Reward (TEST-001)... PASSED (HTTP 200)
Testing: Check Reward (TEST-002)... PASSED (HTTP 200)
Testing: Invalid Bet (should fail)... PASSED (HTTP 400)

Passed: 6
Failed: 0
```

## Manual Testing

If you prefer to test manually with curl:

```bash
# Health check
curl http://localhost:8080/api/jackpot/health

# Place a bet
curl -X POST http://localhost:8080/api/jackpot/bets \
  -H "Content-Type: application/json" \
  -d '{"betId":"BET-001","userId":"USER-123","jackpotId":1,"betAmount":100.00}'

# Check reward
curl http://localhost:8080/api/jackpot/rewards/BET-001
```

## Troubleshooting

**All tests fail**
- Check if app is running on port 8080
- Try health check manually: `curl http://localhost:8080/api/jackpot/health`

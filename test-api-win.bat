@echo off
REM Simple API test script for Jackpot Service (Windows)
REM Requires: curl (included in Windows 10+)

echo ================================
echo Jackpot Service API Test
echo ================================
echo.

set BASE_URL=http://localhost:8080/api/jackpot
set PASSED=0
set FAILED=0

REM Check if service is running
echo Checking if service is running...
curl -s %BASE_URL%/health >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Service not responding. Please start the application first.
    echo Run: mvn spring-boot:run
    exit /b 1
)
echo [OK] Service is ready!
echo.

echo Running API tests...
echo.

REM Test 1: Health check
echo [TEST 1] Health Check...
curl -s -w "%%{http_code}" -o response.txt %BASE_URL%/health > status.txt
set /p STATUS=<status.txt
if "%STATUS%"=="200" (
    echo [PASSED] Health Check ^(HTTP %STATUS%^)
    set /a PASSED+=1
) else (
    echo [FAILED] Health Check ^(HTTP %STATUS%^)
    set /a FAILED+=1
)
echo.

REM Test 2: Place bet on Fixed jackpot
echo [TEST 2] Place Bet ^(Fixed Jackpot^)...
curl -s -w "%%{http_code}" -o response.txt -X POST %BASE_URL%/bets ^
    -H "Content-Type: application/json" ^
    -d "{\"betId\":\"TEST-001\",\"userId\":\"USER-123\",\"jackpotId\":1,\"betAmount\":100.00}" > status.txt
set /p STATUS=<status.txt
if "%STATUS%"=="200" (
    echo [PASSED] Place Bet Fixed ^(HTTP %STATUS%^)
    set /a PASSED+=1
) else (
    echo [FAILED] Place Bet Fixed ^(HTTP %STATUS%^)
    set /a FAILED+=1
)
echo.

REM Test 3: Place bet on Variable jackpot
echo [TEST 3] Place Bet ^(Variable Jackpot^)...
curl -s -w "%%{http_code}" -o response.txt -X POST %BASE_URL%/bets ^
    -H "Content-Type: application/json" ^
    -d "{\"betId\":\"TEST-002\",\"userId\":\"USER-456\",\"jackpotId\":2,\"betAmount\":50.00}" > status.txt
set /p STATUS=<status.txt
if "%STATUS%"=="200" (
    echo [PASSED] Place Bet Variable ^(HTTP %STATUS%^)
    set /a PASSED+=1
) else (
    echo [FAILED] Place Bet Variable ^(HTTP %STATUS%^)
    set /a FAILED+=1
)
echo.

REM Wait for async processing
echo Waiting 2 seconds for async processing...
timeout /t 2 /nobreak >nul
echo.

REM Test 4: Check reward for first bet
echo [TEST 4] Check Reward ^(TEST-001^)...
curl -s -w "%%{http_code}" -o response.txt %BASE_URL%/rewards/TEST-001 > status.txt
set /p STATUS=<status.txt
if "%STATUS%"=="200" (
    echo [PASSED] Check Reward TEST-001 ^(HTTP %STATUS%^)
    set /a PASSED+=1
) else (
    echo [FAILED] Check Reward TEST-001 ^(HTTP %STATUS%^)
    set /a FAILED+=1
)
echo.

REM Test 5: Check reward for second bet
echo [TEST 5] Check Reward ^(TEST-002^)...
curl -s -w "%%{http_code}" -o response.txt %BASE_URL%/rewards/TEST-002 > status.txt
set /p STATUS=<status.txt
if "%STATUS%"=="200" (
    echo [PASSED] Check Reward TEST-002 ^(HTTP %STATUS%^)
    set /a PASSED+=1
) else (
    echo [FAILED] Check Reward TEST-002 ^(HTTP %STATUS%^)
    set /a FAILED+=1
)
echo.

REM Test 6: Invalid bet (should fail)
echo [TEST 6] Invalid Bet ^(should fail with 400^)...
curl -s -w "%%{http_code}" -o response.txt -X POST %BASE_URL%/bets ^
    -H "Content-Type: application/json" ^
    -d "{\"betId\":\"TEST-003\"}" > status.txt
set /p STATUS=<status.txt
if "%STATUS%"=="400" (
    echo [PASSED] Invalid Bet ^(HTTP %STATUS% - validation working^)
    set /a PASSED+=1
) else (
    echo [FAILED] Invalid Bet ^(Expected 400, got %STATUS%^)
    set /a FAILED+=1
)
echo.

REM Cleanup
del response.txt status.txt 2>nul

REM Summary
echo ================================
echo Test Summary
echo ================================
echo Passed: %PASSED%
echo Failed: %FAILED%
set /a TOTAL=%PASSED%+%FAILED%
echo Total: %TOTAL%
echo.

if %FAILED% EQU 0 (
    echo [SUCCESS] All tests passed!
    exit /b 0
) else (
    echo [ERROR] Some tests failed!
    exit /b 1
)

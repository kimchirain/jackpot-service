# Jackpot Service - Backend Assignment

A Spring Boot application for managing jackpot contributions and rewards in betting systems.

## Requirements

- **Java 17+** (tested with Java 17)
- **Maven 3.6+**

## Quick Start

### 1. Build & Run (Default - Mock Kafka Mode)

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```

The application starts on **http://localhost:8080** with mock Kafka (no external dependencies needed).

### 2. Test It Works

```bash
# Health check
curl http://localhost:8080/api/jackpot/health

# Place a bet
curl -X POST http://localhost:8080/api/jackpot/bets \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "BET-001",
    "userId": "USER-123",
    "jackpotId": 1,
    "betAmount": 100.00
  }'

# Check reward
curl http://localhost:8080/api/jackpot/rewards/BET-001
```

## Dependencies

### Core
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Kafka
- H2 Database (in-memory)
- Bean Validation

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/jackpot/health` | Health check |
| POST | `/api/jackpot/bets` | Submit a bet |
| GET | `/api/jackpot/rewards/{betId}` | Check if bet won |

## Configuration

**Mock Kafka (default):**
```properties
jackpot.mock.kafka=true
```

## Database Console

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:jackpotdb`
- Username: `sa`
- Password: (blank)

## Pre-loaded Jackpots

**Jackpot 1 (Fixed):**
- Pool: $10,000
- Contribution: 5% fixed
- Win chance: 1%

**Jackpot 2 (Variable):**
- Pool: $5,000
- Contribution: 10% (decreases)
- Win chance: 0.1% → 100% at $50k

## Project Structure

```
src/main/java/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── entity/         # JPA entities
├── dto/            # Data transfer (records)
├── strategy/       # Strategy pattern
└── config/         # Configuration
```

## Technologies

- **Java 17**
- **Spring Boot 3.2**
- **Kafka** for event-driven architecture
- **JPA/Hibernate** for persistence
- **H2** in-memory database

## Time Investment

~90 minutes focused on:
- Clean architecture
- Production patterns
- Clear documentation

---

**Developed for Backend Engineer technical assessment**

# Restful Booker API Test Automation

Automated REST API tests for the [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html) platform using REST Assured and Java 21.

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Maven | 3.x | Build & dependency management |
| REST Assured | 5.5.6 | HTTP client & API testing |
| TestNG | 7.11.0 | Test framework |
| AssertJ | 3.27.3 | Fluent assertions |
| Jackson | 2.20.0 | JSON serialization / deserialization |

## Project Structure

```
src
├── main/java/com/example/booker
│   ├── client
│   │   └── BookerClient.java          # REST Assured HTTP client
│   ├── config
│   │   └── ApiConfig.java             # Environment-based configuration
│   └── model
│       ├── AuthRequest.java           # Token auth request body
│       ├── AuthResponse.java          # Token auth response body
│       ├── BookingDates.java          # Check-in / check-out dates
│       ├── BookingIdItem.java         # Booking ID wrapper
│       ├── BookingRequest.java        # Create / update booking payload
│       ├── BookingResponse.java       # Booking response payload
│       ├── CreatedBookingResponse.java
│       └── PartialBookingUpdateRequest.java
└── test/java/com/example/booker
    └── tests
        └── BookerApiTest.java         # End-to-end API tests
```

## API Coverage

| Endpoint | Method | Test |
|---|---|---|
| `/ping` | GET | Health check returns 201 |
| `/auth` | POST | Token creation returns valid token |
| `/booking` | GET | Lists booking IDs |
| `/booking/{id}` | GET | Retrieves a single booking |
| `/booking` | POST | Creates a new booking |
| `/booking/{id}` | PUT | Full update of an existing booking |
| `/booking/{id}` | PATCH | Partial update of an existing booking |
| `/booking/{id}` | DELETE | Deletes a booking and verifies 404 |

## Prerequisites

- Java 21+
- Maven 3.x

## Configuration

API credentials are resolved in the following priority order:

1. **System properties** (`-Dbooker.username=...`)
2. **Environment variables** (`BOOKER_USERNAME=...`)

Create a `.env` file in the project root (already git-ignored):

```
BOOKER_BASE_URL=https://restful-booker.herokuapp.com
BOOKER_USERNAME=admin
BOOKER_PASSWORD=password123
```

## Running Tests

```bash
# Using environment variables
source .env && export BOOKER_USERNAME BOOKER_PASSWORD && mvn clean test

# Using system properties
mvn clean test -Dbooker.username=admin -Dbooker.password=password123
```

## License

This project is for educational and testing purposes only.

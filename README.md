# BCI - Bank Customer Information Microservice

Spring Boot 2.5.14 microservice for user registration and authentication with JWT token support and H2 in-memory database.

---

## 📋 Table of Contents

1. [Quick Start](#quick-start)
2. [Prerequisites](#prerequisites)
3. [Project Structure](#project-structure)
4. [Building the Project](#building-the-project)
5. [Running the Project](#running-the-project)
6. [API Endpoints](#api-endpoints)
7. [Testing](#testing)
8. [Architecture Documentation](#architecture-documentation)
9. [Technology Stack](#technology-stack)
10. [Troubleshooting](#troubleshooting)

---

## 🚀 Quick Start

```bash
# Clone the repository
git clone https://github.com/UIngSoft3/prueba_globallogic_bci.git
cd bci

# Build the project
./gradlew clean build

# Run the application
./gradlew bootRun

# Application is ready at http://localhost:8080
```

---

## 📋 Prerequisites

- **Java 11 LTS** or higher
- **Gradle 8.9** (wrapper included - no need to install)
- **Git** (for cloning the repository)
- **No external database required** (H2 in-memory database included)

---

## 📁 Project Structure

```
bci/
├── src/
│   ├── main/
│   │   ├── java/com/globallogic/bci/
│   │   │   ├── BciApplication.java                 # Application entry point
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java             # Spring Security configuration
│   │   │   ├── controller/
│   │   │   │   └── UserController.java             # REST API endpoints (/sign-up, /login)
│   │   │   ├── service/
│   │   │   │   └── UserService.java                # Business logic for users
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java             # JPA repository for data access
│   │   │   ├── entity/
│   │   │   │   ├── User.java                       # User entity (JPA)
│   │   │   │   └── Phone.java                      # Phone entity (JPA)
│   │   │   ├── dto/
│   │   │   │   ├── SignUpRequest.java              # Request DTO for /sign-up
│   │   │   │   ├── UserResponse.java               # Response DTO for users
│   │   │   │   ├── PhoneDto.java                   # Phone data transfer object
│   │   │   │   └── ErrorResponse.java              # Error response DTO
│   │   │   ├── exception/
│   │   │   │   ├── UserAlreadyExistsException.java # Custom exception (422)
│   │   │   │   ├── UserNotFoundException.java       # Custom exception (404)
│   │   │   │   ├── InvalidCredentialsException.java # Custom exception (401)
│   │   │   │   ├── BadRequestException.java        # Custom exception (400)
│   │   │   │   └── GlobalExceptionHandler.java     # Centralized exception handling
│   │   │   └── util/
│   │   │       ├── ValidationUtil.java             # Email and password validation
│   │   │       ├── PasswordEncryptor.java          # BCrypt password encryption
│   │   │       └── JwtTokenProvider.java           # JWT token generation/validation
│   │   └── resources/
│   │       ├── application.properties              # Application configuration
│   │       └── docs/                               # Documentation folder
│   │           ├── README.md                       # Documentation index
│   │           ├── COMPONENT_DIAGRAM.md            # Component architecture reference
│   │           ├── component_diagram.puml          # UML Component Diagram (PlantUML)
│   │           ├── SEQUENCE_DIAGRAM.md             # Sequence diagrams reference
│   │           ├── sequence_signup.puml            # UML Sequence Diagram for /sign-up (PlantUML)
│   │           ├── sequence_login.puml             # UML Sequence Diagram for /login (PlantUML)
│   │           ├── entity_class_diagram.puml       # UML Class Diagram (PlantUML)
│   │           └── postman_test/
│   │               ├── POSTMAN_COLLECTION_README.md # 12 test cases documentation
│   │               ├── BCI_Collection.postman_collection.json
│   │               ├── BCI_Environment.postman_environment.json
│   │               └── BCI_Test_Cases.postman_collection.json
│   └── test/
│       └── groovy/com/globallogic/bci/
│           └── BciApplicationTests.groovy          # Application tests
├── gradle/
│   └── wrapper/                                   # Gradle wrapper files
├── build.gradle                                   # Gradle build configuration
├── settings.gradle                                # Gradle project settings
├── README.md                                      # This file (Main documentation)
└── build/                                         # Generated build artifacts (not committed)
```

---

## 🔨 Building the Project

### Step 1: Clone the Repository
```bash
git clone https://github.com/UIngSoft3/prueba_globallogic_bci.git
cd bci
```

### Step 2: Build with Gradle

**Full build with tests:**
```bash
./gradlew clean build
```

**Build without tests (faster):**
```bash
./gradlew clean build -x test
```

**View Gradle version:**
```bash
./gradlew --version
```

### Expected Output
```
BUILD SUCCESSFUL in Xs
X actionable tasks: X executed
```

---

## ▶️ Running the Project

### Start the Application
```bash
./gradlew bootRun
```

### Application Startup Confirmation
You should see output similar to:
```
2026-01-05 21:30:55.450  INFO 27040 --- [main] com.globallogic.bci.BciApplication : 
Starting BciApplication v0.0.1-SNAPSHOT on HP-PC
2026-01-05 21:30:55.450  INFO 27040 --- [main] com.globallogic.bci.BciApplication : 
Started BciApplication in 4.139 seconds (JVM running for 4.52)
```

### Application Ready
- **Base URL**: `http://localhost:8080`
- **Database**: H2 in-memory database (auto-created on startup)
- **Port**: 8080

---

## 🔌 API Endpoints

### 1. User Registration - POST `/sign-up`

Register a new user with email, password, and optional phone numbers.

**Endpoint**: `POST http://localhost:8080/sign-up`  
**Content-Type**: `application/json`

#### Request Body
```json
{
  "name": "Test User",
  "email": "juan.perez@example.com",
  "password": "Pass123word",
  "phones": [
    {
      "number": 1234567890,
      "citycode": 1,
      "contrycode": "+1"
    }
  ]
}
```

#### Validation Rules

**Email:**
- Must follow standard email format: `^[A-Za-z0-9+_.-]+@(.+)$`
- Must be unique (not already registered)
- Example: ✅ `juan.perez@example.com`, ❌ `invalid.email`

**Password:**
- Length: 8-12 characters (required)
- Must contain EXACTLY ONE uppercase letter (e.g., P, A, Z)
- Must contain EXACTLY TWO digits, non-consecutive (e.g., 1, 23)
- Only lowercase letters, digits, and uppercase letters allowed
- Examples:
  - ✅ `Pass123word` (P=uppercase, 1,2=digits, lowercase letters)
  - ❌ `pass123` (no uppercase letter)
  - ❌ `Pass12word` (only one digit)
  - ❌ `Pass1Pass2` (two uppercase letters)

**Name:**
- Optional (string)

**Phones:**
- Optional (array of phone objects)
- Each phone contains: number (long), citycode (int), contrycode (string)

#### Success Response (HTTP 201 Created)
```json
{
  "id": "e5c6cf84-8860-4c00-91cd-22d3be28904e",
  "created": "Jan 05, 2026 09:30:55 PM",
  "lastLogin": "Jan 05, 2026 09:30:55 PM",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuLnBlcmV6QGV4YW1wbGUuY29tIiwi...",
  "isActive": true,
  "name": "Test User",
  "email": "juan.perez@example.com",
  "phones": [
    {
      "number": 1234567890,
      "citycode": 1,
      "contrycode": "+1"
    }
  ]
}
```

#### Error Responses

**400 Bad Request** - Invalid email format
```json
{
  "error": [
    {
      "timestamp": "2026-01-05T21:30:55.765",
      "codigo": 400,
      "detail": "Invalid email format"
    }
  ]
}
```

**400 Bad Request** - Invalid password format
```json
{
  "error": [
    {
      "timestamp": "2026-01-05T21:30:55.765",
      "codigo": 400,
      "detail": "Password must be 8-12 characters with 1 uppercase, 2 digits, and lowercase letters"
    }
  ]
}
```

**422 Unprocessable Entity** - User already exists
```json
{
  "error": [
    {
      "timestamp": "2026-01-05T21:30:55.765",
      "codigo": 422,
      "detail": "User with email juan.perez@example.com already exists"
    }
  ]
}
```

---

### 2. User Login - GET `/login`

Authenticate using JWT token from previous `/sign-up` response.

**Endpoint**: `GET http://localhost:8080/login`  
**Content-Type**: `application/json`

#### Headers
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuLnBlcmV6QGV4YW1wbGUuY29tIiwi...
Content-Type: application/json
```

#### Query Parameter
- `token` (required): JWT token received from `/sign-up` endpoint

#### Example Request
```bash
curl -X GET "http://localhost:8080/login?token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuLnBlcmV6..." \
  -H "Content-Type: application/json"
```

#### Success Response (HTTP 200 OK)
```json
{
  "id": "e5c6cf84-8860-4c00-91cd-22d3be28904e",
  "created": "Jan 05, 2026 09:30:55 PM",
  "lastLogin": "Jan 05, 2026 09:31:13 PM",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuLnBlcmV6QGV4YW1wbGUuY29tIiwi...",
  "isActive": true,
  "name": "Test User",
  "email": "juan.perez@example.com",
  "phones": [
    {
      "number": 1234567890,
      "citycode": 1,
      "contrycode": "+1"
    }
  ]
}
```

#### Error Responses

**401 Unauthorized** - Invalid or expired token
```json
{
  "error": [
    {
      "timestamp": "2026-01-05T21:30:55.765",
      "codigo": 401,
      "detail": "Invalid or expired token"
    }
  ]
}
```

**404 Not Found** - User not found
```json
{
  "error": [
    {
      "timestamp": "2026-01-05T21:30:55.765",
      "codigo": 404,
      "detail": "User not found"
    }
  ]
}
```

---

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests BciApplicationTests
```

### View Test Results
After running tests, view detailed results at:
```
build/reports/tests/test/index.html
```

### Postman Test Collection

A complete Postman collection with **12 test cases** is included for manual API testing:

**Location**: `src/main/resources/docs/postman_test/`

**Files:**
- `BCI_Collection.postman_collection.json` - Main API collection
- `BCI_Environment.postman_environment.json` - Environment variables
- `BCI_Test_Cases.postman_collection.json` - Additional test cases
- `POSTMAN_COLLECTION_README.md` - **Detailed documentation of all 12 test cases** ⭐

**Test Cases Include:**
1. ✅ User registration (success)
2. ✅ User login (success)
3. ❌ Invalid email format (400)
4. ❌ Invalid password format (400)
5. ❌ User already exists (422)
6. ❌ Invalid/expired token (401)
7. ❌ User not found (404)
8. And more...

**To Import in Postman:**
1. Open Postman
2. Click "Import" → "Choose Files"
3. Select `BCI_Collection.postman_collection.json`
4. Import `BCI_Environment.postman_environment.json`
5. Set environment to "BCI"
6. Run requests from the collection

---

## 📐 Architecture Documentation

The project includes comprehensive UML documentation for understanding the system architecture:

### 1. Component Diagram
**File**: `src/main/resources/docs/component_diagram.puml`  
**Reference**: `src/main/resources/docs/COMPONENT_DIAGRAM.md`

Shows the overall system architecture with:
- Presentation layer (REST Controller)
- Application layer (Service, Security)
- Persistence layer (Repository)
- Data layer (H2 Database)

**Viewable with**: PlantUML online editor, VS Code PlantUML extension, or any UML tool

### 2. Sequence Diagrams

#### Sign-Up Flow
**File**: `src/main/resources/docs/sequence_signup.puml`  
**Shows**: Step-by-step flow of user registration:
1. HTTP Client sends registration request
2. UserController validates and processes
3. UserService creates user with validation
4. Password encryption (BCrypt)
5. JWT token generation
6. User saved to H2 database
7. Response returned to client

#### Login Flow
**File**: `src/main/resources/docs/sequence_login.puml`  
**Shows**: Step-by-step flow of user authentication:
1. HTTP Client sends login request with JWT token
2. Token validation and email extraction
3. User lookup in database
4. Password verification
5. New JWT token generation
6. Response returned to client

**Reference**: `src/main/resources/docs/SEQUENCE_DIAGRAM.md`

### 3. Entity Class Diagram
**File**: `src/main/resources/docs/entity_class_diagram.puml`

Shows the data model:
- **User entity**: UUID, email (unique), password, phones (1:N relationship)
- **Phone entity**: Auto-increment ID, number, citycode, countrycode
- **Request DTOs**: SignUpRequest, PhoneDto
- **Response DTOs**: UserResponse, ErrorResponse, ErrorDetail

---

## 🛠 Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 2.5.14 | Web application framework |
| Java | 11 LTS | Programming language |
| Gradle | 8.9 | Build tool & dependency manager |
| Spring Security | 5.5.x | Authentication & authorization |
| Hibernate | 5.4.x | Object-Relational Mapping (ORM) |
| H2 Database | 1.4.200 | In-memory relational database |
| JJWT | 0.11.5 | JWT token generation & validation |
| BCrypt | Spring Security | Password hashing & encryption |
| Spring Data JPA | 2.5.14 | Data persistence abstraction |

---

## 🔐 Security Features

### Password Encryption
- **Algorithm**: BCrypt with cost factor 10
- **Storage**: Passwords are hashed, never stored in plaintext
- **Verification**: Constant-time comparison to prevent timing attacks

### JWT Authentication
- **Algorithm**: HMAC-SHA256 (HS256)
- **Token Format**: `Header.Payload.Signature`
- **Expiration**: 24 hours (86400 seconds)
- **Claims**: 
  - `sub` (subject): User email
  - `iat` (issued at): Token creation timestamp
  - `exp` (expiration): Token expiration timestamp

### Spring Security Configuration
- CORS: Disabled (stateless REST API)
- CSRF: Disabled (stateless REST API with JWT)
- Session: Stateless (JWT-based)

---

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
  id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  created TIMESTAMP NOT NULL,
  last_login TIMESTAMP,
  is_active BOOLEAN DEFAULT true
);
```

**Key Constraints:**
- `id`: UUID primary key (unique identifier)
- `email`: UNIQUE (prevents duplicate registrations)
- `password`: Hashed with BCrypt
- `created`: Registration timestamp
- `last_login`: Updated on each login
- `is_active`: Account status flag

### Phones Table
```sql
CREATE TABLE phones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  number BIGINT NOT NULL,
  citycode INT,
  contrycode VARCHAR(10),
  user_id VARCHAR(36) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

**Key Constraints:**
- `id`: Auto-increment primary key
- `user_id`: Foreign key to users table
- `ON DELETE CASCADE`: Phones deleted when user is deleted
- Supports 1:N relationship (one user, many phones)

---

## ⚙️ Configuration

### Application Properties
**File**: `src/main/resources/application.properties`

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Spring Boot
spring.application.name=bci
spring.profiles.active=dev

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Hibernate/JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.root=INFO
logging.level.com.globallogic=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Configuration Details

- **Server Port**: 8080 (REST API accessible at `http://localhost:8080`)
- **Database**: H2 in-memory (`jdbc:h2:mem:testdb`)
- **DDL Auto**: `create-drop` (schema created on startup, dropped on shutdown)
- **Logging Level**: DEBUG for application code, INFO for frameworks
- **SQL Logging**: Enabled for debugging queries

---

## 🐛 Troubleshooting

### Port 8080 Already in Use

**Windows:**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process by PID
taskkill /PID <PID> /F
```

**Linux/Mac:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill process by PID
kill -9 <PID>
```

### Gradle Build Issues

**Clean Gradle cache:**
```bash
./gradlew clean --refresh-dependencies
```

**View dependency tree:**
```bash
./gradlew dependencies
```

**Check Java version compatibility:**
```bash
java -version
javac -version
```

Ensure Java 11+ is installed and set as JAVA_HOME.

### Database Connection Issues

- **H2 is in-memory**: Data is lost when application stops
- **Console access**: H2 web console is disabled by default
- **For persistent storage**: Configure PostgreSQL or MySQL in `application.properties`

### Application Won't Start

1. **Check Java version**: `java -version` must be 11 or higher
2. **Check port availability**: Ensure port 8080 is not in use
3. **View logs**: Run `./gradlew bootRun` to see detailed error messages
4. **Clean rebuild**: Try `./gradlew clean build -x test`

---

## 📝 Additional Documentation

For more detailed information, refer to:

1. **UML Diagrams** - `src/main/resources/docs/`
   - Component architecture
   - API sequence flows
   - Entity relationships

2. **Postman Tests** - `src/main/resources/docs/postman_test/POSTMAN_COLLECTION_README.md`
   - 12 complete test cases
   - Request/response examples
   - Error scenario testing

3. **Source Code** - `src/main/java/com/globallogic/bci/`
   - Well-documented Java classes
   - Comprehensive error handling
   - Spring Security configuration

---

## 📅 Project Information

| Detail | Value |
|--------|-------|
| **Version** | 1.0.0 |
| **Release Date** | January 5, 2026 |
| **Java Version** | 11 LTS |
| **Spring Boot Version** | 2.5.14 |
| **Gradle Version** | 8.9 |
| **Last Updated** | January 5, 2026 |

---

## ✨ Features

- ✅ User registration with email and password validation
- ✅ User authentication with JWT tokens
- ✅ BCrypt password encryption (cost factor 10)
- ✅ 24-hour JWT token expiration
- ✅ Comprehensive error handling (400, 401, 404, 422)
- ✅ H2 in-memory database with JPA/Hibernate
- ✅ Spring Security configuration
- ✅ RESTful API design
- ✅ Complete UML diagrams
- ✅ Postman test collection with 12 test cases
- ✅ Gradle wrapper for easy builds
- ✅ Debug logging enabled

---

## 🎯 How Evaluators Should Test This Project

1. **Read this README.md** - Understand the project scope and architecture
2. **Review UML diagrams** - Examine system design in `src/main/resources/docs/`
3. **Build the project** - Run `./gradlew clean build`
4. **Start the application** - Run `./gradlew bootRun`
5. **Import Postman collection** - Use tests in `src/main/resources/docs/postman_test/`
6. **Execute test cases** - Follow `POSTMAN_COLLECTION_README.md`
7. **Verify responses** - Check success (201, 200) and error (400, 401, 404, 422) codes
8. **Review source code** - Examine implementation in `src/main/java/`
9. **Check database schema** - See auto-created tables in logs

---

**Happy testing! 🚀**

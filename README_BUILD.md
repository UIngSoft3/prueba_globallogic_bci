# BCI - Bank Customer Information Microservice

Spring Boot 2.5.14 microservice for user registration and authentication with JWT token support.

## Prerequisites

- **Java 11 LTS** or higher
- **Gradle 8.9** (wrapper included)
- **Git** (for version control)
- No external database required (H2 in-memory database included)

## Project Structure

```
bci/
├── src/
│   ├── main/
│   │   ├── java/com/globallogic/bci/
│   │   │   ├── BciApplication.java                 # Entry point
│   │   │   ├── config/SecurityConfig.java          # Spring Security configuration
│   │   │   ├── controller/UserController.java      # REST endpoints
│   │   │   ├── service/UserService.java            # Business logic
│   │   │   ├── repository/UserRepository.java      # Data access layer
│   │   │   ├── entity/                             # JPA entities
│   │   │   ├── dto/                                # Data transfer objects
│   │   │   ├── exception/                          # Custom exceptions
│   │   │   └── util/                               # Utility classes
│   │   └── resources/
│   │       ├── application.properties               # Application configuration
│   │       └── docs/                                # UML diagrams
│   └── test/                                        # Unit tests
├── gradle/wrapper/                                 # Gradle wrapper files
├── build.gradle                                    # Build configuration
├── settings.gradle                                 # Gradle settings
└── README_BUILD.md                                 # This file
```

## Building the Project

### 1. Clone the Repository
```bash
git clone <repository-url>
cd bci
```

### 2. Build with Gradle
```bash
# Build the project
./gradlew clean build

# Build without running tests
./gradlew clean build -x test

# View Gradle version
./gradlew --version
```

### 3. Expected Output
```
BUILD SUCCESSFUL in Xs
X actionable tasks: X executed
```

## Running the Project

### Start the Application
```bash
./gradlew bootRun
```

### Application Startup Output
```
2026-01-05 21:30:55.450  INFO 27040 --- [main] com.globallogic.bci.BciApplication : 
Starting BciApplication v0.0.1-SNAPSHOT on HP-PC
2026-01-05 21:30:55.450  INFO 27040 --- [main] com.globallogic.bci.BciApplication : 
Started BciApplication in 4.139 seconds (JVM running for 4.52)
```

### Application is Running
- **Base URL**: `http://localhost:8080`
- **Database**: H2 in-memory database
- **Port**: 8080

## API Endpoints

### 1. User Registration - `/sign-up`

**Method**: POST  
**URL**: `http://localhost:8080/sign-up`  
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
- **Email**: Must match regex `^[A-Za-z0-9+_.-]+@(.+)$` (standard email format)
- **Password**: 
  - Must contain exactly ONE uppercase letter
  - Must contain exactly TWO digits (non-consecutive)
  - Must be 8-12 characters long
  - Only lowercase letters, digits, and uppercase letters allowed
  - Example: `Pass123word` ✅, `pass123` ❌ (no uppercase)
- **Name**: Optional (string)
- **Phones**: Optional (array of phone objects)

#### Response (201 Created)
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

### 2. User Login - `/login`

**Method**: GET  
**URL**: `http://localhost:8080/login`  
**Authorization**: Basic Auth (use JWT token from `/sign-up` response)

#### Headers
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuLnBlcmV6QGV4YW1wbGUuY29tIiwi...
Content-Type: application/json
```

#### Query Parameter
- `token` (required): JWT token from previous `/sign-up` response

#### Example Request
```bash
curl -X GET "http://localhost:8080/login?token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuLnBlcmV6..." \
  -H "Content-Type: application/json"
```

#### Response (200 OK)
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

## Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 2.5.14 | Framework |
| Java | 11 LTS | Language |
| Gradle | 8.9 | Build tool |
| Hibernate | 5.4.x | ORM |
| H2 Database | 1.4.200 | In-memory database |
| Spring Security | 5.5.x | Authentication |
| JJWT | 0.11.5 | JWT token generation |
| BCrypt | via Spring Security | Password encryption |

## Java 11 Features Used

1. **Local Variable Type Inference** (`var` keyword)
   - Used in stream operations and local variable declarations
   
2. **String API Enhancements**
   - `isBlank()`, `strip()`, `lines()` methods in validation utilities
   
3. **Stream API Enhancements**
   - Filter and map operations in service layer
   
4. **UUID Support**
   - User ID generation using `java.util.UUID.randomUUID()`
   
5. **Enhanced Reflection**
   - Used in exception handling and entity introspection

## Database Schema

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

## Configuration

### Application Properties
File: `src/main/resources/application.properties`

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

## Security

### Password Encryption
- Passwords are encrypted using BCrypt with cost factor 10
- Never stored in plaintext
- Verified using constant-time comparison

### JWT Token
- Algorithm: HMAC-SHA256 (HS256)
- Secret Key: Configured in application
- Expiration: 24 hours (86400 seconds)
- Claims: subject (email) and issued timestamp

### CORS & CSRF
- CORS: Disabled (REST API only)
- CSRF: Disabled (stateless REST API)

## Testing

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test
```bash
./gradlew test --tests TestClassName
```

### Test Coverage
```bash
./gradlew test jacocoTestReport
```

Current coverage target: 80% (Service layer)

## Troubleshooting

### Port 8080 Already in Use
```bash
# Kill process using port 8080
# On Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# On Linux/Mac:
lsof -i :8080
kill -9 <PID>
```

### Gradle Build Issues
```bash
# Clean Gradle cache
./gradlew clean --refresh-dependencies

# View dependency tree
./gradlew dependencies
```

### Database Connection Issues
- H2 database is in-memory (volatile)
- Data is lost when application stops
- For persistent storage, configure PostgreSQL or MySQL

## Development Workflow

### 1. Create Feature Branch
```bash
git checkout -b feature/your-feature-name
```

### 2. Make Changes
Edit files in `src/main/java/com/globallogic/bci/`

### 3. Test Locally
```bash
./gradlew bootRun
```

### 4. Run Tests
```bash
./gradlew test
```

### 5. Build and Commit
```bash
./gradlew clean build
git add .
git commit -m "feat: your feature description"
git push origin feature/your-feature-name
```

### 6. Create Pull Request
Submit PR for code review on GitHub/GitLab/Bitbucket

## Deployment

### Build Production JAR
```bash
./gradlew clean build -x test
```

Output: `build/libs/bci-0.0.1-SNAPSHOT.jar`

### Run JAR File
```bash
java -jar build/libs/bci-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
```bash
# Build Docker image
docker build -t bci:1.0.0 .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/bci \
  -e SPRING_DATASOURCE_USERNAME=bci_user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  bci:1.0.0
```

## Documentation

### Project Documentation
- `JAVA11_FEATURES.md` - Detailed Java 11 features
- `ARCHITECTURE.md` - System architecture and design patterns
- `MIGRATION.md` - Migration history from Groovy to Java

### UML Diagrams
- `src/main/resources/docs/COMPONENT_DIAGRAM.md` - Component architecture
- `src/main/resources/docs/SEQUENCE_DIAGRAM.md` - API call sequences

## Support & Contacts

- **Issues**: Create GitHub issue with [BUG] prefix
- **Features**: Create GitHub issue with [FEATURE] prefix
- **Security**: Contact security team directly

## License

MIT License - See LICENSE file for details

## Changelog

### Version 1.0.0 (Jan 5, 2026)
- ✅ User registration endpoint (`/sign-up`)
- ✅ User login endpoint (`/login`)
- ✅ JWT token authentication
- ✅ Password encryption (BCrypt)
- ✅ Email and password validation
- ✅ Error handling with custom exceptions
- ✅ H2 database integration
- ✅ Spring Security configuration
- ✅ Comprehensive logging

---

**Last Updated**: January 5, 2026  
**Gradle Version**: 8.9  
**Spring Boot Version**: 2.5.14  
**Java Version**: 11 LTS

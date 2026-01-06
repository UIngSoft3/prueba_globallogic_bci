# BCI - Component Diagram (UML 2.5)

## UML Component Architecture

> **Diagrama UML detallado:** Ver archivo [`component_diagram.puml`](./component_diagram.puml)

---

## Descripción de Componentes

### Arquitectura en Capas

#### **Presentation Layer (HTTP Port 8080)**
- **UserController** - REST Controller que maneja las requests
  - `POST /sign-up` → `registerUser(SignUpRequest)`
  - `GET /login` → `login(String token)`
- **GlobalExceptionHandler** - Manejo centralizado de excepciones
  - `@ControllerAdvice` que convierte excepciones en respuestas JSON

#### **Application Layer (Business Logic)**
- **UserService** - Servicio principal de lógica de negocio
  - `registerUser(SignUpRequest)` → Validación, encriptación, token JWT, persistencia
  - `login(String token)` → Validación de token, actualización, nuevo token
- **SecurityConfig** - Configuración de seguridad Spring
  - Permite `/sign-up` y `/login` sin autenticación
  - Desactiva CSRF para REST API
- **JwtTokenProvider** - Generación y validación de tokens JWT
  - HMAC-SHA256, expiración de 24 horas
  - Extracción de claims del token
- **PasswordEncryptor** - Encriptación de contraseñas
  - BCrypt con cost factor 10
- **ValidationUtil** - Validación de entrada
  - Email: Regex RFC 5322
  - Contraseña: 8-12 caracteres, 1+ mayúscula, 2+ dígitos

#### **Persistence Layer (Data Access)**
- **UserRepository** - DAO para acceso a datos (Spring Data JPA)
  - `findByEmail(String)` → Búsqueda por email
  - `save(User)` → Persistencia de usuario
- **User Entity** - Entidad JPA
  - ID (UUID), email (UNIQUE), password (BCrypt), teléfonos (1:N)
- **Phone Entity** - Entidad JPA
  - Número, código de ciudad, código de país
  - Relación many-to-one con User

#### **Data Layer**
- **H2 Database** - Base de datos en memoria (Hibernate ORM)
  - Tabla `users` con índice en email
  - Tabla `phones` con FK a users (CASCADE DELETE)

---

## Component Interactions

### Request Flow: `/sign-up`

```
HTTP Request (JSON)
    ↓
UserController.signUp()
    ├→ SecurityConfig (permitAll check)
    ├→ UserService.registerUser()
    │  ├→ ValidationUtil (email & password)
    │  ├→ UserRepository (findByEmail)
    │  ├→ PasswordEncryptor (BCrypt encode)
    │  ├→ JwtTokenProvider (generateToken)
    │  └→ UserRepository (save)
    │     └→ Hibernate ORM
    │        └→ H2 Database
    ↓
GlobalExceptionHandler (if error)
    ↓
UserResponse (201 Created with token)
```

### Request Flow: `/login`

```
HTTP Request (JWT token)
    ↓
UserController.login()
    ├→ SecurityConfig (permitAll check)
    ├→ UserService.login()
    │  ├→ JwtTokenProvider (validateToken)
    │  ├→ JwtTokenProvider (extractEmail)
    │  ├→ UserRepository (findByEmail)
    │  │  └→ Hibernate ORM
    │  │     └→ H2 Database (SELECT + phones via 1:N)
    │  ├→ JwtTokenProvider (generateToken)
    │  └→ UserRepository (save)
    │     └→ Hibernate ORM
    │        └→ H2 Database (UPDATE)
    ↓
GlobalExceptionHandler (if error)
    ↓
UserResponse (200 OK with new token)
```

---

## Component Dependencies

| Source | Target | Method | Type |
|--------|--------|--------|------|
| UserController | UserService | registerUser(), login() | Dependency Injection |
| UserService | ValidationUtil | isValidEmail(), isValidPassword() | Utility |
| UserService | PasswordEncryptor | encode(), matches() | Utility |
| UserService | JwtTokenProvider | generateToken(), validateToken(), extractEmail() | Utility |
| UserService | UserRepository | findByEmail(), save() | Spring Data JPA |
| UserRepository | User Entity | ORM Mapping | Hibernate |
| UserRepository | Phone Entity | ORM Mapping | Hibernate |
| User Entity | H2 Database | persists/retrieves | JDBC |
| Phone Entity | H2 Database | persists/retrieves | JDBC |
| SecurityConfig | UserController | configures | Spring Security |

---

## Technology Stack by Layer

| Layer | Component | Technology | Version |
|-------|-----------|-----------|---------|
| **Presentation** | REST API | Spring Boot Web | 2.5.14 |
| | Controller | Spring MVC | 5.3.x |
| | Exception Handler | Spring Framework | 5.3.x |
| **Application** | Service | Spring Framework | 5.3.x |
| | Security Config | Spring Security | 5.5.x |
| | JWT | JJWT | 0.11.5 |
| | Password Hash | BCrypt | Spring Security |
| **Persistence** | Repository | Spring Data JPA | 2.5.x |
| | ORM | Hibernate | 5.4.x |
| **Data** | Database | H2 | 1.4.200 |
| | JDBC Driver | H2 Database | 1.4.200 |

---

## Deployment View

```
┌─────────────────────────────────────────┐
│      Application Server (Port 8080)     │
│  ┌───────────────────────────────────┐  │
│  │   Spring Boot 2.5.14 Application  │  │
│  │  ┌─────────────────────────────┐  │  │
│  │  │   All Components            │  │  │
│  │  │   (Controllers, Services)   │  │  │
│  │  └─────────────────────────────┘  │  │
│  │  ┌─────────────────────────────┐  │  │
│  │  │   H2 In-Memory Database     │  │  │
│  │  │   (users + phones tables)   │  │  │
│  │  └─────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
│              Java 11 Runtime            │
│           (Gradle 8.9 Build)            │
└─────────────────────────────────────────┘
             ↕ HTTP (REST)
        HTTP Clients
    (Postman, Browser, etc)
```

---

## Archivos de Referencia

| Archivo | Tipo | Descripción |
|---------|------|-------------|
| [`component_diagram.puml`](./component_diagram.puml) | UML Component | Diagrama de componentes detallado |
| [`sequence_signup.puml`](./sequence_signup.puml) | UML Sequence | Flujo de registro |
| [`sequence_login.puml`](./sequence_login.puml) | UML Sequence | Flujo de login |
| [`entity_class_diagram.puml`](./entity_class_diagram.puml) | UML Class | Entidades y DTOs |

---

**Document Version**: 1.0.0  
**Last Updated**: January 5, 2026  
**UML Standard**: UML 2.5  
**Tool**: PlantUML (.puml files)

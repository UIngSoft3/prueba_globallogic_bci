# BCI - Sequence Diagrams (UML 2.5)

## 1. User Registration Sequence - `/sign-up`

> **Diagrama UML detallado:** Ver archivo [`sequence_signup.puml`](./sequence_signup.puml)

### Resumen del Flujo

El flujo de registro (`/sign-up`) tiene los siguientes pasos:

1. **Cliente HTTP** envía request POST con JSON (email, password, phones)
2. **UserController** valida que el endpoint sea permitido (permitAll)
3. **UserService** ejecuta la lógica de negocio:
   - Valida formato de email (regex)
   - Valida fortaleza de contraseña (8-12 chars, 1+ mayúscula, 2+ dígitos)
   - Verifica que el email no exista en BD
   - Encripta la contraseña con BCrypt (cost 10)
   - Crea entidad User con UUID
   - Genera token JWT (HMAC-SHA256, 24h validez)
   - Persiste usuario y teléfonos en H2
4. **Respuesta HTTP 201 Created** con usuario y token
5. Si hay error, **GlobalExceptionHandler** retorna código HTTP apropiado

### Manejo de Errores en `/sign-up`

| Paso | Error | HTTP | Excepción |
|------|-------|------|-----------|
| Validación email | Formato inválido | 400 | BadRequestException |
| Validación contraseña | Débil/corta | 400 | BadRequestException |
| Verificación email | Ya existe | 422 | UserAlreadyExistsException |

---

## 2. User Login Sequence - `/login`

> **Diagrama UML detallado:** Ver archivo [`sequence_login.puml`](./sequence_login.puml)

### Resumen del Flujo

El flujo de login (`/login`) tiene los siguientes pasos:

1. **Cliente HTTP** envía GET con token JWT en header Authorization
2. **UserController** valida que el endpoint sea permitido (permitAll)
3. **UserService** ejecuta la lógica de autenticación:
   - Valida que el token JWT sea válido y no esté expirado
   - Extrae el email del token (claim 'sub')
   - Busca el usuario en BD por email
   - Actualiza timestamp `lastLogin`
   - Genera un nuevo token JWT (renovación)
   - Persiste cambios en BD
4. **Respuesta HTTP 200 OK** con usuario actualizado y nuevo token
5. Si hay error, **GlobalExceptionHandler** retorna código HTTP apropiado

### Manejo de Errores en `/login`

| Paso | Error | HTTP | Excepción |
|------|-------|------|-----------|
| Validación token | Token inválido/expirado | 401 | InvalidCredentialsException |
| Búsqueda usuario | Usuario no existe | 404 | UserNotFoundException |
| Falta header | Sin Authorization header | 400 | BadRequestException |

---

## 3. JWT Token Structure

```
JWT Token Format: [Header].[Payload].[Signature]

Header (Base64URL encoded):
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload (Base64URL encoded):
{
  "sub": "user@example.com",      // Subject (email)
  "email": "user@example.com",    // Email claim
  "iat": 1704279400,              // Issued At (timestamp)
  "exp": 1704365800               // Expiration (iat + 24 hours)
}

Signature (HMAC-SHA256):
HMACSHA256(
  base64url(header) + "." + base64url(payload),
  secret_key
)
```

### Token Validation Process

1. Parse token into 3 parts (header.payload.signature)
2. Verify signature using HMAC-SHA256 and secret key
3. Check expiration time: `current_time < exp`
4. Extract claims if all validations pass

---

## 4. Entity and DTO Classes

> **Diagrama UML detallado:** Ver archivo [`entity_class_diagram.puml`](./entity_class_diagram.puml)

### Relaciones de Entidades

```
User (1) ---< Phones (0..*)
  ├── Relation: @OneToMany with cascade=ALL
  ├── OrphanRemoval: true (elimina phones sin user)
  └── Foreign Key: user_id → users.id (ON DELETE CASCADE)
```

### Request DTOs

- **SignUpRequest**: Datos de registro (email, password, phones array)
- **PhoneDto**: Teléfono dentro de SignUpRequest

### Response DTOs

- **UserResponse**: Respuesta con usuario, token y timestamps
- **ErrorResponse**: Respuesta de error con detalles
- **ErrorDetail**: Detalle de error individual

---

## 5. Spring Security Filter Chain

```
HTTP Request
    │
    ▼
SecurityContextPersistenceFilter
    ├─→ Load/create security context
    │
    ▼
HeaderWriterFilter
    ├─→ Add security headers (X-Content-Type-Options, etc.)
    │
    ▼
BasicAuthenticationFilter
    ├─→ Extract credentials (if present)
    │
    ▼
AnonymousAuthenticationFilter
    ├─→ Create anonymous token if not authenticated
    │
    ▼
FilterSecurityInterceptor
    ├─→ Check endpoint authorization:
    │   ├─→ /sign-up → permitAll()
    │   ├─→ /login → permitAll()
    │   └─→ Other endpoints → permitAll() (actual)
    │
    ▼ (Access Granted or 403)

DispatcherServlet
    │
    ▼
UserController / Application Logic
    │
    ▼
HTTP Response
    │
    ▼
SecurityContextPersistenceFilter (cleanup)
    └─→ Clear SecurityContextHolder
```

---

## 6. Database Schema Interaction

### users Table
```sql
CREATE TABLE users (
  id VARCHAR(36) PRIMARY KEY,           -- UUID
  email VARCHAR(255) UNIQUE NOT NULL,   -- Email (UNIQUE)
  password VARCHAR(255) NOT NULL,       -- BCrypt hash
  name VARCHAR(255),                    -- Optional name
  created TIMESTAMP NOT NULL,           -- Creation timestamp
  last_login TIMESTAMP,                 -- Last login timestamp
  is_active BOOLEAN DEFAULT true        -- Account status
);
CREATE INDEX idx_email ON users(email);
```

### phones Table
```sql
CREATE TABLE phones (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,      -- Auto PK
  number BIGINT NOT NULL,                    -- Phone number
  citycode INTEGER,                          -- City code
  contrycode VARCHAR(10),                    -- Country code
  user_id VARCHAR(36) NOT NULL,              -- FK to users
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_user_id ON phones(user_id);
```

### Transaction Flow

| Operación | SQL | Afectadas |
|-----------|-----|-----------|
| /sign-up | INSERT users + INSERT phones | users, phones |
| /login | SELECT users + SELECT phones + UPDATE users | users, phones (read) |

---

## 7. Error Response Format

Todos los errores retornan JSON con el siguiente formato:

```json
{
  "timestamp": "2026-01-05T21:30:55.765",
  "status": 400,
  "error": "Bad Request",
  "message": "Validación fallida",
  "path": "/sign-up",
  "details": [
    {
      "field": "email",
      "message": "El email debe ser un email válido"
    }
  ]
}
```

---

## 8. Archivos PUML (Diagramas UML)

Los diagramas UML se encuentran en archivos separados para mejor visualización:

| Archivo | Tipo | Descripción |
|---------|------|-------------|
| [`component_diagram.puml`](./component_diagram.puml) | Component | Arquitectura de componentes del sistema |
| [`sequence_signup.puml`](./sequence_signup.puml) | Sequence | Flujo de registro `/sign-up` |
| [`sequence_login.puml`](./sequence_login.puml) | Sequence | Flujo de login `/login` |
| [`entity_class_diagram.puml`](./entity_class_diagram.puml) | Class | Entidades, DTOs y relaciones |

### Cómo visualizar los diagramas

1. **VS Code**: Instalar extensión "PlantUML" o "PlantUML Previewer"
2. **Online**: Visitar https://www.plantuml.com/plantuml/uml/ y pegar el contenido
3. **CLI**: Instalar PlantUML y usar: `plantuml diagram.puml`

---

**Document Version**: 1.0.0  
**Last Updated**: January 5, 2026  
**UML Standard**: UML 2.5  
**Diagrama Tool**: PlantUML (.puml files)

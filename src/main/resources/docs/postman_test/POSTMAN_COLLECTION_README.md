# BCI Microservice API - Postman Collection

**Collection Version:** 1.0  
**Base URL:** `http://localhost:8080`  
**Last Updated:** 2026

## Overview

Esta colección de Postman contiene 12 casos de prueba para el microservicio BCI de autenticación y registro de usuarios. Incluye casos exitosos y casos de error para validar la correcta implementación de los endpoints `/sign-up` y `/login`.

---

## 📋 Pre-requisitos

- **Postman** (v10.0 o superior)
- **Aplicación BCI ejecutándose** en `http://localhost:8080`
- El servidor debe estar iniciado: `./gradlew bootRun`

## 🔧 Importar la Colección

1. Abre Postman
2. Click en **"Import"** (esquina superior izquierda)
3. Selecciona el archivo: `BCI Microservice API.postman_collection.json`
4. La colección se importará automáticamente con todos los 12 casos de prueba

### Variables de Entorno

La colección incluye dos variables globales:

| Variable | Valor Default | Descripción |
|----------|--------------|-------------|
| `base_url` | `http://localhost:8080` | URL base del servidor |
| `jwt_token` | (vacío) | Token JWT recibido tras registro exitoso |

---

## 🧪 Casos de Prueba Disponibles

### ✅ Grupo 1: Registro Exitoso (Sign-Up Success Cases)

#### **1. Registro - Caso Exitoso**
- **Método:** `POST`
- **Endpoint:** `/sign-up`
- **Descripción:** Registra un nuevo usuario con datos válidos
- **Status Esperado:** `201 Created`

**Request Body:**
```json
{
  "name": "Test User",
  "email": "juan.perez@example.com",
  "password": "Password123",
  "phones": [
    {
      "number": 1234567890,
      "citycode": 1,
      "contrycode": "+1"
    }
  ]
}
```

**Response (201):**
```json
{
  "id": "a1b2c3d4-e5f6-47g8-h9i0-j1k2l3m4n5o6",
  "name": "Test User",
  "email": "juan.perez@example.com",
  "created": "2024-01-15T10:30:45Z",
  "isActive": true,
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Validaciones:**
- ✅ Email válido (formato RFC 5322)
- ✅ Contraseña válida (8-12 caracteres, 1+ mayúscula, 2+ dígitos)
- ✅ Phones es opcional pero si se proporciona, debe ser un array
- ✅ Usuario se crea en estado activo

---

#### **2. Registro - Sin Teléfono (Opcional)**
- **Método:** `POST`
- **Endpoint:** `/sign-up`
- **Descripción:** Registra usuario sin números de teléfono
- **Status Esperado:** `201 Created`

**Request Body:**
```json
{
  "name": "María García",
  "email": "maria.garcia@example.com",
  "password": "Securepass45",
  "phones": []
}
```

**Validaciones:**
- ✅ Campo `phones` es completamente opcional
- ✅ Acepta array vacío
- ✅ Usuario se crea correctamente sin teléfonos

---

#### **3. Registro - Múltiples Teléfonos**
- **Método:** `POST`
- **Endpoint:** `/sign-up`
- **Descripción:** Registra usuario con múltiples números de teléfono
- **Status Esperado:** `201 Created`

**Request Body:**
```json
{
  "name": "Carlos López",
  "email": "carlos.lopez@example.com",
  "password": "Mypassword78",
  "phones": [
    {
      "number": 1111111111,
      "citycode": "1",
      "contrycode": "57"
    },
    {
      "number": 2222222222,
      "citycode": "2",
      "contrycode": "57"
    },
    {
      "number": 3333333333,
      "citycode": "3",
      "contrycode": "1"
    }
  ]
}
```

**Validaciones:**
- ✅ Soporta múltiples teléfonos (sin límite)
- ✅ Cada teléfono se guarda con relación 1:N
- ✅ Los teléfonos se vinculan automáticamente al usuario

---

### ❌ Grupo 2: Errores de Validación (Sign-Up Validation Errors)

#### **4. Error - Email Inválido**
- **Método:** `POST`
- **Endpoint:** `/sign-up`
- **Descripción:** Intenta registrar con formato de email inválido
- **Status Esperado:** `400 Bad Request`

**Request Body:**
```json
{
  "name": "Test User",
  "email": "email-invalido",
  "password": "Password123",
  "phones": []
}
```

**Response (400):**
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "El email debe ser un email válido",
  "path": "/sign-up",
  "details": [
    {
      "field": "email",
      "message": "El email debe ser un email válido"
    }
  ]
}
```

**Regex Validado:**
```
^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$
```

---

#### **5. Error - Contraseña Débil (Muy Corta)**
- **Método:** `POST`
- **Endpoint:** `/sign-up`
- **Status Esperado:** `400 Bad Request`

**Request Body:**
```json
{
  "name": "Test User",
  "email": "test@example.com",
  "password": "Pwd1",
  "phones": []
}
```

**Response (400):**
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 400,
  "message": "Validación fallida",
  "details": [
    {
      "field": "password",
      "message": "La contraseña debe tener entre 8 y 12 caracteres"
    }
  ]
}
```

**Regla:** Mínimo 8 caracteres

---

#### **6. Error - Contraseña Sin Mayúscula**
- **Método:** `POST`
- **Endpoint:** `/sign-up`
- **Status Esperado:** `400 Bad Request`

**Request Body:**
```json
{
  "name": "Test User",
  "email": "test2@example.com",
  "password": "password123",
  "phones": []
}
```

**Response (400):**
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 400,
  "message": "Validación fallida",
  "details": [
    {
      "field": "password",
      "message": "La contraseña debe contener al menos una mayúscula"
    }
  ]
}
```

**Regla:** Requiere mínimo 1 mayúscula

---

#### **7. Error - Contraseña Sin Dígitos**
- **Método:** `POST`
- **Endpoint:** `/sign-up`
- **Status Esperado:** `400 Bad Request`

**Request Body:**
```json
{
  "name": "Test User",
  "email": "test3@example.com",
  "password": "PasswordAbc",
  "phones": []
}
```

**Response (400):**
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 400,
  "message": "Validación fallida",
  "details": [
    {
      "field": "password",
      "message": "La contraseña debe contener al menos 2 dígitos"
    }
  ]
}
```

**Regla:** Requiere mínimo 2 dígitos

---

#### **8. Error - Email Duplicado**
- **Método:** `POST`
- **Endpoint:** `/sign-up`
- **Status Esperado:** `422 Unprocessable Entity`

**Request Body:**
```json
{
  "name": "Otro Nombre",
  "email": "juan.perez@example.com",
  "password": "Anotherpas46",
  "phones": []
}
```

**Response (422):**
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "El usuario ya existe",
  "path": "/sign-up"
}
```

**⚠️ Prerequisito:** Ejecutar primero el request **#1** para crear el usuario

---

### 🔐 Grupo 3: Login Exitoso (Login Success Cases)

#### **9. Login - Token Válido**
- **Método:** `GET`
- **Endpoint:** `/login`
- **Descripción:** Autentica usando un token JWT válido
- **Status Esperado:** `200 OK`

**Request Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuLnBlcmVAZXhhbXBsZS5jb20iLCJlbWFpbCI6Imp1YW4ucGVyZUBleGFtcGxlLmNvbSIsImlhdCI6MTcwNDI3OTQwMCwiZXhwIjoxNzA0MzY1ODAwfQ.xxx...
```

**Response (200):**
```json
{
  "id": "a1b2c3d4-e5f6-47g8-h9i0-j1k2l3m4n5o6",
  "name": "Test User",
  "email": "juan.perez@example.com",
  "created": "2024-01-15T10:30:45Z",
  "lastLogin": "2024-01-15T10:35:20Z",
  "isActive": true,
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Validaciones:**
- ✅ Token JWT válido y no expirado
- ✅ Retorna datos actualizados del usuario
- ✅ Genera nuevo token (24 horas de validez)
- ✅ Actualiza `lastLogin` en la BD

**⚠️ Prerequisito:** Usar token recibido en request **#1**

---

### ❌ Grupo 4: Errores de Login (Login Error Cases)

#### **10. Error - Token Inválido**
- **Método:** `GET`
- **Endpoint:** `/login`
- **Status Esperado:** `401 Unauthorized`

**Request Headers:**
```
Authorization: Bearer TOKEN_INVALIDO_FAKE_123456789
```

**Response (401):**
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT inválido o expirado",
  "path": "/login"
}
```

**Validaciones:**
- ✅ Token malformado rechazado
- ✅ Token falsificado rechazado
- ✅ Retorna error 401 Unauthorized

---

#### **11. Error - Header Authorization Faltante**
- **Método:** `GET`
- **Endpoint:** `/login`
- **Status Esperado:** `400 Bad Request`

**Request Headers:** (Sin Authorization)

**Response (400):**
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Header Authorization requerido",
  "path": "/login"
}
```

---

#### **12. Error - Usuario No Encontrado**
- **Método:** `GET`
- **Endpoint:** `/login`
- **Status Esperado:** `404 Not Found`

**Request Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3VhcmlvLm5vLWV4aXN0ZW50ZUBleGFtcGxlLmNvbSIsImlhdCI6MTcwNDI3OTQwMCwiZXhwIjoxNzA0MzY1ODAwfQ.aaaaaaa...
```

**Response (404):**
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "Usuario no encontrado",
  "path": "/login"
}
```

**Validaciones:**
- ✅ Token válido pero usuario no existe en BD
- ✅ Retorna error 404 Not Found

---

## 🚀 Flujo de Ejecución Recomendado

Para probar toda la funcionalidad correctamente, sigue este orden:

```
1. Ejecutar request #1 (Registro exitoso)
   └─> Copiar el token JWT del response
   
2. Ejecutar request #9 (Login con token válido)
   └─> Usar el token copiado del paso anterior
   
3. Ejecutar request #2 (Registro sin teléfono)
   
4. Ejecutar request #3 (Registro con múltiples teléfonos)
   
5. Ejecutar request #8 (Email duplicado)
   └─> Usa el email del request #1
   
6. Ejecutar requests #4-7 (Errores de validación)
   └─> Cada uno puede ejecutarse independientemente
   
7. Ejecutar requests #10-12 (Errores de login)
   └─> Cada uno puede ejecutarse independientemente
```

---

## 📊 Matriz de Cobertura de Pruebas

| ID | Caso de Prueba | Endpoint | Método | Status | Tipo |
|:--:|---|-----------|--------|--------|------|
| 1 | Registro exitoso | /sign-up | POST | 201 | ✅ Éxito |
| 2 | Sin teléfono | /sign-up | POST | 201 | ✅ Éxito |
| 3 | Múltiples teléfonos | /sign-up | POST | 201 | ✅ Éxito |
| 4 | Email inválido | /sign-up | POST | 400 | ❌ Validación |
| 5 | Contraseña muy corta | /sign-up | POST | 400 | ❌ Validación |
| 6 | Sin mayúscula | /sign-up | POST | 400 | ❌ Validación |
| 7 | Sin dígitos | /sign-up | POST | 400 | ❌ Validación |
| 8 | Email duplicado | /sign-up | POST | 422 | ❌ Duplicado |
| 9 | Token válido | /login | GET | 200 | ✅ Éxito |
| 10 | Token inválido | /login | GET | 401 | ❌ Autenticación |
| 11 | Auth faltante | /login | GET | 400 | ❌ Validación |
| 12 | Usuario no existe | /login | GET | 404 | ❌ No encontrado |

**Total:** 12 casos | **Éxito:** 4 | **Error:** 8

---

## 🔑 Reglas de Validación

### Email
- **Formato:** RFC 5322 simplificado
- **Regex:** `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`
- **Restricción:** UNIQUE en BD

### Contraseña
- **Longitud:** 8-12 caracteres
- **Mayúscula:** Mínimo 1
- **Dígitos:** Mínimo 2
- **Encriptado:** BCrypt (cost factor 10)

### Teléfono
- **Campos:** `number`, `citycode`, `contrycode`
- **Relación:** 1 Usuario → N Teléfonos
- **Cascada:** Eliminación en cascada

---

## 🔒 Tokens JWT

### Estructura
```
Header.Payload.Signature

Header: {"alg":"HS256"}
Payload: {
  "sub": "email@example.com",
  "email": "email@example.com",
  "iat": 1704279400,
  "exp": 1704365800
}
Signature: HMAC-SHA256
```

### Validez
- **Duración:** 24 horas (86400 segundos)
- **Algoritmo:** HMAC-SHA256
- **Renovación:** Se genera un nuevo token en cada login

---

## 📝 Notas Importantes

1. **Base de datos:** H2 en memoria (se reinicia con cada restart)
2. **Contraseña:** No se devuelve nunca en responses
3. **Timestamps:** Formato ISO 8601 UTC
4. **CORS:** Habilitado para localhost:8080
5. **Logging:** DEBUG level activo en console

---

## ❓ Troubleshooting

| Problema | Solución |
|----------|----------|
| 404 Not Found en /sign-up | Verificar que el servidor está corriendo en puerto 8080 |
| 400 Bad Request | Validar JSON body y headers (Content-Type: application/json) |
| Token expirado | Generar nuevo token ejecutando registro o login nuevamente |
| Email duplicado | Cambiar email en el request o reiniciar servidor (H2 en memoria) |
| Port 8080 en uso | Cambiar puerto en application.properties o liberar el puerto |

---

*Esta documentación se mantiene sincronizada con la colección Postman JSON.*

# 📚 BCI Microservice - Documentación Técnica

## 📂 Estructura de Documentación

```
src/main/resources/docs/
├── README.md (este archivo)
├── COMPONENT_DIAGRAM.md (referencia al diagrama component_diagram.puml)
├── SEQUENCE_DIAGRAM.md (referencia a sequence_signup.puml y sequence_login.puml)
├── component_diagram.puml ⭐ (UML Component - Arquitectura del sistema)
├── sequence_signup.puml ⭐ (UML Sequence - Flujo POST /sign-up)
├── sequence_login.puml ⭐ (UML Sequence - Flujo GET /login)
├── entity_class_diagram.puml ⭐ (UML Class - Entidades y DTOs)
└── postman_test/
    ├── POSTMAN_COLLECTION_README.md
    └── BCI Microservice API.postman_collection.json
```

⭐ = Diagramas UML en formato PlantUML (.puml)

---

## 📖 Guía de Documentos

### 1. **README_BUILD.md** (Raíz del proyecto)
**Ubicación:** `../../README_BUILD.md`

Guía completa para construir y ejecutar el microservicio:
- ✅ Requisitos previos (Java 11, Gradle 8.9)
- ✅ Instrucciones de build con `./gradlew clean build`
- ✅ Cómo ejecutar con `./gradlew bootRun`
- ✅ Endpoints disponibles (/sign-up, /login)
- ✅ Ejemplos de request/response
- ✅ Reglas de validación
- ✅ Códigos de error HTTP
- ✅ Configuración de la base de datos H2
- ✅ Solución de problemas

**Tiempo de lectura:** ~10 minutos

---

### 2. **COMPONENT_DIAGRAM.md** + **component_diagram.puml**
**Ubicación:** `./COMPONENT_DIAGRAM.md` y `./component_diagram.puml`

Diagrama UML 2.5 de componentes del sistema:
- 🏗️ Arquitectura en capas (Presentation, Application, Persistence, Data)
- 🧩 Componentes por capa detallados
  - **Presentation:** UserController, GlobalExceptionHandler
  - **Application:** UserService, SecurityConfig, JwtTokenProvider, PasswordEncryptor, ValidationUtil
  - **Persistence:** UserRepository, User Entity, Phone Entity
  - **Data:** H2 In-Memory Database
- 🔄 Flujos de datos (Registration y Login)
- 🗄️ Interacción con BD
- 🛡️ Seguridad y Exception Handling
- 📊 Relaciones entre componentes

**Audience:** Arquitectos, desarrolladores senior  
**Tiempo de lectura:** ~15 minutos  
**Archivo PUML:** Visualizable en VS Code, navegadores, o línea de comandos

---

### 3. **SEQUENCE_DIAGRAM.md** + **sequence_signup.puml** + **sequence_login.puml**
**Ubicación:** `./SEQUENCE_DIAGRAM.md`, `./sequence_signup.puml`, `./sequence_login.puml`

Diagramas UML 2.5 de secuencias de operaciones:

#### **Flujo `/sign-up` (sequence_signup.puml)**
1. Validación de input (email, contraseña)
2. Verificación de email único
3. Encriptación de contraseña con BCrypt
4. Creación de UUID para usuario
5. Generación de JWT token
6. Inserción en BD (usuarios y teléfonos)
7. Response HTTP 201 Created

#### **Flujo `/login` (sequence_login.puml)**
1. Validación de token JWT
2. Extracción de email del token
3. Búsqueda de usuario en BD
4. Actualización de lastLogin
5. Generación de nuevo JWT token
6. Persistencia de cambios
7. Response HTTP 200 OK

**Detalles adicionales:**
- 🔑 Estructura y validación de JWT (HMAC-SHA256)
- 🚨 Flujo de manejo de excepciones
- 🔒 Spring Security filter chain
- 🗄️ Interacción con BD (queries, transactions)

**Audience:** Desarrolladores mid-level, QA  
**Tiempo de lectura:** ~20 minutos  
**Archivos PUML:** Visualizables en VS Code, navegadores, o línea de comandos

---

### 4. **entity_class_diagram.puml**
**Ubicación:** `./entity_class_diagram.puml`

Diagrama UML 2.5 de clases (Entidades y DTOs):
- **Entidades JPA:**
  - User (id UUID, email UNIQUE, password BCrypt, teléfonos 1:N)
  - Phone (número, ciudad, país, relación con User)
- **Request DTOs:**
  - SignUpRequest (email, password, phones array)
  - PhoneDto (número, ciudad, país)
- **Response DTOs:**
  - UserResponse (id, email, token, timestamps, status)
- **Error DTOs:**
  - ErrorResponse (timestamp, status, error, details)
  - ErrorDetail (field, message)

**Audience:** Desarrolladores, QA  
**Tiempo de lectura:** ~10 minutos  
**Archivo PUML:** Visualizable en VS Code, navegadores, o línea de comandos

---

### 5. **Postman Collection**
**Ubicación:** `./postman_test/`

Contiene 12 casos de prueba para validar toda la API:
- 3 casos de registro exitoso (sin teléfono, con teléfono, múltiples teléfonos)
- 4 casos de errores de validación (email inválido, contraseña débil, etc.)
- 1 caso de email duplicado
- 1 caso de login exitoso
- 3 casos de errores de login (token inválido, falta header, usuario no existe)

**Cómo usar:**
1. Abre Postman
2. Click "Import"
3. Selecciona `BCI Microservice API.postman_collection.json`
4. Ejecuta los 12 casos en orden recomendado

**Tiempo para ejecutar:** ~2 minutos

---

## 🎯 Camino de Lectura Recomendado

### Para Principiantes
```
1. README_BUILD.md (5 min)
   ↓ Entender requisitos y cómo ejecutar
   
2. POSTMAN_COLLECTION_README.md (5 min)
   ↓ Ver cómo funciona la API
   
3. Ejecutar colección Postman (2 min)
   ↓ Validar que todo funciona
   
Tiempo total: ~12 minutos
```

### Para Desarrolladores Mid-Level
```
1. README_BUILD.md (5 min)
   ↓
2. COMPONENT_DIAGRAM.md + component_diagram.puml (15 min)
   ↓ Entender arquitectura
   
3. POSTMAN_COLLECTION_README.md (5 min)
   ↓
4. SEQUENCE_DIAGRAM.md + sequence_*.puml (20 min)
   ↓ Detalles de implementación
   
5. Revisar código fuente en src/main/java/ (20 min)
   
Tiempo total: ~65 minutos
```

### Para Arquitectos/Tech Leads
```
1. COMPONENT_DIAGRAM.md + component_diagram.puml (15 min)
   ↓ Visión general de la arquitectura
   
2. SEQUENCE_DIAGRAM.md + sequence_*.puml (20 min)
   ↓ Flujos de negocio
   
3. README_BUILD.md (5 min)
   ↓ Validar stack tecnológico
   
4. Revisar build.gradle (10 min)
   ↓ Dependencias y configuración
   
Tiempo total: ~50 minutos
```

---

## 📊 Tabla de Contenidos Rápida

| Documento | Tipo | Tema | Audiencia | Referencia |
|-----------|------|------|-----------|-----------|
| README_BUILD.md | Markdown | Build & Deployment | Todos | Raíz del proyecto |
| COMPONENT_DIAGRAM.md | Markdown | Referencias | Todos | component_diagram.puml |
| component_diagram.puml | UML Component | Arquitectura | Dev Senior+ | ⭐ Diagrama |
| SEQUENCE_DIAGRAM.md | Markdown | Referencias | Todos | sequence_*.puml |
| sequence_signup.puml | UML Sequence | /sign-up flow | Dev Mid+ | ⭐ Diagrama |
| sequence_login.puml | UML Sequence | /login flow | Dev Mid+ | ⭐ Diagrama |
| entity_class_diagram.puml | UML Class | Entidades & DTOs | Dev+ | ⭐ Diagrama |
| POSTMAN_COLLECTION_README.md | Markdown | API Testing | QA/Dev | postman_test/ |

---

## 🔍 Cómo Visualizar los Diagramas UML (.puml)

### Opción 1: VS Code (Recomendado)
```
1. Instala extensión "PlantUML" (jebbs.plantuml)
   o "PlantUML Previewer" (eternalcoding.vscode-plantuml)
2. Click derecho en archivo .puml → "Preview PlantUML"
3. Se abre vista previa del diagrama
```

### Opción 2: Sitio Web
```
1. Ve a https://www.plantuml.com/plantuml/uml/
2. Abre archivo .puml en editor de texto
3. Copia todo el contenido (Ctrl+A, Ctrl+C)
4. Pega en el editor online
5. Diagrama se genera automáticamente
```

### Opción 3: Línea de Comandos
```
1. Instala PlantUML: choco install plantuml
2. Ejecuta: plantuml component_diagram.puml
3. Se genera: component_diagram.png
```

---

## 🏗️ Stack Tecnológico en Esta Documentación

### Backend
- **Framework:** Spring Boot 2.5.14 LTS
- **Lenguaje:** Java 11 LTS
- **Build:** Gradle 8.9
- **Base Datos:** H2 1.4.200 (In-Memory)

### Seguridad
- **Autenticación:** JWT (JJWT 0.11.5)
- **Hash Contraseña:** BCrypt (cost factor 10)
- **Framework Seguridad:** Spring Security 5.5.x

### Testing
- **Herramienta:** Postman v10+
- **Casos:** 12 test cases (3 éxito, 8 error, 1 duplicado)

### Documentación
- **Markdown:** Archivos .md para referencias
- **UML Diagrams:** PlantUML (.puml files)
  - UML 2.5 Standard
  - Compatible con VS Code, navegadores y CLI

---

## 📊 Estadísticas de la Documentación

| Métrica | Valor |
|---------|-------|
| Documentos Markdown | 4 |
| Diagramas UML (PUML) | 4 |
| Líneas de Documentación | ~1200+ |
| Diagramas Componentes | 1 |
| Diagramas Secuencias | 2 |
| Diagramas Clases | 1 |
| Casos de Prueba | 12 |
| Ejemplos JSON | 25+ |
| Cobertura de Código | 100% de endpoints |

---

## ✅ Checklist de Lectura

Marca aquí mientras lees la documentación:

- [ ] README_BUILD.md leído
- [ ] COMPONENT_DIAGRAM.md leído + component_diagram.puml visualizado
- [ ] SEQUENCE_DIAGRAM.md leído + sequence_signup.puml visualizado
- [ ] sequence_login.puml visualizado
- [ ] entity_class_diagram.puml visualizado
- [ ] Proyecto compilado exitosamente
- [ ] Servidor iniciado en puerto 8080
- [ ] Colección Postman importada
- [ ] Todos los 12 test cases ejecutados
- [ ] Todas las respuestas validadas

---

## 🔄 Mantenimiento de Documentación

**Última revisión:** Enero 2026  
**Próxima revisión planificada:** Cuando se añadan nuevos endpoints

**Para actualizar la documentación:**
1. Editar archivos .md en src/main/resources/docs/
2. Actualizar archivos .puml si hay cambios en arquitectura
3. Ejecutar todos los 12 test cases para validar
4. Verificar que los diagramas reflejen cambios
5. Actualizar este README si es necesario

---

## 🙋 Preguntas Frecuentes

**P: ¿Dónde descargo Java 11?**  
R: https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html

**P: ¿Cómo instalo Postman?**  
R: https://www.postman.com/downloads/

**P: ¿Cómo visualizo los diagramas .puml?**  
R: Ver sección "Cómo Visualizar los Diagramas UML (.puml)" arriba

**P: ¿Puedo cambiar el puerto 8080?**  
R: Sí, edita `src/main/resources/application.properties` - `server.port=XXXX`

**P: ¿La BD persiste los datos?**  
R: No, H2 es in-memory. Se reinicia con cada restart de la aplicación.

**P: ¿Cómo cambio la duración del JWT?**  
R: En `JwtTokenProvider.java`, modifica `EXPIRATION_TIME = 86400000` (ms)

---

## 📧 Información de Contacto

**Proyecto:** BCI Microservice  
**Equipo:** GlobalLogic  
**Versión API:** 1.0  
**Estado:** ✅ Producción  

---

*Última actualización: Enero 2026*  
*Mantenido por: Equipo de Desarrollo GlobalLogic*  
*Diagramas UML: PlantUML (.puml files)*

---

### 4. **Postman Collection** (Carpeta postman_test/)
**Ubicación:** `./postman_test/`

Contiene:
- 📄 **POSTMAN_COLLECTION_README.md** - Documentación de 12 casos de prueba
- 📦 **BCI Microservice API.postman_collection.json** - Colección ejecutable

**12 Casos de Prueba:**
- 3 casos de registro exitoso
- 4 casos de errores de validación
- 1 caso de email duplicado
- 1 caso de login exitoso
- 3 casos de errores de login

**Cómo usar:**
1. Abre Postman
2. Click en "Import"
3. Selecciona el JSON
4. Ejecuta los 12 casos en orden recomendado

**Tiempo para ejecutar:** ~2 minutos

---

## 🎯 Camino de Lectura Recomendado

### Para Principiantes
```
1. README_BUILD.md (5 min)
   ↓ Entender requisitos y cómo ejecutar
   
2. POSTMAN_COLLECTION_README.md (5 min)
   ↓ Ver cómo funciona la API
   
3. Ejecutar colección Postman (2 min)
   ↓ Validar que todo funciona
   
Tiempo total: ~12 minutos
```

### Para Desarrolladores Mid-Level
```
1. README_BUILD.md (5 min)
   ↓
2. COMPONENT_DIAGRAM.md (15 min)
   ↓ Entender arquitectura
   
3. POSTMAN_COLLECTION_README.md (5 min)
   ↓
4. SEQUENCE_DIAGRAM.md (15 min)
   ↓ Detalles de implementación
   
5. Revisar código fuente en src/main/java/ (20 min)
   
Tiempo total: ~60 minutos
```

### Para Arquitectos/Tech Leads
```
1. COMPONENT_DIAGRAM.md (15 min)
   ↓ Visión general de la arquitectura
   
2. SEQUENCE_DIAGRAM.md (20 min)
   ↓ Flujos de negocio
   
3. README_BUILD.md (5 min)
   ↓ Validar stack tecnológico
   
4. Revisar build.gradle (10 min)
   ↓ Dependencias y configuración
   
Tiempo total: ~50 minutos
```

---

## 🔗 Tabla de Contenidos Rápida

| Documento | Tema | Audiencia | Actualización |
|-----------|------|-----------|--------------|
| README_BUILD.md | Build & Deployment | Todos | ✅ 2024-01-15 |
| COMPONENT_DIAGRAM.md | Arquitectura | Dev Senior+ | ✅ 2024-01-15 |
| SEQUENCE_DIAGRAM.md | Flujos Detallados | Dev Mid+ | ✅ 2024-01-15 |
| POSTMAN_COLLECTION_README.md | API Testing | QA/Dev | ✅ 2024-01-15 |

---

## 🏗️ Stack Tecnológico en Esta Documentación

### Backend
- **Framework:** Spring Boot 2.5.14 LTS
- **Lenguaje:** Java 11 LTS
- **Build:** Gradle 8.9
- **Base Datos:** H2 1.4.200 (In-Memory)

### Seguridad
- **Autenticación:** JWT (JJWT 0.11.5)
- **Hash Contraseña:** BCrypt (cost factor 10)
- **Framework Seguridad:** Spring Security 5.5.x

### Testing
- **Herramienta:** Postman v10+
- **Casos:** 12 test cases (3 éxito, 8 error, 1 duplicado)

---

## 📊 Estadísticas de la Documentación

| Métrica | Valor |
|---------|-------|
| Documentos Totales | 4 |
| Líneas de Documentación | ~1200 |
| Diagramas UML | 2 (Component + Sequence) |
| Casos de Prueba | 12 |
| Ejemplos JSON | 25+ |
| Cobertura de Código | 100% de endpoints |

---

## ✅ Checklist de Lectura

Marca aquí mientras lees la documentación:

- [ ] README_BUILD.md leído
- [ ] Proyecto compilado exitosamente
- [ ] Servidor iniciado en puerto 8080
- [ ] COMPONENT_DIAGRAM.md leído
- [ ] SEQUENCE_DIAGRAM.md leído
- [ ] Colección Postman importada
- [ ] Todos los 12 test cases ejecutados
- [ ] Todas las respuestas validadas

---

## 🔄 Mantenimiento de Documentación

**Última revisión:** Enero 2024  
**Próxima revisión planificada:** Cuando se añadan nuevos endpoints

**Para actualizar la documentación:**
1. Editar archivos .md en src/main/resources/docs/
2. Actualizar colección Postman si hay cambios en API
3. Ejecutar todos los 12 test cases para validar
4. Verificar que los diagramas UML reflejen cambios
5. Actualizar este README si es necesario

---

## 🙋 Preguntas Frecuentes

**P: ¿Dónde descargo Java 11?**  
R: https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html

**P: ¿Cómo instalo Postman?**  
R: https://www.postman.com/downloads/

**P: ¿Puedo cambiar el puerto 8080?**  
R: Sí, edita `src/main/resources/application.properties` - `server.port=XXXX`

**P: ¿La BD persiste los datos?**  
R: No, H2 es in-memory. Se reinicia con cada restart de la aplicación.

**P: ¿Cómo cambio la duración del JWT?**  
R: En `JwtTokenProvider.java`, modifica `EXPIRATION_TIME = 86400000` (ms)

---

## 📧 Información de Contacto

**Proyecto:** BCI Microservice  
**Equipo:** GlobalLogic  
**Versión API:** 1.0  
**Estado:** ✅ Producción  

---

*Última actualización: Enero 2024*  
*Mantenido por: Equipo de Desarrollo GlobalLogic*

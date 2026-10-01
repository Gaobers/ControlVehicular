# DOCUMENTACIÓN BACKEND - Historia de Usuario CV-19: Registrar Kilometraje

---

## 1. Resumen de la Historia de Usuario

| Atributo | Descripción |
|----------|-------------|
| **Código** | CV-19 |
| **Título** | Registrar kilometraje |
| **Rol** | Cliente |
| **Descripción** | Como cliente, quiero consultar mis vehículos y actualizar el kilometraje actual de uno de ellos, para mantener el registro actualizado de su uso. |

**Criterio de aceptación crítico:** El nuevo kilometraje **no puede ser menor** al kilometraje actual guardado. Si se intenta ingresar un valor menor, el sistema debe retornar **HTTP 400 (Bad Request)** con mensaje descriptivo. Si es mayor o igual, se actualiza y retorna **HTTP 200 (OK)**.

---

## 2. Arquitectura y Estructura del Código

El módulo sigue una arquitectura en capas simple (Controller → Service → Repository → Model) con **Spring Data JPA + MySQL**.

```
src/main/java/org/esfe/
├── modelos/
│   └── Vehiculo.java                 # Entidad JPA (@Entity, @Table, @Id, @GeneratedValue)
├── dtos/
│   ├── KilometrajeRequest.java       # DTO de entrada (PATCH body)
│   └── RespuestaDTO.java             # Wrapper de respuesta estandarizado
├── repositorios/
│   └── VehiculoRepository.java       # Extiende JpaRepository<Vehiculo, Long>
├── servicio/
│   └── implementaciones/
│       └── VehiculoService.java      # Lógica de negocio + delega a JpaRepository
└── controladores/
    └── VehiculoController.java       # Endpoints REST + manejo HTTP
```

### Responsabilidades por clase

| Clase | Paquete | Responsabilidad |
|-------|---------|-----------------|
| **Vehiculo** | `modelos` | Entidad que representa un vehículo: `id`, `usuarioId`, `placa`, `marca`, `modelo`, `kilometrajeActual`. Anotada con Lombok (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`). |
| **KilometrajeRequest** | `dtos` | DTO para recibir el cuerpo de la petición PATCH: `{ "kilometraje": 90000 }`. |
| **RespuestaDTO\<T>** | `dtos` | Wrapper genérico para unificar respuestas: `{ exito: boolean, mensaje: string, datos: T }`. |
| **VehiculoRepository** | `repositorios` | Extiende `JpaRepository<Vehiculo, Long>`. Proveé CRUD automático + `findByUsuarioId(Long)`. |
| **VehiculoService** | `servicio.implementaciones` | - Inyecta `VehiculoRepository` (JPA).<br>- `obtenerPorUsuario(usuarioId)`: delega a `repository.findByUsuarioId()`.<br>- `actualizarKilometraje(id, nuevoKm)`: valida regla de negocio, usa `repository.findById()` y `repository.save()`. |
| **VehiculoController** | `controladores` | - Expone endpoints REST con `@CrossOrigin(origins = "*")`.<br>- Maneja códigos HTTP: 200, 400, 404.<br>- Delega lógica al service y mapea respuestas a `RespuestaDTO`. |

---

## 3. Endpoints Implementados

### 3.1 GET /api/vehiculos/usuario/{usuarioId}

**Propósito:** Obtener la lista de vehículos asociados a un usuario.

| Parámetro | Tipo | Ubicación | Descripción |
|-----------|------|-----------|-------------|
| `usuarioId` | Long | Path | Identificador del usuario propietario |

**Respuesta exitosa (HTTP 200 OK):**

```json
{
  "exito": true,
  "mensaje": "Vehículos encontrados",
  "datos": [
    {
      "id": 1,
      "usuarioId": 100,
      "placa": "ABC-123",
      "marca": "Toyota",
      "modelo": "Corolla",
      "kilometrajeActual": 50000
    },
    {
      "id": 2,
      "usuarioId": 100,
      "placa": "XYZ-789",
      "marca": "Honda",
      "modelo": "Civic",
      "kilometrajeActual": 75000
    }
  ]
}
```

**Respuesta sin vehículos (HTTP 200 OK):**
```json
{
  "exito": true,
  "mensaje": "Vehículos encontrados",
  "datos": []
}
```

---

### 3.2 PATCH /api/vehiculos/{id}/kilometraje

**Propósito:** Actualizar el kilometraje actual de un vehículo específico.

| Parámetro | Tipo | Ubicación | Descripción |
|-----------|------|-----------|-------------|
| `id` | Long | Path | Identificador del vehículo a actualizar |

**Request Body (JSON):**

```json
{
  "kilometraje": 55000
}
```

| Campo | Tipo | Requerido | Validación |
|-------|------|-----------|------------|
| `kilometraje` | Integer | Sí | Debe ser ≥ kilometraje actual del vehículo |

---

#### Respuesta exitosa (HTTP 200 OK)

```json
{
  "exito": true,
  "mensaje": "Kilometraje actualizado correctamente",
  "datos": {
    "id": 1,
    "usuarioId": 100,
    "placa": "ABC-123",
    "marca": "Toyota",
    "modelo": "Corolla",
    "kilometrajeActual": 55000
  }
}
```

---

#### Respuesta de error: Kilometraje inferior (HTTP 400 Bad Request)

```json
{
  "exito": false,
  "mensaje": "El nuevo kilometraje (40000) no puede ser menor al actual (50000)",
  "datos": null
}
```

---

#### Respuesta de error: Vehículo no encontrado (HTTP 404 Not Found)

```json
{
  "exito": false,
  "mensaje": "Vehículo no encontrado",
  "datos": null
}
```

---

#### Respuesta de error: Cuerpo inválido (HTTP 400 Bad Request)

```json
{
  "exito": false,
  "mensaje": "El kilometraje es requerido",
  "datos": null
}
```

---

## 4. Reglas de Negocio Aplicadas

### 4.1 Validación de Kilometraje

> **Regla:** `nuevoKilometraje >= kilometrajeActual`

- Se evalúa en `VehiculoService.actualizarKilometraje()`.
- Si `nuevoKilometraje < kilometrajeActual` → retorna `RespuestaDTO` con `exito=false` y mensaje descriptivo.
- El controlador mapea este caso a **HTTP 400**.
- Si `nuevoKilometraje >= kilometrajeActual` → actualiza el campo y retorna **HTTP 200** con el vehículo actualizado.

### 4.2 Persistencia JPA/MySQL

- **ORM:** Spring Data JPA con Hibernate (`spring-boot-starter-data-jpa`).
- **Base de datos:** MySQL (configurado en `application.properties`).
- **Tabla:** `vehiculos` creada/actualizada automáticamente por `spring.jpa.hibernate.ddl-auto=update`.
- **Entidad:** `Vehiculo` anotada con `@Entity`, `@Table(name = "vehiculos")`, `@Id`, `@GeneratedValue(IDENTITY)`.
- **Repositorio:** `VehiculoRepository` extiende `JpaRepository<Vehiculo, Long>` — provee CRUD + `findByUsuarioId()`.
- **Alcance:** Datos persisten en BD real; sobreviven a reinicios del servicio.
- **IDs:** Auto-generados por MySQL (`AUTO_INCREMENT`).

### 4.3 Datos Iniciales (Tabla `vehiculos` en MySQL)

Los datos se insertan directamente en la base de datos (vía script SQL, Flyway, o manualmente). Ejemplo de estructura:

```sql
INSERT INTO vehiculos (usuario_id, placa, marca, modelo, kilometraje_actual) VALUES
(100, 'ABC-123', 'Toyota', 'Corolla', 50000),
(100, 'XYZ-789', 'Honda', 'Civic', 75000),
(200, 'DEF-456', 'Ford', 'Focus', 30000);
```

> **Nota:** Al usar `ddl-auto=update`, Hibernate crea la tabla automáticamente al iniciar. Los datos de prueba deben insertarse aparte (ej. `data.sql`, migración Flyway, o manual en DBeaver).

---

## 5. Guía de Pruebas para Postman

### Configuración previa
- **Base URL:** `http://localhost:8080`
- **Header común:** `Content-Type: application/json`

---

### Prueba 1: Consultar vehículos de un usuario (GET)

1. **Método:** `GET`
2. **URL:** `{{baseUrl}}/api/vehiculos/usuario/100`
3. **Headers:** `Content-Type: application/json`
4. **Enviar**
5. **Resultado esperado:** HTTP 200 + JSON con array de 2 vehículos (Toyota y Honda)

**Variante:** Cambiar `usuarioId` a `200` → retorna 1 vehículo (Ford). Usar `999` → retorna array vacío `[]`.

---

### Prueba 2: Actualizar kilometraje válido (PATCH - éxito)

1. **Método:** `PATCH`
2. **URL:** `{{baseUrl}}/api/vehiculos/1/kilometraje`
3. **Headers:** `Content-Type: application/json`
4. **Body (raw → JSON):**
   ```json
   {
     "kilometraje": 55000
   }
   ```
5. **Enviar**
6. **Resultado esperado:** HTTP 200 + vehículo con `kilometrajeActual: 55000`

**Verificación:** Repetir Prueba 1 (GET usuario 100) → el Toyota ahora muestra 55,000 km.

---

### Prueba 3: Actualizar kilometraje inválido (PATCH - error 400)

1. **Método:** `PATCH`
2. **URL:** `{{baseUrl}}/api/vehiculos/1/kilometraje`
3. **Headers:** `Content-Type: application/json`
4. **Body (raw → JSON):**
   ```json
   {
     "kilometraje": 40000
   }
   ```
5. **Enviar**
6. **Resultado esperado:** HTTP 400 + mensaje:
   ```json
   {
     "exito": false,
     "mensaje": "El nuevo kilometraje (40000) no puede ser menor al actual (55000)",
     "datos": null
   }
   ```

---

### Prueba 4: Actualizar vehículo inexistente (PATCH - error 404)

1. **Método:** `PATCH`
2. **URL:** `{{baseUrl}}/api/vehiculos/999/kilometraje`
3. **Headers:** `Content-Type: application/json`
4. **Body (raw → JSON):**
   ```json
   {
     "kilometraje": 10000
   }
   ```
5. **Enviar**
6. **Resultado esperado:** HTTP 404 + mensaje `"Vehículo no encontrado"`

---

### Prueba 5: Body vacío o campo faltante (PATCH - error 400)

1. **Método:** `PATCH`
2. **URL:** `{{baseUrl}}/api/vehiculos/1/kilometraje`
3. **Headers:** `Content-Type: application/json`
4. **Body (raw → JSON):**
   ```json
   {}
   ```
5. **Enviar**
6. **Resultado esperado:** HTTP 400 + mensaje `"El kilometraje es requerido"`

---

## Notas Técnicas Adicionales

- **CORS:** Configurado globalmente en el controlador con `@CrossOrigin(origins = "*")` para permitir conexiones desde React Native (o cualquier origen).
- **Base de datos:** MySQL con Spring Data JPA (requiere MySQL corriendo en localhost:3306).
- **Tabla auto-creada:** `vehiculos` se genera/actualiza al iniciar por `spring.jpa.hibernate.ddl-auto=update`.
- **Java:** 21 | **Spring Boot:** 4.1.1 | **Build:** Maven
- **Compilación:** `./mvnw compile` (exitoso)
- **Ejecución:** `./mvnw spring-boot:run` (requiere MySQL activo y credenciales correctas en `application.properties`)
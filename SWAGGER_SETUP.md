# Documentación Swagger/OpenAPI - Product Purchasing System

## 📋 Resumen

Se ha implementado una documentación completa de Swagger/OpenAPI en el proyecto con anotaciones limpias y perfectas.

## 🔧 Anotaciones Implementadas

### 1. **@Tag** - Etiquetas de Agrupación
Define categorías para agrupar operaciones relacionadas en la interfaz de Swagger.

**Ejemplo:**
```java
@Tag(
    name = "Productos",
    description = "Operaciones relacionadas con el catálogo de productos..."
)
public class ProductController { }
```

**Ubicaciones:**
- `ProductController` → "Productos"
- `AuthController` → "Autenticación"
- `CartController` → "Carrito de Compras"
- `OrderController` → "Órdenes"
- `CategoryController` → "Categorías"
- `UserProfileController` → "Perfil de Usuario"
- `AddressController` → "Direcciones de Usuario"
- `AdminProductController` → "Administración - Productos"
- `AdminUserController` → "Administración - Usuarios"

---

### 2. **@Operation** - Descripción de Operaciones
Proporciona detalles específicos de cada endpoint.

**Ejemplo:**
```java
@Operation(
    summary = "Listar productos",
    description = "Obtiene una lista de productos disponibles con opciones de búsqueda..."
)
@GetMapping
public List<ProductResponse> listProducts() { }
```

**Características:**
- `summary`: Título corto y conciso (máx 120 caracteres)
- `description`: Descripción detallada de la funcionalidad

---

### 3. **@ApiResponses** - Respuestas HTTP
Define todas las posibles respuestas HTTP que puede retornar un endpoint.

**Ejemplo:**
```java
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Operación exitosa"),
    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
    @ApiResponse(responseCode = "401", description = "No autenticado"),
    @ApiResponse(responseCode = "500", description = "Error interno")
})
public List<ProductResponse> listProducts() { }
```

---

### 4. **@ApiResponse** - Respuesta Individual
Especifica detalles de cada respuesta posible, incluyendo parámetros de contenido.

**Ejemplo con Content y Schema:**
```java
@ApiResponse(
    responseCode = "201",
    description = "Producto creado exitosamente",
    content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ProductResponse.class)
    )
)
```

**Códigos HTTP Standard:**
- `200` - OK (GET, PUT, PATCH exitosos)
- `201` - Created (POST exitosos)
- `204` - No Content (Operaciones sin retorno)
- `400` - Bad Request (Datos inválidos)
- `401` - Unauthorized (Sin autenticación)
- `403` - Forbidden (Sin permisos)
- `404` - Not Found (Recurso no existe)
- `500` - Internal Server Error

---

### 5. **@Parameter** - Parámetros de Entrada
Documenta parámetros de path, query, header, etc.

**Ejemplo:**
```java
@Parameter(
    name = "id",
    description = "ID único del producto a consultar",
    example = "1",
    required = true
)
@PathVariable Long id
```

**Tipos de Parámetros:**
- **@PathVariable**: En la URL (`/products/{id}`)
- **@RequestParam**: Query parameters (`?search=laptop`)
- **@RequestHeader**: Headers HTTP (`Authorization: Bearer...`)

---

### 6. **@Content** - Tipos de Contenido
Define la estructura del contenido de respuesta/solicitud.

**Ejemplo:**
```java
@Content(
    mediaType = MediaType.APPLICATION_JSON_VALUE,
    schema = @Schema(implementation = ProductResponse.class)
)
```

**Media Types:**
- `application/json` - JSON
- `application/xml` - XML
- `text/plain` - Texto plano

---

### 7. **@Schema** - Esquema de Datos
Define la estructura de un objeto (DTOs, Response objects).

**Ejemplo:**
```java
@Schema(
    implementation = ProductResponse.class
)
```

**Atributos:**
- `implementation`: Clase que define el modelo
- `description`: Descripción del modelo
- `example`: Ejemplo de valor

---

### 8. **@ArraySchema** - Array de Objetos
Especifica que la respuesta es un array de elementos.

**Ejemplo:**
```java
@ApiResponse(
    responseCode = "200",
    description = "Lista de productos obtenida exitosamente",
    content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))
    )
)
```

---

## 🗂️ Estructura de Controladores Documentados

### Controladores Públicos (Sin requerimiento de rol)

1. **ProductController** ✓
   - `GET /api/v1/products` - Listar productos
   - `GET /api/v1/products/{id}` - Obtener producto

2. **CategoryController** ✓
   - `GET /api/v1/categories` - Listar categorías
   - `GET /api/v1/categories/tree` - Árbol de categorías
   - `GET /api/v1/categories/{id}` - Detalles de categoría
   - `GET /api/v1/categories/{id}/subcategories` - Subcategorías

3. **AuthController** ✓
   - `POST /api/v1/auth/guest-session` - Crear sesión invitado
   - `POST /api/v1/auth/register` - Registrarse
   - `POST /api/v1/auth/login` - Login
   - `GET /api/v1/auth/me` - Datos de usuario actual
   - `POST /api/v1/auth/logout` - Logout

### Controladores Autenticados

4. **CartController** ✓
   - `GET /api/v1/cart/me` - Obtener carrito
   - `POST /api/v1/cart/items` - Agregar producto
   - `PATCH /api/v1/cart/items/{productId}` - Actualizar cantidad
   - `DELETE /api/v1/cart/items/{productId}` - Remover producto
   - `DELETE /api/v1/cart/items` - Limpiar carrito
   - `POST /api/v1/cart/merge` - Fusionar carritos

5. **OrderController** ✓
   - `POST /api/v1/orders/checkout` - Crear orden
   - `GET /api/v1/orders/me` - Listar mis órdenes
   - `GET /api/v1/orders/{id}` - Detalles de orden

6. **UserProfileController** ✓
   - `PUT /api/v1/user-profile` - Actualizar perfil
   - `PUT /api/v1/user-profile/password` - Cambiar contraseña

7. **AddressController** ✓
   - `GET /api/v1/user-addresses` - Listar direcciones
   - `GET /api/v1/user-addresses/{id}` - Detalles de dirección
   - `POST /api/v1/user-addresses` - Agregar dirección
   - `PUT /api/v1/user-addresses/{id}` - Actualizar dirección
   - `PATCH /api/v1/user-addresses/{id}/default` - Establecer como predeterminada
   - `DELETE /api/v1/user-addresses/{id}` - Eliminar dirección

### Controladores Administrativos (Requieren rol ADMIN)

8. **AdminProductController** ✓
   - `POST /api/v1/admin/products` - Crear producto
   - `PUT /api/v1/admin/products/{id}` - Actualizar producto
   - `DELETE /api/v1/admin/products/{id}` - Eliminar producto

9. **AdminUserController** ✓
   - `POST /api/v1/admin/users` - Crear usuario
   - `GET /api/v1/admin/users` - Listar usuarios
   - `GET /api/v1/admin/users/{id}` - Detalles de usuario
   - `PUT /api/v1/admin/users/{id}` - Actualizar usuario
   - `DELETE /api/v1/admin/users/{id}` - Eliminar usuario

---

## 🔐 Configuración de Seguridad

### OpenApiConfig.java
Archivo de configuración global de OpenAPI ubicado en `config/OpenApiConfig.java`.

**Características:**
- Define esquema de seguridad JWT Bearer
- Configura información de la API
- Especifica contacto y licencia

**Endpoint Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

**Endpoint OpenAPI JSON:**
```
http://localhost:8080/v3/api-docs
```

---

## 📝 Mejores Prácticas Implementadas

### 1. **Claridad en Resúmenes**
- `summary`: Descripción clara y concisa (máx 120 caracteres)
- Formato imperativo: "Listar productos" vs "Listando productos"

### 2. **Descripciones Comprensivas**
- Explican qué hace la operación
- Mencionan requisitos (autenticación, permisos)
- Indican estado de datos (solo activos, etc.)

### 3. **Ejemplos Realistas**
- IDs de ejemplo: `"1"`, `"2"`, etc.
- Tokens de ejemplo para endpoints protegidos
- Valores realistas para búsquedas: `"laptop"`, `"mouse"`

### 4. **Respuestas Consistentes**
Todos los endpoints definen estas respuestas estándar:
- `200` - Éxito (GET, PUT, PATCH)
- `201` - Recurso creado (POST)
- `204` - Éxito sin contenido (DELETE)
- `400` - Entrada inválida
- `401` - No autenticado
- `403` - No autorizado (solo admin)
- `404` - No encontrado
- `500` - Error interno

### 5. **Documentación de Headers**
Todos los endpoints autenticados documentan:
```java
@Parameter(
    name = HttpHeaders.AUTHORIZATION,
    description = "Token JWT en formato: Bearer <token>",
    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
)
@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
```

---

## 🚀 Cómo Usar

### 1. **Verificar Swagger UI**
- Inicia la aplicación
- Accede a `http://localhost:8080/swagger-ui.html`
- Verifica que todas las operaciones se visualicen correctamente

### 2. **Probar Endpoints**
- Usa la interfaz "Try it out" en Swagger UI
- Proporciona ejemplos de entrada
- Visualiza respuestas en tiempo real

### 3. **Integración Frontend**
```bash
# Generar cliente desde OpenAPI
npx @openapitools/openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-axios \
  -o ./api-client
```

---

## 📦 Dependencias

**Ya incluida en pom.xml:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.8</version>
</dependency>
```

---

## 🔍 Anotaciones por Tipo

### De Controlador
- `@Tag` - Agrupa operaciones por categoría

### De Método/Endpoint
- `@Operation` - Describe la operación
- `@ApiResponses` - Define múltiples respuestas posibles
- `@SecurityRequirement` - Especifica requerimientos de seguridad

### De Parámetros
- `@Parameter` - Documenta un parámetro individual
- `@Parameters` - Agrupa múltiples parámetros

### De Respuesta
- `@ApiResponse` - Respuesta individual
- `@Content` - Tipo de contenido de la respuesta
- `@Schema` - Estructura del objeto
- `@ArraySchema` - Array de objetos

---

## ✅ Estado Actual

**✓ Todos los controladores documentados:**
- 9 controladores
- 35+ endpoints
- 100+ anotaciones Swagger

**Características incluidas:**
- ✓ Documentación de todos los endpoints
- ✓ Ejemplos de parámetros y respuestas
- ✓ Códigos HTTP completos
- ✓ Esquema de seguridad JWT
- ✓ Descripción detallada de cada operación
- ✓ Documentación de errores

---

## 📚 Referencias

- **SpringDoc OpenAPI:** https://springdoc.org/
- **OpenAPI Specification:** https://spec.openapis.org/oas/latest
- **Swagger UI:** https://swagger.io/tools/swagger-ui/

---

**Fecha de implementación:** 2026-04-29  
**Versión:** 1.0.0  
**Autor:** Sistema de Anotaciones Swagger


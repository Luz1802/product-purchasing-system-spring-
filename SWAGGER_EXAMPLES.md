# Ejemplos Prácticos de Anotaciones Swagger/OpenAPI

## 1. Anotación @Tag - Agrupación de Endpoints

### Propósito
Agrupa operaciones relacionadas bajo una sola categoría en la UI de Swagger.

### Ejemplo Implementado
```java
@RestController
@RequestMapping("/api/v1/products")
@Tag(
    name = "Productos",
    description = "Operaciones relacionadas con el catálogo de productos. " +
            "Permite listar, buscar y consultar detalles de productos disponibles."
)
public class ProductController {
    // ... métodos
}
```

### Resultado en Swagger UI
- Se crea una sección collapsible llamada "Productos"
- Todos los endpoints de ProductController aparecen bajo esta etiqueta
- La descripción se muestra al expandir la sección

### Atributos Disponibles
```java
@Tag(
    name = "Nombre visible en Swagger",
    description = "Descripción detallada",
    externalDocs = @ExternalDocumentation(url = "https://...")
)
```

---

## 2. Anotación @Operation - Descripción de Endpoint

### Propósito
Proporciona detalles específicos sobre qué hace cada endpoint.

### Ejemplo Implementado
```java
@GetMapping
@Operation(
    summary = "Listar productos",
    description = "Obtiene una lista de productos disponibles con opciones de búsqueda, " +
            "filtrado por categoría y estado de activación."
)
public List<ProductResponse> listProducts() { }
```

### Resultado en Swagger UI
- El `summary` aparece como título del endpoint
- Al hacer clic, se expande mostrando la `description`

### Atributos Disponibles
```java
@Operation(
    summary = "...",           // Máx 120 caracteres
    description = "...",       // Descripción larga
    operationId = "customId",  // ID único de operación
    deprecated = false,        // Marcar como deprecado
    tags = {"Tag1", "Tag2"}    // Tags adicionales
)
```

---

## 3. Anotación @Parameter - Documentar Parámetros

### Propósito
Documenta cada parámetro de entrada (path, query, header).

### Ejemplo 1: Path Variable
```java
@GetMapping("/{id}")
@Operation(summary = "Obtener producto")
public ProductResponse getProduct(
    @Parameter(
        name = "id",
        description = "ID único del producto",
        example = "1",
        required = true
    )
    @PathVariable Long id
) { }
```

### Ejemplo 2: Query Parameter
```java
@GetMapping
@Operation(summary = "Listar productos")
public List<ProductResponse> listProducts(
    @Parameter(
        name = "search",
        description = "Término de búsqueda",
        example = "laptop"
    )
    @RequestParam(required = false) String search
) { }
```

### Ejemplo 3: Header (Autenticación)
```java
@GetMapping("/me")
@Operation(summary = "Datos del usuario actual")
public UserResponse getCurrentUser(
    @Parameter(
        name = "Authorization",
        description = "Token JWT en formato: Bearer <token>",
        example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
    String authorizationHeader
) { }
```

### Atributos Disponibles
```java
@Parameter(
    name = "...",              // Nombre del parámetro
    description = "...",       // Descripción
    example = "...",           // Ejemplo de valor
    required = true,           // ¿Es obligatorio?
    deprecated = false,        // ¿Está deprecado?
    schema = @Schema(...)      // Tipo y estructura
)
```

---

## 4. Anotación @ApiResponse - Respuesta Individual

### Propósito
Documenta una respuesta específica con su código HTTP y contenido.

### Ejemplo 1: Respuesta 200 OK con Lista
```java
@ApiResponse(
    responseCode = "200",
    description = "Lista de productos obtenida exitosamente",
    content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        array = @ArraySchema(
            schema = @Schema(implementation = ProductResponse.class)
        )
    )
)
```

**Resultado JSON:**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "price": 999.99,
    "isActive": true,
    "categoryId": 1
  }
]
```

### Ejemplo 2: Respuesta 201 Created
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

### Ejemplo 3: Respuesta de Error
```java
@ApiResponse(
    responseCode = "404",
    description = "Producto no encontrado"
)
```

### Códigos HTTP Comunes
```
200 - OK (GET exitoso)
201 - Created (POST exitoso)
204 - No Content (DELETE exitoso)
400 - Bad Request (Datos inválidos)
401 - Unauthorized (Sin autenticación)
403 - Forbidden (Sin permisos)
404 - Not Found (Recurso no existe)
500 - Server Error (Error interno)
```

---

## 5. Anotación @ApiResponses - Múltiples Respuestas

### Propósito
Agrupa múltiples @ApiResponse para definir todas las respuestas posibles de un endpoint.

### Ejemplo Completo
```java
@GetMapping("/{id}")
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Detalles del producto obtenidos exitosamente",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "404",
        description = "El producto no existe o está inactivo"
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor"
    )
})
public ProductResponse getProduct(@PathVariable Long id) { }
```

### Patrón Recomendado
```
- 2xx: Éxito
  - 200 OK
  - 201 Created
  - 204 No Content
  
- 4xx: Cliente cometió error
  - 400 Bad Request (datos inválidos)
  - 401 Unauthorized (no autenticado)
  - 403 Forbidden (sin permisos)
  - 404 Not Found (no existe)
  
- 5xx: Servidor falló
  - 500 Internal Server Error
```

---

## 6. Anotación @Content - Tipo de Contenido

### Propósito
Define el tipo MIME y estructura del contenido de una respuesta.

### Ejemplo 1: JSON Object
```java
@Content(
    mediaType = MediaType.APPLICATION_JSON_VALUE,
    schema = @Schema(implementation = ProductResponse.class)
)
```

### Ejemplo 2: JSON Array
```java
@Content(
    mediaType = MediaType.APPLICATION_JSON_VALUE,
    array = @ArraySchema(
        schema = @Schema(implementation = ProductResponse.class)
    )
)
```

### Ejemplo 3: XML
```java
@Content(
    mediaType = "application/xml",
    schema = @Schema(implementation = ProductResponse.class)
)
```

### Media Types Comunes
```
application/json       → JSON
application/xml        → XML
text/plain            → Texto plano
text/html             → HTML
application/pdf       → PDF
multipart/form-data   → Formularios
```

---

## 7. Anotación @Schema - Estructura de Datos

### Propósito
Define la estructura de un objeto (sus propiedades y tipos).

### Ejemplo en DTO
```java
@Schema(
    name = "ProductResponse",
    description = "Información de un producto en formato respuesta"
)
public record ProductResponse(
    @Schema(
        description = "ID único del producto",
        example = "1"
    )
    Long id,
    
    @Schema(
        description = "Nombre del producto",
        example = "MacBook Pro"
    )
    String name,
    
    @Schema(
        description = "Precio en USD",
        example = "1299.99"
    )
    BigDecimal price,
    
    @Schema(
        description = "¿Está disponible?",
        example = "true"
    )
    Boolean isActive
) {}
```

### En Respuesta de Endpoint
```java
@ApiResponse(
    responseCode = "200",
    description = "Producto obtenido",
    content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ProductResponse.class)
    )
)
```

### Atributos Disponibles
```java
@Schema(
    name = "...",              // Nombre del schema
    description = "...",       // Descripción
    example = "...",           // Ejemplo
    type = "string",           // Tipo
    required = true,           // ¿Requerido?
    minimum = "0",             // Valor mínimo
    maximum = "100",           // Valor máximo
    pattern = "^[A-Z].*",     // Patrón regex
    format = "email"           // Formato específico
)
```

---

## 8. Anotación @ArraySchema - Arrays de Objetos

### Propósito
Especifica que una respuesta es un array de elementos.

### Ejemplo 1: Lista Simple
```java
@ApiResponse(
    responseCode = "200",
    description = "Lista de productos",
    content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        array = @ArraySchema(
            schema = @Schema(implementation = ProductResponse.class)
        )
    )
)
```

**Resultado JSON:**
```json
[
  { "id": 1, "name": "Producto 1", ... },
  { "id": 2, "name": "Producto 2", ... }
]
```

### Ejemplo 2: Con Tamaño Mínimo
```java
@ArraySchema(
    minItems = 1,
    maxItems = 100,
    schema = @Schema(implementation = ProductResponse.class)
)
```

### Ejemplo 3: Array Anidado
```java
@ArraySchema(
    schema = @Schema(
        implementation = CategoryResponse.class,
        description = "Lista de categorías con sus subcategorías"
    )
)
```

---

## Caso de Uso Completo: Crear Producto

```java
@RestController
@RequestMapping(ApiRoutes.ADMIN_PRODUCTS)
@Tag(
    name = "Administración - Productos",
    description = "Operaciones administrativas para gestionar productos."
)
public class AdminProductController {

    @PostMapping
    @Operation(
        summary = "Crear nuevo producto",
        description = "Crea un nuevo producto en el catálogo. Requiere permisos de administrador."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Producto creado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos del producto inválidos"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token no válido o no proporcionado"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No tiene permisos de administrador"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<ProductResponse> createProduct(
        @Parameter(
            name = "Authorization",
            description = "Token JWT en formato: Bearer <token>. Debe tener rol ADMIN",
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
        String authorizationHeader,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del producto a crear",
            required = true,
            content = @Content(
                schema = @Schema(implementation = ProductUpsertRequest.class)
            )
        )
        @Valid @RequestBody ProductUpsertRequest request
    ) {
        // Implementación del método
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogApplicationService.createProduct(request));
    }
}
```

### Cómo se ve en Swagger UI:
1. **Categoría:** "Administración - Productos"
2. **Método:** POST
3. **Endpoint:** `/api/v1/admin/products`
4. **Summary:** "Crear nuevo producto"
5. **Description:** Descripción completa
6. **Authorization:** Requiere Bearer Token con rol ADMIN
7. **Request Body:** Formulario para ProductUpsertRequest
8. **Responses:** 
   - 201 Created con ProductResponse
   - 400 Bad Request
   - 401 Unauthorized
   - 403 Forbidden
   - 500 Server Error

---

## Referencia Rápida de Anotaciones

| Anotación | Ubicación | Propósito |
|-----------|-----------|----------|
| `@Tag` | Clase | Agrupa endpoints |
| `@Operation` | Método | Describe operación |
| `@Parameter` | Parámetro | Documenta entrada |
| `@ApiResponse` | Método | Respuesta individual |
| `@ApiResponses` | Método | Múltiples respuestas |
| `@Content` | ApiResponse | Define tipo MIME |
| `@Schema` | Tipo/Propiedad | Define estructura |
| `@ArraySchema` | Content | Define array |
| `@SecurityRequirement` | Clase/Método | Requiere seguridad |
| `@RequestBody` | Parámetro | Documenta body |

---

## ✅ Checklist para Nuevo Endpoint

Cuando agregues un nuevo endpoint, asegúrate de incluir:

- [ ] `@Operation` con summary y description
- [ ] `@Parameter` para cada parámetro (path, query, header)
- [ ] `@ApiResponses` con al menos: 200/201, 400, 401 (si aplica), 500
- [ ] `@Content` y `@Schema` en respuestas exitosas
- [ ] `@ArraySchema` si la respuesta es un array
- [ ] `@RequestBody` si hay body (con `@Schema`)
- [ ] Ejemplos realistas en `@Parameter` y `@Schema`
- [ ] Descripción clara de qué requiere seguridad

---

**Última actualización:** 2026-04-29


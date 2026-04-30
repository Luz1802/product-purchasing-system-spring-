# 🎯 Guía Rápida de Referencia - Anotaciones Swagger

## 🏷️ @Tag
```java
@Tag(
    name = "Categoría API",
    description = "Descripción de la categoría"
)
public class MiController { }
```
**Resultado:** Agrupa todos los endpoints del controlador bajo una categoría

---

## 📝 @Operation
```java
@Operation(
    summary = "Corto resumen",
    description = "Descripción más detallada de qué hace esta operación"
)
@GetMapping
public List<Producto> listar() { }
```
**Resultado:** Título y descripción del endpoint en Swagger

---

## 🔢 @Parameter
```java
@Parameter(
    name = "id",
    description = "ID del recurso",
    example = "1",
    required = true
)
@PathVariable Long id
```
**Resultado:** Documenta cada parámetro con tipo, descripción y ejemplo

**Tipos de Parámetros:**
- `@PathVariable` → `/api/users/{id}`
- `@RequestParam` → `/api/users?page=1`
- `@RequestHeader` → `Authorization: Bearer token`

---

## ✅ @ApiResponse
```java
@ApiResponse(
    responseCode = "200",
    description = "Éxito",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = Producto.class)
    )
)
```
**Resultado:** Define qué devuelve el endpoint

**Códigos Comunes:**
- `200` → OK ✓
- `201` → Created ✓
- `204` → No Content ✓
- `400` → Bad Request ✗
- `401` → Unauthorized ⛔
- `403` → Forbidden 🚫
- `404` → Not Found ❌
- `500` → Server Error 💥

---

## 📋 @ApiResponses
```java
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Éxito"),
    @ApiResponse(responseCode = "400", description = "Error cliente"),
    @ApiResponse(responseCode = "500", description = "Error servidor")
})
```
**Resultado:** Define TODAS las posibles respuestas de un endpoint

---

## 📦 @Content
```java
// JSON Object
@Content(
    mediaType = "application/json",
    schema = @Schema(implementation = Producto.class)
)

// JSON Array
@Content(
    mediaType = "application/json",
    array = @ArraySchema(
        schema = @Schema(implementation = Producto.class)
    )
)
```
**Resultado:** Especifica el tipo y estructura del contenido retornado

---

## 📄 @Schema
```java
// En una clase
@Schema(description = "Información del producto")
public record Producto(
    @Schema(description = "ID único", example = "1")
    Long id,
    
    @Schema(description = "Nombre", example = "Laptop")
    String nombre,
    
    @Schema(description = "Precio", example = "999.99")
    BigDecimal precio
) {}
```
**Resultado:** Define estructura de datos que se visualiza en Swagger

---

## 📚 @ArraySchema
```java
@Content(
    mediaType = "application/json",
    array = @ArraySchema(
        minItems = 1,
        maxItems = 100,
        schema = @Schema(implementation = Producto.class)
    )
)
```
**Resultado:** Especifica que la respuesta es una lista de objetos

---

## 🔐 @SecurityRequirement
```java
@Tag(name = "Admin", description = "...")
@SecurityRequirement(name = "Bearer Token")
public class AdminController {
    // todos los endpoints requieren seguridad
}

// O en un endpoint específico
@PostMapping
@SecurityRequirement(name = "Bearer Token")
public void crear() { }
```
**Resultado:** Marca que se requiere autenticación

---

## 🔥 EJEMPLO COMPLETO

```java
@RestController
@RequestMapping("/api/products")
@Tag(
    name = "Productos",
    description = "Gestión del catálogo de productos"
)
public class ProductosController {

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener producto",
        description = "Recupera los detalles de un producto específico"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Producto.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Producto no existe"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    public Producto obtener(
        @Parameter(name = "id", description = "ID del producto", example = "1")
        @PathVariable Long id
    ) {
        // implementación
    }
}
```

---

## 🎨 En Swagger UI se ve así:

```
📦 Productos
│
└─ GET /api/products/{id}
   ├─ Título: "Obtener producto"
   ├─ Descripción: "Recupera los detalles..."
   ├─ Parámetro:
   │  └─ id (número, requerido, ej: 1)
   ├─ Respuestas:
   │  ├─ 200 OK → Producto (JSON)
   │  ├─ 404 Not Found
   │  └─ 500 Server Error
   └─ Try it out [button]
```

---

## ⚡ PATRONES RÁPIDOS

### Endpoint GET sin parámetros
```java
@GetMapping
@Operation(summary = "Listar todos")
@ApiResponses({
    @ApiResponse(responseCode = "200", 
        content = @Content(array = @ArraySchema(
            schema = @Schema(implementation = Item.class))))
})
public List<Item> listar() { }
```

### Endpoint GET con ID
```java
@GetMapping("/{id}")
@Operation(summary = "Obtener por ID")
@ApiResponses({
    @ApiResponse(responseCode = "200", 
        content = @Content(schema = @Schema(implementation = Item.class))),
    @ApiResponse(responseCode = "404", description = "No encontrado")
})
public Item obtener(@Parameter(example = "1") @PathVariable Long id) { }
```

### Endpoint POST
```java
@PostMapping
@Operation(summary = "Crear nuevo")
@ApiResponses({
    @ApiResponse(responseCode = "201", 
        content = @Content(schema = @Schema(implementation = Item.class))),
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
})
public ResponseEntity<Item> crear(
    @RequestBody @Valid ItemRequest request) { }
```

### Endpoint DELETE
```java
@DeleteMapping("/{id}")
@Operation(summary = "Eliminar")
@ApiResponses({
    @ApiResponse(responseCode = "204", description = "Eliminado"),
    @ApiResponse(responseCode = "404", description = "No encontrado")
})
public ResponseEntity<Void> eliminar(@Parameter(example = "1") @PathVariable Long id) { }
```

### Endpoint con Autenticación
```java
@GetMapping("/me")
@Operation(summary = "Mis datos")
@ApiResponses({
    @ApiResponse(responseCode = "200", 
        content = @Content(schema = @Schema(implementation = Usuario.class))),
    @ApiResponse(responseCode = "401", description = "No autenticado")
})
public Usuario misDatos(
    @Parameter(name = "Authorization", 
        description = "Bearer <token>", 
        example = "Bearer eyJ...")
    @RequestHeader(HttpHeaders.AUTHORIZATION) String auth
) { }
```

---

## 📌 CHECKLIST PARA NUEVO ENDPOINT

- [ ] ¿Tiene @Operation con summary y description?
- [ ] ¿Hay @Parameter para cada parámetro?
- [ ] ¿Tiene @ApiResponses?
- [ ] ¿Incluye respuesta 200/201?
- [ ] ¿Incluye respuesta 400 si acepta entrada?
- [ ] ¿Incluye respuesta 401 si requiere auth?
- [ ] ¿Incluye respuesta 404 si busca por ID?
- [ ] ¿Incluye respuesta 500?
- [ ] ¿Tiene ejemplos realistas?
- [ ] ¿Usa @Content con @Schema?
- [ ] ¿Usa @ArraySchema si retorna lista?

---

## 🌐 URLs en tu Proyecto

```
Swagger UI:    http://localhost:8080/swagger-ui.html
API Docs JSON: http://localhost:8080/v3/api-docs
API Docs YAML: http://localhost:8080/v3/api-docs.yaml
```

---

## 👀 Ver todas las anotaciones en un solo lugar

**Archivo principal:** `OpenApiConfig.java`
```
src/main/java/co/edu/cesde/pps/config/OpenApiConfig.java
```

**Todos los controladores modificados:**
```
src/main/java/co/edu/cesde/pps/web/controller/
├── ProductController.java ✓
├── AuthController.java ✓
├── CartController.java ✓
├── OrderController.java ✓
├── CategoryController.java ✓
├── UserProfileController.java ✓
├── AddressController.java ✓
├── AdminProductController.java ✓
└── AdminUserController.java ✓
```

---

## 💡 Tips Finales

1. **Mantén ejemplos realistas**
   - Malos: `"example": "abc"` 
   - Buenos: `"example": "usuario@email.com"`

2. **Descripciones claras**
   - Malo: "ID del producto"
   - Bueno: "ID único del producto a consultaR"

3. **Respuestas completas**
   - Siempre: 200/201, 400, 500
   - Si auth: 401
   - Si admin: 403

4. **Ejemplos de Bearer Token**
   ```
   "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   ```

5. **Arrays vs Objetos**
   - Array: `@ArraySchema(schema = @Schema(...))`
   - Objeto: `@Schema(implementation = Clase.class)`

---

**Última actualización:** 2026-04-29  
**Estado:** ✅ LISTO PARA PRODUCCIÓN


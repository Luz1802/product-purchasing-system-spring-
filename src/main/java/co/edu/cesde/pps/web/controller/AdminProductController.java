package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.CatalogApplicationService;
import co.edu.cesde.pps.web.dto.request.ProductUpsertRequest;
import co.edu.cesde.pps.web.dto.response.ProductResponse;
import co.edu.cesde.pps.web.security.AdminAccessGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.ADMIN_PRODUCTS)
@Tag(
        name = "Administración - Productos",
        description = "Operaciones administrativas para gestionar productos. " +
                "Solo accesible para usuarios con rol de administrador."
)
@SecurityRequirement(name = "Bearer Token")
public class AdminProductController {

    private final CatalogApplicationService catalogApplicationService;
    private final AdminAccessGuard adminAccessGuard;

    public AdminProductController(CatalogApplicationService catalogApplicationService,
                                  AdminAccessGuard adminAccessGuard) {
        this.catalogApplicationService = catalogApplicationService;
        this.adminAccessGuard = adminAccessGuard;
    }

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
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>. Debe tener rol ADMIN",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductUpsertRequest.class))
            )
            @Valid @RequestBody ProductUpsertRequest request) {
        adminAccessGuard.requireAdmin(authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogApplicationService.createProduct(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar producto",
            description = "Modifica la información de un producto existente. Requiere permisos de administrador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado exitosamente",
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
                    responseCode = "404",
                    description = "Producto no encontrado"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ProductResponse updateProduct(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>. Debe tener rol ADMIN",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID del producto a actualizar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del producto",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductUpsertRequest.class))
            )
            @Valid @RequestBody ProductUpsertRequest request) {
        adminAccessGuard.requireAdmin(authorizationHeader);
        return catalogApplicationService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar producto",
            description = "Borra un producto del catálogo. Requiere permisos de administrador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Producto eliminado exitosamente"
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
                    responseCode = "404",
                    description = "Producto no encontrado"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>. Debe tener rol ADMIN",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID del producto a eliminar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        adminAccessGuard.requireAdmin(authorizationHeader);
        catalogApplicationService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}


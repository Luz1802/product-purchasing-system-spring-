package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.AdminUserApplicationService;
import co.edu.cesde.pps.web.dto.request.CreateAdminUserRequest;
import co.edu.cesde.pps.web.dto.request.UpdateAdminUserRequest;
import co.edu.cesde.pps.web.dto.response.UserResponse;
import co.edu.cesde.pps.web.security.AdminAccessGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.ADMIN_USERS)
@Tag(
        name = "Administración - Usuarios",
        description = "Operaciones administrativas para gestionar usuarios del sistema. " +
                "Solo accesible para usuarios con rol de administrador."
)
@SecurityRequirement(name = "Bearer Token")
public class AdminUserController {

    private final AdminUserApplicationService adminUserApplicationService;
    private final AdminAccessGuard adminAccessGuard;

    public AdminUserController(AdminUserApplicationService adminUserApplicationService,
                               AdminAccessGuard adminAccessGuard) {
        this.adminUserApplicationService = adminUserApplicationService;
        this.adminAccessGuard = adminAccessGuard;
    }

    @PostMapping
    @Operation(
            summary = "Crear nuevo usuario",
            description = "Crea una nueva cuenta de usuario en el sistema. Requiere permisos de administrador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del usuario inválidos o email duplicado"
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
    public ResponseEntity<UserResponse> createUser(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>. Debe tener rol ADMIN",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del usuario a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateAdminUserRequest.class))
            )
            @Valid @RequestBody CreateAdminUserRequest request) {
        adminAccessGuard.requireAdmin(authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminUserApplicationService.createUser(request));
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Obtiene un listado de todos los usuarios registrados en el sistema. Requiere permisos de administrador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))
                    )
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
    public List<UserResponse> listUsers(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>. Debe tener rol ADMIN",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        adminAccessGuard.requireAdmin(authorizationHeader);
        return adminUserApplicationService.listUsers();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalles de un usuario",
            description = "Recupera la información detallada de un usuario específico. Requiere permisos de administrador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalles del usuario obtenidos exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
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
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public UserResponse getUser(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>. Debe tener rol ADMIN",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID del usuario a consultar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        adminAccessGuard.requireAdmin(authorizationHeader);
        return adminUserApplicationService.getUser(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Modifica la información de un usuario existente. Requiere permisos de administrador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del usuario inválidos"
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
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public UserResponse updateUser(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>. Debe tener rol ADMIN",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID del usuario a actualizar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del usuario",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateAdminUserRequest.class))
            )
            @Valid @RequestBody UpdateAdminUserRequest request) {
        adminAccessGuard.requireAdmin(authorizationHeader);
        return adminUserApplicationService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Borra una cuenta de usuario del sistema. Requiere permisos de administrador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado exitosamente"
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
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>. Debe tener rol ADMIN",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID del usuario a eliminar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        adminAccessGuard.requireAdmin(authorizationHeader);
        adminUserApplicationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}


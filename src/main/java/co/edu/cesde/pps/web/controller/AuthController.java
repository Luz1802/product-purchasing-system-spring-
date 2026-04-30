package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.AuthApplicationService;
import co.edu.cesde.pps.web.dto.request.LoginRequest;
import co.edu.cesde.pps.web.dto.request.RegisterRequest;
import co.edu.cesde.pps.web.dto.response.AuthSessionResponse;
import co.edu.cesde.pps.web.dto.response.UserResponse;
import co.edu.cesde.pps.web.security.CurrentSessionResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.AUTH)
@Tag(
        name = "Autenticación",
        description = "Operaciones de autenticación y autorización. " +
                "Incluye registro, login, logout y gestión de sesiones."
)
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public AuthController(AuthApplicationService authApplicationService,
                          CurrentSessionResolver currentSessionResolver) {
        this.authApplicationService = authApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @PostMapping("/guest-session")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear sesión de invitado",
            description = "Crea una nueva sesión para usuarios no autenticados (invitados). " +
                    "Retorna un token que permite acceder a funcionalidades de compras sin registro."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Sesión de invitado creada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthSessionResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public AuthSessionResponse createGuestSession() {
        return authApplicationService.createGuestSession();
    }

    @PostMapping("/register")
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario y retorna un token de sesión autenticada. " +
                    "Valida que el email sea único en el sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthSessionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o email duplicado"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AuthSessionResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para registrar un nuevo usuario",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authApplicationService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login de usuario",
            description = "Autentica un usuario y retorna un token JWT válido para acceder " +
                    "a endpoints protegidos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthSessionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public AuthSessionResponse login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales de login",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Valid @RequestBody LoginRequest request) {
        return authApplicationService.login(request);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Obtener datos del usuario actual",
            description = "Retorna la información del usuario autenticado en la sesión actual. " +
                    "Requiere token de autenticación válido en el header Authorization."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Datos del usuario obtenidos exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no válido o no proporcionado"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public UserResponse getCurrentUser(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader) {
        return authApplicationService.getCurrentUser(
                currentSessionResolver.resolveCurrentToken(authorizationHeader)
        );
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesión",
            description = "Invalida el token actual del usuario autenticado. " +
                    "Después de este endpoint, el token no podrá ser utilizado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Sesión cerrada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no válido o no proporcionado"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> logout(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader) {
        authApplicationService.logout(currentSessionResolver.resolveCurrentToken(authorizationHeader));
        return ResponseEntity.noContent().build();
    }
}


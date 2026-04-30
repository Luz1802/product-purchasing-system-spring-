package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.UserProfileApplicationService;
import co.edu.cesde.pps.web.dto.request.ChangeMyPasswordRequest;
import co.edu.cesde.pps.web.dto.request.UpdateMyProfileRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.USER_PROFILE)
@Tag(
        name = "Perfil de Usuario",
        description = "Gestión del perfil de usuario. Permite actualizar " +
                "información personal y cambiar contraseña."
)
public class UserProfileController {

    private final UserProfileApplicationService userProfileApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public UserProfileController(UserProfileApplicationService userProfileApplicationService,
                                 CurrentSessionResolver currentSessionResolver) {
        this.userProfileApplicationService = userProfileApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @PutMapping
    @Operation(
            summary = "Actualizar perfil de usuario",
            description = "Modifica la información personal del usuario autenticado " +
                    "(nombre, email, teléfono, etc.)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil actualizado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o email duplicado"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public UserResponse updateMyProfile(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del perfil a actualizar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateMyProfileRequest.class))
            )
            @Valid @RequestBody UpdateMyProfileRequest request) {
        return userProfileApplicationService.updateMyProfile(
                currentSessionResolver.resolveCurrentToken(authorizationHeader),
                request
        );
    }

    @PutMapping("/password")
    @Operation(
            summary = "Cambiar contraseña",
            description = "Actualiza la contraseña del usuario autenticado. " +
                    "Requiere la contraseña actual para validación."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Contraseña actualizada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Contraseña actual incorrecta o nueva contraseña inválida"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> changeMyPassword(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para cambiar contraseña",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangeMyPasswordRequest.class))
            )
            @Valid @RequestBody ChangeMyPasswordRequest request) {
        userProfileApplicationService.changeMyPassword(
                currentSessionResolver.resolveCurrentToken(authorizationHeader),
                request
        );
        return ResponseEntity.noContent().build();
    }
}


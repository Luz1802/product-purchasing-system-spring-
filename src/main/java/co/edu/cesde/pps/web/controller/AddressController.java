package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.AddressApplicationService;
import co.edu.cesde.pps.web.dto.request.AddressUpsertRequest;
import co.edu.cesde.pps.web.dto.response.AddressResponse;
import co.edu.cesde.pps.web.security.CurrentSessionResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.USER_ADDRESSES)
@Tag(
        name = "Direcciones de Usuario",
        description = "Gestión de direcciones de envío del usuario. " +
                "Permite crear, actualizar, listar y eliminar direcciones."
)
public class AddressController {

    private final AddressApplicationService addressApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public AddressController(AddressApplicationService addressApplicationService,
                             CurrentSessionResolver currentSessionResolver) {
        this.addressApplicationService = addressApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @GetMapping
    @Operation(
            summary = "Listar mis direcciones",
            description = "Obtiene todas las direcciones de envío registradas por el usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de direcciones obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AddressResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<AddressResponse> listMyAddresses(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader) {
        return addressApplicationService.listMyAddresses(currentSessionResolver.resolveCurrentToken(authorizationHeader));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalles de una dirección",
            description = "Recupera la información detallada de una dirección específica del usuario."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalles de la dirección obtenidos exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddressResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Dirección no encontrada"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public AddressResponse getMyAddress(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID de la dirección a consultar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return addressApplicationService.getMyAddress(currentSessionResolver.resolveCurrentToken(authorizationHeader), id);
    }

    @PostMapping
    @Operation(
            summary = "Agregar nueva dirección",
            description = "Crea una nueva dirección de envío para el usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Dirección creada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddressResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de dirección inválidos"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AddressResponse> addAddress(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la nueva dirección",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AddressUpsertRequest.class))
            )
            @Valid @RequestBody AddressUpsertRequest request) {
        AddressResponse response = addressApplicationService.addAddress(
                currentSessionResolver.resolveCurrentToken(authorizationHeader), request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar dirección",
            description = "Modifica la información de una dirección de envío existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dirección actualizada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddressResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de dirección inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Dirección no encontrada"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public AddressResponse updateAddress(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID de la dirección a actualizar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la dirección",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AddressUpsertRequest.class))
            )
            @Valid @RequestBody AddressUpsertRequest request) {
        return addressApplicationService.updateAddress(currentSessionResolver.resolveCurrentToken(authorizationHeader), id, request);
    }

    @PatchMapping("/{id}/default")
    @Operation(
            summary = "Establecer dirección como predeterminada",
            description = "Marca una dirección como la dirección de envío predeterminada del usuario."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dirección establecida como predeterminada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddressResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Dirección no encontrada"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public AddressResponse setDefaultAddress(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID de la dirección a establecer como predeterminada",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return addressApplicationService.setDefaultAddress(currentSessionResolver.resolveCurrentToken(authorizationHeader), id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar dirección",
            description = "Borra una dirección de envío del usuario."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Dirección eliminada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Dirección no encontrada"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteAddress(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID de la dirección a eliminar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        addressApplicationService.deleteAddress(currentSessionResolver.resolveCurrentToken(authorizationHeader), id);
        return ResponseEntity.noContent().build();
    }
}


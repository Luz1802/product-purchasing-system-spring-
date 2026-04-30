package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.OrderApplicationService;
import co.edu.cesde.pps.web.dto.request.CheckoutRequest;
import co.edu.cesde.pps.web.dto.response.OrderResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.ORDERS)
@Tag(
        name = "Órdenes",
        description = "Gestión de órdenes de compra. Permite realizar checkout, " +
                "listar y consultar detalles de órdenes del usuario."
)
public class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public OrderController(OrderApplicationService orderApplicationService,
                           CurrentSessionResolver currentSessionResolver) {
        this.orderApplicationService = orderApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @PostMapping("/checkout")
    @Operation(
            summary = "Crear nueva orden (checkout)",
            description = "Procesa el carrito actual para crear una nueva orden. " +
                    "Valida inventario y dirección de envío."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Orden creada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Carrito vacío, inventario insuficiente o dirección inválida"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<OrderResponse> checkout(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para procesar el checkout",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CheckoutRequest.class))
            )
            @Valid @RequestBody CheckoutRequest request) {
        OrderResponse response = orderApplicationService.checkout(
                currentSessionResolver.resolveCurrentToken(authorizationHeader),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Listar mis órdenes",
            description = "Obtiene todas las órdenes asociadas al usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de órdenes obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<OrderResponse> listMyOrders(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader) {
        return orderApplicationService.listMyOrders(currentSessionResolver.resolveCurrentToken(authorizationHeader));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalles de una orden",
            description = "Recupera la información detallada de una orden específica. " +
                    "Solo el propietario o administrador puede acceder."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalles de la orden obtenidos exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tiene permiso para acceder a esta orden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public OrderResponse getMyOrder(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @Parameter(
                    name = "id",
                    description = "ID único de la orden a consultar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return orderApplicationService.getMyOrder(currentSessionResolver.resolveCurrentToken(authorizationHeader), id);
    }
}


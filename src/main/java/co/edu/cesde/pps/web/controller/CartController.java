package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.CartApplicationService;
import co.edu.cesde.pps.web.dto.request.AddCartItemRequest;
import co.edu.cesde.pps.web.dto.request.MergeGuestCartRequest;
import co.edu.cesde.pps.web.dto.request.UpdateCartItemQuantityRequest;
import co.edu.cesde.pps.web.dto.response.CartResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.CART)
@Tag(
        name = "Carrito de Compras",
        description = "Gestión del carrito de compras. Permite agregar, actualizar, " +
                "eliminar productos y fusionar carritos de invitados con usuarios autenticados."
)
public class CartController {

    private final CartApplicationService cartApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public CartController(CartApplicationService cartApplicationService,
                          CurrentSessionResolver currentSessionResolver) {
        this.cartApplicationService = cartApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @GetMapping("/me")
    @Operation(
            summary = "Obtener carrito actual",
            description = "Recupera el carrito de compras del usuario autenticado o sesión actual."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrito obtenido exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CartResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CartResponse getCurrentCart(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader) {
        return cartApplicationService.getCurrentCart(currentSessionResolver.resolveCurrentToken(authorizationHeader));
    }

    @PostMapping("/items")
    @Operation(
            summary = "Agregar producto al carrito",
            description = "Añade un nuevo producto al carrito o incrementa la cantidad si ya existe."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto agregado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CartResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o producto no disponible"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CartResponse addItem(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a agregar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AddCartItemRequest.class))
            )
            @Valid @RequestBody AddCartItemRequest request) {
        return cartApplicationService.addItem(currentSessionResolver.resolveCurrentToken(authorizationHeader), request);
    }

    @PatchMapping("/items/{productId}")
    @Operation(
            summary = "Actualizar cantidad de producto",
            description = "Modifica la cantidad de un producto específico en el carrito."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cantidad actualizada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CartResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cantidad inválida"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado en el carrito"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CartResponse updateItemQuantity(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @Parameter(
                    name = "productId",
                    description = "ID del producto a actualizar",
                    example = "1",
                    required = true
            )
            @PathVariable Long productId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nueva cantidad del producto",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCartItemQuantityRequest.class))
            )
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        return cartApplicationService.updateItemQuantity(
                currentSessionResolver.resolveCurrentToken(authorizationHeader),
                productId,
                request
        );
    }

    @DeleteMapping("/items/{productId}")
    @Operation(
            summary = "Remover producto del carrito",
            description = "Elimina completamente un producto del carrito de compras."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto eliminado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CartResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado en el carrito"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CartResponse removeItem(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @Parameter(
                    name = "productId",
                    description = "ID del producto a remover",
                    example = "1",
                    required = true
            )
            @PathVariable Long productId) {
        return cartApplicationService.removeItem(currentSessionResolver.resolveCurrentToken(authorizationHeader), productId);
    }

    @DeleteMapping("/items")
    @Operation(
            summary = "Limpiar carrito completo",
            description = "Elimina todos los productos del carrito de compras actual."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Carrito vaciado exitosamente"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> clearCurrentCart(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader) {
        cartApplicationService.clearCurrentCart(currentSessionResolver.resolveCurrentToken(authorizationHeader));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/merge")
    @Operation(
            summary = "Fusionar carrito de invitado",
            description = "Combina el carrito de un usuario invitado con el carrito " +
                    "de un usuario autenticado después del login."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Carritos fusionados exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CartResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de fusión inválidos"
            ),
            @ApiResponse(responseCode = "401", description = "Sesión no válida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CartResponse mergeGuestCart(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "Token JWT en formato: Bearer <token>",
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del carrito de invitado a fusionar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MergeGuestCartRequest.class))
            )
            @Valid @RequestBody MergeGuestCartRequest request) {
        return cartApplicationService.mergeGuestCart(currentSessionResolver.resolveCurrentToken(authorizationHeader), request);
    }
}


package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.CatalogApplicationService;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.web.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.PRODUCTS)
@Tag(
        name = "Productos",
        description = "Operaciones relacionadas con el catálogo de productos. " +
                "Permite listar, buscar y consultar detalles de productos disponibles."
)
public class ProductController {

    private final CatalogApplicationService catalogApplicationService;

    public ProductController(CatalogApplicationService catalogApplicationService) {
        this.catalogApplicationService = catalogApplicationService;
    }

    @GetMapping
    @Operation(
            summary = "Listar productos",
            description = "Obtiene una lista de productos disponibles con opciones de búsqueda, " +
                    "filtrado por categoría y estado de activación."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de productos obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<ProductResponse> listProducts(
            @Parameter(
                    name = "search",
                    description = "Término de búsqueda para filtrar productos por nombre o descripción",
                    example = "laptop"
            )
            @RequestParam(required = false) String search,

            @Parameter(
                    name = "categoryId",
                    description = "ID de la categoría para filtrar productos",
                    example = "1"
            )
            @RequestParam(required = false) Long categoryId,

            @Parameter(
                    name = "activeOnly",
                    description = "Si es true, solo muestra productos activos (por defecto: true)",
                    example = "true"
            )
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        List<ProductResponse> products = resolveBaseProducts(search, categoryId);

        return products.stream()
                .filter(product -> categoryId == null || categoryId.equals(product.categoryId()))
                .filter(product -> !activeOnly || Boolean.TRUE.equals(product.isActive()))
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalles de un producto",
            description = "Recupera la información detallada de un producto específico por su ID. " +
                    "Solo retorna productos activos."
    )
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
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ProductResponse getProduct(
            @Parameter(
                    name = "id",
                    description = "ID único del producto a consultar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        ProductResponse response = catalogApplicationService.getProduct(id);
        if (!Boolean.TRUE.equals(response.isActive())) {
            throw new EntityNotFoundException("Product", id);
        }
        return response;
    }

    private List<ProductResponse> resolveBaseProducts(String search, Long categoryId) {
        if (search != null && !search.isBlank()) {
            return catalogApplicationService.searchProducts(search);
        }
        if (categoryId != null) {
            return catalogApplicationService.listProductsByCategory(categoryId);
        }
        return catalogApplicationService.listProducts(false);
    }
}


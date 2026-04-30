package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.CatalogApplicationService;
import co.edu.cesde.pps.web.dto.response.CategoryResponse;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.CATEGORIES)
@Tag(
        name = "Categorías",
        description = "Operaciones relacionadas con categorías de productos. " +
                "Permite listar categorías, ver su estructura jerárquica y subcategorías."
)
public class CategoryController {

    private final CatalogApplicationService catalogApplicationService;

    public CategoryController(CatalogApplicationService catalogApplicationService) {
        this.catalogApplicationService = catalogApplicationService;
    }

    @GetMapping
    @Operation(
            summary = "Listar todas las categorías",
            description = "Obtiene un listado plano de todas las categorías disponibles en el sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de categorías obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<CategoryResponse> listCategories() {
        return catalogApplicationService.listCategories();
    }

    @GetMapping("/tree")
    @Operation(
            summary = "Obtener árbol de categorías",
            description = "Retorna la estructura jerárquica de las categorías, " +
                    "mostrando las relaciones padre-hijo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Árbol de categorías obtenido exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<CategoryResponse> listCategoryTree() {
        return catalogApplicationService.listCategoryTree();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalles de una categoría",
            description = "Recupera la información detallada de una categoría específica."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalles de la categoría obtenidos exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CategoryResponse getCategory(
            @Parameter(
                    name = "id",
                    description = "ID único de la categoría a consultar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return catalogApplicationService.getCategory(id);
    }

    @GetMapping("/{id}/subcategories")
    @Operation(
            summary = "Listar subcategorías",
            description = "Obtiene todas las subcategorías pertenecientes a una categoría padre."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de subcategorías obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría padre no encontrada"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<CategoryResponse> listSubcategories(
            @Parameter(
                    name = "id",
                    description = "ID de la categoría padre",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return catalogApplicationService.listSubcategories(id);
    }
}


package co.edu.cesde.pps.mapper;

import co.edu.cesde.pps.dto.CategoryDTO;
import co.edu.cesde.pps.model.Category;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre Category (Entity) y CategoryDTO.
 *
 * Responsabilidades:
 * - Convertir Entity a DTO (toDTO)
 * - Convertir Entity a DTO con jerarquía recursiva (toDTOWithHierarchy)
 * - Convertir DTO a Entity (toEntity)
 * - Manejar null safety
 * - Extraer parentId de la relación
 * - Calcular campos agregados
 */
public class CategoryMapper {

    /**
     * Convierte Category Entity a CategoryDTO (sin subcategorías).
     *
     * @param category Entity a convertir
     * @return CategoryDTO o null si category es null
     */
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());

        // Extraer parentId de la relación
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getCategoryId());
            dto.setParentName(category.getParent().getName());
        }

        dto.setName(category.getName());
        dto.setSlug(category.getSlug());

        // Campos calculados
        dto.setIsRoot(category.isRootCategory()); // Método helper de Category

        // Campos agregados (null safety)
        if (category.getSubcategories() != null) {
            dto.setSubcategoriesCount(category.getSubcategories().size());
        } else {
            dto.setSubcategoriesCount(0);
        }

        if (category.getProducts() != null) {
            dto.setProductsCount(category.getProducts().size());
        } else {
            dto.setProductsCount(0);
        }

        return dto;
    }

    /**
     * Convierte Category Entity a CategoryDTO con subcategorías recursivamente.
     *
     * Útil para construir árbol de categorías completo.
     *
     * @param category Entity a convertir
     * @return CategoryDTO con subcategorías anidadas o null si category es null
     */
    public CategoryDTO toDTOWithHierarchy(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = toDTO(category);

        // Convertir subcategorías recursivamente
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            List<CategoryDTO> subcategoryDTOs = category.getSubcategories().stream()
                    .map(this::toDTOWithHierarchy) // Recursivo
                    .collect(Collectors.toList());
            dto.setSubcategories(subcategoryDTOs);
        }

        return dto;
    }

    /**
     * Convierte CategoryDTO a Category Entity.
     *
     * NOTA: No convierte parent ni subcategories, eso se maneja en el servicio.
     *
     * @param dto DTO a convertir
     * @return Category Entity o null si dto es null
     */
    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setCategoryId(dto.getCategoryId());
        // Parent se debe asignar en el servicio
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());

        return category;
    }

    /**
     * Convierte lista de Category Entities a lista de CategoryDTOs.
     *
     * @param categories Lista de entities
     * @return Lista de DTOs o lista vacía si categories es null
     */
    public List<CategoryDTO> toDTOList(List<Category> categories) {
        if (categories == null) {
            return List.of();
        }

        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte lista de Category Entities a lista de CategoryDTOs con jerarquía.
     *
     * @param categories Lista de entities
     * @return Lista de DTOs con jerarquía o lista vacía si categories es null
     */
    public List<CategoryDTO> toDTOListWithHierarchy(List<Category> categories) {
        if (categories == null) {
            return List.of();
        }

        return categories.stream()
                .map(this::toDTOWithHierarchy)
                .collect(Collectors.toList());
    }

    /**
     * Convierte lista de CategoryDTOs a lista de Category Entities.
     *
     * @param dtos Lista de DTOs
     * @return Lista de entities o lista vacía si dtos es null
     */
    public List<Category> toEntityList(List<CategoryDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
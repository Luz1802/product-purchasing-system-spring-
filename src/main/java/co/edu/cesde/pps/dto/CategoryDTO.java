package co.edu.cesde.pps.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO para transferencia de datos de Categoría.
 *
 * Se utiliza para:
 * - Menú de navegación de categorías
 * - Árbol de categorías jerárquico
 * - Filtros de búsqueda
 * - Breadcrumbs de navegación
 */
public class CategoryDTO {

    private Long categoryId;
    private Long parentId;
    private String parentName;
    private String name;
    private String slug;
    private Boolean isRoot;
    private Integer subcategoriesCount;
    private Integer productsCount;
    private List<CategoryDTO> subcategories;

    // Constructor vacío
    public CategoryDTO() {
        this.subcategories = new ArrayList<>();
    }

    // Constructor básico
    public CategoryDTO(Long categoryId, Long parentId, String name, String slug) {
        this.categoryId = categoryId;
        this.parentId = parentId;
        this.name = name;
        this.slug = slug;
        this.isRoot = parentId == null;
        this.subcategories = new ArrayList<>();
    }

    // Constructor completo
    public CategoryDTO(Long categoryId, Long parentId, String parentName, String name,
                       String slug, Integer subcategoriesCount, Integer productsCount) {
        this.categoryId = categoryId;
        this.parentId = parentId;
        this.parentName = parentName;
        this.name = name;
        this.slug = slug;
        this.isRoot = parentId == null;
        this.subcategoriesCount = subcategoriesCount;
        this.productsCount = productsCount;
        this.subcategories = new ArrayList<>();
    }

    // Getters y Setters

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(Boolean isRoot) {
        this.isRoot = isRoot;
    }

    public Integer getSubcategoriesCount() {
        return subcategoriesCount;
    }

    public void setSubcategoriesCount(Integer subcategoriesCount) {
        this.subcategoriesCount = subcategoriesCount;
    }

    public Integer getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(Integer productsCount) {
        this.productsCount = productsCount;
    }

    public List<CategoryDTO> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<CategoryDTO> subcategories) {
        this.subcategories = subcategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDTO that = (CategoryDTO) o;
        return Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }

    @Override
    public String toString() {
        return "CategoryDTO{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", isRoot=" + isRoot +
                ", subcategoriesCount=" + subcategoriesCount +
                ", productsCount=" + productsCount +
                '}';
    }
}
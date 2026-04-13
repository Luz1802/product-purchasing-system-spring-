package co.edu.cesde.pps.model;

import co.edu.cesde.pps.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad Product - Representa productos vendibles en la tienda.
 *
 * Campos:
 * - productId: Identificador único del producto (PK)
 * - category: Categoría del producto (N:1 con Category)
 * - sku: Stock Keeping Unit (UNIQUE) - código único de inventario
 * - name: Nombre del producto
 * - description: Descripción detallada del producto
 * - image: URL pública de la imagen principal del producto
 * - price: Precio actual del producto (BigDecimal para precisión monetaria)
 * - stockQty: Cantidad en stock/inventario
 * - isActive: Indica si el producto está activo (visible en catálogo)
 * - createdAt: Fecha de creación del producto
 *
 * Tabla BD: products
 *
 * Consideraciones de diseño:
 * - price usa BigDecimal para evitar errores de redondeo en cálculos monetarios
 * - isActive permite ocultar productos sin borrarlos de la base de datos
 * - sku único facilita integración con sistemas de inventario externos
 *
 * Relaciones (futuro - etapa09):
 * - N:1 con Category (muchos productos pertenecen a una categoría)
 * - 1:N con CartItem (un producto puede estar en múltiples carritos)
 * - 1:N con OrderItem (un producto puede estar en múltiples órdenes)
 *
 * Refactorizado con Lombok en Etapa 07.
 * Anotaciones JPA básicas agregadas en Etapa 08.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", length = 1000)
    private String image;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_qty", nullable = false)
    private Integer stockQty;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Setters personalizados con validación (override de Lombok)

    public void setPrice(BigDecimal price) {
        ValidationUtils.validateNonNegative(price, "price");
        this.price = price;
    }

    public void setStockQty(Integer stockQty) {
        ValidationUtils.validateNonNegative(stockQty, "stockQty");
        this.stockQty = stockQty;
    }

    // Método helper para verificar disponibilidad
    public boolean isAvailable() {
        return isActive != null && isActive && stockQty != null && stockQty > 0;
    }

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    // toString personalizado sin navegación a objetos relacionados (solo IDs)

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", categoryId=" + (category != null ? category.getCategoryId() : null) +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", stockQty=" + stockQty +
                ", isActive=" + isActive +
                ", isAvailable=" + isAvailable() +
                ", createdAt=" + createdAt +
                '}';
    }
}
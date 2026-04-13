package co.edu.cesde.pps.dto;

import co.edu.cesde.pps.enums.CartStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO para transferencia de datos de Carrito.
 *
 * Se utiliza para:
 * - Mostrar carrito actual del usuario
 * - Actualizar carrito
 * - Respuestas de API del carrito
 * - Histórico de carritos
 */
public class CartDTO {

    private Long cartId;
    private Long userId;
    private String userEmail;
    private CartStatus status;
    private Boolean isGuest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartItemDTO> items;
    private Integer itemsCount;
    private BigDecimal total;
    private String totalFormatted;

    // Constructor vacío
    public CartDTO() {
        this.items = new ArrayList<>();
    }

    // Constructor completo
    public CartDTO(Long cartId, Long userId, String userEmail, CartStatus status,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.status = status;
        this.isGuest = userId == null;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = new ArrayList<>();
    }

    // Getters y Setters

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public Boolean getIsGuest() {
        return isGuest;
    }

    public void setIsGuest(Boolean isGuest) {
        this.isGuest = isGuest;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public Integer getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(Integer itemsCount) {
        this.itemsCount = itemsCount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTotalFormatted() {
        return totalFormatted;
    }

    public void setTotalFormatted(String totalFormatted) {
        this.totalFormatted = totalFormatted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartDTO cartDTO = (CartDTO) o;
        return Objects.equals(cartId, cartDTO.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId);
    }

    @Override
    public String toString() {
        return "CartDTO{" +
                "cartId=" + cartId +
                ", status=" + status +
                ", isGuest=" + isGuest +
                ", itemsCount=" + itemsCount +
                ", total=" + total +
                '}';
    }
}
package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.CartStatus;
import co.edu.cesde.pps.util.CalculationUtils;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Entidad Cart - Carrito de compras.
 *
 * Maneja carritos tanto de usuarios registrados como de invitados (guests).
 *
 * Campos:
 * - cartId: Identificador único del carrito (PK)
 * - user: Usuario dueño del carrito (N:1 con User) - NULLABLE para invitados
 * - session: Sesión asociada al carrito (N:1 con UserSession)
 * - status: Estado del carrito (OPEN, ABANDONED, CONVERTED)
 * - createdAt: Fecha de creación del carrito
 * - updatedAt: Fecha de última actualización
 * - items: Lista de items del carrito (1:N con CartItem)
 *
 * Estados del carrito:
 * - OPEN: Carrito activo en uso
 * - ABANDONED: Carrito abandonado (inactivo > X días)
 * - CONVERTED: Carrito convertido a orden (checkout completado)
 *
 * Relaciones:
 * - N:1 con User (opcional - muchos carritos pueden pertenecer a un usuario)
 * - N:1 con UserSession (muchos carritos pertenecen a una sesión)
 * - 1:N con CartItem (un carrito tiene muchos items)
 */
@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Nullable - NULL para invitados

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private UserSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CartStatus status = CartStatus.OPEN;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Colección para relación 1:N
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cart-items")
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    // Método helper para calcular total del carrito
    public BigDecimal calculateTotal() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<BigDecimal> subtotals = items.stream()
                .map(CartItem::calculateSubtotal)
                .collect(Collectors.toList());
        return CalculationUtils.calculateCartTotal(subtotals);
    }

    // Método helper para verificar si el carrito está abierto
    public boolean isOpen() {
        return status == CartStatus.OPEN;
    }

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(cartId, cart.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId);
    }

    // toString personalizado sin navegación a objetos relacionados (solo IDs y tamaño de colección)

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", sessionId=" + (session != null ? session.getSessionId() : null) +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                ", total=" + calculateTotal() +
                '}';
    }

    public boolean isGuestCart() {
        return user == null;
    }
}
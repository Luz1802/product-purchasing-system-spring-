package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Entidad OrderStatus - Catálogo de estados posibles de una orden.
 *
 * Ejemplos: pending, paid, shipped, delivered, cancelled
 *
 * Campos:
 * - orderStatusId: Identificador único del estado (PK)
 * - name: Nombre único del estado (UNIQUE)
 * - description: Descripción del estado (NULLABLE)
 *
 * Tabla BD: order_statuses
 *
 * Relaciones (futuro - etapa09):
 * - 1:N con Order (un estado puede aplicar a múltiples órdenes)
 *
 * Refactorizado con Lombok en Etapa 07.
 * Anotaciones JPA básicas agregadas en Etapa 08.
 */
@Entity
@Table(name = "order_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_status_id")
    private Long orderStatusId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStatus that = (OrderStatus) o;
        return Objects.equals(orderStatusId, that.orderStatusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderStatusId);
    }

    // toString personalizado sin navegación a objetos relacionados

    @Override
    public String toString() {
        return "OrderStatus{" +
                "orderStatusId=" + orderStatusId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
package co.edu.cesde.pps.model;

import lombok.*;

import java.util.Objects;

/**
 * Entidad PaymentStatus - Catálogo de estados posibles de un pago.
 *
 * Ejemplos: pending, approved, rejected, refunded
 *
 * Campos:
 * - paymentStatusId: Identificador único del estado (PK)
 * - name: Nombre único del estado (UNIQUE)
 *
 * Relaciones (futuro - etapa02):
 * - 1:N con Payment (un estado puede aplicar a múltiples pagos)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentStatus {

    private Long paymentStatusId;
    private String name;

    // Constructor con campos obligatorios
    public PaymentStatus(String name) {
        this.name = name;
    }

    // Getters y Setters

    public Long getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(Long paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentStatus that = (PaymentStatus) o;
        return Objects.equals(paymentStatusId, that.paymentStatusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentStatusId);
    }

}

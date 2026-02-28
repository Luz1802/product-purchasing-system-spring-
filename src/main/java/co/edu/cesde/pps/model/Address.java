package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Entidad Address - Direcciones del usuario (envío y facturación).
 *
 * Almacena direcciones físicas para propósitos de entrega y facturación.
 *
 * Campos:
 * - addressId: Identificador único de la dirección (PK)
 * - user: Usuario propietario (N:1 con User)
 * - type: Tipo de dirección (SHIPPING o BILLING)
 * - line1: Línea 1 de dirección (calle principal)
 * - line2: Línea 2 de dirección (apartamento, oficina - NULLABLE)
 * - city: Ciudad
 * - state: Estado/departamento
 * - country: País
 * - postalCode: Código postal
 * - isDefault: Indica si es la dirección por defecto del usuario
 *
 * Relaciones:
 * - N:1 con User (muchas direcciones pertenecen a un usuario)
 */
@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AddressType type;

    @Column(name = "line1", nullable = false, length = 255)
    private String line1;

    @Column(name = "line2", length = 255)
    private String line2;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(addressId, address.addressId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressId);
    }

    // toString personalizado sin navegación a objetos relacionados (solo IDs)

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", type=" + type +
                ", line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
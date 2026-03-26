package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Entidad Role - Define tipos de usuario o niveles de acceso.
 *
 * Ejemplos de roles: admin, customer, manager
 *
 * Campos:
 * - roleId: Identificador único del rol (PK)
 * - name: Nombre único del rol (UNIQUE)
 * - description: Descripción del rol y sus permisos
 *
 * Tabla BD: roles
 *
 * Relaciones (futuro - etapa09):
 * - 1:N con User (un rol puede tener múltiples usuarios)
 *
 * Refactorizado con Lombok en Etapa 07.
 * Anotaciones JPA básicas agregadas en Etapa 08.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }

    // toString personalizado sin navegación a objetos relacionados

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
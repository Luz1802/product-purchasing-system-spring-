package co.edu.cesde.pps.model;

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
 * Relaciones (futuro - etapa02):
 * - 1:N con User (un rol puede tener múltiples usuarios)
 */
public class Role {

    private Long roleId;
    private String name;
    private String description;

    // Constructor con campos obligatorios
    public Role(String name) {
        this.name = name;
    }

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

}

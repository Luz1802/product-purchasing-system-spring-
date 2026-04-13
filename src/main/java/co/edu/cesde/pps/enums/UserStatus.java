package co.edu.cesde.pps.enums;

/**
 * Enumeración de estados posibles para un usuario.
 *
 * Estados:
 * - ACTIVE: Usuario activo, puede usar el sistema normalmente
 * - INACTIVE: Usuario inactivo, no puede iniciar sesión
 * - BLOCKED: Usuario bloqueado por políticas de seguridad o negocio
 */
public enum UserStatus {
    /**
     * Usuario activo - Puede usar el sistema normalmente
     */
    ACTIVE,

    /**
     * Usuario inactivo - No puede iniciar sesión (cuenta deshabilitada temporalmente)
     */
    INACTIVE,

    /**
     * Usuario bloqueado - Bloqueado por políticas de seguridad
     */
    BLOCKED
}
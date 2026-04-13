package co.edu.cesde.pps.enums;

/**
 * Enumeración de estados posibles para un carrito de compras.
 *
 * Estados:
 * - OPEN: Carrito activo, el usuario puede seguir agregando/quitando items
 * - CONVERTED: Carrito convertido en orden (checkout completado)
 * - ABANDONED: Carrito abandonado (ej: carrito de invitado tras merge)
 */
public enum CartStatus {
    /**
     * Carrito activo - Usuario puede agregar/quitar items
     */
    OPEN,

    /**
     * Carrito convertido en orden - Checkout completado exitosamente
     */
    CONVERTED,

    /**
     * Carrito abandonado - Usuario no completó la compra o se hizo merge
     */
    ABANDONED
}
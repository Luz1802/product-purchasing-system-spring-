package co.edu.cesde.pps.exception;

/**
 * Excepción lanzada cuando ocurre un error durante el proceso de fusión (merge) de carritos.
 *
 * El merge de carritos es una operación crítica que ocurre cuando:
 * - Un usuario invitado se registra y ya existe un carrito del usuario
 * - Un usuario inicia sesión y tiene un carrito de invitado activo
 *
 * Esta excepción puede ocurrir por:
 * - Carritos en estados incompatibles (no OPEN)
 * - Productos duplicados con conflictos irresolubles
 * - Errores de consistencia de datos durante el merge
 *
 * Ver documentación completa del algoritmo de Cart Merge en:
 * documents_external/er_model_documentation.md - Sección 5
 */
public class CartMergeException extends BusinessException {

    private final Long guestCartId;
    private final Long userCartId;

    /**
     * Constructor con IDs de ambos carritos
     *
     * @param guestCartId ID del carrito de invitado
     * @param userCartId ID del carrito del usuario registrado
     * @param message Descripción del error durante el merge
     */
    public CartMergeException(Long guestCartId, Long userCartId, String message) {
        super(String.format("Cart merge failed between guest cart %d and user cart %d: %s",
                guestCartId, userCartId, message));
        this.guestCartId = guestCartId;
        this.userCartId = userCartId;
    }

    /**
     * Constructor con mensaje simple
     *
     * @param message Descripción del error durante el merge
     */
    public CartMergeException(String message) {
        super(message);
        this.guestCartId = null;
        this.userCartId = null;
    }

    /**
     * Constructor con mensaje y causa raíz
     *
     * @param message Descripción del error durante el merge
     * @param cause Excepción que causó el fallo del merge
     */
    public CartMergeException(String message, Throwable cause) {
        super(message, cause);
        this.guestCartId = null;
        this.userCartId = null;
    }

    public Long getGuestCartId() {
        return guestCartId;
    }

    public Long getUserCartId() {
        return userCartId;
    }
}
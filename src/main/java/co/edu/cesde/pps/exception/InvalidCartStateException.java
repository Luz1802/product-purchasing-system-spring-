package co.edu.cesde.pps.exception;

import co.edu.cesde.pps.enums.CartStatus;

/**
 * Excepción lanzada cuando se intenta realizar una operación en un carrito
 * que está en un estado inválido para esa operación.
 *
 * Ejemplos de uso:
 * - Intentar agregar items a un carrito CONVERTED o ABANDONED
 * - Intentar hacer checkout de un carrito que no está OPEN
 * - Intentar modificar un carrito ya convertido a orden
 * - Intentar hacer merge con carritos en estado incorrecto
 *
 * Esta excepción protege la integridad del flujo de negocio del carrito.
 */
public class InvalidCartStateException extends BusinessException {

    private final Long cartId;
    private final CartStatus currentState;
    private final CartStatus requiredState;

    /**
     * Constructor con ID del carrito y estados
     *
     * @param cartId ID del carrito
     * @param currentState Estado actual del carrito
     * @param requiredState Estado requerido para la operación
     * @param operation Descripción de la operación que se intentó realizar
     */
    public InvalidCartStateException(Long cartId, CartStatus currentState,
                                     CartStatus requiredState, String operation) {
        super(String.format("Cannot perform '%s' on cart %d. Current state: %s, Required state: %s",
                operation, cartId, currentState, requiredState));
        this.cartId = cartId;
        this.currentState = currentState;
        this.requiredState = requiredState;
    }

    /**
     * Constructor con mensaje simple
     *
     * @param message Descripción del error de estado
     */
    public InvalidCartStateException(String message) {
        super(message);
        this.cartId = null;
        this.currentState = null;
        this.requiredState = null;
    }

    public Long getCartId() {
        return cartId;
    }

    public CartStatus getCurrentState() {
        return currentState;
    }

    public CartStatus getRequiredState() {
        return requiredState;
    }
}
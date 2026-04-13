package co.edu.cesde.pps.exception;

/**
 * Excepción lanzada cuando una operación requiere autenticación válida
 * y la sesión o credenciales no cumplen las reglas esperadas.
 */
public class AuthenticationException extends BusinessException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
package co.edu.cesde.pps.exception;

/**
 * Excepción lanzada cuando se detecta una violación de reglas de validación de datos.
 *
 * Se utiliza para validaciones de negocio como:
 * - Valores nulos en campos requeridos
 * - Valores fuera de rango permitido
 * - Formatos inválidos (email, teléfono, etc.)
 * - Violaciones de restricciones de negocio
 *
 * Ejemplos de uso:
 * - Precio negativo
 * - Cantidad de stock negativa
 * - Email con formato inválido
 * - Fecha de expiración en el pasado
 */
public class ValidationException extends BusinessException {

    private final String fieldName;
    private final Object invalidValue;

    /**
     * Constructor con nombre de campo y valor inválido
     *
     * @param fieldName Nombre del campo que falló la validación
     * @param invalidValue Valor que causó el error de validación
     * @param message Mensaje descriptivo del error
     */
    public ValidationException(String fieldName, Object invalidValue, String message) {
        super(String.format("Validation failed for field '%s' with value '%s': %s",
                fieldName, invalidValue, message));
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    /**
     * Constructor con mensaje simple
     *
     * @param message Mensaje descriptivo del error de validación
     */
    public ValidationException(String message) {
        super(message);
        this.fieldName = null;
        this.invalidValue = null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }
}
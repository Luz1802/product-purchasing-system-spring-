package co.edu.cesde.pps.exception;

/**
 * Excepción lanzada cuando se intenta crear una entidad que violaría una restricción de unicidad.
 *
 * Se utiliza para detectar duplicados en campos UNIQUE antes de intentar persistir:
 * - Email de usuario duplicado
 * - SKU de producto duplicado
 * - Slug de categoría duplicado
 * - Order number duplicado
 * - Session token duplicado
 *
 * Esta excepción ayuda a proporcionar mensajes de error claros al usuario
 * sobre conflictos de unicidad.
 */
public class DuplicateEntityException extends BusinessException {

    private final String entityType;
    private final String fieldName;
    private final Object duplicateValue;

    /**
     * Constructor con tipo de entidad, campo y valor duplicado
     *
     * @param entityType Tipo de entidad (ej: "User", "Product")
     * @param fieldName Nombre del campo único que tiene duplicado
     * @param duplicateValue Valor que está duplicado
     */
    public DuplicateEntityException(String entityType, String fieldName, Object duplicateValue) {
        super(String.format("%s already exists with %s: %s",
                entityType, fieldName, duplicateValue));
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.duplicateValue = duplicateValue;
    }

    /**
     * Constructor con mensaje simple
     *
     * @param message Descripción del error de duplicado
     */
    public DuplicateEntityException(String message) {
        super(message);
        this.entityType = null;
        this.fieldName = null;
        this.duplicateValue = null;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getDuplicateValue() {
        return duplicateValue;
    }
}
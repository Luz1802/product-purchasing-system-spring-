package co.edu.cesde.pps.exception;

/**
 * Excepción lanzada cuando una entidad solicitada no se encuentra en el sistema.
 *
 * Se utiliza en operaciones de búsqueda por ID, email, SKU u otros identificadores
 * únicos cuando la entidad no existe en la base de datos.
 *
 * Ejemplos de uso:
 * - Usuario no encontrado por ID
 * - Producto no encontrado por SKU
 * - Orden no encontrada por número de orden
 * - Categoría no encontrada por slug
 */
public class EntityNotFoundException extends BusinessException {

    private final String entityType;
    private final Object searchCriteria;

    /**
     * Constructor con tipo de entidad e identificador
     *
     * @param entityType Tipo de entidad que no fue encontrada (ej: "User", "Product")
     * @param searchCriteria Criterio de búsqueda usado (ID, email, SKU, etc.)
     */
    public EntityNotFoundException(String entityType, Object searchCriteria) {
        super(String.format("%s not found with criteria: %s", entityType, searchCriteria));
        this.entityType = entityType;
        this.searchCriteria = searchCriteria;
    }

    /**
     * Constructor con mensaje personalizado
     *
     * @param message Mensaje descriptivo del error
     */
    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = null;
        this.searchCriteria = null;
    }

    public String getEntityType() {
        return entityType;
    }

    public Object getSearchCriteria() {
        return searchCriteria;
    }
}
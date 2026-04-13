package co.edu.cesde.pps.exception;

/**
 * Excepción lanzada cuando no hay suficiente stock disponible para una operación.
 *
 * Se utiliza en operaciones que requieren verificación de inventario:
 * - Agregar producto al carrito
 * - Procesar orden (checkout)
 * - Actualizar cantidad en carrito
 *
 * Esta excepción previene overselling y mantiene la integridad del inventario.
 */
public class InsufficientStockException extends BusinessException {

    private final Long productId;
    private final String productSku;
    private final Integer requestedQuantity;
    private final Integer availableStock;

    /**
     * Constructor completo con detalles del producto y stock
     *
     * @param productId ID del producto sin stock suficiente
     * @param productSku SKU del producto
     * @param requestedQuantity Cantidad solicitada
     * @param availableStock Stock disponible actual
     */
    public InsufficientStockException(Long productId, String productSku,
                                      Integer requestedQuantity, Integer availableStock) {
        super(String.format("Insufficient stock for product %s (ID: %d). Requested: %d, Available: %d",
                productSku, productId, requestedQuantity, availableStock));
        this.productId = productId;
        this.productSku = productSku;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    /**
     * Constructor con mensaje simple
     *
     * @param message Descripción del error de stock
     */
    public InsufficientStockException(String message) {
        super(message);
        this.productId = null;
        this.productSku = null;
        this.requestedQuantity = null;
        this.availableStock = null;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductSku() {
        return productSku;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }
}
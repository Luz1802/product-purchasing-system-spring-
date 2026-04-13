package co.edu.cesde.pps.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO para transferencia de datos de Item de Orden.
 *
 * Se utiliza para:
 * - Mostrar items en detalles de orden
 * - Histórico de compras
 * - Facturas y recibos
 */
public class OrderItemDTO {

    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productSku;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String unitPriceFormatted;
    private String lineTotalFormatted;

    // Constructor vacío
    public OrderItemDTO() {
    }

    // Constructor completo
    public OrderItemDTO(Long orderItemId, Long orderId, Long productId, String productName,
                        String productSku, Integer quantity, BigDecimal unitPrice,
                        BigDecimal lineTotal) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    // Getters y Setters

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getUnitPriceFormatted() {
        return unitPriceFormatted;
    }

    public void setUnitPriceFormatted(String unitPriceFormatted) {
        this.unitPriceFormatted = unitPriceFormatted;
    }

    public String getLineTotalFormatted() {
        return lineTotalFormatted;
    }

    public void setLineTotalFormatted(String lineTotalFormatted) {
        this.lineTotalFormatted = lineTotalFormatted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemDTO that = (OrderItemDTO) o;
        return Objects.equals(orderItemId, that.orderItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderItemId);
    }

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "orderItemId=" + orderItemId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", lineTotal=" + lineTotal +
                '}';
    }
}
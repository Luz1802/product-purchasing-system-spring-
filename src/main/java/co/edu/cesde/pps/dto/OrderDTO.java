package co.edu.cesde.pps.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO para transferencia de datos de Orden.
 *
 * Se utiliza para:
 * - Mostrar detalles de orden
 * - Listado de órdenes del usuario
 * - Tracking de orden
 * - Respuestas de API
 */
public class OrderDTO {

    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private String orderStatusName;
    private AddressDTO shippingAddress;
    private AddressDTO billingAddress;
    private List<OrderItemDTO> items;
    private Integer itemsCount;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal total;
    private String subtotalFormatted;
    private String taxFormatted;
    private String shippingCostFormatted;
    private String totalFormatted;
    private LocalDateTime createdAt;

    // Constructor vacío
    public OrderDTO() {
        this.items = new ArrayList<>();
    }

    // Constructor completo
    public OrderDTO(Long orderId, String orderNumber, Long userId, String userEmail,
                    String userFullName, String orderStatusName, BigDecimal subtotal,
                    BigDecimal tax, BigDecimal shippingCost, BigDecimal total,
                    LocalDateTime createdAt) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
        this.orderStatusName = orderStatusName;
        this.subtotal = subtotal;
        this.tax = tax;
        this.shippingCost = shippingCost;
        this.total = total;
        this.createdAt = createdAt;
        this.items = new ArrayList<>();
    }

    // Getters y Setters

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName = orderStatusName;
    }

    public AddressDTO getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public AddressDTO getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(AddressDTO billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public Integer getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(Integer itemsCount) {
        this.itemsCount = itemsCount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getSubtotalFormatted() {
        return subtotalFormatted;
    }

    public void setSubtotalFormatted(String subtotalFormatted) {
        this.subtotalFormatted = subtotalFormatted;
    }

    public String getTaxFormatted() {
        return taxFormatted;
    }

    public void setTaxFormatted(String taxFormatted) {
        this.taxFormatted = taxFormatted;
    }

    public String getShippingCostFormatted() {
        return shippingCostFormatted;
    }

    public void setShippingCostFormatted(String shippingCostFormatted) {
        this.shippingCostFormatted = shippingCostFormatted;
    }

    public String getTotalFormatted() {
        return totalFormatted;
    }

    public void setTotalFormatted(String totalFormatted) {
        this.totalFormatted = totalFormatted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDTO orderDTO = (OrderDTO) o;
        return Objects.equals(orderId, orderDTO.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderStatusName='" + orderStatusName + '\'' +
                ", total=" + total +
                ", itemsCount=" + itemsCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
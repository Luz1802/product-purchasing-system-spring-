package co.edu.cesde.pps.util;

import java.math.BigDecimal;
import java.util.List;

/**
 * Clase utilitaria para cálculos de negocio del dominio.
 *
 * Proporciona métodos estáticos para:
 * - Cálculos de totales de carritos y órdenes
 * - Cálculos de subtotales de items
 * - Cálculos de impuestos
 * - Cálculos de costos de envío
 *
 * Esta clase centraliza la lógica de cálculo que antes estaba dispersa
 * en las entidades del modelo.
 */
public final class CalculationUtils {

    // Constructor privado para prevenir instanciación
    private CalculationUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Calcula el subtotal de un item del carrito
     *
     * @param unitPrice Precio unitario del producto
     * @param quantity Cantidad del producto
     * @return Subtotal (unitPrice * quantity)
     */
    public static BigDecimal calculateCartItemSubtotal(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return MoneyUtils.multiply(unitPrice, quantity);
    }

    /**
     * Calcula el subtotal de un item de orden
     *
     * @param unitPrice Precio unitario del producto
     * @param quantity Cantidad del producto
     * @return Subtotal (unitPrice * quantity)
     */
    public static BigDecimal calculateOrderItemLineTotal(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return MoneyUtils.multiply(unitPrice, quantity);
    }

    /**
     * Calcula el total de un carrito sumando todos sus items
     *
     * @param itemSubtotals Lista de subtotales de los items
     * @return Total del carrito
     */
    public static BigDecimal calculateCartTotal(List<BigDecimal> itemSubtotals) {
        if (itemSubtotals == null || itemSubtotals.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return itemSubtotals.stream()
                .map(MoneyUtils::normalize)
                .reduce(BigDecimal.ZERO, MoneyUtils::add);
    }

    /**
     * Calcula el total de una orden
     *
     * @param subtotal Subtotal de los items
     * @param tax Impuestos
     * @param shippingCost Costo de envío
     * @return Total de la orden (subtotal + tax + shippingCost)
     */
    public static BigDecimal calculateOrderTotal(BigDecimal subtotal, BigDecimal tax, BigDecimal shippingCost) {
        BigDecimal normalizedSubtotal = MoneyUtils.normalize(subtotal);
        BigDecimal normalizedTax = MoneyUtils.normalize(tax);
        BigDecimal normalizedShipping = MoneyUtils.normalize(shippingCost);

        return MoneyUtils.add(
                MoneyUtils.add(normalizedSubtotal, normalizedTax),
                normalizedShipping
        );
    }

    /**
     * Calcula el subtotal de una orden sumando los line totals de todos sus items
     *
     * @param itemLineTotals Lista de line totals de los items
     * @return Subtotal de la orden
     */
    public static BigDecimal calculateOrderSubtotal(List<BigDecimal> itemLineTotals) {
        if (itemLineTotals == null || itemLineTotals.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return itemLineTotals.stream()
                .map(MoneyUtils::normalize)
                .reduce(BigDecimal.ZERO, MoneyUtils::add);
    }

    /**
     * Calcula impuestos sobre un subtotal
     *
     * @param subtotal Subtotal sobre el cual calcular impuestos
     * @param taxRate Tasa de impuesto en porcentaje (ej: 19 para 19%)
     * @return Monto de impuestos
     */
    public static BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        if (subtotal == null || taxRate == null) {
            return BigDecimal.ZERO;
        }
        return MoneyUtils.percentage(subtotal, taxRate);
    }

    /**
     * Calcula el costo de envío basado en el peso y zona
     * (Implementación simplificada - en producción sería más complejo)
     *
     * @param subtotal Subtotal de la orden
     * @param shippingZone Zona de envío (1-5)
     * @return Costo de envío calculado
     */
    public static BigDecimal calculateShippingCost(BigDecimal subtotal, int shippingZone) {
        BigDecimal normalizedSubtotal = MoneyUtils.normalize(subtotal);

        // Envío gratis para compras superiores al umbral
        if (normalizedSubtotal.compareTo(Constants.FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }

        // Costo base por zona
        BigDecimal baseCost = MoneyUtils.of(5.0 * shippingZone);

        // Costo adicional por porcentaje del subtotal
        BigDecimal percentageCost = MoneyUtils.percentage(normalizedSubtotal, BigDecimal.valueOf(2));

        return MoneyUtils.add(baseCost, percentageCost);
    }

    /**
     * Calcula descuento sobre un subtotal
     *
     * @param subtotal Subtotal sobre el cual aplicar descuento
     * @param discountPercentage Porcentaje de descuento (ej: 10 para 10%)
     * @return Monto del descuento
     */
    public static BigDecimal calculateDiscount(BigDecimal subtotal, BigDecimal discountPercentage) {
        if (subtotal == null || discountPercentage == null) {
            return BigDecimal.ZERO;
        }
        return MoneyUtils.percentage(subtotal, discountPercentage);
    }

    /**
     * Calcula el total a pagar después de aplicar un descuento
     *
     * @param subtotal Subtotal original
     * @param discountAmount Monto del descuento
     * @return Total después del descuento
     */
    public static BigDecimal applyDiscount(BigDecimal subtotal, BigDecimal discountAmount) {
        return MoneyUtils.subtract(subtotal, discountAmount);
    }

    /**
     * Calcula el precio unitario promedio ponderado de items
     *
     * @param prices Lista de precios unitarios
     * @param quantities Lista de cantidades correspondientes
     * @return Precio promedio ponderado
     */
    public static BigDecimal calculateWeightedAveragePrice(List<BigDecimal> prices, List<Integer> quantities) {
        if (prices == null || quantities == null ||
                prices.isEmpty() || quantities.isEmpty() ||
                prices.size() != quantities.size()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalValue = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (int i = 0; i < prices.size(); i++) {
            BigDecimal price = MoneyUtils.normalize(prices.get(i));
            int quantity = quantities.get(i);
            totalValue = MoneyUtils.add(totalValue, MoneyUtils.multiply(price, quantity));
            totalQuantity += quantity;
        }

        if (totalQuantity == 0) {
            return BigDecimal.ZERO;
        }

        return MoneyUtils.divide(totalValue, BigDecimal.valueOf(totalQuantity));
    }

    /**
     * Valida que el stock sea suficiente para una cantidad solicitada
     *
     * @param availableStock Stock disponible
     * @param requestedQuantity Cantidad solicitada
     * @return true si hay stock suficiente
     */
    public static boolean hasEnoughStock(Integer availableStock, Integer requestedQuantity) {
        if (availableStock == null || requestedQuantity == null) {
            return false;
        }
        return availableStock >= requestedQuantity;
    }

    /**
     * Calcula el nuevo stock después de una venta
     *
     * @param currentStock Stock actual
     * @param soldQuantity Cantidad vendida
     * @return Nuevo stock
     */
    public static Integer calculateNewStock(Integer currentStock, Integer soldQuantity) {
        if (currentStock == null || soldQuantity == null) {
            return currentStock;
        }
        return Math.max(0, currentStock - soldQuantity);
    }

    /**
     * Calcula el porcentaje que representa un valor respecto a un total
     *
     * @param value Valor parcial
     * @param total Valor total
     * @return Porcentaje (0-100)
     */
    public static BigDecimal calculatePercentageOfTotal(BigDecimal value, BigDecimal total) {
        if (value == null || total == null || MoneyUtils.isZero(total)) {
            return BigDecimal.ZERO;
        }

        BigDecimal ratio = MoneyUtils.divide(value, total);
        return MoneyUtils.multiply(ratio, BigDecimal.valueOf(100));
    }
}
package co.edu.cesde.pps.enums;

/**
 * Enumeración de tipos de dirección.
 *
 * Tipos:
 * - SHIPPING: Dirección de envío
 * - BILLING: Dirección de facturación
 *
 * Una dirección puede ser de envío, de facturación, o ambas (dos registros separados).
 */
public enum AddressType {
    /**
     * Dirección de envío - Donde se entregan los productos
     */
    SHIPPING,

    /**
     * Dirección de facturación - Para emitir factura/recibo
     */
    BILLING
}
package co.edu.cesde.pps.util;

import java.math.BigDecimal;

/**
 * Clase que centraliza las constantes del sistema.
 *
 * Incluye:
 * - Valores por defecto
 * - Límites y umbrales de negocio
 * - Configuraciones del sistema
 * - Tasas y porcentajes
 *
 * Usar estas constantes en lugar de valores mágicos dispersos en el código
 * facilita el mantenimiento y la configuración del sistema.
 */
public final class Constants {

    // Constructor privado para prevenir instanciación
    private Constants() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    // ========== CONFIGURACIÓN DE SESIONES ==========

    /**
     * Tiempo de expiración por defecto de sesiones de invitado (en horas)
     */
    public static final int DEFAULT_SESSION_EXPIRATION_HOURS = 24;

    /**
     * Tiempo de expiración de sesiones de usuario registrado (en horas)
     */
    public static final int USER_SESSION_EXPIRATION_HOURS = 168; // 7 días

    // ========== CONFIGURACIÓN DE CARRITOS ==========

    /**
     * Tiempo máximo de inactividad antes de considerar un carrito abandonado (en horas)
     */
    public static final int CART_ABANDONMENT_HOURS = 48;

    /**
     * Cantidad máxima de un producto en el carrito
     */
    public static final int MAX_CART_ITEM_QUANTITY = 99;

    /**
     * Cantidad mínima de un producto en el carrito
     */
    public static final int MIN_CART_ITEM_QUANTITY = 1;

    // ========== CONFIGURACIÓN DE PRODUCTOS ==========

    /**
     * Stock mínimo para considerar un producto con bajo inventario
     */
    public static final int LOW_STOCK_THRESHOLD = 10;

    /**
     * Stock mínimo para permitir ventas
     */
    public static final int MIN_STOCK_FOR_SALE = 1;

    /**
     * Precio mínimo permitido para un producto
     */
    public static final BigDecimal MIN_PRODUCT_PRICE = MoneyUtils.of(0.01);

    /**
     * Precio máximo permitido para un producto
     */
    public static final BigDecimal MAX_PRODUCT_PRICE = MoneyUtils.of(999999.99);

    // ========== CONFIGURACIÓN DE ÓRDENES ==========

    /**
     * Tasa de impuestos por defecto (porcentaje)
     * 19% para Colombia
     */
    public static final BigDecimal DEFAULT_TAX_RATE = BigDecimal.valueOf(19);

    /**
     * Umbral de compra para envío gratis
     */
    public static final BigDecimal FREE_SHIPPING_THRESHOLD = MoneyUtils.of(100.00);

    /**
     * Costo base de envío
     */
    public static final BigDecimal BASE_SHIPPING_COST = MoneyUtils.of(5.00);

    /**
     * Monto mínimo para procesar una orden
     */
    public static final BigDecimal MIN_ORDER_AMOUNT = MoneyUtils.of(1.00);

    /**
     * Monto máximo para procesar una orden
     */
    public static final BigDecimal MAX_ORDER_AMOUNT = MoneyUtils.of(999999.99);

    // ========== CONFIGURACIÓN DE USUARIOS ==========

    /**
     * Longitud mínima de la contraseña
     */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Longitud máxima de la contraseña
     */
    public static final int MAX_PASSWORD_LENGTH = 100;

    /**
     * Longitud mínima del nombre
     */
    public static final int MIN_NAME_LENGTH = 2;

    /**
     * Longitud máxima del nombre
     */
    public static final int MAX_NAME_LENGTH = 100;

    /**
     * Longitud máxima del email
     */
    public static final int MAX_EMAIL_LENGTH = 255;

    // ========== CONFIGURACIÓN DE DIRECCIONES ==========

    /**
     * Número máximo de direcciones por usuario
     */
    public static final int MAX_ADDRESSES_PER_USER = 10;

    // ========== CONFIGURACIÓN DE PAGINACIÓN ==========

    /**
     * Tamaño de página por defecto para listados
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Tamaño máximo de página permitido
     */
    public static final int MAX_PAGE_SIZE = 100;

    // ========== CONFIGURACIÓN DE PAGOS ==========

    /**
     * Tiempo máximo para completar un pago (en minutos)
     */
    public static final int PAYMENT_TIMEOUT_MINUTES = 30;

    /**
     * Número máximo de reintentos de pago permitidos
     */
    public static final int MAX_PAYMENT_RETRIES = 3;

    // ========== FORMATOS Y PATRONES ==========

    /**
     * Formato de números de orden: ORD-YYYYMMDD-XXXXXX
     */
    public static final String ORDER_NUMBER_PREFIX = "ORD-";

    /**
     * Longitud del identificador único en número de orden
     */
    public static final int ORDER_NUMBER_UNIQUE_LENGTH = 6;

    /**
     * Formato de SKU de producto
     */
    public static final String SKU_PREFIX = "SKU-";

    // ========== MENSAJES DE ERROR COMUNES ==========

    public static final String ERROR_NULL_VALUE = "Value cannot be null";
    public static final String ERROR_NEGATIVE_VALUE = "Value cannot be negative";
    public static final String ERROR_POSITIVE_VALUE = "Value must be positive";
    public static final String ERROR_INVALID_RANGE = "Value is outside valid range";
    public static final String ERROR_INVALID_FORMAT = "Invalid format";
    public static final String ERROR_EMPTY_COLLECTION = "Collection cannot be empty";
    public static final String ERROR_DUPLICATE_ENTRY = "Duplicate entry found";
    public static final String ERROR_NOT_FOUND = "Entity not found";
    public static final String ERROR_INSUFFICIENT_STOCK = "Insufficient stock";
    public static final String ERROR_INVALID_STATE = "Invalid state for operation";

    // ========== CONFIGURACIÓN DE MONEDAS ==========

    /**
     * Moneda por defecto del sistema
     */
    public static final String DEFAULT_CURRENCY = "USD";

    /**
     * Moneda secundaria (Colombia)
     */
    public static final String SECONDARY_CURRENCY = "COP";

    // ========== ROLES DEL SISTEMA ==========

    /**
     * Nombre del rol administrador
     */
    public static final String ROLE_ADMIN = "ADMIN";

    /**
     * Nombre del rol cliente
     */
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    /**
     * ID del rol por defecto para nuevos usuarios
     */
    public static final Long DEFAULT_ROLE_ID = 2L; // CUSTOMER

    // ========== ESTADOS DEL SISTEMA ==========

    /**
     * ID del estado de orden inicial
     */
    public static final Long ORDER_STATUS_PENDING_ID = 1L;

    /**
     * ID del estado de pago pendiente
     */
    public static final Long PAYMENT_STATUS_PENDING_ID = 1L;

    /**
     * ID del método de pago tarjeta de crédito
     */
    public static final Long PAYMENT_METHOD_CREDIT_CARD_ID = 1L;
}
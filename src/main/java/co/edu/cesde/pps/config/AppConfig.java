package co.edu.cesde.pps.config;

/**
 * Configuración general de la aplicación.
 *
 * Centraliza configuraciones que no son específicas de base de datos,
 * como límites de la aplicación, timeouts, configuraciones de negocio, etc.
 *
 * En futuras etapas, esta clase se puede expandir para usar:
 * - @Configuration (Spring)
 * - @PropertySource para cargar desde application.properties
 * - Variables de entorno
 */
public class AppConfig {

    // Configuración de Sesiones
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    private static final int GUEST_SESSION_TIMEOUT_HOURS = 24;
    private static final int USER_SESSION_TIMEOUT_HOURS = 168; // 7 días

    // Configuración de Carritos
    private static final int CART_ABANDONMENT_THRESHOLD_HOURS = 48;
    private static final int MAX_ITEMS_PER_CART = 50;
    private static final int MAX_QUANTITY_PER_ITEM = 99;

    // Configuración de Productos
    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int PRODUCTS_PER_PAGE = 20;
    private static final int MAX_PRODUCTS_PER_PAGE = 100;

    // Configuración de Órdenes
    private static final String ORDER_NUMBER_PREFIX = "ORD-";
    private static final int ORDER_NUMBER_LENGTH = 12;

    // Configuración de Paginación
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    // Configuración de Impuestos y Envío
    private static final double DEFAULT_TAX_RATE = 19.0; // 19% para Colombia
    private static final double FREE_SHIPPING_THRESHOLD = 100.0;
    private static final double BASE_SHIPPING_COST = 5.0;

    // Configuración de Seguridad
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    // Configuración de Usuarios
    private static final int MAX_ADDRESSES_PER_USER = 10;

    // Constructor privado para prevenir instanciación
    private AppConfig() {
        throw new AssertionError("AppConfig is a utility class and cannot be instantiated");
    }

    // Getters para configuraciones de sesión

    public static int getSessionTimeoutMinutes() {
        return SESSION_TIMEOUT_MINUTES;
    }

    public static int getGuestSessionTimeoutHours() {
        return GUEST_SESSION_TIMEOUT_HOURS;
    }

    public static int getUserSessionTimeoutHours() {
        return USER_SESSION_TIMEOUT_HOURS;
    }

    // Getters para configuraciones de carrito

    public static int getCartAbandonmentThresholdHours() {
        return CART_ABANDONMENT_THRESHOLD_HOURS;
    }

    public static int getMaxItemsPerCart() {
        return MAX_ITEMS_PER_CART;
    }

    public static int getMaxQuantityPerItem() {
        return MAX_QUANTITY_PER_ITEM;
    }

    // Getters para configuraciones de productos

    public static int getLowStockThreshold() {
        return LOW_STOCK_THRESHOLD;
    }

    public static int getProductsPerPage() {
        return PRODUCTS_PER_PAGE;
    }

    public static int getMaxProductsPerPage() {
        return MAX_PRODUCTS_PER_PAGE;
    }

    // Getters para configuraciones de órdenes

    public static String getOrderNumberPrefix() {
        return ORDER_NUMBER_PREFIX;
    }

    public static int getOrderNumberLength() {
        return ORDER_NUMBER_LENGTH;
    }

    // Getters para configuraciones de paginación

    public static int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    public static int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }

    // Getters para configuraciones de impuestos y envío

    public static double getDefaultTaxRate() {
        return DEFAULT_TAX_RATE;
    }

    public static double getFreeShippingThreshold() {
        return FREE_SHIPPING_THRESHOLD;
    }

    public static double getBaseShippingCost() {
        return BASE_SHIPPING_COST;
    }

    // Getters para configuraciones de seguridad

    public static int getMinPasswordLength() {
        return MIN_PASSWORD_LENGTH;
    }

    public static int getMaxPasswordLength() {
        return MAX_PASSWORD_LENGTH;
    }

    public static int getMaxLoginAttempts() {
        return MAX_LOGIN_ATTEMPTS;
    }

    public static int getLockoutDurationMinutes() {
        return LOCKOUT_DURATION_MINUTES;
    }

    // Getters para configuraciones de usuarios

    public static int getMaxAddressesPerUser() {
        return MAX_ADDRESSES_PER_USER;
    }

    /**
     * Obtiene el ambiente de ejecución desde variables de entorno.
     * Por defecto: development
     *
     * @return environment (development, staging, production)
     */
    public static String getEnvironment() {
        String environment = System.getProperty("APP_ENVIRONMENT");
        if (environment != null && !environment.isBlank()) {
            return environment;
        }
        return System.getenv().getOrDefault("APP_ENVIRONMENT", "development");
    }

    /**
     * Verifica si la aplicación está en modo producción.
     *
     * @return true si está en producción
     */
    public static boolean isProduction() {
        return "production".equalsIgnoreCase(getEnvironment());
    }

    /**
     * Verifica si la aplicación está en modo desarrollo.
     *
     * @return true si está en desarrollo
     */
    public static boolean isDevelopment() {
        return "development".equalsIgnoreCase(getEnvironment());
    }

    /**
     * Obtiene el nombre de la aplicación.
     *
     * @return nombre de la aplicación
     */
    public static String getApplicationName() {
        return "Product Purchasing System";
    }

    /**
     * Obtiene la versión de la aplicación.
     *
     * @return versión
     */
    public static String getVersion() {
        return "1.0-SNAPSHOT";
    }
}
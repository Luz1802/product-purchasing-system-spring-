package co.edu.cesde.pps.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated Configuración histórica del bootstrap manual de JPA.
 *     Se conserva solo como referencia y no debe usarse en el flujo activo de Spring Boot.
 */
@Deprecated(since = "2026-04")
public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String DEFAULT_DB_HOST = "localhost";
    private static final String DEFAULT_DB_PORT = "3306";
    private static final String DEFAULT_DB_NAME = "pps_db";
    private static final String DEFAULT_DB_USER = "user_pps";
    private static final String DEFAULT_DB_PASSWORD = "";
    private static final String DEFAULT_DIALECT = "org.hibernate.dialect.MySQL8Dialect";
    private static final String DEFAULT_DDL_AUTO = "update";
    private static final boolean DEFAULT_SHOW_SQL = true;
    private static final boolean DEFAULT_FORMAT_SQL = true;
    private static final boolean DEFAULT_USE_SQL_COMMENTS = true;
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final int DEFAULT_MIN_POOL_SIZE = 5;
    private static final int DEFAULT_MAX_POOL_SIZE = 20;
    private static final int DEFAULT_IDLE_TIMEOUT = 300000;
    private DatabaseConfig() {
        throw new AssertionError("DatabaseConfig is a utility class and cannot be instantiated");
    }
    public static String getDbHost() {
        String host = System.getenv().getOrDefault("DB_HOST", DEFAULT_DB_HOST);
        if (System.getenv("DB_HOST") == null) {
            log.debug("DB_HOST not set, using default: {}", DEFAULT_DB_HOST);
        }
        return host;
    }
    public static String getDbPort() {
        String port = System.getenv().getOrDefault("DB_PORT", DEFAULT_DB_PORT);
        if (System.getenv("DB_PORT") == null) {
            log.debug("DB_PORT not set, using default: {}", DEFAULT_DB_PORT);
        }
        return port;
    }
    public static String getDbName() {
        String dbName = System.getenv().getOrDefault("DB_NAME", DEFAULT_DB_NAME);
        if (System.getenv("DB_NAME") == null) {
            log.debug("DB_NAME not set, using default: {}", DEFAULT_DB_NAME);
        }
        return dbName;
    }
    public static String getDbUser() {
        String user = System.getenv().getOrDefault("DB_USER", DEFAULT_DB_USER);
        if (System.getenv("DB_USER") == null) {
            log.debug("DB_USER not set, using default: {}", DEFAULT_DB_USER);
        }
        return user;
    }
    public static String getDbPassword() {
        String password = System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_DB_PASSWORD);
        if (password.isEmpty()) {
            log.warn("DB_PASSWORD is empty - not recommended for production");
        }
        return password;
    }
    public static int getPoolSize() {
        String poolSizeStr = System.getenv().getOrDefault("DB_POOL_SIZE", String.valueOf(DEFAULT_POOL_SIZE));
        try {
            return Integer.parseInt(poolSizeStr);
        } catch (NumberFormatException e) {
            log.warn("Invalid DB_POOL_SIZE: {}, using default: {}", poolSizeStr, DEFAULT_POOL_SIZE);
            return DEFAULT_POOL_SIZE;
        }
    }
    public static String getJdbcUrl() {
        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                getDbHost(), getDbPort(), getDbName());
        log.info("JDBC URL configured: {}", url);
        return url;
    }
    public static String getDriverClassName() { return "com.mysql.cj.jdbc.Driver"; }
    public static String getHibernateDialect() { return DEFAULT_DIALECT; }
    public static String getHibernateDdlAuto() { return DEFAULT_DDL_AUTO; }
    public static boolean isShowSql() { return DEFAULT_SHOW_SQL; }
    public static boolean isFormatSql() { return DEFAULT_FORMAT_SQL; }
    public static boolean isUseSqlComments() { return DEFAULT_USE_SQL_COMMENTS; }
    public static int getMinPoolSize() { return DEFAULT_MIN_POOL_SIZE; }
    public static int getMaxPoolSize() { return DEFAULT_MAX_POOL_SIZE; }
    public static int getIdleTimeout() { return DEFAULT_IDLE_TIMEOUT; }
}
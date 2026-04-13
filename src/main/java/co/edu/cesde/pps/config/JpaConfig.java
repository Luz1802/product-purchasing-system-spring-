package co.edu.cesde.pps.config;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * @deprecated Bootstrap manual histórico de JPA.
 *     La aplicación usa ahora EntityManagerFactory y transacciones administradas por Spring Boot.
 */
@Deprecated(since = "2026-04")
public final class JpaConfig {
    private JpaConfig() {
        throw new AssertionError("JpaConfig is a utility class and cannot be instantiated");
    }
    public static EntityManagerFactory getEntityManagerFactory() {
        throw manualConfigurationDisabled();
    }
    public static EntityManager createEntityManager() {
        throw manualConfigurationDisabled();
    }
    public static void close() {
        throw manualConfigurationDisabled();
    }

    private static UnsupportedOperationException manualConfigurationDisabled() {
        return new UnsupportedOperationException(
                "JpaConfig es legado del bootstrap manual de JPA. " +
                        "Use la autoconfiguración de Spring Boot, los repositorios Spring Data JPA " +
                        "y las transacciones con @Transactional."
        );
    }
}
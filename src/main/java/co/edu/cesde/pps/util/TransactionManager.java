package co.edu.cesde.pps.util;
import jakarta.persistence.EntityManager;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @deprecated Utilidad histórica de la configuración JPA manual.
 *     La aplicación ahora usa transacciones administradas por Spring con {@code @Transactional}.
 */
@Deprecated(since = "2026-04")
public final class TransactionManager {

    private TransactionManager() {
        throw new AssertionError("TransactionManager is a utility class and cannot be instantiated");
    }

    public static <T> T executeInTransaction(Function<EntityManager, T> operation) {
        Objects.requireNonNull(operation, "operation cannot be null");
        throw manualConfigurationDisabled();
    }

    public static void executeInTransaction(Consumer<EntityManager> operation) {
        Objects.requireNonNull(operation, "operation cannot be null");
        throw manualConfigurationDisabled();
    }

    public static <T> T executeReadOnly(Function<EntityManager, T> operation) {
        Objects.requireNonNull(operation, "operation cannot be null");
        throw manualConfigurationDisabled();
    }

    private static UnsupportedOperationException manualConfigurationDisabled() {
        return new UnsupportedOperationException(
                "TransactionManager es legado de la configuración JPA manual. " +
                        "Use servicios Spring con @Transactional o repositorios Spring Data JPA."
        );
    }
}
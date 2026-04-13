package co.edu.cesde.pps.util;
import co.edu.cesde.pps.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Pattern;
public final class ValidationUtils {
    private static final Logger log = LoggerFactory.getLogger(ValidationUtils.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{7,15}$");
    private ValidationUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            log.debug("Validation failed: {} is null", fieldName);
            throw new ValidationException(fieldName, null, "Field cannot be null");
        }
    }
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            log.debug("Validation failed: {} is null or blank", fieldName);
            throw new ValidationException(fieldName, value, "Field cannot be null or empty");
        }
    }
    public static void validateNotEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            log.debug("Validation failed: {} collection is null or empty", fieldName);
            throw new ValidationException(fieldName, collection, "Collection cannot be null or empty");
        }
    }
    public static void validatePositive(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("Validation failed: {} must be positive, got: {}", fieldName, value);
            throw new ValidationException(fieldName, value, "Value must be positive (> 0)");
        }
    }
    public static void validateNonNegative(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            log.debug("Validation failed: {} cannot be negative, got: {}", fieldName, value);
            throw new ValidationException(fieldName, value, "Value cannot be negative");
        }
    }
    public static void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            log.debug("Validation failed: {} must be positive, got: {}", fieldName, value);
            throw new ValidationException(fieldName, value, "Value must be positive (> 0)");
        }
    }
    public static void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            log.debug("Validation failed: {} cannot be negative, got: {}", fieldName, value);
            throw new ValidationException(fieldName, value, "Value cannot be negative");
        }
    }
    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            log.debug("Validation failed: {} must be between {} and {}, got: {}", fieldName, min, max, value);
            throw new ValidationException(fieldName, value, String.format("Value must be between %d and %d", min, max));
        }
    }
    public static void validateRange(BigDecimal value, BigDecimal min, BigDecimal max, String fieldName) {
        if (value == null || value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            log.debug("Validation failed: {} must be between {} and {}, got: {}", fieldName, min, max, value);
            throw new ValidationException(fieldName, value, String.format("Value must be between %s and %s", min, max));
        }
    }
    public static void validateEmail(String email) {
        validateNotBlank(email, "email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            log.debug("Validation failed: Invalid email format: {}", email);
            throw new ValidationException("email", email, "Invalid email format");
        }
    }
    public static void validateEmail(String email, String fieldName) {
        validateNotBlank(email, fieldName);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            log.debug("Validation failed: Invalid email format for {}: {}", fieldName, email);
            throw new ValidationException(fieldName, email, "Invalid email format");
        }
    }
    public static void validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            log.debug("Validation failed: Invalid phone format: {}", phone);
            throw new ValidationException("phone", phone, "Invalid phone format");
        }
    }
    public static void validatePhone(String phone, String fieldName) {
        if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            log.debug("Validation failed: Invalid phone format for {}: {}", fieldName, phone);
            throw new ValidationException(fieldName, phone, "Invalid phone format");
        }
    }
    public static void validateLength(String value, int minLength, int maxLength, String fieldName) {
        validateNotNull(value, fieldName);
        int length = value.length();
        if (length < minLength || length > maxLength) {
            log.debug("Validation failed: {} length must be between {} and {}, got: {}", fieldName, minLength, maxLength, length);
            throw new ValidationException(fieldName, value, String.format("Length must be between %d and %d characters", minLength, maxLength));
        }
    }
    public static void validateMinLength(String value, int minLength, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.length() < minLength) {
            log.debug("Validation failed: {} length must be at least {}, got: {}", fieldName, minLength, value.length());
            throw new ValidationException(fieldName, value, String.format("Length must be at least %d characters", minLength));
        }
    }
    public static void validateMaxLength(String value, int maxLength, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.length() > maxLength) {
            log.debug("Validation failed: {} length must be at most {}, got: {}", fieldName, maxLength, value.length());
            throw new ValidationException(fieldName, value, String.format("Length must be at most %d characters", maxLength));
        }
    }
}
package co.edu.cesde.pps.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Clase utilitaria para operaciones con valores monetarios.
 *
 * Proporciona métodos estáticos para:
 * - Operaciones aritméticas con redondeo consistente
 * - Formateo de valores monetarios
 * - Comparaciones seguras
 * - Conversión entre tipos
 *
 * Todas las operaciones usan BigDecimal para evitar errores de redondeo.
 * La escala por defecto es 2 decimales con redondeo HALF_EVEN (bankers rounding).
 */
public final class MoneyUtils {

    /**
     * Escala por defecto para valores monetarios (2 decimales)
     */
    public static final int DEFAULT_SCALE = 2;

    /**
     * Modo de redondeo por defecto (HALF_EVEN - bankers rounding)
     */
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;

    // Constructor privado para prevenir instanciación
    private MoneyUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Crea un BigDecimal con la escala y redondeo apropiados para dinero
     *
     * @param amount Valor a convertir
     * @return BigDecimal con escala 2 y redondeo HALF_EVEN
     */
    public static BigDecimal of(double amount) {
        return BigDecimal.valueOf(amount).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Crea un BigDecimal con la escala y redondeo apropiados para dinero
     *
     * @param amount Valor a convertir
     * @return BigDecimal con escala 2 y redondeo HALF_EVEN
     */
    public static BigDecimal of(long amount) {
        return BigDecimal.valueOf(amount).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Crea un BigDecimal con la escala y redondeo apropiados para dinero
     *
     * @param amount Valor como String
     * @return BigDecimal con escala 2 y redondeo HALF_EVEN
     */
    public static BigDecimal of(String amount) {
        return new BigDecimal(amount).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Normaliza un BigDecimal a la escala monetaria estándar
     *
     * @param amount Valor a normalizar
     * @return BigDecimal normalizado o BigDecimal.ZERO si es null
     */
    public static BigDecimal normalize(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        }
        return amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Suma dos valores monetarios
     *
     * @param a Primer valor
     * @param b Segundo valor
     * @return Suma normalizada
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        BigDecimal normalizedA = normalize(a);
        BigDecimal normalizedB = normalize(b);
        return normalizedA.add(normalizedB).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Resta dos valores monetarios (a - b)
     *
     * @param a Minuendo
     * @param b Sustraendo
     * @return Diferencia normalizada
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        BigDecimal normalizedA = normalize(a);
        BigDecimal normalizedB = normalize(b);
        return normalizedA.subtract(normalizedB).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Multiplica un valor monetario por una cantidad
     *
     * @param amount Valor monetario
     * @param quantity Cantidad (puede ser entero o decimal)
     * @return Producto normalizado
     */
    public static BigDecimal multiply(BigDecimal amount, BigDecimal quantity) {
        BigDecimal normalizedAmount = normalize(amount);
        if (quantity == null) {
            quantity = BigDecimal.ZERO;
        }
        return normalizedAmount.multiply(quantity).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Multiplica un valor monetario por una cantidad entera
     *
     * @param amount Valor monetario
     * @param quantity Cantidad
     * @return Producto normalizado
     */
    public static BigDecimal multiply(BigDecimal amount, int quantity) {
        return multiply(amount, BigDecimal.valueOf(quantity));
    }

    /**
     * Divide un valor monetario entre un divisor
     *
     * @param amount Dividendo
     * @param divisor Divisor
     * @return Cociente normalizado
     */
    public static BigDecimal divide(BigDecimal amount, BigDecimal divisor) {
        BigDecimal normalizedAmount = normalize(amount);
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Cannot divide by zero or null");
        }
        return normalizedAmount.divide(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Calcula un porcentaje de un valor monetario
     *
     * @param amount Valor base
     * @param percentage Porcentaje (ej: 15 para 15%)
     * @return Valor del porcentaje
     */
    public static BigDecimal percentage(BigDecimal amount, BigDecimal percentage) {
        BigDecimal normalizedAmount = normalize(amount);
        BigDecimal normalizedPercentage = percentage != null ? percentage : BigDecimal.ZERO;
        return normalizedAmount.multiply(normalizedPercentage)
                .divide(BigDecimal.valueOf(100), DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Verifica si un valor es positivo (mayor que cero)
     *
     * @param amount Valor a verificar
     * @return true si es positivo, false en otro caso
     */
    public static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si un valor es negativo (menor que cero)
     *
     * @param amount Valor a verificar
     * @return true si es negativo, false en otro caso
     */
    public static boolean isNegative(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Verifica si un valor es cero
     *
     * @param amount Valor a verificar
     * @return true si es cero, false en otro caso
     */
    public static boolean isZero(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Retorna el mayor de dos valores
     *
     * @param a Primer valor
     * @param b Segundo valor
     * @return El valor mayor
     */
    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        BigDecimal normalizedA = normalize(a);
        BigDecimal normalizedB = normalize(b);
        return normalizedA.max(normalizedB);
    }

    /**
     * Retorna el menor de dos valores
     *
     * @param a Primer valor
     * @param b Segundo valor
     * @return El valor menor
     */
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        BigDecimal normalizedA = normalize(a);
        BigDecimal normalizedB = normalize(b);
        return normalizedA.min(normalizedB);
    }

    /**
     * Formatea un valor monetario para visualización
     *
     * @param amount Valor a formatear
     * @param currencyCode Código de moneda (ej: "USD", "COP", "EUR")
     * @param locale Locale para el formato
     * @return String formateado (ej: "$1,234.56")
     */
    public static String format(BigDecimal amount, String currencyCode, Locale locale) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        if (currencyCode != null) {
            formatter.setCurrency(Currency.getInstance(currencyCode));
        }
        return formatter.format(amount);
    }

    /**
     * Formatea un valor monetario en USD para US
     *
     * @param amount Valor a formatear
     * @return String formateado (ej: "$1,234.56")
     */
    public static String formatUSD(BigDecimal amount) {
        return format(amount, "USD", Locale.US);
    }

    /**
     * Formatea un valor monetario en COP para Colombia
     *
     * @param amount Valor a formatear
     * @return String formateado (ej: "$1.234,56")
     */
    public static String formatCOP(BigDecimal amount) {
        return format(amount, "COP", new Locale("es", "CO"));
    }

    /**
     * Formatea un valor monetario en EUR para Europa
     *
     * @param amount Valor a formatear
     * @return String formateado (ej: "€1.234,56")
     */
    public static String formatEUR(BigDecimal amount) {
        return format(amount, "EUR", Locale.FRANCE);
    }
}
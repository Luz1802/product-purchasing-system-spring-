package co.edu.cesde.pps.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Clase utilitaria para operaciones con fechas y tiempos.
 *
 * Proporciona métodos estáticos para:
 * - Comparaciones de fechas
 * - Validaciones de rangos temporales
 * - Formateo de fechas
 * - Cálculos de diferencias temporales
 */
public final class DateTimeUtils {

    /**
     * Formato por defecto para fechas: dd/MM/yyyy HH:mm:ss
     */
    public static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Formato corto para fechas: dd/MM/yyyy
     */
    public static final DateTimeFormatter DATE_ONLY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Formato ISO 8601: yyyy-MM-dd'T'HH:mm:ss
     */
    public static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Constructor privado para prevenir instanciación
    private DateTimeUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Verifica si una fecha está en el pasado
     *
     * @param dateTime Fecha a verificar
     * @return true si la fecha es anterior a ahora
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Verifica si una fecha está en el futuro
     *
     * @param dateTime Fecha a verificar
     * @return true si la fecha es posterior a ahora
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Verifica si una fecha ha expirado (está en el pasado)
     *
     * @param expirationDate Fecha de expiración
     * @return true si la fecha ha expirado
     */
    public static boolean isExpired(LocalDateTime expirationDate) {
        return isPast(expirationDate);
    }

    /**
     * Verifica si una fecha está dentro de un rango
     *
     * @param dateTime Fecha a verificar
     * @param start Inicio del rango (inclusivo)
     * @param end Fin del rango (inclusivo)
     * @return true si la fecha está dentro del rango
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        if (dateTime == null || start == null || end == null) {
            return false;
        }
        return !dateTime.isBefore(start) && !dateTime.isAfter(end);
    }

    /**
     * Calcula la diferencia en días entre dos fechas
     *
     * @param start Fecha inicial
     * @param end Fecha final
     * @return Número de días de diferencia
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calcula la diferencia en horas entre dos fechas
     *
     * @param start Fecha inicial
     * @param end Fecha final
     * @return Número de horas de diferencia
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Calcula la diferencia en minutos entre dos fechas
     *
     * @param start Fecha inicial
     * @param end Fecha final
     * @return Número de minutos de diferencia
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Agrega días a una fecha
     *
     * @param dateTime Fecha base
     * @param days Número de días a agregar
     * @return Nueva fecha con los días agregados
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusDays(days);
    }

    /**
     * Agrega horas a una fecha
     *
     * @param dateTime Fecha base
     * @param hours Número de horas a agregar
     * @return Nueva fecha con las horas agregadas
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }

    /**
     * Formatea una fecha con el formato por defecto
     *
     * @param dateTime Fecha a formatear
     * @return String formateado o null si la fecha es null
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * Formatea una fecha con formato personalizado
     *
     * @param dateTime Fecha a formatear
     * @param formatter Formateador a usar
     * @return String formateado o null si la fecha es null
     */
    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(formatter);
    }

    /**
     * Formatea una fecha en formato ISO 8601
     *
     * @param dateTime Fecha a formatear
     * @return String formateado o null si la fecha es null
     */
    public static String formatISO(LocalDateTime dateTime) {
        return format(dateTime, ISO_FORMATTER);
    }

    /**
     * Parsea un String a LocalDateTime usando el formato por defecto
     *
     * @param dateTimeString String a parsear
     * @return LocalDateTime parseado
     */
    public static LocalDateTime parse(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, DEFAULT_FORMATTER);
    }

    /**
     * Parsea un String a LocalDateTime usando formato ISO 8601
     *
     * @param dateTimeString String a parsear
     * @return LocalDateTime parseado
     */
    public static LocalDateTime parseISO(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, ISO_FORMATTER);
    }

    /**
     * Obtiene la fecha/hora actual
     *
     * @return LocalDateTime.now()
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Compara dos fechas de forma segura (maneja nulls)
     *
     * @param date1 Primera fecha
     * @param date2 Segunda fecha
     * @return -1 si date1 < date2, 0 si son iguales, 1 si date1 > date2
     *         Nulls se consideran como la fecha más antigua
     */
    public static int compare(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null && date2 == null) {
            return 0;
        }
        if (date1 == null) {
            return -1;
        }
        if (date2 == null) {
            return 1;
        }
        return date1.compareTo(date2);
    }
}
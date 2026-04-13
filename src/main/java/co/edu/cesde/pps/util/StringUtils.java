package co.edu.cesde.pps.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Clase utilitaria para operaciones con Strings.
 *
 * Proporciona métodos estáticos para:
 * - Generación de slugs URL-friendly
 * - Sanitización de texto
 * - Validaciones de formato
 * - Transformaciones comunes
 */
public final class StringUtils {

    private static final Pattern SLUG_PATTERN = Pattern.compile("[^a-z0-9]+");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    // Constructor privado para prevenir instanciación
    private StringUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Verifica si un String es nulo o vacío
     *
     * @param str String a verificar
     * @return true si es null, vacío o solo espacios en blanco
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Verifica si un String no es nulo ni vacío
     *
     * @param str String a verificar
     * @return true si tiene contenido
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Genera un slug URL-friendly desde un texto
     *
     * Convierte: "Laptops Gaming 15\"" → "laptops-gaming-15"
     *
     * @param text Texto a convertir
     * @return Slug en minúsculas con guiones
     */
    public static String slugify(String text) {
        if (isBlank(text)) {
            return "";
        }

        // Normalizar caracteres Unicode (quitar acentos)
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");

        // Convertir a minúsculas
        String lowercase = withoutAccents.toLowerCase(Locale.ROOT);

        // Reemplazar espacios y caracteres no permitidos por guiones
        String slug = SLUG_PATTERN.matcher(lowercase).replaceAll("-");

        // Eliminar guiones al inicio y final
        slug = slug.replaceAll("^-+", "").replaceAll("-+$", "");

        // Reemplazar múltiples guiones consecutivos por uno solo
        slug = slug.replaceAll("-+", "-");

        return slug;
    }

    /**
     * Capitaliza la primera letra de un String
     *
     * @param str String a capitalizar
     * @return String con primera letra mayúscula
     */
    public static String capitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Capitaliza cada palabra de un String
     *
     * @param str String a capitalizar
     * @return String con cada palabra capitalizada
     */
    public static String capitalizeWords(String str) {
        if (isBlank(str)) {
            return str;
        }

        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(capitalize(words[i]));
        }

        return result.toString();
    }

    /**
     * Trunca un String a una longitud máxima
     *
     * @param str String a truncar
     * @param maxLength Longitud máxima
     * @return String truncado
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }

    /**
     * Trunca un String y agrega "..." al final
     *
     * @param str String a truncar
     * @param maxLength Longitud máxima (incluyendo los puntos suspensivos)
     * @return String truncado con "..."
     */
    public static String truncateWithEllipsis(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        if (maxLength <= 3) {
            return truncate(str, maxLength);
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Sanitiza un String removiendo caracteres especiales peligrosos
     *
     * @param str String a sanitizar
     * @return String sanitizado
     */
    public static String sanitize(String str) {
        if (str == null) {
            return null;
        }
        // Remover caracteres de control y caracteres potencialmente peligrosos
        return str.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                .replaceAll("[<>\"']", "");
    }

    /**
     * Normaliza espacios en blanco (reemplaza múltiples espacios por uno solo)
     *
     * @param str String a normalizar
     * @return String con espacios normalizados
     */
    public static String normalizeWhitespace(String str) {
        if (str == null) {
            return null;
        }
        return WHITESPACE_PATTERN.matcher(str.trim()).replaceAll(" ");
    }

    /**
     * Genera un String de longitud fija con padding a la izquierda
     *
     * @param str String original
     * @param length Longitud deseada
     * @param padChar Caracter para rellenar
     * @return String con padding
     */
    public static String padLeft(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - str.length()) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * Genera un String de longitud fija con padding a la derecha
     *
     * @param str String original
     * @param length Longitud deseada
     * @param padChar Caracter para rellenar
     * @return String con padding
     */
    public static String padRight(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * Retorna un valor por defecto si el String es nulo o vacío
     *
     * @param str String a verificar
     * @param defaultValue Valor por defecto
     * @return El String original o el valor por defecto
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }

    /**
     * Enmascara un String mostrando solo los últimos N caracteres
     * Útil para números de tarjeta, emails, etc.
     *
     * @param str String a enmascarar
     * @param visibleChars Número de caracteres visibles al final
     * @param maskChar Caracter para enmascarar
     * @return String enmascarado
     */
    public static String maskExceptLast(String str, int visibleChars, char maskChar) {
        if (str == null || str.length() <= visibleChars) {
            return str;
        }
        int maskLength = str.length() - visibleChars;
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < maskLength; i++) {
            masked.append(maskChar);
        }
        masked.append(str.substring(maskLength));
        return masked.toString();
    }

    /**
     * Genera un extracto de texto para preview
     *
     * @param text Texto completo
     * @param maxLength Longitud máxima del extracto
     * @return Extracto con "..." si fue truncado
     */
    public static String excerpt(String text, int maxLength) {
        if (isBlank(text) || text.length() <= maxLength) {
            return text;
        }

        // Buscar el último espacio antes del límite para no cortar palabras
        int cutIndex = text.lastIndexOf(' ', maxLength);
        if (cutIndex == -1 || cutIndex < maxLength / 2) {
            cutIndex = maxLength;
        }

        return text.substring(0, cutIndex).trim() + "...";
    }
}
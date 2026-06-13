package cm.edu.bibliotheque.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateUtil() {
    }

    public static String format(LocalDate date) {
        return date == null ? "" : date.format(DISPLAY_FORMAT);
    }

    public static LocalDate parseIso(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
}

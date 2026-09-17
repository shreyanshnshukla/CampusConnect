package campusconnect.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Centralizes date/time parsing and formatting (java.time API) used for persistence and display. */
public final class DateUtil {
    private DateUtil() {}

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text.trim(), DATE_FORMAT);
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    public static LocalDateTime parseDateTime(String text) {
        return LocalDateTime.parse(text.trim(), DATETIME_FORMAT);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMAT);
    }
}

package custom.striker.service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Utility class for parsing date/time strings into OffsetDateTime.
 * Supports multiple common formats and numeric epoch values.
 */
public final class DateService {

    private static final ZoneId FALLBACK_ZONE = ZoneId.systemDefault();

    private static final List<DateTimeFormatter> LOCAL_DATE_TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("M/d/yyyy H:mm"),
            DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss"),
            DateTimeFormatter.ofPattern("M/d/yy H:mm"),        // added two-digit year w/ time
            DateTimeFormatter.ofPattern("M/d/yy H:mm:ss"),     // added two-digit year w/ seconds
            DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss"),
            DateTimeFormatter.ofPattern("dd MMM yyyy H:mm", Locale.ENGLISH)
    );

    private static final List<DateTimeFormatter> LOCAL_DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("M/d/yyyy"),
            DateTimeFormatter.ofPattern("M/d/yy"),             // added two-digit year
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    );

    /**
     * Attempts to parse the input string into an OffsetDateTime using multiple strategies:
     * @param input - the date/time string to parse
     * @return an Optional containing the parsed OffsetDateTime, or empty if parsing failed
     */
    public static Optional<OffsetDateTime> parseToOffsetDateTime(String input) {
        if (input == null) {
            return Optional.empty();
        }
        String s = input.trim();
        if (s.isEmpty()) {
            return Optional.empty();
        }

        // Try direct OffsetDateTime / ZonedDateTime / Instant parses
        try {
            return Optional.of(OffsetDateTime.parse(s));
        } catch (DateTimeParseException ignored) {}

        try {
            return Optional.of(ZonedDateTime.parse(s).toOffsetDateTime());
        } catch (DateTimeParseException ignored) {}

        try {
            Instant inst = Instant.parse(s);
            return Optional.of(OffsetDateTime.ofInstant(inst, FALLBACK_ZONE));
        } catch (DateTimeParseException ignored) {}

        // Numeric epoch (seconds or milliseconds)
        if (s.matches("^-?\\d+$")) {
            try {
                long n = Long.parseLong(s);
                // heuristic: if value looks like millis (>= 1e12) treat as millis
                if (Math.abs(n) > 1_000_000_000_000L) {
                    return Optional.of(OffsetDateTime.ofInstant(Instant.ofEpochMilli(n), FALLBACK_ZONE));
                } else {
                    return Optional.of(OffsetDateTime.ofInstant(Instant.ofEpochSecond(n), FALLBACK_ZONE));
                }
            } catch (NumberFormatException ignored) {}
        }

        // Try LocalDateTime formatters
        for (DateTimeFormatter fmt : LOCAL_DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(s, fmt);
                return Optional.of(ZonedDateTime.of(ldt, FALLBACK_ZONE).toOffsetDateTime());
            } catch (DateTimeParseException ignored) {}
        }

        // Try LocalDate formatters (use start of day)
        for (DateTimeFormatter fmt : LOCAL_DATE_FORMATTERS) {
            try {
                LocalDate ld = LocalDate.parse(s, fmt);
                return Optional.of(ZonedDateTime.of(ld.atStartOfDay(), FALLBACK_ZONE).toOffsetDateTime());
            } catch (DateTimeParseException ignored) {}
        }

        return Optional.empty();
    }

}

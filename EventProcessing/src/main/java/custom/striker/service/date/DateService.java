package custom.striker.service.date;

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

    /**
     * Computes a new OffsetDateTime based on the provided DateRule and starting date.
     * @param rule - the DateRule containing the number of years, months, and days to add
     * @param date - the starting OffsetDateTime to apply the rule to
     * @return an Optional containing the computed OffsetDateTime, or empty if the rule or date is null
     */
    public static Optional<OffsetDateTime> computeDateRule(DateRule rule, OffsetDateTime date) {
        if (rule == null || date == null) {
            return Optional.empty();
        }
        OffsetDateTime result = date;
        result = result.withHour(0).withMinute(0).withSecond(0).withNano(0);

        if (rule.getYears() > 0) {
            YearMonth ym = YearMonth.of(result.getYear() + 1, 1);
            int safeDay = Math.min(rule.getDays(), ym.lengthOfMonth());
            result = result.withYear(result.getYear() + 1);
            result = result.withMonth(rule.getMonths());
            result = result.withDayOfMonth(safeDay);
        } else if (rule.getMonths() > 0) {
            int month = result.getMonthValue() + rule.getMonths();
            int year = result.getYear();

            if (month > 12) {
                month = month % 12;
                year += 1;
            }

            YearMonth ym = YearMonth.of(year, month);
            int safeDay = Math.min(rule.getDays(), ym.lengthOfMonth());
            result = result.withMonth(month);
            result = result.withDayOfMonth(safeDay);
        } else {
            result = result.plusDays(rule.getDays());
        }
        return Optional.of(result);
    }

    /**
     * Returns the current date/time as an OffsetDateTime using the service's fallback zone.
     * @return current OffsetDateTime
     */
    public static OffsetDateTime now() {
        return ZonedDateTime.now(FALLBACK_ZONE).toOffsetDateTime();
    }

}

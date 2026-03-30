package custom.striker.service.date;

/**
 * Class representing a date rule, which consists of a number of days, months, and years. The rule is initialized
 * from a string in the format "D M Y", where D, M, and Y are integers representing the number of days, months,
 * and years respectively.
 */
public final class DateRule {

    private final int days;
    private final int months;
    private final int years;

    public DateRule(String rule) {
        String[] parts = rule.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date rule format. Expected format: 'D M Y' where D, M, Y " +
                    "are integers representing days, months, and years respectively.");
        }

        try {
            this.days = Integer.parseInt(parts[0]);
            this.months = Integer.parseInt(parts[1]);
            this.years = Integer.parseInt(parts[2]);

            if (days < 0 || months < 0 || years < 0) {
                throw new IllegalArgumentException("Invalid date rule format. All parts must be integers >= 0.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid date rule format. All parts must be integers >= 0.", e);
        }
    }

    public int getDays() {
        return days;
    }

    public int getMonths() {
        return months;
    }

    public int getYears() {
        return years;
    }
}

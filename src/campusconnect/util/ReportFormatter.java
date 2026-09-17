package campusconnect.util;

/** Small formatting helpers so reports/menus render consistently. */
public final class ReportFormatter {
    private ReportFormatter() {}

    public static String separator() {
        return "----------------------------------------------------------------------";
    }

    public static String doubleSeparator() {
        return "========================================================================";
    }

    public static String currency(double amount) {
        return String.format("Rs. %,.2f", amount);
    }

    public static String percent(double value) {
        return String.format("%.2f%%", value);
    }
}

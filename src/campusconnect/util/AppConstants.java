package campusconnect.util;

import campusconnect.model.SponsorshipPackageType;

/**
 * Central home for configurable business rules and file names, so
 * thresholds and paths are not scattered as magic numbers/strings
 * through the service layer.
 */
public final class AppConstants {
    private AppConstants() {}

    public static final String DATA_DIR = "data";

    public static final String EVENTS_FILE = "events.txt";
    public static final String PARTICIPANTS_FILE = "participants.txt";
    public static final String SPONSORS_FILE = "sponsors.txt";
    public static final String SPONSORSHIPS_FILE = "sponsorships.txt";
    public static final String REGISTRATIONS_FILE = "registrations.txt";
    public static final String EXPENSES_FILE = "expenses.txt";

    public static final double SILVER_MIN = 20000;
    public static final double SILVER_MAX = 49999;
    public static final double GOLD_MIN = 50000;
    public static final double GOLD_MAX = 99999;
    public static final double PLATINUM_MIN = 100000;

    public static final double BUDGET_WARNING_THRESHOLD_PERCENT = 90.0;

    /** Maps a sponsorship amount to a package tier, or null if below the minimum threshold. */
    public static SponsorshipPackageType recommendPackage(double amount) {
        if (amount >= PLATINUM_MIN) return SponsorshipPackageType.PLATINUM;
        if (amount >= GOLD_MIN) return SponsorshipPackageType.GOLD;
        if (amount >= SILVER_MIN) return SponsorshipPackageType.SILVER;
        return null;
    }
}

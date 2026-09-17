package campusconnect.test;

/** Runs every test class in the suite and prints a final pass/fail summary. */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Running CampusConnect test suite...");

        Results overall = new Results();
        overall.merge(EventAndParticipantTests.runAll());
        overall.merge(SponsorshipAndBudgetTests.runAll());
        overall.merge(RecommendationAndAnalyticsTests.runAll());

        System.out.println();
        System.out.println("========================================================================");
        System.out.println("TEST SUMMARY: " + overall.getPassed() + "/" + overall.getTotal() + " passed");
        if (!overall.getFailures().isEmpty()) {
            System.out.println("Failed tests: " + overall.getFailures());
            System.exit(1);
        } else {
            System.out.println("All tests passed.");
        }
    }
}

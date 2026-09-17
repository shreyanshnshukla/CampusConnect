package campusconnect.test;

import java.util.ArrayList;
import java.util.List;

/** Collects pass/fail outcomes for a run of tests and prints each result as it happens. */
public class Results {
    private final List<String> failures = new ArrayList<>();
    private int total = 0;
    private int passed = 0;

    public void record(String testName, boolean pass, String detail) {
        total++;
        if (pass) {
            passed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.out.println("  [FAIL] " + testName + " - " + detail);
            failures.add(testName);
        }
    }

    public int getTotal() { return total; }
    public int getPassed() { return passed; }
    public List<String> getFailures() { return failures; }

    public void merge(Results other) {
        this.total += other.total;
        this.passed += other.passed;
        this.failures.addAll(other.failures);
    }
}

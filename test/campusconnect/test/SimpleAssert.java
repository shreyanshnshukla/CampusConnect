package campusconnect.test;

/** Minimal hand-rolled assertion helpers - no JUnit dependency required. */
public final class SimpleAssert {
    private SimpleAssert() {}

    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    public static void assertTrue(String testName, boolean condition, Results results) {
        results.record(testName, condition, condition ? "" : "expected true but was false");
    }

    public static void assertEquals(String testName, Object expected, Object actual, Results results) {
        boolean pass = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        results.record(testName, pass, pass ? "" : "expected <" + expected + "> but was <" + actual + ">");
    }

    public static void assertThrows(String testName, ThrowingRunnable runnable, Results results) {
        try {
            runnable.run();
            results.record(testName, false, "expected an exception but none was thrown");
        } catch (Exception e) {
            results.record(testName, true, "");
        }
    }
}

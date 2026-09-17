package campusconnect.report;

/**
 * Base type for all generated reports. Callers (ReportService, Main) only
 * depend on this common contract; each concrete subtype decides how to
 * assemble its own content - a legitimate use of abstraction/polymorphism
 * since the three report types share a shape (a title + generated text)
 * but genuinely differ in what they compute and display.
 */
public abstract class Report {
    protected final String title;

    protected Report(String title) {
        this.title = title;
    }

    public abstract String generate();

    public String getTitle() {
        return title;
    }
}

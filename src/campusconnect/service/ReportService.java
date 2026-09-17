package campusconnect.service;

import campusconnect.exception.EventNotFoundException;
import campusconnect.model.Event;
import campusconnect.report.EventReport;
import campusconnect.report.FinancialReport;
import campusconnect.report.Report;
import campusconnect.report.SponsorReport;
import campusconnect.util.ReportFormatter;

public class ReportService {
    private final EventService eventService;
    private final RegistrationService registrationService;
    private final SponsorshipService sponsorshipService;
    private final BudgetService budgetService;
    private final AnalyticsService analyticsService;

    public ReportService(EventService eventService, RegistrationService registrationService,
                          SponsorshipService sponsorshipService, BudgetService budgetService,
                          AnalyticsService analyticsService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.sponsorshipService = sponsorshipService;
        this.budgetService = budgetService;
        this.analyticsService = analyticsService;
    }

    public Report buildEventReport(String eventId) {
        return new EventReport(eventId, eventService, registrationService, sponsorshipService,
                budgetService, analyticsService);
    }

    public Report buildSponsorReport(String eventId) {
        return new SponsorReport(eventId, sponsorshipService);
    }

    public Report buildFinancialReport(String eventId) {
        return new FinancialReport(eventId, budgetService);
    }

    public String compareEvents(String eventIdA, String eventIdB) {
        StringBuilder sb = new StringBuilder();
        try {
            Event a = eventService.getEvent(eventIdA);
            Event b = eventService.getEvent(eventIdB);

            long regA = analyticsService.getRegistrationCount(eventIdA);
            long regB = analyticsService.getRegistrationCount(eventIdB);
            double revA = budgetService.getTotalRevenue(eventIdA);
            double revB = budgetService.getTotalRevenue(eventIdB);
            double expA = budgetService.getTotalExpenses(eventIdA);
            double expB = budgetService.getTotalExpenses(eventIdB);

            sb.append(ReportFormatter.doubleSeparator()).append("\n");
            sb.append("EVENT COMPARISON\n");
            sb.append(ReportFormatter.doubleSeparator()).append("\n");
            sb.append(String.format("%-25s %-20s %-20s%n", "Metric", a.getName(), b.getName()));
            sb.append(ReportFormatter.separator()).append("\n");
            sb.append(String.format("%-25s %-20d %-20d%n", "Registrations", regA, regB));
            sb.append(String.format("%-25s %-20s %-20s%n", "Revenue",
                    ReportFormatter.currency(revA), ReportFormatter.currency(revB)));
            sb.append(String.format("%-25s %-20s %-20s%n", "Expenses",
                    ReportFormatter.currency(expA), ReportFormatter.currency(expB)));
            sb.append(String.format("%-25s %-20s %-20s%n", "Net Balance",
                    ReportFormatter.currency(revA - expA), ReportFormatter.currency(revB - expB)));
        } catch (EventNotFoundException e) {
            sb.append("Could not compare events: ").append(e.getMessage());
        }
        return sb.toString();
    }
}

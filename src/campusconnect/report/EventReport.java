package campusconnect.report;

import campusconnect.exception.EventNotFoundException;
import campusconnect.model.Event;
import campusconnect.service.AnalyticsService;
import campusconnect.service.BudgetService;
import campusconnect.service.EventService;
import campusconnect.service.RegistrationService;
import campusconnect.service.SponsorshipService;
import campusconnect.util.ReportFormatter;

public class EventReport extends Report {
    private final String eventId;
    private final EventService eventService;
    private final RegistrationService registrationService;
    private final SponsorshipService sponsorshipService;
    private final BudgetService budgetService;
    private final AnalyticsService analyticsService;

    public EventReport(String eventId, EventService eventService, RegistrationService registrationService,
                        SponsorshipService sponsorshipService, BudgetService budgetService,
                        AnalyticsService analyticsService) {
        super("Event Report");
        this.eventId = eventId;
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.sponsorshipService = sponsorshipService;
        this.budgetService = budgetService;
        this.analyticsService = analyticsService;
    }

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        try {
            Event event = eventService.getEvent(eventId);
            long registrations = analyticsService.getRegistrationCount(eventId);
            long attendance = analyticsService.getAttendanceCount(eventId);
            double attendancePercent = registrationService.getAttendancePercentage(eventId);
            int sponsorCount = sponsorshipService.getSponsorshipsForEvent(eventId).size();
            double sponsorshipRevenue = sponsorshipService.getTotalConfirmedSponsorshipForEvent(eventId);
            double totalExpenses = budgetService.getTotalExpenses(eventId);
            double netBalance = budgetService.getNetBalance(eventId);
            double utilization = budgetService.getBudgetUtilizationPercent(eventId);

            sb.append(ReportFormatter.doubleSeparator()).append("\n");
            sb.append("EVENT REPORT: ").append(event.getName()).append(" [").append(eventId).append("]\n");
            sb.append(ReportFormatter.doubleSeparator()).append("\n");
            sb.append(event).append("\n");
            sb.append(ReportFormatter.separator()).append("\n");
            sb.append("Registrations: ").append(registrations).append("\n");
            sb.append("Attendance: ").append(attendance).append(" (")
                    .append(ReportFormatter.percent(attendancePercent)).append(")\n");
            sb.append("Sponsors: ").append(sponsorCount).append("\n");
            sb.append("Sponsorship Revenue: ").append(ReportFormatter.currency(sponsorshipRevenue)).append("\n");
            sb.append("Total Expenses: ").append(ReportFormatter.currency(totalExpenses)).append("\n");
            sb.append("Net Balance: ").append(ReportFormatter.currency(netBalance)).append("\n");
            sb.append("Budget Utilization: ").append(ReportFormatter.percent(utilization)).append("\n");
        } catch (EventNotFoundException e) {
            sb.append("Could not generate report: ").append(e.getMessage());
        }
        return sb.toString();
    }
}

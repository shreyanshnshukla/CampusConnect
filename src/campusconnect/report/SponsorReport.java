package campusconnect.report;

import campusconnect.model.Sponsorship;
import campusconnect.service.SponsorshipService;
import campusconnect.util.ReportFormatter;

import java.util.List;

public class SponsorReport extends Report {
    private final String eventId;
    private final SponsorshipService sponsorshipService;

    public SponsorReport(String eventId, SponsorshipService sponsorshipService) {
        super("Sponsor Report");
        this.eventId = eventId;
        this.sponsorshipService = sponsorshipService;
    }

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        List<Sponsorship> sponsorships = sponsorshipService.getSponsorshipsForEvent(eventId);
        sb.append(ReportFormatter.doubleSeparator()).append("\n");
        sb.append("SPONSOR REPORT for event ").append(eventId).append("\n");
        sb.append(ReportFormatter.doubleSeparator()).append("\n");
        if (sponsorships.isEmpty()) {
            sb.append("No sponsorships recorded for this event.\n");
        }
        for (Sponsorship s : sponsorships) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}

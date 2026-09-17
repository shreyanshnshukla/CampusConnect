package campusconnect.service;

import campusconnect.exception.EventNotFoundException;
import campusconnect.model.Event;
import campusconnect.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Aggregation helpers shared across reports, built with Streams over the repositories. */
public class AnalyticsService {
    private final EventRepository eventRepository;
    private final RegistrationService registrationService;
    private final SponsorshipService sponsorshipService;
    private final BudgetService budgetService;

    public AnalyticsService(EventRepository eventRepository, RegistrationService registrationService,
                             SponsorshipService sponsorshipService, BudgetService budgetService) {
        this.eventRepository = eventRepository;
        this.registrationService = registrationService;
        this.sponsorshipService = sponsorshipService;
        this.budgetService = budgetService;
    }

    public long getRegistrationCount(String eventId) {
        return registrationService.getRegistrationsForEvent(eventId).stream()
                .filter(r -> r.getStatus().name().equals("REGISTERED"))
                .count();
    }

    public long getAttendanceCount(String eventId) {
        return registrationService.getRegistrationsForEvent(eventId).stream()
                .filter(r -> r.isAttendance())
                .count();
    }

    public List<Event> getAllEventsSortedByExpectedSize() {
        return eventRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(b.getExpectedParticipants(), a.getExpectedParticipants()))
                .collect(Collectors.toList());
    }

    public double getTotalNetBalanceAcrossEvents() {
        return eventRepository.findAll().stream()
                .mapToDouble(e -> {
                    try {
                        return budgetService.getNetBalance(e.getEventId());
                    } catch (EventNotFoundException ex) {
                        return 0.0;
                    }
                })
                .sum();
    }
}

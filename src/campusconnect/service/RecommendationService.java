package campusconnect.service;

import campusconnect.exception.EventNotFoundException;
import campusconnect.model.Event;
import campusconnect.model.Sponsor;
import campusconnect.repository.EventRepository;
import campusconnect.repository.SponsorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rule-based scoring engine that ranks sponsors for a given event.
 * This is a deterministic weighted-scoring algorithm over five
 * criteria totalling 100 points - it is NOT machine learning.
 */
public class RecommendationService {
    private static final int INDUSTRY_RELEVANCE_WEIGHT = 30;
    private static final int AUDIENCE_COMPATIBILITY_WEIGHT = 25;
    private static final int CAPACITY_WEIGHT = 20;
    private static final int VISIBILITY_WEIGHT = 15;
    private static final int PARTNERSHIP_HISTORY_WEIGHT = 10;

    private final EventRepository eventRepository;
    private final SponsorRepository sponsorRepository;
    private final SponsorshipService sponsorshipService;

    public RecommendationService(EventRepository eventRepository, SponsorRepository sponsorRepository,
                                  SponsorshipService sponsorshipService) {
        this.eventRepository = eventRepository;
        this.sponsorRepository = sponsorRepository;
        this.sponsorshipService = sponsorshipService;
    }

    public static class ScoredSponsor {
        public final Sponsor sponsor;
        public final int score;

        public ScoredSponsor(Sponsor sponsor, int score) {
            this.sponsor = sponsor;
            this.score = score;
        }
    }

    public List<ScoredSponsor> recommendSponsorsForEvent(String eventId) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No event found with ID: " + eventId));

        List<ScoredSponsor> scored = new ArrayList<>();
        for (Sponsor sponsor : sponsorRepository.findAll()) {
            scored.add(new ScoredSponsor(sponsor, scoreSponsor(event, sponsor)));
        }
        return scored.stream()
                .sorted((a, b) -> Integer.compare(b.score, a.score))
                .collect(Collectors.toList());
    }

    private int scoreSponsor(Event event, Sponsor sponsor) {
        return scoreIndustryRelevance(event, sponsor)
                + scoreAudienceCompatibility(event)
                + scoreCapacity(sponsor)
                + scoreVisibility(event)
                + scorePartnershipHistory(event, sponsor);
    }

    /** Higher score when the sponsor's industry aligns with the event category. */
    private int scoreIndustryRelevance(Event event, Sponsor sponsor) {
        String category = event.getCategory().toLowerCase();
        String industry = sponsor.getIndustry().toLowerCase();
        if (industry.contains(category) || category.contains(industry)) {
            return INDUSTRY_RELEVANCE_WEIGHT;
        }
        return INDUSTRY_RELEVANCE_WEIGHT / 2;
    }

    /** Larger expected audiences are more attractive to sponsors. */
    private int scoreAudienceCompatibility(Event event) {
        int expected = event.getExpectedParticipants();
        if (expected >= 500) return AUDIENCE_COMPATIBILITY_WEIGHT;
        if (expected >= 200) return (int) (AUDIENCE_COMPATIBILITY_WEIGHT * 0.75);
        if (expected >= 50) return AUDIENCE_COMPATIBILITY_WEIGHT / 2;
        return AUDIENCE_COMPATIBILITY_WEIGHT / 4;
    }

    /** Sponsors with a larger sponsorship history score higher on capacity. */
    private int scoreCapacity(Sponsor sponsor) {
        double totalHistorical = sponsorshipService.getAllSponsorships().stream()
                .filter(s -> s.getSponsorId().equals(sponsor.getSponsorId()))
                .mapToDouble(s -> s.getAmount())
                .sum();
        if (totalHistorical >= 100000) return CAPACITY_WEIGHT;
        if (totalHistorical >= 50000) return (int) (CAPACITY_WEIGHT * 0.75);
        if (totalHistorical > 0) return CAPACITY_WEIGHT / 2;
        return CAPACITY_WEIGHT / 4;
    }

    /** Larger venues/capacities imply higher visibility for the sponsor's branding. */
    private int scoreVisibility(Event event) {
        if (event.getCapacity() >= 500) return VISIBILITY_WEIGHT;
        if (event.getCapacity() >= 200) return (int) (VISIBILITY_WEIGHT * 0.7);
        return VISIBILITY_WEIGHT / 2;
    }

    /** Sponsors who have previously sponsored this exact event score higher. */
    private int scorePartnershipHistory(Event event, Sponsor sponsor) {
        boolean hasPriorPartnership = sponsorshipService.getSponsorshipsForEvent(event.getEventId()).stream()
                .anyMatch(s -> s.getSponsorId().equals(sponsor.getSponsorId()));
        return hasPriorPartnership ? PARTNERSHIP_HISTORY_WEIGHT : 0;
    }
}

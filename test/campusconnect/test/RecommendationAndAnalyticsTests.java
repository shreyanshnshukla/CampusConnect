package campusconnect.test;

import campusconnect.model.Event;
import campusconnect.model.Participant;
import campusconnect.model.Registration;
import campusconnect.model.Sponsor;
import campusconnect.repository.EventRepository;
import campusconnect.repository.ExpenseRepository;
import campusconnect.repository.ParticipantRepository;
import campusconnect.repository.RegistrationRepository;
import campusconnect.repository.SponsorRepository;
import campusconnect.repository.SponsorshipRepository;
import campusconnect.service.AnalyticsService;
import campusconnect.service.BudgetService;
import campusconnect.service.EventService;
import campusconnect.service.ParticipantService;
import campusconnect.service.RecommendationService;
import campusconnect.service.RegistrationService;
import campusconnect.service.SponsorService;
import campusconnect.service.SponsorshipService;
import campusconnect.util.FileManager;

import java.time.LocalDate;
import java.util.List;

public class RecommendationAndAnalyticsTests {

    public static Results runAll() {
        Results results = new Results();
        System.out.println("\n== Recommendation & Analytics Tests ==");

        testRecommendationAlgorithm(results);
        testAnalyticsCalculations(results);

        return results;
    }

    private static void testRecommendationAlgorithm(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("recommendation-algorithm");
            EventRepository eventRepository = new EventRepository(fm);
            EventService eventService = new EventService(eventRepository);
            SponsorRepository sponsorRepository = new SponsorRepository(fm);
            SponsorService sponsorService = new SponsorService(sponsorRepository);
            SponsorshipService sponsorshipService =
                    new SponsorshipService(new SponsorshipRepository(fm), eventRepository, sponsorRepository);
            RecommendationService recommendationService =
                    new RecommendationService(eventRepository, sponsorRepository, sponsorshipService);

            Event event = eventService.createEvent("Tech Summit", "Coding Club", "Technology",
                    LocalDate.now().plusDays(40), "Main Hall", 600, 0, 550, 300000);

            Sponsor relevantSponsor = sponsorService.addSponsor("ABC Technologies", "Technology", "Contact A",
                    "a@abc.com", "9000000001");
            sponsorService.addSponsor("Green Foods Ltd", "Food & Beverage", "Contact B",
                    "b@green.com", "9000000002");

            List<RecommendationService.ScoredSponsor> ranked =
                    recommendationService.recommendSponsorsForEvent(event.getEventId());
            boolean relevantRankedHigher = ranked.get(0).sponsor.getSponsorId().equals(relevantSponsor.getSponsorId());
            SimpleAssert.assertTrue("industry-relevant sponsor ranks above unrelated sponsor",
                    relevantRankedHigher, results);
            SimpleAssert.assertTrue("scores stay within 0-100 range",
                    ranked.stream().allMatch(s -> s.score >= 0 && s.score <= 100), results);
        } catch (Exception e) {
            results.record("recommendation algorithm", false, "unexpected exception: " + e.getMessage());
        }
    }

    private static void testAnalyticsCalculations(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("analytics-calculations");
            EventRepository eventRepository = new EventRepository(fm);
            EventService eventService = new EventService(eventRepository);
            ParticipantService participantService = new ParticipantService(new ParticipantRepository(fm));
            RegistrationRepository registrationRepository = new RegistrationRepository(fm);
            RegistrationService registrationService = new RegistrationService(registrationRepository, eventRepository);
            SponsorRepository sponsorRepository = new SponsorRepository(fm);
            SponsorshipService sponsorshipService =
                    new SponsorshipService(new SponsorshipRepository(fm), eventRepository, sponsorRepository);
            ExpenseRepository expenseRepository = new ExpenseRepository(fm);
            BudgetService budgetService =
                    new BudgetService(expenseRepository, eventRepository, registrationRepository, sponsorshipService);
            AnalyticsService analyticsService =
                    new AnalyticsService(eventRepository, registrationService, sponsorshipService, budgetService);

            Event event = eventService.createEvent("Workshop Series", "IEEE Club", "Workshop",
                    LocalDate.now().plusDays(12), "Lab 3", 3, 0, 3, 10000);
            Participant p1 = participantService.addParticipant("Ishaan Verma", "ishaan@example.com",
                    "9333333333", "VIT Bhopal");
            Participant p2 = participantService.addParticipant("Zara Khan", "zara@example.com",
                    "9444444444", "VIT Bhopal");

            Registration r1 = registrationService.register(event.getEventId(), p1.getUserId());
            registrationService.register(event.getEventId(), p2.getUserId());
            registrationService.markAttendance(r1.getRegistrationId());

            long registrationCount = analyticsService.getRegistrationCount(event.getEventId());
            long attendanceCount = analyticsService.getAttendanceCount(event.getEventId());

            SimpleAssert.assertEquals("registration count reflects active registrations", 2L, registrationCount, results);
            SimpleAssert.assertEquals("attendance count reflects marked attendance", 1L, attendanceCount, results);

            double attendancePercent = registrationService.getAttendancePercentage(event.getEventId());
            SimpleAssert.assertEquals("attendance percentage computed correctly", 50.0, attendancePercent, results);
        } catch (Exception e) {
            results.record("analytics calculations", false, "unexpected exception: " + e.getMessage());
        }
    }
}

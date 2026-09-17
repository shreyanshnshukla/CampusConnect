package campusconnect.test;

import campusconnect.model.Event;
import campusconnect.model.Participant;
import campusconnect.model.Registration;
import campusconnect.repository.EventRepository;
import campusconnect.repository.ParticipantRepository;
import campusconnect.repository.RegistrationRepository;
import campusconnect.service.EventService;
import campusconnect.service.ParticipantService;
import campusconnect.service.RegistrationService;
import campusconnect.util.FileManager;

import java.time.LocalDate;

public class EventAndParticipantTests {

    public static Results runAll() {
        Results results = new Results();
        System.out.println("\n== Event & Participant Tests ==");

        testEventCreation(results);
        testEventValidation(results);
        testParticipantRegistration(results);
        testDuplicateRegistration(results);
        testCapacityRestriction(results);

        return results;
    }

    private static void testEventCreation(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("event-creation");
            EventService eventService = new EventService(new EventRepository(fm));
            Event event = eventService.createEvent("Tech Fest", "Coding Club", "Technical",
                    LocalDate.of(2026, 12, 1), "Main Auditorium", 100, 50.0, 80, 100000);
            SimpleAssert.assertEquals("event creation sets name", "Tech Fest", event.getName(), results);
            SimpleAssert.assertEquals("event starts as UPCOMING", "UPCOMING", event.getStatus().name(), results);
        } catch (Exception e) {
            results.record("event creation", false, "unexpected exception: " + e.getMessage());
        }
    }

    private static void testEventValidation(Results results) {
        FileManager fm = TestUtil.freshFileManager("event-validation");
        EventService eventService = new EventService(new EventRepository(fm));
        SimpleAssert.assertThrows("event creation rejects empty name", () ->
                eventService.createEvent("", "Club", "Technical", LocalDate.now(), "Venue", 50, 0, 40, 10000),
                results);
        SimpleAssert.assertThrows("event creation rejects negative budget", () ->
                eventService.createEvent("Valid Name", "Club", "Technical", LocalDate.now(), "Venue", 50, 0, 40, -500),
                results);
        SimpleAssert.assertThrows("event creation rejects zero capacity", () ->
                eventService.createEvent("Valid Name", "Club", "Technical", LocalDate.now(), "Venue", 0, 0, 40, 10000),
                results);
    }

    private static void testParticipantRegistration(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("participant-registration");
            EventRepository eventRepository = new EventRepository(fm);
            EventService eventService = new EventService(eventRepository);
            ParticipantService participantService = new ParticipantService(new ParticipantRepository(fm));
            RegistrationService registrationService =
                    new RegistrationService(new RegistrationRepository(fm), eventRepository);

            Event event = eventService.createEvent("Hack Night", "CS Club", "Technical",
                    LocalDate.now().plusDays(10), "Lab 1", 5, 0, 5, 5000);
            Participant participant = participantService.addParticipant("Asha Rao", "asha@example.com",
                    "9999999999", "VIT Bhopal");

            Registration registration = registrationService.register(event.getEventId(), participant.getUserId());
            SimpleAssert.assertEquals("registration links correct event", event.getEventId(),
                    registration.getEventId(), results);
        } catch (Exception e) {
            results.record("participant registration", false, "unexpected exception: " + e.getMessage());
        }
    }

    private static void testDuplicateRegistration(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("duplicate-registration");
            EventRepository eventRepository = new EventRepository(fm);
            EventService eventService = new EventService(eventRepository);
            ParticipantService participantService = new ParticipantService(new ParticipantRepository(fm));
            RegistrationService registrationService =
                    new RegistrationService(new RegistrationRepository(fm), eventRepository);

            Event event = eventService.createEvent("Design Sprint", "UI/UX Club", "Workshop",
                    LocalDate.now().plusDays(5), "Design Lab", 10, 0, 10, 5000);
            Participant participant = participantService.addParticipant("Ravi Kumar", "ravi@example.com",
                    "8888888888", "VIT Bhopal");

            registrationService.register(event.getEventId(), participant.getUserId());
            SimpleAssert.assertThrows("second registration for same event is rejected", () ->
                    registrationService.register(event.getEventId(), participant.getUserId()), results);
        } catch (Exception e) {
            results.record("duplicate registration setup", false, "unexpected exception: " + e.getMessage());
        }
    }

    private static void testCapacityRestriction(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("capacity-restriction");
            EventRepository eventRepository = new EventRepository(fm);
            EventService eventService = new EventService(eventRepository);
            ParticipantService participantService = new ParticipantService(new ParticipantRepository(fm));
            RegistrationService registrationService =
                    new RegistrationService(new RegistrationRepository(fm), eventRepository);

            Event event = eventService.createEvent("Mini Workshop", "Robotics Club", "Workshop",
                    LocalDate.now().plusDays(3), "Lab 2", 1, 0, 1, 2000);
            Participant p1 = participantService.addParticipant("Neha Singh", "neha@example.com",
                    "7777777777", "VIT Bhopal");
            Participant p2 = participantService.addParticipant("Kabir Shah", "kabir@example.com",
                    "6666666666", "VIT Bhopal");

            registrationService.register(event.getEventId(), p1.getUserId());
            SimpleAssert.assertThrows("registration beyond capacity is rejected", () ->
                    registrationService.register(event.getEventId(), p2.getUserId()), results);
        } catch (Exception e) {
            results.record("capacity restriction setup", false, "unexpected exception: " + e.getMessage());
        }
    }
}

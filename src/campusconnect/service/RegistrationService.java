package campusconnect.service;

import campusconnect.exception.CapacityExceededException;
import campusconnect.exception.DuplicateRegistrationException;
import campusconnect.exception.EventNotFoundException;
import campusconnect.exception.RegistrationNotFoundException;
import campusconnect.model.Event;
import campusconnect.model.Registration;
import campusconnect.model.RegistrationStatus;
import campusconnect.repository.EventRepository;
import campusconnect.repository.RegistrationRepository;

import java.time.LocalDateTime;
import java.util.List;

public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private int idCounter;

    public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.idCounter = registrationRepository.findAll().size() + 1;
    }

    public Registration register(String eventId, String participantId)
            throws EventNotFoundException, DuplicateRegistrationException, CapacityExceededException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No event found with ID: " + eventId));

        if (registrationRepository.isDuplicateActiveRegistration(eventId, participantId)) {
            throw new DuplicateRegistrationException(
                    "Participant " + participantId + " is already registered for event " + eventId);
        }

        long currentCount = registrationRepository.countActiveRegistrations(eventId);
        if (currentCount >= event.getCapacity()) {
            throw new CapacityExceededException(
                    "Event " + eventId + " has reached its capacity of " + event.getCapacity());
        }

        String registrationId = "REG" + String.format("%04d", idCounter++);
        Registration registration = new Registration(registrationId, eventId, participantId, LocalDateTime.now());
        registrationRepository.save(registration);
        return registration;
    }

    public void cancelRegistration(String registrationId) throws RegistrationNotFoundException {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException("No registration found with ID: " + registrationId));
        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
    }

    public void markAttendance(String registrationId) throws RegistrationNotFoundException {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException("No registration found with ID: " + registrationId));
        registration.setAttendance(true);
        registrationRepository.save(registration);
    }

    public List<Registration> getRegistrationsForEvent(String eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public double getAttendancePercentage(String eventId) {
        List<Registration> regs = registrationRepository.findByEventId(eventId);
        long active = regs.stream().filter(r -> r.getStatus() == RegistrationStatus.REGISTERED).count();
        if (active == 0) return 0.0;
        long attended = regs.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.REGISTERED && r.isAttendance())
                .count();
        return (attended * 100.0) / active;
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }
}

package campusconnect.service;

import campusconnect.exception.EventNotFoundException;
import campusconnect.exception.InvalidDataException;
import campusconnect.model.Event;
import campusconnect.model.EventStatus;
import campusconnect.repository.EventRepository;
import campusconnect.util.InputValidator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventService {
    private final EventRepository eventRepository;
    private int idCounter;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.idCounter = eventRepository.findAll().size() + 1;
    }

    public Event createEvent(String name, String organizerClub, String category, LocalDate date,
                              String venue, int capacity, double registrationFee,
                              int expectedParticipants, double budget) throws InvalidDataException {
        InputValidator.requireNonEmpty(name, "Event name");
        InputValidator.requireNonEmpty(organizerClub, "Organizer club");
        InputValidator.requireNonEmpty(venue, "Venue");
        if (capacity <= 0) {
            throw new InvalidDataException("Capacity must be positive.");
        }
        InputValidator.requireNonNegative(registrationFee, "Registration fee");
        InputValidator.requireNonNegative(budget, "Budget");

        String eventId = "EVT" + String.format("%03d", idCounter++);
        Event event = new Event(eventId, name, organizerClub, category, date, venue, capacity,
                registrationFee, expectedParticipants, budget);
        eventRepository.save(event);
        return event;
    }

    public Event getEvent(String eventId) throws EventNotFoundException {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No event found with ID: " + eventId));
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> searchEvents(String keyword) {
        String lower = keyword.toLowerCase();
        return eventRepository.findAll().stream()
                .filter(e -> e.getName().toLowerCase().contains(lower)
                        || e.getCategory().toLowerCase().contains(lower)
                        || e.getOrganizerClub().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public void updateEvent(String eventId, String name, String venue, int capacity,
                             double registrationFee, double budget) throws EventNotFoundException, InvalidDataException {
        Event event = getEvent(eventId);
        InputValidator.requireNonEmpty(name, "Event name");
        InputValidator.requireNonEmpty(venue, "Venue");
        if (capacity <= 0) {
            throw new InvalidDataException("Capacity must be positive.");
        }
        InputValidator.requireNonNegative(registrationFee, "Registration fee");
        InputValidator.requireNonNegative(budget, "Budget");

        event.setName(name);
        event.setVenue(venue);
        event.setCapacity(capacity);
        event.setRegistrationFee(registrationFee);
        event.setBudget(budget);
        eventRepository.save(event);
    }

    public void updateStatus(String eventId, EventStatus status) throws EventNotFoundException {
        Event event = getEvent(eventId);
        event.setStatus(status);
        eventRepository.save(event);
    }

    public void cancelEvent(String eventId) throws EventNotFoundException {
        updateStatus(eventId, EventStatus.CANCELLED);
    }

    public void deleteEvent(String eventId) throws EventNotFoundException {
        getEvent(eventId);
        eventRepository.delete(eventId);
    }

    public List<Event> getEventsSortedByDate() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(Event::getDate))
                .collect(Collectors.toList());
    }
}

package campusconnect.model;

import java.time.LocalDate;

public class Event {
    private final String eventId;
    private String name;
    private String organizerClub;
    private String category;
    private LocalDate date;
    private String venue;
    private int capacity;
    private double registrationFee;
    private int expectedParticipants;
    private double budget;
    private EventStatus status;

    public Event(String eventId, String name, String organizerClub, String category, LocalDate date,
                 String venue, int capacity, double registrationFee, int expectedParticipants, double budget) {
        this.eventId = eventId;
        this.name = name;
        this.organizerClub = organizerClub;
        this.category = category;
        this.date = date;
        this.venue = venue;
        this.capacity = capacity;
        this.registrationFee = registrationFee;
        this.expectedParticipants = expectedParticipants;
        this.budget = budget;
        this.status = EventStatus.UPCOMING;
    }

    public String getEventId() { return eventId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOrganizerClub() { return organizerClub; }
    public void setOrganizerClub(String organizerClub) { this.organizerClub = organizerClub; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public double getRegistrationFee() { return registrationFee; }
    public void setRegistrationFee(double registrationFee) { this.registrationFee = registrationFee; }
    public int getExpectedParticipants() { return expectedParticipants; }
    public void setExpectedParticipants(int expectedParticipants) { this.expectedParticipants = expectedParticipants; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | Club: %s | %s | Venue: %s | Capacity: %d | Fee: %.2f | Status: %s",
                eventId, name, category, organizerClub, date, venue, capacity, registrationFee, status);
    }
}

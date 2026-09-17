package campusconnect.model;

import java.time.LocalDateTime;

public class Registration {
    private final String registrationId;
    private final String eventId;
    private final String participantId;
    private final LocalDateTime registrationDate;
    private boolean attendance;
    private RegistrationStatus status;

    public Registration(String registrationId, String eventId, String participantId, LocalDateTime registrationDate) {
        this.registrationId = registrationId;
        this.eventId = eventId;
        this.participantId = participantId;
        this.registrationDate = registrationDate;
        this.attendance = false;
        this.status = RegistrationStatus.REGISTERED;
    }

    public String getRegistrationId() { return registrationId; }
    public String getEventId() { return eventId; }
    public String getParticipantId() { return participantId; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public boolean isAttendance() { return attendance; }
    public void setAttendance(boolean attendance) { this.attendance = attendance; }
    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%s] Event:%s Participant:%s Date:%s Attendance:%s Status:%s",
                registrationId, eventId, participantId, registrationDate, attendance, status);
    }
}

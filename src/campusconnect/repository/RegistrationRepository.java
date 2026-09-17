package campusconnect.repository;

import campusconnect.model.Registration;
import campusconnect.model.RegistrationStatus;
import campusconnect.util.AppConstants;
import campusconnect.util.DateUtil;
import campusconnect.util.FileManager;

import java.util.*;

public class RegistrationRepository {
    private final Map<String, Registration> registrations = new LinkedHashMap<>();
    private final FileManager fileManager;
    private final String filePath;

    public RegistrationRepository(FileManager fileManager) {
        this.fileManager = fileManager;
        this.filePath = fileManager.resolvePath(AppConstants.REGISTRATIONS_FILE);
        load();
    }

    public void save(Registration registration) {
        registrations.put(registration.getRegistrationId(), registration);
        persist();
    }

    public Optional<Registration> findById(String registrationId) {
        return Optional.ofNullable(registrations.get(registrationId));
    }

    public List<Registration> findAll() {
        return new ArrayList<>(registrations.values());
    }

    public List<Registration> findByEventId(String eventId) {
        List<Registration> result = new ArrayList<>();
        for (Registration r : registrations.values()) {
            if (r.getEventId().equals(eventId)) result.add(r);
        }
        return result;
    }

    public boolean isDuplicateActiveRegistration(String eventId, String participantId) {
        for (Registration r : registrations.values()) {
            if (r.getEventId().equals(eventId) && r.getParticipantId().equals(participantId)
                    && r.getStatus() == RegistrationStatus.REGISTERED) {
                return true;
            }
        }
        return false;
    }

    public long countActiveRegistrations(String eventId) {
        long count = 0;
        for (Registration r : registrations.values()) {
            if (r.getEventId().equals(eventId) && r.getStatus() == RegistrationStatus.REGISTERED) {
                count++;
            }
        }
        return count;
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Registration r : registrations.values()) {
            lines.add(String.join("|", r.getRegistrationId(), r.getEventId(), r.getParticipantId(),
                    DateUtil.formatDateTime(r.getRegistrationDate()), String.valueOf(r.isAttendance()),
                    r.getStatus().name()));
        }
        fileManager.writeLines(filePath, lines);
    }

    private void load() {
        for (String line : fileManager.readLines(filePath)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            Registration r = new Registration(p[0], p[1], p[2], DateUtil.parseDateTime(p[3]));
            r.setAttendance(Boolean.parseBoolean(p[4]));
            r.setStatus(RegistrationStatus.valueOf(p[5]));
            registrations.put(r.getRegistrationId(), r);
        }
    }
}

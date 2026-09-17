package campusconnect.repository;

import campusconnect.model.Event;
import campusconnect.model.EventStatus;
import campusconnect.util.AppConstants;
import campusconnect.util.DateUtil;
import campusconnect.util.FileManager;

import java.util.*;

public class EventRepository {
    private final Map<String, Event> events = new LinkedHashMap<>();
    private final FileManager fileManager;
    private final String filePath;

    public EventRepository(FileManager fileManager) {
        this.fileManager = fileManager;
        this.filePath = fileManager.resolvePath(AppConstants.EVENTS_FILE);
        load();
    }

    public void save(Event event) {
        events.put(event.getEventId(), event);
        persist();
    }

    public Optional<Event> findById(String eventId) {
        return Optional.ofNullable(events.get(eventId));
    }

    public List<Event> findAll() {
        return new ArrayList<>(events.values());
    }

    public void delete(String eventId) {
        events.remove(eventId);
        persist();
    }

    public boolean exists(String eventId) {
        return events.containsKey(eventId);
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Event e : events.values()) {
            lines.add(String.join("|",
                    e.getEventId(), e.getName(), e.getOrganizerClub(), e.getCategory(),
                    DateUtil.formatDate(e.getDate()), e.getVenue(), String.valueOf(e.getCapacity()),
                    String.valueOf(e.getRegistrationFee()), String.valueOf(e.getExpectedParticipants()),
                    String.valueOf(e.getBudget()), e.getStatus().name()));
        }
        fileManager.writeLines(filePath, lines);
    }

    private void load() {
        for (String line : fileManager.readLines(filePath)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            Event e = new Event(p[0], p[1], p[2], p[3], DateUtil.parseDate(p[4]), p[5],
                    Integer.parseInt(p[6]), Double.parseDouble(p[7]), Integer.parseInt(p[8]),
                    Double.parseDouble(p[9]));
            e.setStatus(EventStatus.valueOf(p[10]));
            events.put(e.getEventId(), e);
        }
    }
}

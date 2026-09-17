package campusconnect.repository;

import campusconnect.model.Participant;
import campusconnect.util.AppConstants;
import campusconnect.util.FileManager;

import java.util.*;

public class ParticipantRepository {
    private final Map<String, Participant> participants = new LinkedHashMap<>();
    private final FileManager fileManager;
    private final String filePath;

    public ParticipantRepository(FileManager fileManager) {
        this.fileManager = fileManager;
        this.filePath = fileManager.resolvePath(AppConstants.PARTICIPANTS_FILE);
        load();
    }

    public void save(Participant participant) {
        participants.put(participant.getUserId(), participant);
        persist();
    }

    public Optional<Participant> findById(String participantId) {
        return Optional.ofNullable(participants.get(participantId));
    }

    public List<Participant> findAll() {
        return new ArrayList<>(participants.values());
    }

    public boolean exists(String participantId) {
        return participants.containsKey(participantId);
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Participant p : participants.values()) {
            lines.add(String.join("|", p.getUserId(), p.getName(), p.getEmail(), p.getPhone(), p.getCollege()));
        }
        fileManager.writeLines(filePath, lines);
    }

    private void load() {
        for (String line : fileManager.readLines(filePath)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            Participant participant = new Participant(p[0], p[1], p[2], p[3], p[4]);
            participants.put(participant.getUserId(), participant);
        }
    }
}

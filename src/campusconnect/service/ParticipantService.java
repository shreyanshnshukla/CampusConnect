package campusconnect.service;

import campusconnect.exception.InvalidDataException;
import campusconnect.exception.ParticipantNotFoundException;
import campusconnect.model.Participant;
import campusconnect.repository.ParticipantRepository;
import campusconnect.util.InputValidator;

import java.util.List;
import java.util.stream.Collectors;

public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private int idCounter;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
        this.idCounter = participantRepository.findAll().size() + 1;
    }

    public Participant addParticipant(String name, String email, String phone, String college)
            throws InvalidDataException {
        InputValidator.requireNonEmpty(name, "Name");
        InputValidator.requireValidEmail(email);
        InputValidator.requireNonEmpty(phone, "Phone");
        InputValidator.requireNonEmpty(college, "College");

        String participantId = "PAR" + String.format("%03d", idCounter++);
        Participant participant = new Participant(participantId, name, email, phone, college);
        participantRepository.save(participant);
        return participant;
    }

    public Participant getParticipant(String participantId) throws ParticipantNotFoundException {
        return participantRepository.findById(participantId)
                .orElseThrow(() -> new ParticipantNotFoundException("No participant found with ID: " + participantId));
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public List<Participant> searchParticipants(String keyword) {
        String lower = keyword.toLowerCase();
        return participantRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lower)
                        || p.getEmail().toLowerCase().contains(lower)
                        || p.getCollege().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}

package campusconnect.repository;

import campusconnect.model.PaymentStatus;
import campusconnect.model.Sponsorship;
import campusconnect.model.SponsorshipPackageType;
import campusconnect.model.SponsorshipStatus;
import campusconnect.util.AppConstants;
import campusconnect.util.FileManager;

import java.util.*;

public class SponsorshipRepository {
    private final Map<String, Sponsorship> sponsorships = new LinkedHashMap<>();
    private final FileManager fileManager;
    private final String filePath;

    public SponsorshipRepository(FileManager fileManager) {
        this.fileManager = fileManager;
        this.filePath = fileManager.resolvePath(AppConstants.SPONSORSHIPS_FILE);
        load();
    }

    public void save(Sponsorship sponsorship) {
        sponsorships.put(sponsorship.getSponsorshipId(), sponsorship);
        persist();
    }

    public Optional<Sponsorship> findById(String sponsorshipId) {
        return Optional.ofNullable(sponsorships.get(sponsorshipId));
    }

    public List<Sponsorship> findAll() {
        return new ArrayList<>(sponsorships.values());
    }

    public List<Sponsorship> findByEventId(String eventId) {
        List<Sponsorship> result = new ArrayList<>();
        for (Sponsorship s : sponsorships.values()) {
            if (s.getEventId().equals(eventId)) result.add(s);
        }
        return result;
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Sponsorship s : sponsorships.values()) {
            lines.add(String.join("|", s.getSponsorshipId(), s.getEventId(), s.getSponsorId(),
                    s.getPackageType().name(), String.valueOf(s.getAmount()), s.getStatus().name(),
                    s.getPaymentStatus().name()));
        }
        fileManager.writeLines(filePath, lines);
    }

    private void load() {
        for (String line : fileManager.readLines(filePath)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            Sponsorship s = new Sponsorship(p[0], p[1], p[2], SponsorshipPackageType.valueOf(p[3]),
                    Double.parseDouble(p[4]));
            s.setStatus(SponsorshipStatus.valueOf(p[5]));
            s.setPaymentStatus(PaymentStatus.valueOf(p[6]));
            sponsorships.put(s.getSponsorshipId(), s);
        }
    }
}

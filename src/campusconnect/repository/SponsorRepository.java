package campusconnect.repository;

import campusconnect.model.Sponsor;
import campusconnect.util.AppConstants;
import campusconnect.util.FileManager;

import java.util.*;

public class SponsorRepository {
    private final Map<String, Sponsor> sponsors = new LinkedHashMap<>();
    private final FileManager fileManager;
    private final String filePath;

    public SponsorRepository(FileManager fileManager) {
        this.fileManager = fileManager;
        this.filePath = fileManager.resolvePath(AppConstants.SPONSORS_FILE);
        load();
    }

    public void save(Sponsor sponsor) {
        sponsors.put(sponsor.getSponsorId(), sponsor);
        persist();
    }

    public Optional<Sponsor> findById(String sponsorId) {
        return Optional.ofNullable(sponsors.get(sponsorId));
    }

    public List<Sponsor> findAll() {
        return new ArrayList<>(sponsors.values());
    }

    public boolean exists(String sponsorId) {
        return sponsors.containsKey(sponsorId);
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Sponsor s : sponsors.values()) {
            lines.add(String.join("|", s.getSponsorId(), s.getCompanyName(), s.getIndustry(),
                    s.getContactPerson(), s.getEmail(), s.getPhone()));
        }
        fileManager.writeLines(filePath, lines);
    }

    private void load() {
        for (String line : fileManager.readLines(filePath)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            Sponsor sponsor = new Sponsor(p[0], p[1], p[2], p[3], p[4], p[5]);
            sponsors.put(sponsor.getSponsorId(), sponsor);
        }
    }
}

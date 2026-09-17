package campusconnect.service;

import campusconnect.exception.InvalidDataException;
import campusconnect.exception.SponsorNotFoundException;
import campusconnect.model.Sponsor;
import campusconnect.repository.SponsorRepository;
import campusconnect.util.InputValidator;

import java.util.List;
import java.util.stream.Collectors;

public class SponsorService {
    private final SponsorRepository sponsorRepository;
    private int idCounter;

    public SponsorService(SponsorRepository sponsorRepository) {
        this.sponsorRepository = sponsorRepository;
        this.idCounter = sponsorRepository.findAll().size() + 1;
    }

    public Sponsor addSponsor(String companyName, String industry, String contactPerson,
                               String email, String phone) throws InvalidDataException {
        InputValidator.requireNonEmpty(companyName, "Company name");
        InputValidator.requireNonEmpty(industry, "Industry");
        InputValidator.requireNonEmpty(contactPerson, "Contact person");
        InputValidator.requireValidEmail(email);
        InputValidator.requireNonEmpty(phone, "Phone");

        String sponsorId = "SPN" + String.format("%03d", idCounter++);
        Sponsor sponsor = new Sponsor(sponsorId, companyName, industry, contactPerson, email, phone);
        sponsorRepository.save(sponsor);
        return sponsor;
    }

    public Sponsor getSponsor(String sponsorId) throws SponsorNotFoundException {
        return sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new SponsorNotFoundException("No sponsor found with ID: " + sponsorId));
    }

    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAll();
    }

    public List<Sponsor> searchSponsors(String keyword) {
        String lower = keyword.toLowerCase();
        return sponsorRepository.findAll().stream()
                .filter(s -> s.getCompanyName().toLowerCase().contains(lower)
                        || s.getIndustry().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public void updateSponsor(String sponsorId, String contactPerson, String email, String phone)
            throws SponsorNotFoundException, InvalidDataException {
        Sponsor sponsor = getSponsor(sponsorId);
        InputValidator.requireNonEmpty(contactPerson, "Contact person");
        InputValidator.requireValidEmail(email);
        InputValidator.requireNonEmpty(phone, "Phone");
        sponsor.setContactPerson(contactPerson);
        sponsor.setEmail(email);
        sponsor.setPhone(phone);
        sponsorRepository.save(sponsor);
    }
}

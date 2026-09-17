package campusconnect.service;

import campusconnect.exception.EventNotFoundException;
import campusconnect.exception.InvalidDataException;
import campusconnect.exception.SponsorNotFoundException;
import campusconnect.exception.SponsorshipNotFoundException;
import campusconnect.model.PaymentStatus;
import campusconnect.model.Sponsorship;
import campusconnect.model.SponsorshipPackageType;
import campusconnect.model.SponsorshipStatus;
import campusconnect.repository.EventRepository;
import campusconnect.repository.SponsorRepository;
import campusconnect.repository.SponsorshipRepository;
import campusconnect.util.AppConstants;
import campusconnect.util.InputValidator;

import java.util.List;

public class SponsorshipService {
    private final SponsorshipRepository sponsorshipRepository;
    private final EventRepository eventRepository;
    private final SponsorRepository sponsorRepository;
    private int idCounter;

    public SponsorshipService(SponsorshipRepository sponsorshipRepository, EventRepository eventRepository,
                               SponsorRepository sponsorRepository) {
        this.sponsorshipRepository = sponsorshipRepository;
        this.eventRepository = eventRepository;
        this.sponsorRepository = sponsorRepository;
        this.idCounter = sponsorshipRepository.findAll().size() + 1;
    }

    /** Rule-based lookup - NOT machine learning - mapping an amount to a package tier. */
    public SponsorshipPackageType recommendPackage(double amount) throws InvalidDataException {
        InputValidator.requirePositive(amount, "Sponsorship amount");
        SponsorshipPackageType type = AppConstants.recommendPackage(amount);
        if (type == null) {
            throw new InvalidDataException(
                    "Amount is below the minimum sponsorship threshold of " + AppConstants.SILVER_MIN);
        }
        return type;
    }

    public Sponsorship createSponsorship(String eventId, String sponsorId, double amount)
            throws EventNotFoundException, SponsorNotFoundException, InvalidDataException {
        if (!eventRepository.exists(eventId)) {
            throw new EventNotFoundException("No event found with ID: " + eventId);
        }
        if (!sponsorRepository.exists(sponsorId)) {
            throw new SponsorNotFoundException("No sponsor found with ID: " + sponsorId);
        }
        SponsorshipPackageType packageType = recommendPackage(amount);

        String sponsorshipId = "SHP" + String.format("%03d", idCounter++);
        Sponsorship sponsorship = new Sponsorship(sponsorshipId, eventId, sponsorId, packageType, amount);
        sponsorshipRepository.save(sponsorship);
        return sponsorship;
    }

    public void updateStatus(String sponsorshipId, SponsorshipStatus status) throws SponsorshipNotFoundException {
        Sponsorship s = getSponsorship(sponsorshipId);
        s.setStatus(status);
        sponsorshipRepository.save(s);
    }

    public void updatePaymentStatus(String sponsorshipId, PaymentStatus paymentStatus) throws SponsorshipNotFoundException {
        Sponsorship s = getSponsorship(sponsorshipId);
        s.setPaymentStatus(paymentStatus);
        sponsorshipRepository.save(s);
    }

    public Sponsorship getSponsorship(String sponsorshipId) throws SponsorshipNotFoundException {
        return sponsorshipRepository.findById(sponsorshipId)
                .orElseThrow(() -> new SponsorshipNotFoundException("No sponsorship found with ID: " + sponsorshipId));
    }

    public List<Sponsorship> getSponsorshipsForEvent(String eventId) {
        return sponsorshipRepository.findByEventId(eventId);
    }

    public List<Sponsorship> getAllSponsorships() {
        return sponsorshipRepository.findAll();
    }

    public double getTotalConfirmedSponsorshipForEvent(String eventId) {
        return sponsorshipRepository.findByEventId(eventId).stream()
                .filter(s -> s.getStatus() == SponsorshipStatus.CONFIRMED || s.getStatus() == SponsorshipStatus.COMPLETED)
                .mapToDouble(Sponsorship::getAmount)
                .sum();
    }
}

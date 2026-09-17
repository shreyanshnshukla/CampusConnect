package campusconnect.model;

public class Sponsorship {
    private final String sponsorshipId;
    private final String eventId;
    private final String sponsorId;
    private SponsorshipPackageType packageType;
    private double amount;
    private SponsorshipStatus status;
    private PaymentStatus paymentStatus;

    public Sponsorship(String sponsorshipId, String eventId, String sponsorId,
                        SponsorshipPackageType packageType, double amount) {
        this.sponsorshipId = sponsorshipId;
        this.eventId = eventId;
        this.sponsorId = sponsorId;
        this.packageType = packageType;
        this.amount = amount;
        this.status = SponsorshipStatus.PROSPECT;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public String getSponsorshipId() { return sponsorshipId; }
    public String getEventId() { return eventId; }
    public String getSponsorId() { return sponsorId; }
    public SponsorshipPackageType getPackageType() { return packageType; }
    public void setPackageType(SponsorshipPackageType packageType) { this.packageType = packageType; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public SponsorshipStatus getStatus() { return status; }
    public void setStatus(SponsorshipStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    @Override
    public String toString() {
        return String.format("[%s] Event:%s Sponsor:%s Package:%s Amount:%.2f Status:%s Payment:%s",
                sponsorshipId, eventId, sponsorId, packageType, amount, status, paymentStatus);
    }
}

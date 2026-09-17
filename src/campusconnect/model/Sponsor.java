package campusconnect.model;

public class Sponsor {
    private final String sponsorId;
    private String companyName;
    private String industry;
    private String contactPerson;
    private String email;
    private String phone;

    public Sponsor(String sponsorId, String companyName, String industry, String contactPerson,
                    String email, String phone) {
        this.sponsorId = sponsorId;
        this.companyName = companyName;
        this.industry = industry;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
    }

    public String getSponsorId() { return sponsorId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Industry: %s | Contact: %s (%s, %s)",
                sponsorId, companyName, industry, contactPerson, email, phone);
    }
}

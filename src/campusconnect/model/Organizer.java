package campusconnect.model;

public class Organizer extends User {
    private String clubName;

    public Organizer(String userId, String name, String email, String phone, String clubName) {
        super(userId, name, email, phone);
        this.clubName = clubName;
    }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    @Override
    public String describeRole() { return "Organizer"; }
}

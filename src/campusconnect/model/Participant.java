package campusconnect.model;

public class Participant extends User {
    private String college;

    public Participant(String userId, String name, String email, String phone, String college) {
        super(userId, name, email, phone);
        this.college = college;
    }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    @Override
    public String describeRole() { return "Participant"; }
}

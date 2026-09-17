package campusconnect.model;

/**
 * Common identity data shared by everyone who uses the system.
 * Organizer and Participant differ in what role-specific data they carry
 * and how they describe themselves - a legitimate use of inheritance
 * since both are fundamentally "a person with contact details" first.
 */
public abstract class User {
    private final String userId;
    private String name;
    private String email;
    private String phone;

    public User(String userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    /** Each concrete subtype names its own role - overridden, not switched on a type field. */
    public abstract String describeRole();

    @Override
    public String toString() {
        return String.format("%s [%s] - %s (%s, %s)", describeRole(), userId, name, email, phone);
    }
}

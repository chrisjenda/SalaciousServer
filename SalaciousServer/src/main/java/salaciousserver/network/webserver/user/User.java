package salaciousserver.network.webserver.user;
public class User {
    public final String username;
    public final String hashedPassword;
    public final String accessLevel;

    public User(String username, String hashedPassword, String accessLevel) {
        this.username = username;
        this.accessLevel = accessLevel;
        this.hashedPassword = hashedPassword;
    }

    public String getUserName() {
        return username;
    }
}

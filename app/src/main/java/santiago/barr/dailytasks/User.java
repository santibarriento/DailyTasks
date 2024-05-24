package santiago.barr.dailytasks;

public class User {
    public String userName;
    public String email;
    public String photoUrl;
    public String phone;
    public boolean notifications;
    public boolean darkTheme;

    // constructor vacío necesario para llamadas a DataSnapshot.getValue(User.class)
    public User() {
    }

    // constructor
    public User(String userName, String email, String photoUrl, String phone, boolean notifications, boolean darkTheme) {
        this.userName = userName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phone = phone;
        this.notifications = notifications;
        this.darkTheme = darkTheme;
    }
}

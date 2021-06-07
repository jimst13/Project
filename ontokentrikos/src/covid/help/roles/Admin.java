package covid.help.roles;

public class Admin extends User{
    public static final boolean isAdmin = true;

    public Admin(String name, String phoneNumber) {
        super(name, phoneNumber);
    }
}

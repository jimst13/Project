package covid.help.items;

public class Service extends Entity{
    public Service(String name, String description, int id) {
        super(name, description, id);
    }

    @Override
    public String getDetails() {
        String parentDetails = super.getDetails();
        return String.format("%s - Service", parentDetails);
    }
}

package covid.help.items;

public class Entity {
    protected String name, description;
    protected int id;

    protected Entity(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEntityInfo() {
        return String.format("%s (%d)", this.name, this.id);
    }

    public String getDetails() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.getEntityInfo(), this.getDetails());
    }
}

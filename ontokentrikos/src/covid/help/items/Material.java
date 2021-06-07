package covid.help.items;

public class Material extends Entity{
    private final long level1, level2, level3;

    public Material(String name, String description, int id, long level1, long level2, long level3) {
        super(name, description, id);
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
    }

    public long getLevel1() {
        return level1;
    }

    public long getLevel2() {
        return level2;
    }

    public long getLevel3() {
        return level3;
    }

    @Override
    public String getDetails() {
        String parentDetails = super.getDetails();
        return String.format("%s -- Material with level 1: %d, level 2: %d, level 3: %d", parentDetails, this.level1, this.level2, this.level3);
    }
}

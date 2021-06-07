package covid.help.donations;

import covid.help.items.Entity;

public class RequestDonation {
    private Entity entity;
    private long quantity;

    public RequestDonation(Entity entity, long quantity) {
        this.entity = entity;
        this.quantity = quantity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void addQuantity(double amount) {
        this.quantity += amount;
    }

    public long getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RequestDonation)) {
            return false;
        }

        RequestDonation that = ((RequestDonation) obj);
        return this.entity.getId() == that.entity.getId();
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}

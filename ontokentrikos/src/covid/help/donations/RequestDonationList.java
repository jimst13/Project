package covid.help.donations;

import covid.help.exceptions.InvalidEntityException;
import covid.help.exceptions.InvalidEntityQuantityException;
import covid.help.items.Material;
import covid.help.items.Service;
import covid.help.roles.Organization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class RequestDonationList{

    protected List<RequestDonation> rdEntities;

    public RequestDonationList() {
        this.rdEntities = new ArrayList<>();
    }

    public List<RequestDonation> getRdEntities() {
        return rdEntities;
    }

    public RequestDonation get(int entityId) {
        return this.rdEntities.stream().filter(x -> x.getEntity().getId() == entityId).findAny().orElse(null);
    }

    public long numOfMaterials(){
        LongAdder a = new LongAdder();

        rdEntities.stream()
                .filter(x -> x.getEntity() instanceof Material)
                .map(RequestDonation::getQuantity)
                .forEach(a::add);

        return a.longValue();
    }

    public long numOfServices() {
        LongAdder a = new LongAdder();

        rdEntities.stream()
                .filter(x -> x.getEntity() instanceof Service)
                .map(RequestDonation::getQuantity)
                .forEach(a::add);

        return a.longValue();
    }

    public void add(RequestDonation requestDonation, Organization org) throws InvalidEntityException, InvalidEntityQuantityException {
        RequestDonation alreadyExistingDonation = this.get(requestDonation.getEntity().getId());
        if (alreadyExistingDonation != null) {
            alreadyExistingDonation.addQuantity(requestDonation.getQuantity());
            return;
        }

        if (!org.getEntityList().contains(requestDonation.getEntity())){
            throw new InvalidEntityException(String.format("Entity %s is not part of organization %s", requestDonation.getEntity(), org.getName()));
        }

        RequestDonation toAdd = new RequestDonation(requestDonation.getEntity(), requestDonation.getQuantity());
        this.rdEntities.add(toAdd);
    }

    public void remove(RequestDonation requestDonation) {
        this.rdEntities.remove(requestDonation);
    }

    public void modify(RequestDonation requestDonation, long newQuantity, Organization organization) {
        requestDonation.setQuantity(newQuantity);
    }

    public List<RequestDonation> monitor() {
        System.out.printf("Request Donation List Size: %d\n", this.rdEntities.size());
        for (int i = 0; i < this.rdEntities.size(); i++){
            RequestDonation rd = this.rdEntities.get(i);
            System.out.printf("%d. %s - %d\n", i, rd.getEntity().getName(), rd.getQuantity());

        }

        return this.rdEntities;
    }

    public void reset() {
        this.rdEntities.clear();
    }

    public List<Material> listMaterials() {
        return this.rdEntities.stream().filter(x -> x.getEntity() instanceof Material).map(x -> (Material) x.getEntity()).collect(Collectors.toList());
    }
}

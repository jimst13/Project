package covid.help.donations;

import covid.help.exceptions.InvalidEntityException;
import covid.help.exceptions.InvalidEntityQuantityException;
import covid.help.items.Entity;
import covid.help.items.Material;
import covid.help.items.Service;
import covid.help.roles.Beneficiary;
import covid.help.roles.Organization;

import java.util.ArrayList;
import java.util.List;

public class Requests extends RequestDonationList {
    private final Beneficiary beneficiary;
    public Requests(Beneficiary b) {
        super();
        this.beneficiary = b;
    }

    private static long getEntitySupplyInOrganization(Entity e, Organization org){
        RequestDonation entityInDonations = org.getCurrentDonations().getRdEntities().stream()
                .filter(x -> x.getEntity().getId() == e.getId()).findFirst().orElse(null);

        if (entityInDonations == null) {
            return 0;
        }

        return entityInDonations.getQuantity();
    }

    @Override
    public void add(RequestDonation requestDonation, Organization org) throws InvalidEntityException, InvalidEntityQuantityException {
       boolean isEntitySupplyEnough = getEntitySupplyInOrganization(requestDonation.getEntity(), org) >= requestDonation.getQuantity();

       if (!isEntitySupplyEnough){
           throw new InvalidEntityQuantityException(String.format("Donation quantity for entity %s are not enough for >= %d items", requestDonation.getEntity().getName(), requestDonation.getQuantity()));
       }

       if (!validRequestDonation(requestDonation)){
           throw new InvalidEntityQuantityException(String.format("Beneficiary can't ask for %d quantity of %s (try asking for less)", requestDonation.getQuantity(), requestDonation.getEntity().getName()));
       }

        super.add(requestDonation, org);
    }

    private boolean validRequestDonation(RequestDonation requestDonation) {
        if (requestDonation.getEntity() instanceof Service){
            return true;
        }

        Material requestDonationMaterial = (Material) requestDonation.getEntity();

        // find out how much he has received
        long alreadyRequestedQuantity = 0;
        RequestDonation receivedDonation = beneficiary.getReceivedList().getRdEntities().stream()
                .filter(x -> x.getEntity().getId() == requestDonation.getEntity().getId())
                .findFirst().orElse(null);

        if (receivedDonation != null) {
            alreadyRequestedQuantity =  receivedDonation.getQuantity();
        }

        // calculate how much he needs in total
        long accumulatedRequestedQuantity = alreadyRequestedQuantity + requestDonation.getQuantity();
        long allowedQuantityForLevel = requestDonationMaterial.getLevel1();

        if(beneficiary.getNoPersons() >= 2) {
            allowedQuantityForLevel = requestDonationMaterial.getLevel2();
        }

        if(beneficiary.getNoPersons() >= 5) {
            allowedQuantityForLevel = requestDonationMaterial.getLevel3();
        }

        return accumulatedRequestedQuantity <= allowedQuantityForLevel;
    }

    @Override
    public void modify(RequestDonation requestDonation, long newQuantity, Organization organization) {
        super.modify(requestDonation, newQuantity, organization);
    }



    public void commit(Organization org){
        List<RequestDonation> processedRequestDonations = new ArrayList<>();
        for (RequestDonation rd: this.rdEntities){
            if (validRequestDonation(rd)) {
                try {
                    processedRequestDonations.add(rd);
                    this.beneficiary.receiveDonation(rd, org);

                    RequestDonation pledgedRd = org.getCurrentDonations().get(rd.getEntity().getId());
                    pledgedRd.addQuantity(-rd.getQuantity());
                    System.out.println(String.format("Received %d of %s", rd.getQuantity(), rd.getEntity().getName()));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println("... Beneficiary will not receive said donation");
                }
            }
        }

        for (RequestDonation rd : processedRequestDonations) {
            this.rdEntities.remove(rd);
        }
    }

}

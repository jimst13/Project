package covid.help.donations;

import covid.help.exceptions.InvalidEntityException;
import covid.help.exceptions.InvalidEntityQuantityException;
import covid.help.roles.Organization;

// a donator makes offers
public class Offers extends RequestDonationList {

    public void commit(Organization org){
        for (RequestDonation rd: this.rdEntities){
            try {
                org.getCurrentDonations().add(rd, org);
                System.out.println(String.format("Pledged %d of %s", rd.getQuantity(), rd.getEntity().getName()));
            } catch (InvalidEntityException | InvalidEntityQuantityException e) {
                System.out.println(e.getMessage());
                System.out.println("... Entity will not be added to the request donation list");
            }
        }

        this.reset();
    }



}

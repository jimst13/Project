package covid.help.roles;

import covid.help.donations.Offers;
import covid.help.donations.RequestDonation;
import covid.help.donations.RequestDonationList;
import covid.help.exceptions.InvalidEntityException;
import covid.help.exceptions.InvalidEntityQuantityException;

import java.util.List;

public class Donator extends User{
    private Offers offersList;

    public Donator(String name, String phoneNumber) {
        super(name, phoneNumber);
        this.offersList = new Offers();
    }

    public void add(RequestDonation rd, Organization org) throws InvalidEntityException, InvalidEntityQuantityException {
        this.offersList.add(rd, org);
    }

    public void remove(RequestDonation rd) {
        this.offersList.remove(rd);
    }

    public Offers getOffersList() {
        return offersList;
    }

    @Override
    public String toString() {
        return "Donator " + this.name + "(" + this.phoneNumber + ")";
    }
}

package covid.help.roles;

import covid.help.donations.Request;
import covid.help.donations.RequestDonation;
import covid.help.donations.RequestDonationList;
import covid.help.donations.Requests;
import covid.help.exceptions.InvalidEntityException;
import covid.help.exceptions.InvalidEntityQuantityException;

public class Beneficiary extends User {
    private int noPersons = 1;
    RequestDonationList receivedList;
    Requests requestList;

    public Beneficiary(String name, String phoneNumber) {
        super(name, phoneNumber);
        this.receivedList = new RequestDonationList();
        this.requestList = new Requests(this);
    }

    public Beneficiary(String name, String phoneNumber, int noPersons) {
        this(name, phoneNumber);
        this.noPersons = noPersons;
    }

    public RequestDonationList getReceivedList() {
        return receivedList;
    }

    public void receiveDonation(RequestDonation requestDonation, Organization organization){
        try {
            this.receivedList.add(requestDonation, organization);
        } catch (InvalidEntityException | InvalidEntityQuantityException e) {
            e.printStackTrace();
        }
    }

    public void clearReceivedList(){
        this.receivedList.reset();
    }

    public void requestItems(Request request, Organization org) throws InvalidEntityException, InvalidEntityQuantityException {
        this.requestList.add(request, org);
    }

    public int getNoPersons() {
        return noPersons;
    }

    public Requests getRequestList() {
        return requestList;
    }

    @Override
    public String toString() {
        String receivedText = "";
        String requestedText = "";

        return "Beneficiary " + this.name + "(" + this.phoneNumber + ")\n" +
                "Received:\n" + receivedText + "\n" +
                "Requested:\n" + requestedText;
    }

    public void removeDonation(RequestDonation donation) {
        this.requestList.remove(donation);
    }
}

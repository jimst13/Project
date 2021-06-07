package covid.help;

import covid.help.donations.Request;
import covid.help.donations.RequestDonation;
import covid.help.exceptions.AlreadyExistingRoleException;
import covid.help.exceptions.DuplicateEntityIdException;
import covid.help.exceptions.InvalidEntityException;
import covid.help.exceptions.InvalidEntityQuantityException;
import covid.help.items.Material;
import covid.help.items.Service;
import covid.help.roles.Admin;
import covid.help.roles.Beneficiary;
import covid.help.roles.Donator;
import covid.help.roles.Organization;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Organization org = new Organization("Ceid Covid Helpers");

        Material rice = new Material("Rice", "Uncle Bens", 0, 1, 3, 5);
        Material sugar = new Material("Sugar", "Generic Sugar Cane", 1, 1, 2, 3);
        Material milk = new Material("Milk", "Moooooo", 2, 2, 4, 5);

        Service medical = new Service("Medical Support", "Drugs and stuff", 3);
        Service nursery = new Service("Nursery Support", "Babysitting for ill people", 3);
        Service babysit = new Service("Babysitting Support", "For babies", 3);

        Admin admin = new Admin("admin", "111");

        Donator donator = new Donator("donator", "222");

        Beneficiary beneficiary1 = new Beneficiary("beneficiary1", "888", 1);
        Beneficiary beneficiary2 = new Beneficiary("beneficiary2", "999", 2);

        System.out.println("Adding entities to organization");
        System.out.println();

        try {
            org.addEntity(rice);
            org.addEntity(sugar);
            org.addEntity(milk);
            org.addEntity(medical);
            org.addEntity(nursery);
            org.addEntity(babysit);
        } catch (DuplicateEntityIdException e) {
            e.printStackTrace();
        }

        org.setAdmin(admin);

        try {
            org.insertDonator(donator);
        } catch (AlreadyExistingRoleException e) {
            e.printStackTrace();
        }

        try {
            org.insertBeneficiary(beneficiary1);
            org.insertBeneficiary(beneficiary2);
        } catch (AlreadyExistingRoleException e) {
            e.printStackTrace();
        }

        System.out.println("Main: donating items");

        try {
            donator.add(new RequestDonation(rice, 3), org);
            donator.add(new RequestDonation(milk, 99), org);
            donator.add(new RequestDonation(babysit, 5), org);
            donator.getOffersList().commit(org);
        } catch (InvalidEntityException | InvalidEntityQuantityException e) {
            e.printStackTrace();
        }
        System.out.println();

        System.out.println("Main: benecifiary1 requesting items");
        try {
            beneficiary1.requestItems(new Request(milk, 2), org);
            beneficiary1.requestItems(new Request(medical, 3), org);
            beneficiary1.getRequestList().commit(org);
        } catch (InvalidEntityException | InvalidEntityQuantityException e) {
            e.printStackTrace();
        }
        System.out.println();

        System.out.println("Starting Menu");
        System.out.println();

        new Menu(org).run();
    }
}

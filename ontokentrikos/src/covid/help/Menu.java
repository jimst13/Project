package covid.help;

import covid.help.donations.Request;
import covid.help.donations.RequestDonation;
import covid.help.exceptions.AlreadyExistingRoleException;
import covid.help.exceptions.InvalidEntityException;
import covid.help.exceptions.InvalidEntityQuantityException;
import covid.help.exceptions.InvalidSelectedIndexException;
import covid.help.items.Entity;
import covid.help.items.Material;
import covid.help.roles.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final Scanner sc;
    private final Organization org;

    public Menu(Organization org) {
        this.org = org;
        this.sc = new Scanner(System.in);
    }

    private void p(String s){
        System.out.println(s);
    }

    // rs = read string
    private String rs(String prompt){
        System.out.print(prompt);
        return sc.nextLine();
    }

    // ri = read int
    private int ri(String prompt){
        System.out.print(prompt);
        int option = 0;
        try {
            option = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            // e.printStackTrace();
            return -1;
        }

        return option;
    }

    public void run(){
        while(true) { // until user does not want to log in or register
            p("");
            String phone = rs("Login with phone > "); // login

            User u = org.getUserWithPhone(phone);
            if (u == null) {
                p("No user found with that phone. Do you want to register?");
                String ans = rs("y/n > ");
                if (!ans.equals("y")) {
                    p("bye");
                    continue;
                }
                // register
                String name = rs("Name > ");
                String role = rs("beneficiary or donator ? > ");

                if (role.equals("beneficiary")) {
                    int numPersons = ri("family member count (1, 2, 3...) > ");
                    if (numPersons < 1) {
                        p("Invalid family member count");
                        continue;
                    }
                    Beneficiary b = new Beneficiary(name, phone, numPersons);
                    try {
                        org.insertBeneficiary(b);
                    } catch (AlreadyExistingRoleException e) {
                        e.printStackTrace();
                        u = b = null;
                    }

                    u = b; // set to logged in user
                    p("Registered as a beneficiary");
                } else if(role.equals("donator")) {
                    Donator d = new Donator(name, phone);
                    try {
                        org.insertDonator(d);
                    } catch (AlreadyExistingRoleException e) {
                        e.printStackTrace();
                        u = d = null;
                    }

                    u = d; // set to logged in user
                    p("Registered as a donator");
                } else {
                    p("Incorrect role"); continue; // retry login-register
                }
            }

            p("Hello " + u.getName());

            if (u instanceof Admin){
                runAdmin((Admin) u);
            }
            if (u instanceof Donator) {
                runDonator((Donator) u);
            }
            if (u instanceof Beneficiary){
                runBeneficiary((Beneficiary) u);
            }
        }
    }

    private void runBeneficiary(Beneficiary u) {
        while(true){
            p("");
            p("Beneficiary UI -- Actions available:");
            String action = rs("add, show, commit, logout, exit > ");

            switch (action) {
                case "add":
                    runEntityViewer(u);
                    break;
                case "show":
                    boolean returnImmediately = runBeneficiarySelfManagement(u);
                    if (returnImmediately){
                        return;
                    }
                    break;
                case "commit":
                    u.getRequestList().commit(org);
                    return;
                case "logout":
                    return;
                case "exit":
                    System.exit(0);
                default:
                    // p("Invalid action");
                    break;
            }
        }
    }

    private boolean runBeneficiarySelfManagement(Beneficiary u) {
        while(true){
            List<RequestDonation> requests = u.getRequestList().monitor(); // will print list of requested entities
            String action = rs("edit, clear, commit > ");
            switch (action){
                case "edit":
                    int idx = ri("Show details for idx > ");
                    if (idx == -1) {
                        continue;
                    }

                    RequestDonation donation = requests.get(idx);
                    p("Selected request: ");
                    p(donation.getEntity().toString());

                    int newQuantity = ri("How much to request? (0 = delete) > ");
                    if (newQuantity == 0 ){
                        // delete
                        u.removeDonation(donation);
                    } else {
                        u.getRequestList().modify(donation, newQuantity, org);
                    }
                    break;
                case "clear":
                    u.getRequestList().reset();
                    break;
                case "commit":
                    u.getRequestList().commit(org);
                    return true;
                default:
                    return false;
            }
        }
    }

    private boolean runDonatorSelfManagement(Donator u) {
        while(true){
            List<RequestDonation> availableEntities = u.getOffersList().monitor(); // will print list of pledgeable entities
            String action = rs("edit, clear, commit > ");
            switch (action){
                case "edit":
                    int idx = ri("Show details for idx > ");
                    if (idx == -1) {
                        continue;
                    }

                    RequestDonation entity = availableEntities.get(idx);
                    p("Selected entity: ");
                    p(entity.getEntity().getName());

                    RequestDonation alreadyPledgedEntity = u.getOffersList().get(entity.getEntity().getId());
                    long alreadyPledgedEntityAmount = alreadyPledgedEntity.getQuantity();

                    p(String.format("You've already pledged %d quantity of this.", alreadyPledgedEntityAmount));

                    int newQuantity = ri("How much to pledge? (0 = delete) > ");
                    if (newQuantity < 0) {
                        p("Can't pledge a negative amount of something.");
                        break;
                    }
                    if (newQuantity == 0){
                        if (alreadyPledgedEntityAmount > 0){
                            u.remove(alreadyPledgedEntity);
                        }
                        // delete
                    } else {
                        u.getOffersList().modify(entity, newQuantity, org);
                    }
                    break;
                case "clear":
                    u.getOffersList().reset();
                    break;
                case "commit":
                    u.getOffersList().commit(org);
                    return true;
                default:
                    return false;
            }
        }
    }


    private void runDonator(Donator u) {
        while(true){
            p("");
            p("Donator UI -- Actions available:");
            String action = rs("add, show, commit, logout, exit > ");

            switch (action) {
                case "add":
                    runEntityViewer(u);
                    break;
                case "show":
                    boolean returnImmediately = runDonatorSelfManagement(u);
                    if (returnImmediately){
                        return;
                    }
                    break;
                case "commit":
                    u.getOffersList().commit(org);
                    return;
                case "logout":
                    return;
                case "exit":
                    System.exit(0);
                default:
                    // p("Invalid action");
                    break;
            }
        }
    }

    private void runAdmin(Admin u) {
        while(true){
            p("");
            p("Admin UI -- Actions available:");
            String action = rs("view, monitor, logout, exit > ");

            switch (action) {
                case "view":
                    runEntityViewer(u);
                    break;
                case "monitor":
                    p("list beneficiaries (lb), list donators (ld), reset beneficiaries (rst)");
                    String act = rs("lb, ld, rst > ");
                    if (act.equals("lb")) {
                        runAdminBeneficiaryManagement();
                    }
                    if (act.equals("ld")) {
                        runAdminDonatorManagement();
                    }
                    if (act.equals("rst")) {
                        // reset all received items
                        org.resetAllBeneficiaries();
                    }
                    break;
                case "logout":
                    return;
                case "exit":
                    System.exit(0);
                default:
                    // p("Invalid action");
                    break;
            }
        }
    }

    private void runEntityViewer(User u) {
        boolean isDonator = u instanceof Donator;
        boolean isBeneficiary = u instanceof Beneficiary;

        System.out.printf("1. Materials (%d), 2. Services (%d)\n", org.numOfDonatedMaterials(), org.numOfDonatedServices());
        String sel = rs("1, 2 > ");

        List<? extends Entity> entities;
        if (sel.equals("1")){
            entities = org.getAvailableMaterials();
        }else if (sel.equals("2")){
            entities = org.getAvailableServices();
        }else{
            // go back
            return;
        }

        while (true) {
            try {
                p("Available entities:");
                for (int i = 0; i < entities.size(); i++) {
                    System.out.printf("%d. %s\n", i, entities.get(i).getName());
                }

                int idx = ri("Show details for idx > ");
                if (idx == -1) {
                    return; // go back
                }

                if (idx >= entities.size() || idx < -1) {
                    throw new InvalidSelectedIndexException("Invalid index selected");
                }

                Entity targetEntity = entities.get(idx);
                p(targetEntity.toString());

                // donator can {pledge} specific item / quantity
                // beneficiary can {request} specific item / quantity
                if (isDonator || isBeneficiary) {
                    String actionDisplay = "pledge";
                    if (isBeneficiary) {
                        actionDisplay = "request";
                    }
                    p("Do you want to " + actionDisplay + " this?");
                    String decision = rs("y/n > ");

                    if (decision.equals("y")) {
                        int count = ri("How much quantity will you " + actionDisplay + "? > ");

                        if (isDonator) {
                            RequestDonation rd = new RequestDonation(targetEntity, count);
                            ((Donator) u).add(rd, org);
                        }
                        if (isBeneficiary) {
                            Request r = new Request(targetEntity, count);
                            ((Beneficiary) u).requestItems(r, org);
                        }
                    }
                }
            }catch (Exception e){
                p(e.getMessage());
            }
        }
    }


    private void runAdminBeneficiaryManagement() {
        while(true) {  // load beneficiaries
            try {
                List<Beneficiary> beneficiaries = org.listBeneficiaries();

                int idx = ri("Show details for idx > ");
                if (idx == -1) {
                    break; // go back
                }

                if (idx >= beneficiaries.size() || idx < -1) {
                    throw new InvalidSelectedIndexException("Invalid index selected");
                }

                Beneficiary targetBeneficiary = beneficiaries.get(idx);
                p(targetBeneficiary.toString());

                String ba = rs("clear, delete > ");
                if (ba.equals("clear")) {
                    targetBeneficiary.clearReceivedList();
                }
                if (ba.equals("delete")) {
                    org.removeBeneficiary(targetBeneficiary);
                }
            }catch(Exception e){
                p(e.getMessage());
            }
        }
    }

    private void runAdminDonatorManagement() {
        while(true) { // load donators
            try {
                List<Donator> donators = org.listDonators();

                int idx = ri("Show details for idx > ");
                if (idx == -1) {
                    break; // go back
                }

                if (idx >= donators.size() || idx < -1) {
                    throw new InvalidSelectedIndexException("Invalid index selected");
                }

                Donator targetDonator = donators.get(idx);
                p(targetDonator.toString());

                String ba = rs("delete > ");
                if (ba.equals("delete")) {
                    org.removeDonator(targetDonator);
                }
            }catch (Exception e){
                p(e.getMessage());
            }
        }
    }

}

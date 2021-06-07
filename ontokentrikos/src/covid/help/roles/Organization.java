package covid.help.roles;

import covid.help.donations.RequestDonationList;
import covid.help.exceptions.AlreadyExistingRoleException;
import covid.help.exceptions.DuplicateEntityIdException;
import covid.help.items.Entity;
import covid.help.items.Material;
import covid.help.items.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Organization {
    private String name;
    private Admin admin;

    private List<Entity> entityList = new ArrayList<>(); // dianemontai se beneficiaries

    private List<Donator> donatorList = new ArrayList<>();
    private List<Beneficiary> beneficiaryList = new ArrayList<>();

    private RequestDonationList currentDonations = new RequestDonationList();

    public Organization(String name) {
        this.name = name;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void addEntity(Entity e) throws DuplicateEntityIdException {
        if (this.entityList.contains(e)){
           throw new DuplicateEntityIdException(String.format("Entity already exists with id %d\n",  e.getId()));
        }
        this.entityList.add(e);
    }

    public void removeEntity(Entity e){
        // only admin
        this.entityList.remove(e);
    }

    public void insertDonator(Donator d) throws AlreadyExistingRoleException {
        if (this.donatorList.contains(d)){
            throw new AlreadyExistingRoleException(String.format("Donator %s already exists\n", d.getName()));
        }
        this.donatorList.add(d);
    }

    public void removeDonator(Donator d){
        this.donatorList.remove(d);
    }

    public void insertBeneficiary(Beneficiary b) throws AlreadyExistingRoleException {
        if (this.beneficiaryList.contains(b)){
            throw new AlreadyExistingRoleException(String.format("Beneficiary %s already exists\n", b.getName()));
        }
        this.beneficiaryList.add(b);
    }

    public void removeBeneficiary(Beneficiary b){
        this.beneficiaryList.remove(b);
    }

    public long numOfDonatedMaterials(){
        return this.currentDonations.numOfMaterials();
    }

    public long numOfDonatedServices(){
        return this.currentDonations.numOfServices();
    }

    public void listEntities(){
        List<Material> materials = this.entityList.stream().filter(x -> x instanceof Material).map(x -> (Material) x).collect(Collectors.toList());
        List<Service> services = this.entityList.stream().filter(x -> x instanceof Service).map(x -> (Service) x).collect(Collectors.toList());

        System.out.println("In total:");
        System.out.printf("Materials: %d\n", materials.size());
        System.out.printf("Services: %d\n", services.size());
        System.out.println("");

        System.out.println("Materials List:");

        for (Material m : materials) {
            System.out.println(m);
        }

        System.out.println("Services List:");

        for (Service s : services) {
            System.out.println(s);
        }
    }

    public List<Beneficiary> listBeneficiaries(){
        for (int i = 0; i < this.beneficiaryList.size(); i++){
            System.out.printf("%d. %s\n", i, this.beneficiaryList.get(i).getName());
        }

        return this.beneficiaryList;
    }

    public List<Donator> listDonators(){
        for (int i = 0; i < this.donatorList.size(); i++){
            System.out.printf("%d. %s\n", i, this.donatorList.get(i).getName());
        }

        return this.donatorList;
    }

    public void resetAllBeneficiaries(){
        for(Beneficiary b: this.beneficiaryList){
            b.clearReceivedList();
        }
    }

    public RequestDonationList getCurrentDonations() {
        return currentDonations;
    }

    public List<Material> getAvailableMaterials() {
        return entityList.stream().filter(x -> x instanceof Material).map(x -> (Material) x).collect(Collectors.toList());
    }

    public List<Service> getAvailableServices() {
        return entityList.stream().filter(x -> x instanceof Service).map(x -> (Service) x).collect(Collectors.toList());
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public String getName() {
        return name;
    }

    public User getUserWithPhone(String phone) {
        List<User> allUsers = new ArrayList<>();
        allUsers.add(admin);
        allUsers.addAll(donatorList);
        allUsers.addAll(beneficiaryList);

        return allUsers.stream().filter(x -> x.getPhoneNumber().equals(phone)).findFirst().orElse(null);
    }
}

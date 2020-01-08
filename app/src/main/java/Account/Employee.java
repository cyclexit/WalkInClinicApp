package Account;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import Rating.Rate;
import Service.Service;

public class Employee extends Account {

    /*
     *  constant
     */
    public static final String TYPE = "employee";

    /*
     *  instance
     */
    private ClinicProfile clinicProfile;
    private ArrayList<Service> selectedServices;
    private ArrayList<Rate> rates;


    /*
     *  constructor
     */
    public Employee() {}

    public Employee(String Id, String firstName, String lastName, String email, String userName,
                    String password) {
        super(Id, firstName, lastName, email, userName, password);
    }

    /*
     *  member methods
     */

    // getter
    public String getType() {
        return TYPE;
    }

    public ClinicProfile getClinicProfile() {
        return clinicProfile;
    }

    public ArrayList<Service> getSelectedServices() {
        return selectedServices;
    }

    public ArrayList<Rate> getRates() {
        return rates;
    }

    // setter
    public void setClinicName(String name) {
        clinicProfile.setClinicName(name);
    }

    public void setClinicProfile(ClinicProfile clinicProfile) {
        this.clinicProfile = clinicProfile;
    }

    public void setSelectedServices(ArrayList<Service> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public void setRates(ArrayList<Rate> rates) {
        this.rates = rates;
    }

    // other
    public void rateService(DatabaseReference databaseServices, Service service, int rate) {
        service.setRate(rate);
        databaseServices.child(service.getId()).child("rate").setValue(rate);
    }

    public void setUpClinic(ClinicProfile clinicProfile) {
        this.clinicProfile = clinicProfile;
        DatabaseReference databaseEmployee = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account").child(TYPE).child(getId());
        databaseEmployee.child(ClinicProfile.TYPE).setValue(clinicProfile);
    }

    public void updateClinic(ClinicProfile clinicProfile) {
        DatabaseReference databaseEmployee = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account").child(TYPE).child(getId());
        databaseEmployee.child(ClinicProfile.TYPE).child("address").setValue(clinicProfile.getAddress());
        databaseEmployee.child(ClinicProfile.TYPE).child("clinicName").setValue(clinicProfile.getClinicName());
        databaseEmployee.child(ClinicProfile.TYPE).child("insurance").setValue(clinicProfile.getInsurance());
        databaseEmployee.child(ClinicProfile.TYPE).child("payment").setValue(clinicProfile.getPayment());
        databaseEmployee.child(ClinicProfile.TYPE).child("phoneNum").setValue(clinicProfile.getPhoneNum());
        this.clinicProfile.setAddress(clinicProfile.getAddress());
        this.clinicProfile.setInsurance(clinicProfile.getInsurance());
        this.clinicProfile.setPhoneNum(clinicProfile.getPhoneNum());
        this.clinicProfile.setClinicName(clinicProfile.getClinicName());
        this.clinicProfile.setPayment(clinicProfile.getPayment());
    }

    public void selectService(Service service) {
        selectedServices.add(service);
        DatabaseReference databaseEmployee = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account").child(TYPE).child(getId());
        databaseEmployee.child("selectedServices").child(service.getId()).setValue(service);
    }

    public void unselectService(Service service) {
        selectedServices.remove(service);
        DatabaseReference databaseEmployee = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account").child(TYPE).child(this.getId());
        databaseEmployee.child("selectedServices").child(service.getId()).removeValue();
    }

    public void setStartTime(String day, String startTime) {
        DatabaseReference databaseStartTimes = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account").
                child(TYPE).child(this.getId()).child("clinicProfile").child("startTime");
        databaseStartTimes.child(day).setValue(startTime);
    }

    public void setEndTime(String day, String endTime) {
        DatabaseReference databaseEndTimes = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account").
                child(TYPE).child(this.getId()).child("clinicProfile").child("endTime");
        databaseEndTimes.child(day).setValue(endTime);
    }
}
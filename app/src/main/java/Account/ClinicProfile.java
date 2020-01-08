package Account;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ClinicProfile implements Serializable {

    /*
     *  constant
     */
    public static final String TYPE = "clinicProfile";

    /*
     *  enumeration class
     */
    public enum Payment {CreditCard, DebitCard, Cash, Cheque};
    public enum Insurance {Personal, Group, Travel};

    /*
     *  instances
     */
    private String address;
    private String phoneNum;
    private String clinicName;
    private Payment payment;
    private Insurance insurance;
    private HashMap<String, String> startTimes;
    private HashMap<String, String> endTimes;

    /*
     *  constructor
     */
    public ClinicProfile() {}

    public ClinicProfile(String address, String phoneNum, String clinicName,
                         Payment payment, Insurance insurance) {
        this.address = address;
        this.phoneNum = phoneNum;
        this.clinicName = clinicName;
        this.payment = payment;
        this.insurance = insurance;
        startTimes = new HashMap<>();
        endTimes = new HashMap<>();
    }

    /*
     *  methods
     */

    // getter
    public String getAddress() {
        return address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getClinicName() {
        return clinicName;
    }

    public Payment getPayment() {
        return payment;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public String getStartTime(String day) {
        return startTimes.get(day);
    }

    public String getEndTime(String day) {
        return endTimes.get(day);
    }

    // setter
    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setInsurance(Insurance insurance) { this.insurance = insurance; }

    public void setPayment(Payment payment) { this.payment = payment; }

    public void setStartTime(String day, String startTime) {
        if (startTimes.containsKey(day)) {
            startTimes.replace(day, startTime);
        } else {
            startTimes.put(day, startTime);
        }
    }

    public void setEndTime(String day, String endTime) {
        if (endTimes.containsKey(day)) {
            endTimes.replace(day, endTime);
        } else {
            endTimes.put(day, endTime);
        }
    }
}

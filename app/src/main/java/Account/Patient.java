package Account;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Appointment.Appointment;
import Rating.Rate;
import Service.Service;

public class Patient extends Account {

    // constant
    public static final String TYPE = "patient";

    // constructor
    public Patient() {}

    public Patient(String Id, String firstName, String lastName, String email, String userName,
                   String password) {
        super(Id, firstName, lastName, email, userName, password);
    }

    // getter
    public String getType() {
        return TYPE;
    }

    // method
    public void makeAppointment(Appointment appointment) {
        DatabaseReference databaseAppointments = FirebaseDatabase.getInstance(
            "https://walkinclinicapp-4854b.firebaseio.com/").getReference("appointment");
        String id = databaseAppointments.push().getKey();
        appointment.setId(id);
        databaseAppointments.child(id).setValue(appointment);
    }

    public void checkInAppointment(Appointment appointment) {
        DatabaseReference databaseAppointments = FirebaseDatabase.getInstance(
            "https://walkinclinicapp-4854b.firebaseio.com/").getReference("appointment");
        databaseAppointments.child(appointment.getId()).removeValue();
    }

    public ArrayList<Appointment> getMyAppointment(ArrayList<Appointment> appointments) {
        ArrayList<Appointment> mine = new ArrayList<>();
        // String = employee email + date
        // List: appointments on the current date of the employee
        Map<String, List<Appointment>> map = new HashMap<>();
        for (int i = 0; i < appointments.size(); ++i) {
            Appointment current = appointments.get(i);
            String employeeEmail = current.getEmployeeEmail();
            String patientEmail= current.getPatientEmail();
            String date = current.getDate();
            String key = employeeEmail + date;
            if (!map.containsKey(key)) {
                ArrayList<Appointment> temp = new ArrayList<>();
                current.setWaitingTime(0 + "min");
                temp.add(current);
                map.put(key, temp);
            } else {
                int size = map.get(key).size();
                current.setWaitingTime((size * 15) + "min");
                map.get(key).add(current);
            }
            if (patientEmail.equals(this.getEmail())) {
                mine.add(current);
            }
        }
        return mine;
    }

    public Employee searchClinicByName(String name, ArrayList<Employee> employees) {
        for (int i = 0; i < employees.size(); ++i) {
            if (employees.get(i).getClinicProfile().getClinicName().equals(name)) {
                return employees.get(i);
            }
        }
        return null;
    }

    public Employee searchClinicByAddress (String address, ArrayList<Employee> employees) {
        for (int i = 0; i < employees.size(); ++i) {
            if (employees.get(i).getClinicProfile().getAddress().equals(address)) {
                return employees.get(i);
            }
        }
        return null;
    }

    public ArrayList<Employee> searchClinicByWorkingHour(String day, String time, ArrayList<Employee> employees) {
        ArrayList<Employee> results = new ArrayList<>();
        char enteredTime[] = time.toCharArray();

        for (int i = 0; i < employees.size(); ++i) {
            if (employees.get(i).getClinicProfile().getStartTime(day) == null
                    || employees.get(i).getClinicProfile().getEndTime(day) == null) {
                continue;
            }
            char[] startTime = employees.get(i).getClinicProfile().getStartTime(day).toCharArray();
            char[] endTime = employees.get(i).getClinicProfile().getEndTime(day).toCharArray();

            if (checkEnteredTime(enteredTime, startTime, endTime)) {
                results.add(employees.get(i));
            }
        }
        return results;
    }

    public ArrayList<Employee> searchClinicByService(String serviceName, ArrayList<Employee> employees) {
        ArrayList<Employee> results = new ArrayList<>();
        for (int i = 0; i < employees.size(); ++i) {
            ArrayList<Service> services = employees.get(i).getSelectedServices();
            for (int j = 0; j < services.size(); ++j) {
                if (services.get(j).getServiceName().equals(serviceName)) {
                    results.add(employees.get(i));
                }
            }
        }
        return results;
    }

    public void rateClinic(String employeeID, Rate rate) {
        DatabaseReference databaseEmployee = FirebaseDatabase.getInstance(
            "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account").child(Employee.TYPE).child(employeeID);
        String rateID = databaseEmployee.push().getKey();
        rate.setId(rateID);
        databaseEmployee.child("rate").child(rateID).setValue(rate);
    }

    // private method
    private int calcHour(char[] time) {
        int res = 0;
        for (int i = 0; i < time.length; ++i) {
            if (time[i] == ':') {
                break;
            }
            res = res * 10 + (time[i] - '0');
        }
        return res;
    }

    private int calcMinute(char[] time) {
        int res = 0, w = 1;
        for (int i = time.length - 1; i >= 0; --i) {
            if (time[i] == ':') {
                break;
            }
            res += w * (time[i] - '0');
            w *= 10;
        }
        return res;
    }

    private boolean checkEnteredTime(char[] enteredTime, char[] startTime, char[] endTime) {
        int enteredHour = calcHour(enteredTime), enteredMinute = calcMinute(enteredTime);
        int startHour = calcHour(startTime), startMinute = calcMinute(startTime);
        int endHour = calcHour(endTime), endMinute = calcMinute(endTime);

        if (enteredHour < startHour || enteredHour > endHour) {
            return false;
        } else if (enteredHour == startHour) {
            return enteredMinute >= startMinute;
        } else if (enteredHour == endHour) {
            return enteredMinute <= endMinute;
        } else {
            return true;
        }

    }
}

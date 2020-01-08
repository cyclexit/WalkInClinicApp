package Appointment;

public class Appointment {
    /*
     * instance
     */
    private String id;
    private String clinicName;
    private String serviceName;
    private String employeeEmail;
    private String patientEmail;
    private String date;
    private String waitingTime;

    /*
     * constructor
     */
    public Appointment() {}

    public Appointment(String clinicName, String serviceName, String employeeEmail, String patientEmail, String date) {
        this.clinicName = clinicName;
        this.serviceName = serviceName;
        this.employeeEmail = employeeEmail;
        this.patientEmail = patientEmail;
        this.date = date;
    }

    // getter
    public String getId() {
        return id;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDate() { return date; }

    public String getWaitingTime() { return waitingTime; }

    public String getEmployeeEmail() { return employeeEmail; }

    public String getPatientEmail() {return patientEmail; }

    // setter
    public void setId(String id) {
        this.id = id;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setDate(String date) { this.date = date; }

    public void setWaitingTime(String waitingTime) { this.waitingTime =  waitingTime; }

    public void setEmployeeEmail(String employeeEmail){ this.employeeEmail = employeeEmail; }

    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }
}

package com.example.walkinclinicapp.Deliverable4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import Account.ClinicProfile;
import Account.Employee;
import Account.Patient;
import Appointment.Appointment;
import Service.Service;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PatientTest {
    private Patient patient;
    private ArrayList<Employee> employees;
    private ArrayList<Appointment> appointments;
    private ArrayList<Appointment> noMatchAppointments;

    //Variables used for testing
    private String address = "AdressOne";
    private String service = "Operation";
    private String time = "8:30";
    private String day = "Saturday";
    private String clinicName = "Clinic";
    private String patientEmail = "email";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        //Initializing Patient class
        patient = new Patient("ID", "firstName", "lastName", patientEmail,
                "usernamae", "password");

        //Initializing Employee List
        employees = new ArrayList<Employee>();

        //Dummy Employee Classes
        Employee em1, em2, em3;
        em1 = new Employee("id1", "first1", "last1", "email1",
                "username1", "password1");
        em2 = new Employee("id2", "first2", "last2", "email2",
                "username2", "password2");
        em3 = new Employee("id3", "first3", "last3", "email3",
                "username3", "password3");

        //Dummy Clinic Profile Classes
        ClinicProfile c1, c2, c3;
        c1 = new ClinicProfile(address, "phone1", "name1",
                ClinicProfile.Payment.Cash, ClinicProfile.Insurance.Personal);
        c2 = new ClinicProfile("AdressTwo", "phone2", "name3",
                ClinicProfile.Payment.Cash, ClinicProfile.Insurance.Personal);
        c3 = new ClinicProfile("AdressThree", "phone3", clinicName,
                ClinicProfile.Payment.Cash, ClinicProfile.Insurance.Personal);

        //Setting Start and End time for Clinic Profile
        c1.setStartTime("Friday", "7:00");
        c1.setEndTime("Friday", "15:00");

        c2.setStartTime(day, time);
        c2.setEndTime(day,"16:00");

        c3.setStartTime("Monday","9:00");
        c3.setEndTime("Monday","17:00");

        //Setting Employee Class' Clinic Profile
        em1.setUpClinic(c1);
        em2.setUpClinic(c2);
        em3.setUpClinic(c3);

        //Dummy Service Lists
        ArrayList<Service> sL1, sL2, sL3;
        sL1 = new ArrayList<Service>();
        sL2 = new ArrayList<Service>();
        sL3 = new ArrayList<Service>();

        //Dummy Service Classes
        Service s1, s2, s3, s4, s5, s6, s7;
        s1 = new Service("id1", "Injection", "p1");
        s2 = new Service("id2", "Checkup", "p2");
        s3 = new Service("id3", "Testing","p3");
        s4 = new Service("id4", "Prescription","p4");
        s5 = new Service("id5", "Emergency","p5");
        s6 = new Service("id6", "Xray","p6");
        s7 = new Service("id7", service, "p7");

        //Populating Service Array List
        sL1.add(s1);
        sL1.add(s2);
        sL1.add(s7);

        sL2.add(s3);
        sL2.add(s4);
        sL2.add(s7);

        sL3.add(s5);
        sL3.add(s6);

        //Setting Employee class' Service List
        em1.setSelectedServices(sL1);
        em2.setSelectedServices(sL2);
        em3.setSelectedServices(sL3);

        //Populating Employee Array List
        employees.add(em1);
        employees.add(em2);
        employees.add(em3);

        //Initialize Appointment Array List
        appointments = new ArrayList<Appointment>();
        noMatchAppointments = new ArrayList<Appointment>();

        //Dummy Appointment Class
        Appointment a1, a2, a3, a4;
        a1 = new Appointment("em1", patientEmail, "date1", "wait1", day);
        a2 = new Appointment("em2", patientEmail, "date2", "wait2", day);
        a3 = new Appointment("em3", "emp3", "date3", "wait3", "Monday");
        a4 = new Appointment("em4", "emp4", "date4", "wait4", "Monday");

        //Populating Appointment Array List
        appointments.add(a1);
        appointments.add(a2);
        appointments.add(a3);
        appointments.add(a4);

        noMatchAppointments.add(a3);
        noMatchAppointments.add(a4);
    }

    //This test expects to find an Employee class with the selected address
    @Test
    public void adressSearchTest() {
        Employee testClass = patient.searchClinicByAddress(address, employees);
        assertNotNull(testClass);
    }

    //This does not expect to find an Employee class with the selected address
    @Test
    public void adressSearchTestTwo() {
        Employee testClass = patient.searchClinicByAddress("fakeAdress", employees);
        assertNull(testClass);
    }

    //This test expects to find an Employee cass with the selected clinic name
    @Test
    public void nameSearchTest() {
        Employee testClass = patient.searchClinicByName(clinicName, employees);
        assertNotNull(testClass);
    }

    //This test does not expect to find an Employee class with the selected address
    @Test
    public void nameSearchTestTwo() {
        Employee testClass = patient.searchClinicByName("fakeName", employees);
        assertNull(testClass);
    }

    //This test expects to find 2 Employee class containing the selected Service
    @Test
    public void serviceSearchTest() {
        ArrayList<Employee> testList = patient.searchClinicByService(service, employees);
        int testSize = testList.size();
        assertEquals(2, testSize);
    }

    //This test expects to find 0 Employee class containing the selected Service
    @Test
    public void serviceSearchTestTwo() {
        ArrayList<Employee> testList = patient.searchClinicByService("NotAService", employees);
        int testSize = testList.size();
        assertEquals(0, testSize);
    }

    //This test expects find 1 Employee class containing the selecting Working Time
    @Test
    public void timeSearchTest() {
        ArrayList<Employee> testList = patient.searchClinicByWorkingHour(day, time, employees);
        int testSize = testList.size();
        assertEquals(1, testSize);
    }

    //This test expects find 0 Employee class containing the selecting Working Time
    @Test
    public void timeSearchTestTwo() {
        ArrayList<Employee> testList = patient.searchClinicByWorkingHour("Tuesday","21:20", employees);
        int testSize = testList.size();
        assertEquals(0, testSize);
    }

    //This function expects to find 0 Appointment class containing the Patient's Email
    @Test
    public void getAppointmentTest() {
        ArrayList<Appointment> testList = patient.getMyAppointment(appointments);
        int testSize = testList.size();
        assertEquals(0, testSize);
    }

    //This functions expects to find 0 Appointment class contianing the Patient's Email
    @Test
    public void getAppointmentTestTwo() {
        ArrayList<Appointment> testList = patient.getMyAppointment(noMatchAppointments);
        int testSize = testList.size();
        assertEquals(0, testSize);
    }



}

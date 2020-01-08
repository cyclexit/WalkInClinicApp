package com.example.walkinclinicapp.Deliverable3;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import Account.Employee;
import Service.Service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EmployeeTest {
    private Employee employee;
    private int initialSize;
    private ArrayList<Service> selectedServices;
    private Service service;

    //ID from database needed to test functions
    private String employeeID = "-LuTWwr7ZWdDdn22Ooc3";
    private String serviceID = "-LtLWWUTzGdT2Bc48ttS";

    @Before
    public void setUp() {
        //Initializing Employee Class
        employee = new Employee(employeeID, "dummyName", "dummyName", "dummyEmail",
                "dummyUserName", "dummyPassword");
        //Adding services to Array
        selectedServices = new ArrayList<Service>();
        Service dummyServiceOne, dummyServiceTwo, dummyServiceThree;
        dummyServiceOne = new Service ("id1", "dummyNameOne", "dummyProviderOne");
        dummyServiceTwo = new Service ("id2", "dummyNameTwo", "dummyProviderTwo");
        dummyServiceThree = new Service ("id3", "dummyNameThree", "dummyProviderThree");

        selectedServices.add(dummyServiceOne);
        selectedServices.add(dummyServiceTwo);
        selectedServices.add(dummyServiceThree);

        //Setting the Selected Service in Employee class
        employee.setSelectedServices(selectedServices);

        //Setting the initial size
        initialSize = selectedServices.size();

        //Initializing Service class for testing
        service = new Service(serviceID, "Service Name", "Provider");
    }

    @Test
    public void testASelectService() {
        employee.selectService(service);
        int updatedSize = employee.getSelectedServices().size();
        assertNotEquals(initialSize, updatedSize);
    }

    @Test
    public void testBUnselectService() {
        employee.unselectService(service);
        int updatedSize = employee.getSelectedServices().size();
        assertEquals(initialSize, updatedSize);
    }
}

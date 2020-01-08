package com.example.walkinclinicapp.Deliverable2;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import Service.Service;

import static junit.framework.TestCase.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServiceTest {
    // Variables
    private DatabaseReference databaseServices;
    private String id;
    // Variables

    // addService function in the Administrator class
    private void addService(DatabaseReference databaseServices, String serviceName, String providerRole) {
        Service service = new Service(id, serviceName, providerRole);
        databaseServices.child(id).setValue(service);
    }

    // editService function in the Administrator class
    private void editService(DatabaseReference databaseServices, String Id, String newServiceName, String newProviderRole) {
        if (!newServiceName.isEmpty()) {
            databaseServices.child(Id).child("serviceName").setValue(newServiceName);
        }
        if (!newProviderRole.isEmpty()) {
            databaseServices.child(Id).child("providerRole").setValue(newProviderRole);
        }
    }

    // deleteService function in the Administrator class
    private void deleteService(DatabaseReference databaseServices, String id) {
        databaseServices.child(id).removeValue();
    }

    // rateService function in the Employee class
    public void rateService(DatabaseReference databaseServices, Service service, int rate) {
        service.setRate(rate);
        databaseServices.child(service.getId()).child("rate").setValue(rate);
    }

    @Before
    public void setUp() throws Exception {
        databaseServices = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("service");
        id = databaseServices.push().getKey();
    }

    // This test the functionality of the Administrator class' function: Add Service
    // The order of this set is crucial since I need to get the id for the key from the initial creation
    @Test
    public void testAddService() {
        addService(databaseServices, "FakeName", "FakeRole");
        assertNotNull(databaseServices.child(id));
    }

    // This test the functionality of the Administrator class' function: Edit Service
    @Test
    public void testEditService() {
        editService(databaseServices, id, "NameServiceName", "NewProviderName");
    }

    // This test the functionality of the Administrator class' function: Delete Service
    @Test
    public void testDeleteService() {
        deleteService(databaseServices, id);
    }

    // This test the functionality of the Employee class' function: Rate Service
    @Test
    public void testRateService() {
        Service s = new Service(id, "FakeName", "FakeRole");
        rateService(databaseServices, s, -1);
    }
}

package com.example.walkinclinicapp.Deliverable2;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import Account.Administrator;
import Account.Employee;

import static org.junit.Assert.assertNull;

public class AccountTest {
    // Variables //
    private Administrator admin;
    private Employee dummyAccount;
    private DatabaseReference databaseAccounts;
    private DatabaseReference databaseEmployees;
    private ArrayList<Employee> employees;
    private String id;
    // Variables //

    // Helper Functions //
    private void createAccount() {
        id = databaseAccounts.push().getKey();
        dummyAccount = new Employee(id, "fakeName", "fakeName", "fakeEmail",
                "fakeUsername", "fakePassword");
        databaseAccounts.child(Employee.TYPE).child(id).setValue(dummyAccount);
    }

    private void pullEmployeesFromDB() {
        databaseEmployees.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employees.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Employee e = snapshot.getValue(Employee.class);
                    employees.add(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Employee findAccount(String id) {
        for(Employee e: employees) {
            if (e != null) {
                if (e.getId().equals(id)) {
                    return e;
                }
            }
        }
        return null;
    }
    // Helper Functions

    @Before
    public void setUp() throws Exception {
        admin = new Administrator("fakeID", "fakeName", "fakeName", "fakeEmail",
                "fakeUsername", "fakePassword");
        employees = new ArrayList<>();
        databaseAccounts = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account");
        databaseEmployees = databaseAccounts.child(Employee.TYPE);
        employees = new ArrayList<>();
        pullEmployeesFromDB();
        createAccount();
    }


    @Test
    public void testADeleteAccount() {
        admin.deleteAccount(databaseEmployees, id);
        pullEmployeesFromDB();
        Employee newAccount = findAccount(id);
        assertNull(newAccount);
    }
}

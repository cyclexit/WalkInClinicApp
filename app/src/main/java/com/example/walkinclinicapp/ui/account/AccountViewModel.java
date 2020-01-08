package com.example.walkinclinicapp.ui.account;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import Account.*;
import Service.Service;

public class AccountViewModel extends ViewModel {


    private ArrayList<Employee> employees;
    private ArrayList<Patient> patients;

    private DatabaseReference databaseAccounts;
    private DatabaseReference databaseEmployees;
    private DatabaseReference databasePatients;

    private MutableLiveData<DatabaseReference> databaseAccountsLiveData;
    private MutableLiveData<DatabaseReference> databaseEmployeesLiveData;
    private MutableLiveData<DatabaseReference> databasePatientsLiveData;

    public final MutableLiveData<ArrayList<Employee>> employeesLiveData;
    public final MutableLiveData<ArrayList<Patient>> patientsLiveData;

    public AccountViewModel() {
        databaseAccounts = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account");
        databaseEmployees = databaseAccounts.child(Employee.TYPE);
        databasePatients = databaseAccounts.child(Patient.TYPE);

        databaseAccountsLiveData = new MutableLiveData<>();
        databaseEmployeesLiveData = new MutableLiveData<>();
        databasePatientsLiveData = new MutableLiveData<>();
        employeesLiveData = new MutableLiveData<>();
        patientsLiveData = new MutableLiveData<>();

        employees = new ArrayList<>();
        patients = new ArrayList<>();

        databaseAccountsLiveData.setValue(databaseAccounts);
        databaseEmployeesLiveData.setValue(databaseEmployees);
        databasePatientsLiveData.setValue(databasePatients);

        databaseEmployees.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employees.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String Id = snapshot.child("id").getValue(String.class);
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String userName = snapshot.child("userName").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);
                    Employee e = new Employee(Id, firstName, lastName, email, userName, password);

                    DataSnapshot clinicProfileSnapshot = snapshot.child("clinicProfile");
                    try {
                        String phoneNum = (clinicProfileSnapshot.child("phoneNum").getValue(String.class));
                        String clinicName = (clinicProfileSnapshot.child("clinicName").getValue(String.class));
                        String address = (clinicProfileSnapshot.child("address").getValue(String.class));
                        ClinicProfile.Payment payment = (clinicProfileSnapshot.child("payment").getValue(ClinicProfile.Payment.class));
                        ClinicProfile.Insurance insurance = (clinicProfileSnapshot.child("insurance").getValue(ClinicProfile.Insurance.class));
                        ClinicProfile cp = new ClinicProfile(address, phoneNum, clinicName, payment, insurance);
                        for (DataSnapshot day : clinicProfileSnapshot.child("startTime").getChildren()) {
                            cp.setStartTime(day.getKey(), day.getValue(String.class));
                        }
                        for (DataSnapshot day : clinicProfileSnapshot.child("endTime").getChildren()) {
                            cp.setEndTime(day.getKey(), day.getValue(String.class));
                        }
                        e.setClinicProfile(cp);
                    } catch (Exception ex) {
                        Log.d("PullDataException", ex.toString());
                    }

                    DataSnapshot selectedServicesSnapshot = snapshot.child("selectedServices");
                    try {
                        ArrayList<Service> services = new ArrayList<>();
                        for (DataSnapshot snapshot1 : selectedServicesSnapshot.getChildren()) {
                            services.add(snapshot1.getValue(Service.class));
                        }
                        e.setSelectedServices(services);
                    } catch (Exception ex) {
                        Log.d("PullDataException", ex.toString());
                    }
                    employees.add(e);
                }
                employeesLiveData.postValue(employees);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databasePatients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patients.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Patient p = snapshot.getValue(Patient.class);
                    patients.add(p);
                }
                patientsLiveData.postValue(patients);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setEmployeesAccounts(ArrayList<Employee> employees){
        employeesLiveData.setValue(employees);
    }

    public void setPatientAccounts(ArrayList<Patient> patients){
        patientsLiveData.setValue(patients);
    }

    public LiveData<ArrayList<Employee>> getEmployeesLiveData(){
        return employeesLiveData;
    }

    public LiveData<ArrayList<Patient>> getPatientsLiveData(){
        return patientsLiveData;
    }

    public LiveData<DatabaseReference> getEmployeeDBReferenceLD() {
        return databaseEmployeesLiveData;
    }

    public LiveData<DatabaseReference> getPatientDBReferenceLD() {
        return databasePatientsLiveData;
    }
}

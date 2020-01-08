package com.example.walkinclinicapp.ui.home;

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

import java.io.Serializable;
import java.util.ArrayList;

import Account.ClinicProfile;
import Account.Employee;
import Rating.Rate;
import Service.Service;

public class HomeViewModel extends ViewModel implements Serializable {

    private ArrayList<Employee> employees;
    private MutableLiveData<ArrayList<Employee>> employeesMutableLiveData;

    private ArrayList<Service> selectedServices;
    private MutableLiveData<ArrayList<Service>> selectedServicesMutableLiveData;

    private ArrayList<Rate> rates;
    private MutableLiveData<ArrayList<Rate>> ratesMutableLiveData;

    private Employee savedSelectedClinic;

    // constructor for patient & admin
    public HomeViewModel(){
        DatabaseReference employeeDatabaseReference = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account")
                .child(Employee.TYPE);

        employees = new ArrayList<Employee>();
        employeesMutableLiveData = new MutableLiveData<>();
        employeeDatabaseReference.addValueEventListener(new ValueEventListener() {
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
                    boolean ok = false;
                    try {
                        String phoneNum = (clinicProfileSnapshot.child("phoneNum").getValue(String.class));
                        String clinicName = (clinicProfileSnapshot.child("clinicName").getValue(String.class));
                        String address = (clinicProfileSnapshot.child("address").getValue(String.class));
                        ClinicProfile.Payment payment = (clinicProfileSnapshot.child("payment").getValue(ClinicProfile.Payment.class));
                        ClinicProfile.Insurance insurance = (clinicProfileSnapshot.child("insurance").getValue(ClinicProfile.Insurance.class));
                        ClinicProfile cp = new ClinicProfile(address, phoneNum, clinicName, payment, insurance);
                        for (DataSnapshot day : clinicProfileSnapshot.child("startTime").getChildren()) {
                            if (day.getValue(String.class) != null) {
                                ok = true;
                            }
                            cp.setStartTime(day.getKey(), day.getValue(String.class));
                        }
                        for (DataSnapshot day : clinicProfileSnapshot.child("endTime").getChildren()) {
                            if (day.getValue(String.class) != null) {
                                ok = true;
                            }
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
                    if (ok) {
                        employees.add(e);
                    }
                }
                employeesMutableLiveData.postValue(employees);
                // Log.d("employeesHomeVM", (employees == null)? "true":employees.size()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // constructor for employee
    public HomeViewModel(String employeeId) {
        // get the selected services of the employee
        DatabaseReference databaseReferenceSelectedService = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account").child("employee")
                .child(employeeId).child("selectedServices");

        selectedServices = new ArrayList<Service>();
        selectedServicesMutableLiveData = new MutableLiveData<ArrayList<Service>>();

        databaseReferenceSelectedService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedServices.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service s = snapshot.getValue(Service.class);
                    selectedServices.add(s);
                }
                selectedServicesMutableLiveData.postValue(selectedServices);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // get the rates of the employee
        DatabaseReference databaseRates = FirebaseDatabase.getInstance(
            "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account").child("employee")
            .child(employeeId).child("rate");

        rates = new ArrayList<>();
        ratesMutableLiveData = new MutableLiveData<>();

        databaseRates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rates.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rate rate = snapshot.getValue(Rate.class);
                    rates.add(rate);
                }
                ratesMutableLiveData.postValue(rates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<ArrayList<Service>> getSelectedServiceLiveData() {
        return selectedServicesMutableLiveData;
    }

    public LiveData<ArrayList<Employee>> getEmployeesMutableLiveData() {
        return employeesMutableLiveData;
    }

    public LiveData<ArrayList<Rate>> getRatesMutableLiveData() {
        return ratesMutableLiveData;
    }

    public void saveState(Employee employee){
        savedSelectedClinic = employee;
    }

    public Employee readState(){
        return savedSelectedClinic;
    }
}
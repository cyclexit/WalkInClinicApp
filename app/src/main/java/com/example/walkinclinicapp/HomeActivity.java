package com.example.walkinclinicapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Account.Administrator;
import Account.ClinicProfile;
import Account.Employee;
import Account.Patient;
import Service.Service;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    // instances
    private View headView;
    private Menu sideBarMenu;

    private DatabaseReference databaseAccounts;
    private DatabaseReference databaseEmployees;
    private DatabaseReference databasePatients;
    private DatabaseReference databaseServices;

    private ArrayList<Service> services;
    private ArrayList<Employee> employees;
    private ArrayList<Patient> patients;

    private Administrator admin;
    private Employee employee;
    private Patient patient;

    protected boolean isAdmin, isEmployee, isPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // find UI components
        setContentView(R.layout.home_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_account,
                R.id.nav_service, R.id.nav_appointment).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        headView = navigationView.getHeaderView(0);
        sideBarMenu = navigationView.getMenu();

        // get the logged-in user instance
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.getString("accountType").equals(Administrator.TYPE)) {
            isAdmin = true;
            admin = (Administrator) bundle.getSerializable(Administrator.TYPE);
            // Log.d("Administrator", "true");
        } else if (bundle.getString("accountType").equals(Employee.TYPE)) {
            isEmployee = true;
            employee = (Employee) bundle.getSerializable(Employee.TYPE);
            // Log.d("Employee", "true");
        } else {
            isPatient = true;
            patient = (Patient) bundle.getSerializable(Patient.TYPE);
            // Log.d("Patient", "true");
        }

        // initialize arrays
        services = new ArrayList<>();
        employees = new ArrayList<>();
        patients = new ArrayList<>();

        // initialize database reference
        databaseAccounts = FirebaseDatabase.getInstance(
            "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account");
        databaseEmployees = databaseAccounts.child(Employee.TYPE);
        databasePatients = databaseAccounts.child(Patient.TYPE);
        databaseServices = FirebaseDatabase.getInstance(
            "https://walkinclinicapp-4854b.firebaseio.com/").getReference("service");

        // set admin's components
        setHeadViewInfo();
        if (isAdmin){
            sideBarMenu.findItem(R.id.nav_account).setVisible(true);
        }else if (isPatient){
            sideBarMenu.findItem(R.id.nav_appointment).setVisible(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isAdmin) {
            databaseEmployees.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databasePatients.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Patient p = snapshot.getValue(Patient.class);
                        patients.add(p);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service s = snapshot.getValue(Service.class);
                    services.add(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_signout:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    // class methods
    protected void setHeadViewInfo() {
        TextView loginEmail= headView.findViewById(R.id.textViewEmail);
        TextView loginWelcome = headView.findViewById(R.id.textViewWelcome);
        TextView loginAccountType = headView.findViewById(R.id.textViewAccountType);

        if (isAdmin) {
            String welcomeMsg = "Welcome! Administrator";
            // set texts
            loginWelcome.setText(welcomeMsg);
            loginEmail.setText(admin.getEmail());
            loginAccountType.setText(admin.getType());
        } else if (isEmployee) {
            String welcomeMsg = "Welcome! " + employee.getFirstName() + " " + employee.getLastName();
            // set texts
            loginWelcome.setText(welcomeMsg);
            loginEmail.setText(employee.getEmail());
            loginAccountType.setText(employee.getType());
        } else {
            String welcomeMsg = "Welcome! " + patient.getFirstName() + " " + patient.getLastName();
            // set texts
            loginWelcome.setText(welcomeMsg);
            loginEmail.setText(patient.getEmail());
            loginAccountType.setText(patient.getType());
        }
    }
}

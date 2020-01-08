package com.example.walkinclinicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import Account.*;
import Service.Service;

public class MainActivity extends AppCompatActivity {

    // constant
    private final int CREATE_ACCOUNT_REQUEST = 0;
    private static final int CREATE_ACCOUNT_SUCCESS = 1;

    // instance
    private EditText editTextEmailOrUserName;
    private EditText editTextPassword;

    private DatabaseReference databaseAccounts;
    private DatabaseReference databaseAdmin;
    private DatabaseReference databaseEmployees;
    private DatabaseReference databasePatients;

    private Administrator admin;
    private ArrayList<Employee> employees;
    private ArrayList<Patient> patients;

    // override method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmailOrUserName = findViewById(R.id.editTextEmailOrUsername);
        editTextPassword = findViewById(R.id.editTextPasswordL);

        databaseAccounts = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account");
        databaseAdmin = databaseAccounts.child(Administrator.TYPE);
        databaseEmployees = databaseAccounts.child(Employee.TYPE);
        databasePatients = databaseAccounts.child(Patient.TYPE);

        admin = null;
        employees = new ArrayList<>();
        patients = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseAdmin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Administrator a = snapshot.getValue(Administrator.class);
                    admin = a;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == CREATE_ACCOUNT_REQUEST && resultCode == CREATE_ACCOUNT_SUCCESS
                && data != null) {
            String email = data.getStringExtra("userEmail");
            String password = data.getStringExtra("userPassword");
            editTextEmailOrUserName.setText(email);
            editTextPassword.setText(password);
        }
    }

    // onClick method
    public void register(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivityForResult(intent, CREATE_ACCOUNT_REQUEST);
    }

    public void signIn(View view) {
        if (notEmpty()) {
            String userName = editTextEmailOrUserName.getText().toString().trim();
            String password = editTextPassword.getText().toString();
            String encryptedPassword = password;
            try {
                MessageDigest encrypt = MessageDigest.getInstance("SHA-256");
                encrypt.update(password.getBytes());
                encryptedPassword = byteToHex(encrypt.digest());
            } catch (NoSuchAlgorithmException e) {
                Log.d("NoSuchAlgorithm", "No Such Algorithm");
            }

            // intent to home activity
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

            // local adm account
            if (userName.equals("adm#") && password.equals("112233")){
                setIntentAdmin(intent, admin);
                startActivity(intent);
                finish();
            }

            // database account
            if (userName.equals(admin.getEmail()) || userName.equals(admin.getUserName())) {
                if (encryptedPassword.equals(admin.getPassword())) {
                    setIntentAdmin(intent, admin);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Incorrect password.",
                        Toast.LENGTH_LONG).show();
                }
            } else if (getIndexEmployees(userName) != -1) {
                int index = getIndexEmployees(userName);
                if (encryptedPassword.equals(employees.get(index).getPassword())) {
                    setIntentEmployee(intent, employees.get(index));
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Incorrect password.",
                        Toast.LENGTH_LONG).show();
                }
            } else if (getIndexPatients(userName) != -1) {
                int index = getIndexPatients(userName);
                if (encryptedPassword.equals(patients.get(index).getPassword())) {
                    setIntentPatient(intent, patients.get(index));
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Incorrect password.",
                        Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "The entered E-mail or user name does not exist.",
                    Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please complete your login information.",
                    Toast.LENGTH_LONG).show();
        }
    }

    // other method
    private boolean notEmpty() {
        if (!editTextEmailOrUserName.getText().toString().isEmpty() &&
                !editTextPassword.getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void setIntentAdmin(Intent intent, Administrator admin) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Administrator.TYPE, admin);
        bundle.putString("accountType", Administrator.TYPE);
        intent.putExtras(bundle);
    }

    public void setIntentEmployee(Intent intent, Employee employee) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Employee.TYPE, employee);
        bundle.putString("accountType", Employee.TYPE);
        intent.putExtras(bundle);
    }

    public void setIntentPatient(Intent intent, Patient patient) {
        Bundle bundle = new Bundle();
        bundle.putString("accountType", Patient.TYPE);
        bundle.putSerializable(Patient.TYPE, patient);
        intent.putExtras(bundle);
    }

    private int getIndexEmployees(String str) {
        for (int i = 0; i < employees.size(); ++i) {
            if (isEmail(str)) {
                if (str.equals(employees.get(i).getEmail())) {
                    return i;
                }
            } else {
                if (str.equals(employees.get(i).getUserName())) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getIndexPatients(String str) {
        for (int i = 0; i < patients.size(); ++i) {
            if (isEmail(str)) {
                if (str.equals(patients.get(i).getEmail())) {
                    return i;
                }
            } else {
                if (str.equals(patients.get(i).getUserName())) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String byteToHex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int i = 0; i <b.length; ++i) {
            stmp=(java.lang.Integer.toHexString(b[i] & 0XFF));
            if (stmp.length() == 1) hs = hs + "0" + stmp;
            else hs = hs + stmp;
            if (i < b.length - 1)  hs = hs + ":";
        }
        return hs.toUpperCase();
    }
}

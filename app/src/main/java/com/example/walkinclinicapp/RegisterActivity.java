package com.example.walkinclinicapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import Account.Employee;
import Account.Patient;

public class RegisterActivity extends AppCompatActivity {

    // constant
    private static final int CREATE_ACCOUNT_REQUEST = 0;
    private static final int CREATE_ACCOUNT_SUCCESS = 1;
    private static final String ADMINISTRATOR_USER_EMAIL = "admin@admin.com";
    private static final String ADMINISTRATOR_USER_NAME = "admin";

    // instance
    private EditText editTextFirstNameR;
    private EditText editTextLastNameR;
    private EditText editTextEmailR;
    private EditText editTextUserNameR;
    private EditText editTextPasswordR1;
    private EditText editTextPasswordR2;
    private RadioButton radioButtonEmployee;
    private RadioButton radioButtonPatient;

    private DatabaseReference databaseAccounts;
    private DatabaseReference databaseEmployees;
    private DatabaseReference databasePatients;

    private ArrayList<String> emails;
    private ArrayList<String> userNames;

    // override method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextFirstNameR = findViewById(R.id.editTextFirstNameR);
        editTextLastNameR = findViewById(R.id.editTextLastNameR);
        editTextEmailR = findViewById(R.id.editTextEmailR);
        editTextUserNameR = findViewById(R.id.editTextUserNameR);
        editTextPasswordR1 = findViewById(R.id.editTextPasswordR1);
        editTextPasswordR2 = findViewById(R.id.editTextPasswordR2);
        radioButtonEmployee = findViewById(R.id.radioButtonEmployee);
        radioButtonPatient = findViewById(R.id.radioButtonPatient);

        databaseAccounts = FirebaseDatabase.getInstance(
            "https://walkinclinicapp-4854b.firebaseio.com/").getReference("account");
        databaseEmployees = databaseAccounts.child(Employee.TYPE);
        databasePatients = databaseAccounts.child(Patient.TYPE);

        emails = new ArrayList<>();
        emails.add(ADMINISTRATOR_USER_EMAIL);
        userNames = new ArrayList<>();
        userNames.add(ADMINISTRATOR_USER_NAME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseEmployees.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    emails.add(snapshot.child("email").getValue(String.class));
                    userNames.add(snapshot.child("userName").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databasePatients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Patient p = snapshot.getValue(Patient.class);
                    emails.add(p.getEmail());
                    userNames.add(snapshot.child("userName").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // onClick method
    public void createAccount(View view) {
        if (notEmpty()) {
            String firstName = editTextFirstNameR.getText().toString();
            String lastName = editTextLastNameR.getText().toString();
            String email = editTextEmailR.getText().toString();
            String userName = editTextUserNameR.getText().toString();
            String password1 = editTextPasswordR1.getText().toString();
            String password2 = editTextPasswordR2.getText().toString();

            // password not entered did not checked
            if (!isEmailValid(email)) {
                Toast.makeText(this, "Please enter a valid E-mail address.",
                    Toast.LENGTH_LONG).show();
            } else if (!isNewEmail(email)) {
                Toast.makeText(this,
                    "The entered E-mail is registered.",
                    Toast.LENGTH_LONG).show();
            } else if (!isNewUserName(userName)) {
                Toast.makeText(this, "The entered user name is registered.",
                   Toast.LENGTH_LONG).show();
            } else if (!password1.equals(password2)) {
                Toast.makeText(this, "The passwords you enter do not match.",
                    Toast.LENGTH_LONG).show();
            } else if (!radioButtonEmployee.isChecked() && !radioButtonPatient.isChecked()) {
                Toast.makeText(this, "Please select an account type.",
                    Toast.LENGTH_LONG).show();
            } else {
                String Id = databaseAccounts.push().getKey();
                String encryptedPassword = password1;
                try {
                    MessageDigest encrypt = MessageDigest.getInstance("SHA-256");
                    encrypt.update(password1.getBytes());
                    encryptedPassword = byteToHex(encrypt.digest());
                } catch (NoSuchAlgorithmException e) {
                    Log.d("NoSuchAlgorithm", "No such algorithm");
                }

                if (radioButtonEmployee.isChecked()) {
                    Employee employee = new Employee(
                        Id, firstName, lastName, email, userName, encryptedPassword);
                    databaseAccounts.child(Employee.TYPE).child(Id).setValue(employee);
                    Toast.makeText(this, "Successfully create an employee account.",
                        Toast.LENGTH_SHORT).show();
                } else if (radioButtonPatient.isChecked()) {
                    Patient patient = new Patient(
                        Id, firstName, lastName, email, userName, encryptedPassword);
                    databaseAccounts.child(Patient.TYPE).child(Id).setValue(patient);
                    Toast.makeText(this, "Successfully create an patient account.",
                        Toast.LENGTH_SHORT).show();
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("userEmail", email);
                returnIntent.putExtra("userPassword", password1);
                setResult(CREATE_ACCOUNT_SUCCESS, returnIntent);
                finish();
            }
        } else {
            Toast.makeText(this, "Please complete the information above.",
                Toast.LENGTH_LONG).show();
        }
    }

    // other method
    private boolean notEmpty() {
        if (!editTextFirstNameR.getText().toString().isEmpty() &&
            !editTextLastNameR.getText().toString().isEmpty() &&
            !editTextEmailR.getText().toString().isEmpty() &&
            !editTextUserNameR.getText().toString().isEmpty() &&
            !editTextPasswordR1.getText().toString().isEmpty() &&
            !editTextPasswordR2.getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isNewEmail(String email) {
        for (int i = 0; i < emails.size(); ++i) {
            if (emails.get(i).equals(email)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNewUserName(String userName) {
        for (int i = 0; i < userNames.size(); ++i) {
            if (userNames.get(i).equals(userName)) {
                return false;
            }
        }
        return true;
    }

    private String byteToHex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int i = 0; i <b.length; ++i) {
            stmp = (java.lang.Integer.toHexString(b[i] & 0XFF));
            if (stmp.length() == 1) hs = hs + "0" + stmp;
            else hs = hs + stmp;
            if (i < b.length - 1)  hs = hs + ":";
        }
        return hs.toUpperCase();
    }
}

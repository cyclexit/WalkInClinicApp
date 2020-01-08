package com.example.walkinclinicapp.ui.appointment;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Account.Patient;
import Appointment.Appointment;


public class AppointmentViewModel extends ViewModel {

    private ArrayList<Appointment> appointments;
    private MutableLiveData<ArrayList<Appointment>> appointmentMutableLiveData;

    /*
        When a user login as a patient, you can use this HomeViewModel constructor to get the appointments
        for the  current patient account. You need to pass a patient object as the only parameter.
        In this constructor, it will calculate the waiting for the current patient, and add the appointments
        of this patient to the MutableLiveData.
    */

    public AppointmentViewModel(Patient patient) {
        DatabaseReference databaseAppointment = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("appointment");
        final String patientEmail = patient.getEmail();
        appointments = new ArrayList<>();
        appointmentMutableLiveData = new MutableLiveData<>();
        final Map<String, List<Appointment>> counter = new HashMap<>();

        databaseAppointment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointments.clear();
                counter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    String mapkey = appointment.getEmployeeEmail() + appointment.getDate();
                    if (!counter.containsKey(mapkey)) {
                        appointment.setWaitingTime(0 + "min");
                        ArrayList<Appointment> temp = new ArrayList<>();
                        temp.add(appointment);
                        counter.put(mapkey, temp);
                    } else {
                        int cnt = counter.get(mapkey).size();
                        appointment.setWaitingTime(15 * cnt + "min");
                        counter.get(mapkey).add(appointment);
                    }
                    if (appointment.getPatientEmail().equals(patientEmail)) {
                        appointments.add(appointment);
                    }
                }
                appointmentMutableLiveData.postValue(appointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<ArrayList<Appointment>> getBookedAppointmentLiveData() {
        return appointmentMutableLiveData;
    }
}

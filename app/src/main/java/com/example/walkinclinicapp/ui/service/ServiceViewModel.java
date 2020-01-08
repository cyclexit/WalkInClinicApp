package com.example.walkinclinicapp.ui.service;

import android.database.Observable;
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

import Account.Employee;
import Service.Service;

public class ServiceViewModel extends ViewModel {

    private ArrayList<Service> services;
    private DatabaseReference databaseServices;
    private MutableLiveData<DatabaseReference> databaseServicesLiveData;
    private MutableLiveData<ArrayList<Service>> servicesLiveData;

    public ServiceViewModel() {
        databaseServices = FirebaseDatabase.getInstance(
                "https://walkinclinicapp-4854b.firebaseio.com/").getReference("service");

        databaseServicesLiveData = new MutableLiveData<>();
        servicesLiveData = new MutableLiveData<>();
        services = new ArrayList<Service>();

        databaseServicesLiveData.setValue(databaseServices);
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                services.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service s = snapshot.getValue(Service.class);
                    services.add(s);
                }
                servicesLiveData.postValue(services);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<ArrayList<Service>> getServiceLiveData() {
        return servicesLiveData;
    }

    public LiveData<DatabaseReference> getServiceReferenceLiveData() {
        return databaseServicesLiveData;
    }
}
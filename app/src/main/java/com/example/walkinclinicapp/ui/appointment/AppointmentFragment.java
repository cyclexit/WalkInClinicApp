package com.example.walkinclinicapp.ui.appointment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.walkinclinicapp.R;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;

import Account.Patient;
import Appointment.Appointment;

public class AppointmentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private SwipeRecyclerView recyclerView;
    private SwipeRecyclerView.Adapter listAdapter;

    private Patient patient;
    private ArrayList<Appointment> appointments;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_appointment, container, false);
        Bundle bundle = this.getActivity().getIntent().getExtras();
        patient = (Patient) bundle.getSerializable(Patient.TYPE);

        if (patient != null){
            refreshLayout = root.findViewById(R.id.appointmentRefreshLayout);
            recyclerView = root.findViewById(R.id.recyclerViewAppointment);
            appointments = new ArrayList<Appointment>();

            AppointmentViewModel appointmentViewModel = new AppointmentViewModel(patient);
            appointmentViewModel.getBookedAppointmentLiveData().observe(this,
                    new Observer<ArrayList<Appointment>>() {

                @Override
                public void onChanged(ArrayList<Appointment> a) {
                    appointments = a;
                    listAdapter = new AppointmentList(getContext(), appointments);
                    recyclerView.setAdapter(listAdapter);
                }
            });

            SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
                @Override
                public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                    // right detail button
                    SwipeMenuItem setRateService = new SwipeMenuItem(getContext())
                            .setTextColor(Color.parseColor("#ffffff"))
                            .setBackgroundColor(Color.parseColor("#33cc33"))
                            .setText("Check in")
                            .setTextSize(16)
                            .setHeight(200)
                            .setWidth(350);
                    rightMenu.addMenuItem(setRateService);
                }
            };
            recyclerView.setSwipeMenuCreator(swipeMenuCreator);

            OnItemMenuClickListener itemMenuClickListener = new OnItemMenuClickListener() {
                @Override
                public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                    menuBridge.closeMenu();
                    int direction = menuBridge.getDirection(); // leftMenu = 1; rightMenu = -1
                    int menuPosition = menuBridge.getPosition();
                    Appointment selectAppointment = appointments.get(adapterPosition);
                    if (direction == -1 && menuPosition == 0) {
                        try {
                            if (selectAppointment.getWaitingTime().equals("0min")){
                                patient.checkInAppointment(selectAppointment);
                            }else {
                                Toast.makeText(getContext(), ("Please check in the appointment with 0min"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.d("setRateException", e.toString());
                        }
                    }
                }
            };
            recyclerView.setOnItemMenuClickListener(itemMenuClickListener);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            listAdapter = new AppointmentList(getContext(), appointments);
            recyclerView.setAdapter(listAdapter);

            refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorBlue, R.color.colorGreen);
            refreshLayout.setOnRefreshListener(this);
            onRefresh();
        }
        return root;
    }

    @Override
    public void onRefresh() {
        try {
            Log.d("Appointment", (appointments==null)? "true":appointments.size()+"");
            listAdapter = new AppointmentList(getContext(), appointments);
            recyclerView.setAdapter(listAdapter);
        } catch (Exception e) {
            Log.d("appointRefreshException", e.toString());
        }
        refreshLayout.setRefreshing(false);
    }
}

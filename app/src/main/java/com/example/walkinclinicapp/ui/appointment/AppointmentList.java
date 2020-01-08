package com.example.walkinclinicapp.ui.appointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.walkinclinicapp.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;

import Appointment.Appointment;

public class AppointmentList extends SwipeRecyclerView.Adapter<AppointmentList.AppointmentViewHolder>{
    private Context context;
    private ArrayList<Appointment> appointments;

    // view holder class
    public static class AppointmentViewHolder extends SwipeRecyclerView.ViewHolder {
        private TextView appointmentName;
        private TextView appointmentDate;
        private TextView appointmentWaitTime;
        private TextView appointmentClinicName;

        private AppointmentViewHolder(View v) {
            super(v);
            appointmentName = (TextView) v.findViewById(R.id.textViewAppointmentServiceName);
            appointmentClinicName = (TextView) v.findViewById(R.id.textViewAppointmentClinicName);
            appointmentDate = (TextView) v.findViewById(R.id.textViewDate);
            appointmentWaitTime = (TextView) v.findViewById(R.id.textViewWaitingTime);
        }

        private void setAppointmentText(Appointment appointment) {
            appointmentName.setText(appointment.getServiceName());
            appointmentDate.setText("Date: " + appointment.getDate());
            appointmentWaitTime.setText("Waiting Time: " + appointment.getWaitingTime());
            appointmentClinicName.setText("at " + appointment.getClinicName());
        }
    }

    // constructor
    public AppointmentList(Context context, ArrayList<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    // Create new view (invoked by the layout manager)
    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_appointment, parent, false);
        AppointmentViewHolder appointmentViewHolder = new AppointmentViewHolder(view);
        return appointmentViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, int position) {
        // get element from dataset at this position
        // replace the contents of the view with that element
        holder.setAppointmentText(appointments.get(position));
    }

    // Return the size
    @Override
    public int getItemCount() {
        if (appointments == null){
            return 0;
        }
        return appointments.size();
    }
}

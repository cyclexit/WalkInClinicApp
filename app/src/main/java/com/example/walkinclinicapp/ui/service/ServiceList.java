package com.example.walkinclinicapp.ui.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.walkinclinicapp.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import Service.Service;

public class ServiceList extends SwipeRecyclerView.Adapter<ServiceList.ServiceViewHolder> {
    // instance
    private Context context;
    private ArrayList<Service> services;

    // view holder class
    public static class ServiceViewHolder extends SwipeRecyclerView.ViewHolder {
        private TextView serviceName;
        private TextView serviceProvider;
        private TextView serviceRate;

        private ServiceViewHolder(View v) {
            super(v);
            serviceName = (TextView) v.findViewById(R.id.textViewServiceName);
            serviceProvider = (TextView) v.findViewById(R.id.textViewServiceProvider);
            serviceRate = (TextView) v.findViewById(R.id.textViewServiceRate);
        }

        //
        private void setServiceText(Service service) {
            serviceName.setText(service.getServiceName());
            serviceProvider.setText("provided by " + service.getProviderRole());
            serviceRate.setText("Service Price: " + service.getRate());
        }
    }

    // constructor
    public ServiceList(Context context, ArrayList<Service> services) {
        this.context = context;
        this.services = services;
        notifyDataSetChanged();
    }

    // Create new view (invoked by the layout manager)
    @Override
    public ServiceList.ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_service, parent, false);
        ServiceViewHolder serviceViewHolder = new ServiceViewHolder(view);
        return serviceViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        // get element from dataset at this position
        // replace the contents of the view with that element
        holder.setServiceText(services.get(position));
    }

    // Return the size
    @Override
    public int getItemCount() {
        if (services == null){
            return 0;
        }
        return services.size();
    }
}
package com.example.walkinclinicapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.walkinclinicapp.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import java.util.ArrayList;
import Service.Service;

public class HomeServicesList extends SwipeRecyclerView.Adapter<HomeServicesList.ServiceViewHolder> {
    // instance
    private Context context;
    private ArrayList<Service> services;

    // view holder class
    public static class ServiceViewHolder extends SwipeRecyclerView.ViewHolder {
        private TextView serviceItem;

        private ServiceViewHolder(View v) {
            super(v);
            serviceItem = (TextView) v.findViewById(R.id.textViewSelectedListItem);
        }

        private void setServiceText(Service service) {
            serviceItem.setText(service.getServiceName());
        }
    }

    // constructor
    public HomeServicesList(Context context, ArrayList<Service> services) {
        this.context = context;
        this.services = services;
        notifyDataSetChanged();
    }

    // Create new view (invoked by the layout manager)
    @Override
    public HomeServicesList.ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_selected, parent, false);
        ServiceViewHolder serviceViewHolder = new ServiceViewHolder(view);
        return serviceViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
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

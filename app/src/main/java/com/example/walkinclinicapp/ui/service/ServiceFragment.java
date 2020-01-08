package com.example.walkinclinicapp.ui.service;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.walkinclinicapp.R;
import com.example.walkinclinicapp.ui.DividerVertical;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import Account.Administrator;
import Account.Employee;
import Account.Patient;
import Service.Service;

public class ServiceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRecyclerView recyclerView;
    private SwipeRecyclerView.Adapter listAdapter;
    private SwipeRefreshLayout refreshLayout;

    private DatabaseReference databaseServices;

    private Administrator admin;
    private Employee employee;
    private Patient patient;

    private ArrayList<Service> services;
    private boolean isAdmin, isEmployee, isPatient;
    private ServiceViewModel serviceViewModel;
    private TextView serviceStatus;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_service, container, false);

        // get account's type
        Bundle bundle = this.getActivity().getIntent().getExtras();
        if (bundle.getString("accountType").equals(Administrator.TYPE)) {
            isAdmin = true;
            admin = (Administrator) bundle.getSerializable(Administrator.TYPE);
            Log.d("Administrator", "yes");
        } else if (bundle.getString("accountType").equals(Employee.TYPE)) {
            isEmployee = true;
            employee = (Employee) bundle.getSerializable(Employee.TYPE);
            Log.d("Employee", "yes");
        } else {
            isPatient = true;
            patient = (Patient) bundle.getSerializable(Patient.TYPE);
            Log.d("Patient", "yes");
        }

        // initialize ui instances
        refreshLayout = root.findViewById(R.id.serviceRefreshLayout);
        recyclerView = root.findViewById(R.id.recyclerViewServices);
        serviceStatus = root.findViewById(R.id.textViewServiceStatus);
        FloatingActionButton addServiceButton = root.findViewById(R.id.floatActionButtonAddService);

        // observe data in view model
        services = new ArrayList<Service>();    // at the first login, the array will be null
        serviceViewModel = ViewModelProviders.of(getActivity()).get(ServiceViewModel.class);

        serviceViewModel.getServiceLiveData().observe(this, new Observer<ArrayList<Service>>() {
            @Override
            public void onChanged(ArrayList<Service> s) {
                services = s;
                onRefresh();
            }
        });

        databaseServices = serviceViewModel.getServiceReferenceLiveData().getValue();

        // set onClick listener for service list
        if (isAdmin) {
            // On click listener for add service button
            addServiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddServicePopup();
                }
            });
            addServiceButton.show();

            // create swipe menu for each item
            SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
                @Override
                public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                    // right modify button
                    SwipeMenuItem modifyService = new SwipeMenuItem(getContext())
                            .setTextColor(Color.parseColor("#ffffff"))
                            .setBackgroundColor(Color.parseColor("#33cc33"))
                            .setText("Modify")
                            .setTextSize(16)
                            .setHeight(200)
                            .setWidth(300);
                    rightMenu.addMenuItem(modifyService);

                    // right delete button
                    SwipeMenuItem deleteService = new SwipeMenuItem(getContext())
                            .setTextColor(Color.parseColor("#ffffff"))
                            .setBackgroundColor(Color.parseColor("#e60000"))
                            .setText("Delete")
                            .setTextSize(16)
                            .setHeight(200)
                            .setWidth(300);
                    rightMenu.addMenuItem(deleteService);
                }
            };
            recyclerView.setSwipeMenuCreator(swipeMenuCreator);

            // create swipe menu listener for each item
            OnItemMenuClickListener itemMenuClickListener = new OnItemMenuClickListener() {
                @Override
                public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                    menuBridge.closeMenu();
                    int direction = menuBridge.getDirection(); // leftMenu = 1; rightMenu = -1
                    int menuPosition = menuBridge.getPosition();
                    Service selectService = services.get(adapterPosition);
                    if (direction == -1 && menuPosition == 0) {
                        // right modify btn
                        try {
                            showModifyServicePopup(services.get(adapterPosition).getId());
                        } catch (Exception e) {
                            Log.d("modifyException", e.toString());
                        }
                    }
                    if (direction == -1 && menuPosition == 1) {
                        // right delete btn
                        try{
                            services.remove(adapterPosition);
                            admin.deleteService(databaseServices, selectService.getId());
                        }catch (Exception e){
                            Log.d("deleteException", e.toString());
                        }
                        onRefresh();
                    }
                }
            };
            recyclerView.setOnItemMenuClickListener(itemMenuClickListener);

        }else if (isEmployee) {
            SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
                @Override
                public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                    // right select btn
                    SwipeMenuItem selectService = new SwipeMenuItem(getContext())
                            .setTextColor(Color.parseColor("#ffffff"))
                            .setBackgroundColor(Color.parseColor("#33cc33"))
                            .setText("Select")
                            .setTextSize(16)
                            .setHeight(200)
                            .setWidth(300);
                    rightMenu.addMenuItem(selectService);

                    // right set service rate btn
                    SwipeMenuItem setRateService = new SwipeMenuItem(getContext())
                            .setTextColor(Color.parseColor("#ffffff"))
                            .setBackgroundColor(Color.parseColor("#e68a00"))
                            .setText("Set Price")
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
                    Service selectService = services.get(adapterPosition);
                    if (direction == -1 && menuPosition == 0) {
                        try {
                            employee.selectService(selectService);
                        } catch (Exception e) {
                            Log.d("selectException", e.toString());
                        }
                    }
                    if (direction == -1 && menuPosition == 1) {
                        try {
                            showSetRatePopup(selectService);
                        } catch (Exception e) {
                            Log.d("setRateException", e.toString());
                        }
                    }

                }
            };
            recyclerView.setOnItemMenuClickListener(itemMenuClickListener);

        }else if (isPatient) {
            SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
                @Override
                public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                    // right detail button
                    SwipeMenuItem setRateService = new SwipeMenuItem(getContext())
                            .setTextColor(Color.parseColor("#ffffff"))
                            .setBackgroundColor(Color.parseColor("#0052cc"))
                            .setText("Detail")
                            .setTextSize(16)
                            .setHeight(200)
                            .setWidth(300);
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
                    if (direction == -1 && menuPosition == 0) {
                        try {
                            Toast.makeText(getContext(), ("I don't know the detail"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.d("setRateException", e.toString());
                        }
                    }
                }
            };
            recyclerView.setOnItemMenuClickListener(itemMenuClickListener);
        }

        // ---------- init layout ----------
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listAdapter = new ServiceList(getContext(), services);
        recyclerView.setAdapter(listAdapter);
        recyclerView.addItemDecoration(new DividerVertical(getContext()));
        recyclerView.setEnabled(false);

        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorBlue, R.color.colorGreen);
        refreshLayout.setOnRefreshListener(this);
        onRefresh();
        return root;
    }

    @Override
    public void onRefresh() {
        // refresh
        if (services.size() != 0){
            serviceStatus.setVisibility(View.INVISIBLE);
        } else {
            serviceStatus.setText("Sorry! No service is available now.");
        }
        try {
            listAdapter = new ServiceList(getContext(), services);
            recyclerView.setAdapter(listAdapter);
        } catch (Exception e) {
            Log.d("serviceRefreshException", e.toString());
        }
        refreshLayout.setRefreshing(false);
    }

    // class methods
    private void showAddServicePopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_add_service, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextServiceNamePopup);
        final EditText editTextProvider  = (EditText) dialogView.findViewById(R.id.editTextServiceProviderPopup);
        final Button buttonAdd = (Button) dialogView.findViewById(R.id.addButtonPopup);

        dialogBuilder.setTitle("Add a service");
        final AlertDialog builder = dialogBuilder.create();
        builder.show();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String provider = editTextProvider.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(provider)) {
                    admin.addService(databaseServices, name, provider, -1);
                    builder.dismiss();
                }
            }
        });
        onRefresh();
    }

    private void showModifyServicePopup(String id) {
        final String service_id = id;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_modify_service, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextServiceNamePopup);
        final EditText editTextProvider  = (EditText) dialogView.findViewById(R.id.editTextServiceProviderPopup);
        final Button buttonModify = (Button) dialogView.findViewById(R.id.modifyButtonPopup);

        dialogBuilder.setTitle("Modify service");
        final AlertDialog builder = dialogBuilder.create();
        builder.show();

        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String provider = editTextProvider.getText().toString().trim();
                admin.editService(databaseServices, service_id, name, provider);
                builder.dismiss();
            }
        });
        onRefresh();
    }

    private void showSetRatePopup (final Service service) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_set_rate, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextRate = (EditText) dialogView.findViewById(R.id.editTextSetRate);
        final Button buttonModify = (Button) dialogView.findViewById(R.id.setRateButtonPopup);

        dialogBuilder.setTitle("Set rate for service");
        final AlertDialog builder = dialogBuilder.create();
        builder.show();

        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rate = Integer.parseInt(editTextRate.getText().toString().trim());
                employee.rateService(databaseServices, service, rate);
                builder.dismiss();
            }
        });
        onRefresh();
    }
}
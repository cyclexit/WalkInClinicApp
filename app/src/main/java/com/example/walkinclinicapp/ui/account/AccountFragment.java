package com.example.walkinclinicapp.ui.account;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import Account.*;

public class AccountFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    // instants
    private SwipeRecyclerView recyclerViewEmployee;
    private SwipeRecyclerView recyclerViewPatient;
    private SwipeRecyclerView.Adapter listAdapterEmployee;
    private SwipeRecyclerView.Adapter listAdapterPatient;
    private SwipeRefreshLayout refreshLayout;

    private DatabaseReference employeeDBReference;
    private DatabaseReference patientDBReference;

    private Administrator admin;

    private ArrayList<Account> accountsEmployee;
    private ArrayList<Account> accountsPatient;
    private ArrayList<Employee> employees;
    private ArrayList<Patient> patients;
;
    private AccountViewModel accountViewModel;
    private TextView accountStatus;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        // initialize admin instance
        Bundle bundle = this.getActivity().getIntent().getExtras();
        admin = (Administrator) bundle.getSerializable(Administrator.TYPE);

        // if not admin, do nothing
        if (admin != null){
            // find UI components
            accountStatus = root.findViewById(R.id.textViewAccountStatus);
            refreshLayout = root.findViewById(R.id.accountRefreshLayout);
            recyclerViewEmployee = root.findViewById(R.id.recyclerViewEmployee);
            recyclerViewPatient = root.findViewById(R.id.recyclerViewPatient);

            accountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
            accountViewModel.employeesLiveData.observe(this, new Observer<ArrayList<Employee>>() {
                @Override
                public void onChanged(ArrayList<Employee> e) {
                    employees = e;
                    onRefresh();
                }
            });
            accountViewModel.patientsLiveData.observe(this, new Observer<ArrayList<Patient>>() {
                @Override
                public void onChanged(ArrayList<Patient> p) {
                    patients = p;
                    onRefresh();
                }
            });

            employeeDBReference = accountViewModel.getEmployeeDBReferenceLD().getValue();
            patientDBReference = accountViewModel.getPatientDBReferenceLD().getValue();

            accountsEmployee = new ArrayList<Account>();
            accountsPatient = new ArrayList<Account>();
            employees = new ArrayList<Employee>();
            patients = new ArrayList<Patient>();
            accountsEmployee.addAll(employees);
            accountsPatient.addAll(patients);

            accountStatus = root.findViewById(R.id.textViewAccountStatus);

            // init data
            if (accountsPatient.size() != 0 || accountsEmployee.size() != 0) {
                accountStatus.setVisibility(View.GONE);
            } else {
                accountStatus.setText(String.format("%s", "There is no other account currently"));
            }

            // ---------- create item menu ----------
            SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
                @Override
                public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                    // right delete button
                    SwipeMenuItem deleteAccount = new SwipeMenuItem(getContext())
                            .setTextColor(Color.parseColor("#ffffff"))
                            .setBackgroundColor(Color.parseColor("#e60000"))
                            .setText("Delete")
                            .setTextSize(16)
                            .setHeight(200)
                            .setWidth(300);
                    rightMenu.addMenuItem(deleteAccount);
                }
            };
            recyclerViewEmployee.setSwipeMenuCreator(swipeMenuCreator);
            recyclerViewPatient.setSwipeMenuCreator(swipeMenuCreator);

            // ---------- listeners ----------
            recyclerViewEmployee.setOnItemMenuClickListener(new OnItemMenuClickListener() {
                @Override
                public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                    menuBridge.closeMenu();
                    int direction = menuBridge.getDirection(); // leftMenu = 1; rightMenu = -1
                    int menuPosition = menuBridge.getPosition();
                    Account selectAccount = accountsEmployee.get(adapterPosition);
                    Log.d("Position", adapterPosition+"");
                    if (direction == -1 && menuPosition == 0) {
                        // delete
                        try{
                            accountsEmployee.remove(adapterPosition);
                            admin.deleteAccount(employeeDBReference, selectAccount.getId());
                            onRefresh();
                            Toast.makeText(getContext(), ("Deleted"), Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Log.d("Exception", e.toString());
                        }
                        onRefresh();
                    }
                }
            });
            recyclerViewPatient.setOnItemMenuClickListener(new OnItemMenuClickListener() {
                @Override
                public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                    menuBridge.closeMenu();
                    int direction = menuBridge.getDirection(); // leftMenu = 1; rightMenu = -1
                    int menuPosition = menuBridge.getPosition();
                    Account selectAccount = accountsPatient.get(adapterPosition);
                    Log.d("Position", adapterPosition+"");
                    if (direction == -1 && menuPosition == 0) {
                        // delete
                        try{
                            accountsPatient.remove(adapterPosition);
                            admin.deleteAccount(patientDBReference, selectAccount.getId());
                            onRefresh();
                            Toast.makeText(getContext(), ("Deleted"), Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Log.d("Exception", e.toString());
                        }
                        onRefresh();
                    }
                }
            });

            // ---------- init layout ----------
            recyclerViewEmployee.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerViewPatient.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerViewEmployee.addItemDecoration(new DividerVertical(getContext()));
            recyclerViewPatient.addItemDecoration(new DividerVertical(getContext()));
            listAdapterEmployee = new AccountList(getContext(), accountsEmployee);
            listAdapterPatient = new AccountList(getContext(), accountsPatient);
            recyclerViewEmployee.setAdapter(listAdapterEmployee);
            recyclerViewPatient.setAdapter(listAdapterPatient);
            recyclerViewEmployee.setEnabled(false);
            recyclerViewPatient.setEnabled(false);

            refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorBlue, R.color.colorGreen);
            refreshLayout.setOnRefreshListener(this);
            refreshLayout.setRefreshing(false);
        }
        return root;
    }

    @Override
    public void onRefresh() {
        // refresh
        accountsEmployee.clear();
        accountsPatient.clear();
        accountsEmployee.addAll(employees);
        accountsPatient.addAll(patients);

        if (accountsPatient.size() != 0 || accountsEmployee.size() != 0) {
            accountStatus.setVisibility(View.GONE);
        } else {
            accountStatus.setText("Sorry! No account is available now.");
        }
        try {
            listAdapterEmployee = new AccountList(getContext(), accountsEmployee);
            listAdapterPatient = new AccountList(getContext(), accountsPatient);
            recyclerViewEmployee.setAdapter(listAdapterEmployee);
            recyclerViewPatient.setAdapter(listAdapterPatient);
        } catch (Exception e) {
            Log.d("refreshException", e.toString());
        }
        refreshLayout.setRefreshing(false);
    }
}
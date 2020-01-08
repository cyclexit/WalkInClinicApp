package com.example.walkinclinicapp.ui.home;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.walkinclinicapp.R;
import com.example.walkinclinicapp.ui.DividerVertical;
import com.example.walkinclinicapp.ui.service.ServiceViewModel;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Account.*;
import Appointment.Appointment;
import Rating.Rate;
import Service.Service;
import Account.ClinicProfile.*;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // Views
    private PopupWindow dropDownList;
    private SwipeRecyclerView selectedServicesList;
    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;
    private ListView searchResultList;
    private ListView searchByServiceResultList;
    private ListView clinicsList;
    private ListView ratingListView;

    private Button onClickCreateProfile;
    private Button onClickSaveWorkingHour;
    private Button onClickRateClinic;
    private RadioButton findByNameBtn;
    private RadioButton findByAddressBtn;
    private RadioButton findByWorkingHourBtn;
    private RadioButton findByServiceBtn;

    private CardView profileCard;
    private CardView searchCard;
    private CardView searchResultCard;
    private CardView workingHourCard;
    private CardView selectedServiceCard;
    private CardView searchByServiceCard;
    private CardView cardViewRating;

    private TextView startTimeMondaySelect;
    private TextView endTimeMondaySelect;
    private TextView startTimeMondayShow;
    private TextView endTimeMondayShow;
    private TextView startTimeTuesdaySelect;
    private TextView endTimeTuesdaySelect;
    private TextView startTimeTuesdayShow;
    private TextView endTimeTuesdayShow;
    private TextView startTimeWednesdaySelect;
    private TextView endTimeWednesdaySelect;
    private TextView startTimeWednesdayShow;
    private TextView endTimeWednesdayShow;
    private TextView startTimeThursdaySelect;
    private TextView endTimeThursdaySelect;
    private TextView startTimeThursdayShow;
    private TextView endTimeThursdayShow;
    private TextView startTimeFridaySelect;
    private TextView endTimeFridaySelect;
    private TextView startTimeFridayShow;
    private TextView endTimeFridayShow;

    private TextView timeSetAlert;
    private TextView nullProfileAlert;
    private TextView clinicNameShow;
    private TextView phoneShow;
    private TextView addressShow;
    private TextView insuranceNameShow;
    private TextView paymentShow;
    private TextView clinicsCount;

    private SwipeRecyclerView.Adapter listAdapter;
    private ArrayAdapter<String> searchAdapter;

    private ArrayList<Service> availableService;
    private ArrayList<String> paymentChoices;
    private ArrayList<Service> selectedServices;
    private ArrayList<String> startTimeChoices;
    private ArrayList<String> endTimeChoices;
    private ArrayList<Employee> employees;
    private ArrayList<String> employeesName;
    private ArrayList<String> employeesAddress;
    private ArrayList<String> employeesTimeStrings;
    private ArrayList<Employee> employeesSearchedByTime;
    private ArrayList<String> employeeServicesStrings;
    private ArrayList<Employee> employeesSearchedByService;
    private ArrayList<Rate> ratingList;

    private int year, month, day;
    private String chosenDate;
    private String chosenTime;
    private String chosenService;
    private String dayOfWeek;

    private ClinicProfile clinicProfile;

    private Administrator admin;
    private Employee employee;
    private Employee selectedEmployee;
    private Patient patient;
    private HomeViewModel homeViewModel;

    private boolean isAdmin, isEmployee, isPatient, isNewUser,
            isSearchByName, isSearchByAddress, isSearchByTime, isSearchByService;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Bundle bundle = this.getActivity().getIntent().getExtras();
        isNewUser = true;
        assert bundle != null;
        if (bundle.getString("accountType").equals(Administrator.TYPE)) {
            isAdmin = true;
            admin = (Administrator) bundle.getSerializable(Administrator.TYPE);
            // Log.d("Administrator", "true");
        } else if (bundle.getString("accountType").equals(Employee.TYPE)) {
            isEmployee = true;
            employee = (Employee) bundle.getSerializable(Employee.TYPE);
            // Log.d("Employee", "true");
        } else {
            isPatient = true;
            patient = (Patient) bundle.getSerializable(Patient.TYPE);
            // Log.d("Patient", "true");
        }

        // find ui component's id
        selectedServicesList = root.findViewById(R.id.recyclerViewSelectedServices);
        timeSetAlert = root.findViewById(R.id.textViewTimeSetAlert);
        nullProfileAlert = root.findViewById(R.id.textViewNullProfileAlert);
        clinicNameShow = root.findViewById(R.id.textViewClinicName);
        phoneShow = root.findViewById(R.id.textViewPhone);
        addressShow = root.findViewById(R.id.textViewAddressHome);
        insuranceNameShow = root.findViewById(R.id.textViewInsuranceType);
        paymentShow = root.findViewById(R.id.textViewPaymentType);
        onClickCreateProfile = (Button) root.findViewById(R.id.buttonCreateProfile);
        onClickSaveWorkingHour = (Button) root.findViewById(R.id.buttonSaveTime);
        onClickRateClinic = (Button) root.findViewById(R.id.buttonPatientRate);
        refreshLayout = root.findViewById(R.id.selectedRefreshLayout);
        searchResultCard = root.findViewById(R.id.cardViewSearchResult);
        searchResultList = root.findViewById(R.id.searchListView);
        searchByServiceCard = root.findViewById(R.id.cardViewByServiceSearchResult);
        searchByServiceResultList = root.findViewById(R.id.searchByServiceListView);
        searchCard = root.findViewById(R.id.cardViewSearch);
        profileCard = root.findViewById(R.id.cardViewProfile);
        workingHourCard = root.findViewById(R.id.cardViewWorkingHour);
        selectedServiceCard = root.findViewById(R.id.cardViewSelectedServices);
        ratingListView = root.findViewById(R.id.listViewRating);
        cardViewRating = root.findViewById(R.id.cardViewRating);

        startTimeMondaySelect = root.findViewById(R.id.textViewMondaySelectStart);
        endTimeMondaySelect = root.findViewById(R.id.textViewMondaySelectEnd);
        startTimeMondayShow = root.findViewById(R.id.textViewMondayStartShow);
        endTimeMondayShow = root.findViewById(R.id.textViewMondayEndShow);

        startTimeTuesdaySelect = root.findViewById(R.id.textViewTuesdaySelectStart);
        endTimeTuesdaySelect = root.findViewById(R.id.textViewTuesdaySelectEnd);
        startTimeTuesdayShow = root.findViewById(R.id.textViewTuesdayStartShow);
        endTimeTuesdayShow = root.findViewById(R.id.textViewTuesdayEndShow);

        startTimeWednesdaySelect = root.findViewById(R.id.textViewWednesdaySelectStart);
        endTimeWednesdaySelect = root.findViewById(R.id.textViewWednesdaySelectEnd);
        startTimeWednesdayShow = root.findViewById(R.id.textViewWednesdayStartShow);
        endTimeWednesdayShow = root.findViewById(R.id.textViewWednesdayEndShow);

        startTimeThursdaySelect = root.findViewById(R.id.textViewThursdaySelectStart);
        endTimeThursdaySelect = root.findViewById(R.id.textViewThursdaySelectEnd);
        startTimeThursdayShow = root.findViewById(R.id.textViewThursdayStartShow);
        endTimeThursdayShow = root.findViewById(R.id.textViewThursdayEndShow);

        startTimeFridaySelect = root.findViewById(R.id.textViewFridaySelectStart);
        endTimeFridaySelect = root.findViewById(R.id.textViewFridaySelectEnd);
        startTimeFridayShow = root.findViewById(R.id.textViewFridayStartShow);
        endTimeFridayShow = root.findViewById(R.id.textViewFridayEndShow);

        // init calender
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // create swipe menu for selected list's item
        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                // right modify button
                if (isEmployee){
                    SwipeMenuItem unselectService = new SwipeMenuItem(getContext())
                            .setTextColor(Color.parseColor("#ffffff"))
                            .setBackgroundColor(Color.parseColor("#e68a00"))
                            .setText("Unselect")
                            .setTextSize(16)
                            .setHeight(200)
                            .setWidth(300);
                    rightMenu.addMenuItem(unselectService);
                }
                if (isPatient){
                    SwipeMenuItem bookService = new SwipeMenuItem(getContext())
                            .setTextColor(Color.parseColor("#ffffff"))
                            .setBackgroundColor(Color.parseColor("#33cc33"))
                            .setText("Book")
                            .setTextSize(16)
                            .setHeight(200)
                            .setWidth(300);
                    rightMenu.addMenuItem(bookService);
                }
            }
        };
        selectedServicesList.setSwipeMenuCreator(swipeMenuCreator);

        // set listener for swipe menu
        OnItemMenuClickListener itemMenuClickListener = new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                menuBridge.closeMenu();
                int direction = menuBridge.getDirection(); // leftMenu = 1; rightMenu = -1
                int menuPosition = menuBridge.getPosition();
                Service selectService = selectedServices.get(adapterPosition);
                if (direction == -1 && menuPosition == 0) {
                    try {
                        if (isEmployee){
                            employee.unselectService(selectService);
                        }else if (isPatient){
                            showBookingPopup(selectService);
                        }
                    } catch (Exception e) {
                        Log.d("unselectOrBookException", e.toString());
                    }
                    onRefresh();
                }
            }
        };
        selectedServicesList.setOnItemMenuClickListener(itemMenuClickListener);

        // init selected services list
        selectedServicesList.setLayoutManager(new LinearLayoutManager(getContext()));
        selectedServicesList.addItemDecoration(new DividerVertical(getContext()));
        ratingListView.setEnabled(false);

        // init refresh layout
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorBlue, R.color.colorGreen);
        refreshLayout.setOnRefreshListener(this);

        // Main
        if(isEmployee) {
            // TODO: Update local clinicProfile after modified it
            // show/hide ui components
            startTimeMondaySelect.setVisibility(View.VISIBLE);
            endTimeMondaySelect.setVisibility(View.VISIBLE);
            startTimeMondayShow.setVisibility(View.GONE);
            endTimeMondayShow.setVisibility(View.GONE);
            startTimeTuesdaySelect.setVisibility(View.VISIBLE);
            endTimeTuesdaySelect.setVisibility(View.VISIBLE);
            startTimeTuesdayShow.setVisibility(View.GONE);
            endTimeTuesdayShow.setVisibility(View.GONE);
            startTimeWednesdaySelect.setVisibility(View.VISIBLE);
            endTimeWednesdaySelect.setVisibility(View.VISIBLE);
            startTimeWednesdayShow.setVisibility(View.GONE);
            endTimeWednesdayShow.setVisibility(View.GONE);
            startTimeThursdaySelect.setVisibility(View.VISIBLE);
            endTimeThursdaySelect.setVisibility(View.VISIBLE);
            startTimeThursdayShow.setVisibility(View.GONE);
            endTimeThursdayShow.setVisibility(View.GONE);
            startTimeFridaySelect.setVisibility(View.VISIBLE);
            endTimeFridaySelect.setVisibility(View.VISIBLE);
            startTimeFridayShow.setVisibility(View.GONE);
            endTimeFridayShow.setVisibility(View.GONE);
            initClinicInfo();
        }else{
            searchView = root.findViewById(R.id.searchView);
            findByNameBtn = root.findViewById(R.id.radioButtonByName);
            findByAddressBtn = root.findViewById(R.id.radioButtonByAddress);
            findByWorkingHourBtn = root.findViewById(R.id.radioButtonByWorkingHour);
            findByServiceBtn = root.findViewById(R.id.radioButtonByService);
            // show/hide ui components
            profileCard.setVisibility(View.GONE);
            workingHourCard.setVisibility(View.GONE);
            selectedServiceCard.setVisibility(View.GONE);
            onClickCreateProfile.setVisibility(View.GONE);
            onClickSaveWorkingHour.setVisibility(View.GONE);
            nullProfileAlert.setVisibility(View.GONE);
            timeSetAlert.setVisibility(View.GONE);
            if (isAdmin){
                cardViewRating.setVisibility(View.GONE);
                CardView clinicsCard = root.findViewById(R.id.clinicsforAdminCard);
                clinicsCount = root.findViewById(R.id.textViewClinicsAdmin);
                clinicsList = root.findViewById(R.id.clinicsList);
                clinicsCard.setVisibility(View.VISIBLE);
                clinicsCount.setVisibility(View.VISIBLE);

                homeViewModel = new HomeViewModel();
                homeViewModel.getEmployeesMutableLiveData().observe(this,
                        new Observer<ArrayList<Employee>>() {
                            @Override
                            public void onChanged(ArrayList<Employee> e) {
                                employeesName = new ArrayList<String>();
                                for (Employee employee: e){
                                    if (employee.getClinicProfile().getClinicName() != null){
                                        employeesName.add(employee.getClinicProfile().getClinicName());
                                    }
                                }
                                searchAdapter = new ArrayAdapter<String>(getContext(),
                                        android.R.layout.simple_list_item_1, employeesName);
                                clinicsList.setAdapter(searchAdapter);
                                if (e.size() != employeesName.size()){
                                    clinicsCount.setText("Hi admin! You are managing " + e.size() + " clinics now.\nWith "
                                            + (e.size()-employeesName.size()) + " clinic's profile is null.");
                                }else {
                                    clinicsCount.setText("Hi admin! You are managing " + e.size() + " clinics now.");
                                }
                            }
                        });
            }
            if (isPatient){
                cardViewRating.setVisibility(View.GONE);
                searchCard.setVisibility(View.VISIBLE);
                startTimeMondayShow.setVisibility(View.VISIBLE);
                endTimeMondayShow.setVisibility(View.VISIBLE);
                startTimeMondaySelect.setVisibility(View.INVISIBLE);
                endTimeMondaySelect.setVisibility(View.INVISIBLE);
                startTimeTuesdayShow.setVisibility(View.VISIBLE);
                endTimeTuesdayShow.setVisibility(View.VISIBLE);
                startTimeTuesdaySelect.setVisibility(View.INVISIBLE);
                endTimeTuesdaySelect.setVisibility(View.INVISIBLE);
                startTimeWednesdayShow.setVisibility(View.VISIBLE);
                endTimeWednesdayShow.setVisibility(View.VISIBLE);
                startTimeWednesdaySelect.setVisibility(View.INVISIBLE);
                endTimeWednesdaySelect.setVisibility(View.INVISIBLE);
                startTimeThursdayShow.setVisibility(View.VISIBLE);
                endTimeThursdayShow.setVisibility(View.VISIBLE);
                startTimeThursdaySelect.setVisibility(View.INVISIBLE);
                endTimeThursdaySelect.setVisibility(View.INVISIBLE);
                startTimeFridayShow.setVisibility(View.VISIBLE);
                endTimeFridayShow.setVisibility(View.VISIBLE);
                startTimeFridaySelect.setVisibility(View.INVISIBLE);
                endTimeFridaySelect.setVisibility(View.INVISIBLE);
                onClickRateClinic.setVisibility(View.VISIBLE);
                initPatientInfo();
            }
        }
        // show rating if is patient or employee
        return root;
    }

    @Override
    public void onRefresh() {
        try {
            if (isEmployee) {
                if (!isNewUser) {
                    onClickCreateProfile.setText("Modify");
                }
                try {
                    clinicProfile = employee.getClinicProfile();
                }catch (NullPointerException e){
                    isNewUser = true;
                }
                setTextViews();
            } else if (isPatient) {
                if (isSearchByName || isSearchByAddress) {
                    employeesName.clear();
                    employeesAddress.clear();
                    for (Employee e : employees) {
                        employeesName.add(e.getClinicProfile().getClinicName());
                        employeesAddress.add(e.getClinicProfile().getAddress());
                    }
                }
                else if (isSearchByTime) {
                    employeesTimeStrings.clear();
                    employeesSearchedByTime = patient.
                            searchClinicByWorkingHour(dayOfWeek, chosenTime, employees);
                    if (employeesSearchedByTime.size() == 0) {
                        Toast.makeText(getContext(), ("No clinic is available at this time"),
                                Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Size", employeesSearchedByTime.size()+"");
                    for (Employee e : employeesSearchedByTime) {
                        employeesTimeStrings.add(e.getClinicProfile().getStartTime("Monday") + " - "
                                + e.getClinicProfile().getEndTime("Monday"));
                    }
                }
                else if (isSearchByService) {
                    employeeServicesStrings.clear();
                    for (Service s : availableService) {
                        employeeServicesStrings.add(s.getServiceName());
                    }
                    searchAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_list_item_1, employeeServicesStrings);
                    searchResultList.setAdapter(searchAdapter);
                }
                try {
                    clinicProfile = selectedEmployee.getClinicProfile();
                    setTextViews();
                    startTimeMondayShow.setText(clinicProfile.getStartTime("Monday"));
                    endTimeMondayShow.setText(clinicProfile.getEndTime("Monday"));
                    startTimeTuesdayShow.setText(clinicProfile.getStartTime("Tuesday"));
                    endTimeTuesdayShow.setText(clinicProfile.getEndTime("Tuesday"));
                    startTimeWednesdayShow.setText(clinicProfile.getStartTime("Wednesday"));
                    endTimeWednesdayShow.setText(clinicProfile.getEndTime("Wednesday"));
                    startTimeThursdayShow.setText(clinicProfile.getStartTime("Thursday"));
                    endTimeThursdayShow.setText(clinicProfile.getEndTime("Thursday"));
                    startTimeFridayShow.setText(clinicProfile.getStartTime("Friday"));
                    endTimeFridayShow.setText(clinicProfile.getEndTime("Friday"));
                    setListViewHeightBasedOnChildren(ratingListView);
                } catch (Exception e) {
                    Log.d("refreshExceptionInner", e.toString());
                }
            }
            HomeRatingList ratingAdapter = new HomeRatingList(getLayoutInflater(), ratingList);
            ratingListView.setAdapter(ratingAdapter);
            setListViewHeightBasedOnChildren(ratingListView);
            listAdapter = new HomeServicesList(getContext(), selectedServices);
            selectedServicesList.setAdapter(listAdapter);
        }catch (Exception e){
            Log.d("refreshException", e.toString());
        }
        refreshLayout.setRefreshing(false);
    }

    // init UI for employee
    private void initClinicInfo(){
        // fetch database's data
        homeViewModel = new HomeViewModel(employee.getId());
        homeViewModel.getSelectedServiceLiveData().observe(this,
                new Observer<ArrayList<Service>>() {
                    @Override
                    public void onChanged(ArrayList<Service> services) {
                        selectedServices = services;
                        onRefresh();
                    }
                });

        ratingList = new ArrayList<Rate>();
        homeViewModel.getRatesMutableLiveData().observe(this,
                new Observer<ArrayList<Rate>>() {
                    @Override
                    public void onChanged(ArrayList<Rate> rates) {
                        ratingList = rates;
                        onRefresh();
                    }
                });

        // find out new employee or not
        clinicProfile = employee.getClinicProfile();
        if (clinicProfile.getClinicName() != null) {
            // set up profile's info
            isNewUser = false;
            nullProfileAlert.setVisibility(View.GONE);
            onClickCreateProfile.setText("Modify");
            Log.d("isNew", "false");
            // display info
            clinicNameShow.setText(clinicProfile.getClinicName());
            phoneShow.setText(clinicProfile.getPhoneNum());
            addressShow.setText(clinicProfile.getAddress());
            insuranceNameShow.setText(clinicProfile.getInsurance().toString() + " insurance");
            paymentShow.setText(clinicProfile.getPayment().toString());
            // init working hour
            try {
                if (clinicProfile.getStartTime("Monday") != null
                        && clinicProfile.getStartTime("Tuesday") != null
                        && clinicProfile.getStartTime("Wednesday") != null
                        && clinicProfile.getStartTime("Tuesday") != null
                        && clinicProfile.getStartTime("Friday") != null) {
                    timeSetAlert.setVisibility(View.GONE);
                    startTimeMondaySelect.setText(clinicProfile.getStartTime("Monday"));
                    endTimeMondaySelect.setText(clinicProfile.getEndTime("Monday"));
                    startTimeTuesdaySelect.setText(clinicProfile.getStartTime("Tuesday"));
                    endTimeTuesdaySelect.setText(clinicProfile.getEndTime("Tuesday"));
                    startTimeWednesdaySelect.setText(clinicProfile.getStartTime("Wednesday"));
                    endTimeWednesdaySelect.setText(clinicProfile.getEndTime("Wednesday"));
                    startTimeThursdaySelect.setText(clinicProfile.getStartTime("Tuesday"));
                    endTimeThursdaySelect.setText(clinicProfile.getEndTime("Tuesday"));
                    startTimeFridaySelect.setText(clinicProfile.getStartTime("Friday"));
                    endTimeFridaySelect.setText(clinicProfile.getEndTime("Friday"));
                } else {
                    timeSetAlert.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), ("Please set your working hour"), Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                Log.d("TimeSetError", "Cannot set start/end time text");
            }
        } else if (clinicProfile.getStartTime("Monday") != null
                && clinicProfile.getEndTime("Monday") != null
                && clinicProfile.getStartTime("Tuesday") != null
                && clinicProfile.getEndTime("Tuesday") != null
                && clinicProfile.getStartTime("Wednesday") != null
                && clinicProfile.getEndTime("Wednesday") != null
                && clinicProfile.getStartTime("Tuesday") != null
                && clinicProfile.getEndTime("Tuesday") != null
                && clinicProfile.getStartTime("Friday") != null
                && clinicProfile.getEndTime("Friday") != null) {
            // show if start/end time is existed
            timeSetAlert.setVisibility(View.GONE);
            startTimeMondaySelect.setText(clinicProfile.getStartTime("Monday"));
            endTimeMondaySelect.setText(clinicProfile.getEndTime("Monday"));
            startTimeTuesdaySelect.setText(clinicProfile.getStartTime("Tuesday"));
            endTimeTuesdaySelect.setText(clinicProfile.getEndTime("Tuesday"));
            startTimeWednesdaySelect.setText(clinicProfile.getStartTime("Wednesday"));
            endTimeWednesdaySelect.setText(clinicProfile.getEndTime("Wednesday"));
            startTimeThursdaySelect.setText(clinicProfile.getStartTime("Tuesday"));
            endTimeThursdaySelect.setText(clinicProfile.getEndTime("Tuesday"));
            startTimeFridaySelect.setText(clinicProfile.getStartTime("Friday"));
            endTimeFridaySelect.setText(clinicProfile.getEndTime("Friday"));
        } else {
            Log.d("NullClinicProfile", "clinic profile is null");
        }

        addTimeChoices();
        // set listener for startTime choices list
        startTimeMondaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for start time
                createChoiceList(getContext(), startTimeMondaySelect, startTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(startTimeMondaySelect, 0, 10);
                }
            }
        });

        // set listener for endTime choices list
        endTimeMondaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for end time
                createChoiceList(getContext(), endTimeMondaySelect, endTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(endTimeMondaySelect, 0, 10);
                }
            }
        });

        // set listener for startTime choices list
        startTimeTuesdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for start time
                createChoiceList(getContext(), startTimeTuesdaySelect, startTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(startTimeTuesdaySelect, 0, 10);
                }
            }
        });

        // set listener for endTime choices list
        endTimeTuesdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for end time
                createChoiceList(getContext(), endTimeTuesdaySelect, endTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(endTimeTuesdaySelect, 0, 10);
                }
            }
        });

        // set listener for startTime choices list
        startTimeWednesdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for start time
                createChoiceList(getContext(), startTimeWednesdaySelect, startTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(startTimeWednesdaySelect, 0, 10);
                }
            }
        });

        // set listener for endTime choices list
        endTimeWednesdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for end time
                createChoiceList(getContext(), endTimeWednesdaySelect, endTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(endTimeWednesdaySelect, 0, 10);
                }
            }
        });

        // set listener for startTime choices list
        startTimeThursdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for start time
                createChoiceList(getContext(), startTimeThursdaySelect, startTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(startTimeThursdaySelect, 0, 10);
                }
            }
        });

        // set listener for endTime choices list
        endTimeThursdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for end time
                createChoiceList(getContext(), endTimeThursdaySelect, endTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(endTimeThursdaySelect, 0, 10);
                }
            }
        });

        // set listener for startTime choices list
        startTimeFridaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for start time
                createChoiceList(getContext(), startTimeFridaySelect, startTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(startTimeFridaySelect, 0, 10);
                }
            }
        });

        // set listener for endTime choices list
        endTimeFridaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup list for end time
                createChoiceList(getContext(), endTimeFridaySelect, endTimeChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(endTimeFridaySelect, 0, 10);
                }
            }
        });

        // set listener for create profile btn
        onClickCreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePopup();
            }
        });

        // set listener for save working hour btn
        onClickSaveWorkingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkingHour();
            }
        });
    }

    // init UI for patient
    private void initPatientInfo(){
        // initially expand the search view
        searchView.onActionViewExpanded();
        // obtain the editText from searchView
        int searchViewPlateId = searchView.getContext().getResources().
                getIdentifier("android:id/search_src_text", null, null);
        final EditText searchPlateEditText = (EditText) searchView.findViewById(searchViewPlateId);
        searchPlateEditText.setEnabled(false);

        // obtain clinics' data & available services
        employees = new ArrayList<Employee>();
        homeViewModel = new HomeViewModel();
        if (homeViewModel.readState() != null){
            Log.d("Yes", "yes");
            selectedEmployee = homeViewModel.readState();
            clinicProfile = selectedEmployee.getClinicProfile();
            onRefresh();
        }
        homeViewModel.getEmployeesMutableLiveData().observe(this,
                new Observer<ArrayList<Employee>>() {
                    @Override
                    public void onChanged(ArrayList<Employee> e) {
                        for (Employee employee:e){
                            if (employee.getClinicProfile().getClinicName()!=null){
                                employees.add(employee);
                            }
                        }
                        onRefresh();
                    }
                });
        ServiceViewModel serviceViewModel = new ServiceViewModel();
        serviceViewModel.getServiceLiveData().observe(this,
                new Observer<ArrayList<Service>>() {
            @Override
            public void onChanged(ArrayList<Service> services) {
                availableService = services;
                onRefresh();
            }
        });

        try {
            employeesName = new ArrayList<String>();
            employeesAddress = new ArrayList<String>();
            employeesTimeStrings = new ArrayList<String>();
            employeeServicesStrings = new ArrayList<String>();
            searchResultList.setTextFilterEnabled(true);

            // onClick for search by name
            findByNameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchPlateEditText.setEnabled(true);
                    isSearchByName = true; isSearchByAddress = false;
                    isSearchByTime = false; isSearchByService = false;
                    searchAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_list_item_1, employeesName);
                    searchResultList.setAdapter(searchAdapter);
                    searchResultCard.setVisibility(View.VISIBLE);
                    searchByServiceCard.setVisibility(View.GONE);
                    onRefresh();
                }
            });
            // onClick for search by address
            findByAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchPlateEditText.setEnabled(true);
                    isSearchByName = false; isSearchByAddress = true;
                    isSearchByTime = false; isSearchByService = false;
                    searchAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_list_item_1, employeesAddress);
                    searchResultList.setAdapter(searchAdapter);
                    searchResultCard.setVisibility(View.VISIBLE);
                    searchByServiceCard.setVisibility(View.GONE);
                    onRefresh();
                }
            });

            // onClick for search by time
            findByWorkingHourBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do not allow user to modify selected time
                    searchPlateEditText.setEnabled(false);
                    isSearchByName = false; isSearchByAddress = false;
                    isSearchByTime = true; isSearchByService = false;
                    searchResultCard.setVisibility(View.GONE);
                    searchByServiceCard.setVisibility(View.GONE);
                    showTimePicker(1);
                    showDatePicker(2, null);
                }
            });

            // onClick for search by service
            findByServiceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchPlateEditText.setEnabled(true);
                    isSearchByName = false; isSearchByAddress = false;
                    isSearchByTime = false; isSearchByService = true;
                    onRefresh();
                    searchResultCard.setVisibility(View.VISIBLE);
                    searchByServiceCard.setVisibility(View.GONE);   // gone for the second searched
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (isSearchByService){
                        searchByServiceCard.setVisibility(View.GONE);
                        searchAdapter.getFilter().filter(newText);
                    }else if (!isSearchByTime){
                        searchResultCard.setVisibility(View.VISIBLE);
                        searchAdapter.getFilter().filter(newText);
                    }
                    return false;
                }
            });

            // when user click on a clinic
            searchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    searchView.setQuery("",true);
                    searchResultCard.setVisibility(View.GONE);
                    searchView.clearFocus();
                    if (isSearchByName){
                        String name = searchResultList.getItemAtPosition(position).toString();
                        selectedEmployee = patient.searchClinicByName(name, employees);
                        showSelectedClinic();
                    }else if (isSearchByAddress){
                        String address = searchResultList.getItemAtPosition(position).toString();
                        selectedEmployee = patient.searchClinicByAddress(address, employees);
                        showSelectedClinic();
                    }else if (isSearchByTime){
                        selectedEmployee = employeesSearchedByTime.get(position);
                        showSelectedClinic();
                    }else if (isSearchByService){
                        searchResultCard.setVisibility(View.GONE);
                        chosenService = searchResultList.getItemAtPosition(position).toString();
                        employeesSearchedByService = patient.
                                searchClinicByService(chosenService, employees);
                        Log.d("byService", employeesSearchedByService.size()+"");
                        ArrayList<String> tempString = new ArrayList<String>();
                        for (Employee e:employeesSearchedByService){
                            tempString.add(e.getClinicProfile().getClinicName());
                        }
                        searchAdapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1, tempString);
                        searchByServiceResultList.setAdapter(searchAdapter);
                        searchByServiceCard.setVisibility(View.VISIBLE);
                        // on item click for clinics after chosen service
                        searchByServiceResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                searchByServiceCard.setVisibility(View.GONE);
                                selectedEmployee = employeesSearchedByService.get(position);
                                showSelectedClinic();
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            Log.d("searchException", e.toString());
        }
        // set listener for patient rating clinic
        onClickRateClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingClinic();
            }
        });
    }

    private void showRatingClinic(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_rate_clinic, null);
        dialogBuilder.setView(dialogView);

        final TextView username = (TextView) dialogView.findViewById(R.id.textViewPopupUsername);
        final EditText editTextComment = (EditText) dialogView.findViewById(R.id.editTextComment);
        final Button buttonRating = (Button) dialogView.findViewById(R.id.buttonRateClinic);
        final SimpleRatingBar ratingBar = (SimpleRatingBar) dialogView.findViewById(R.id.ratingBarHomeList);

        dialogBuilder.setTitle("Rate your experience");
        username.setText("Hey, " + patient.getUserName()+", how's your feeling?");
        final AlertDialog builder = dialogBuilder.create();
        builder.show();

        buttonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rate rate = new Rate(patient.getUserName(), ratingBar.getRating(),
                        editTextComment.getText().toString().trim());
                patient.rateClinic(selectedEmployee.getId(), rate);
                builder.dismiss();
            }
        });
        onRefresh();
    }

    private void setTextViews(){
        clinicNameShow.setText(clinicProfile.getClinicName());
        phoneShow.setText(clinicProfile.getPhoneNum());
        addressShow.setText(clinicProfile.getAddress());
        insuranceNameShow.setText(clinicProfile.getInsurance().toString() + " insurance");
        paymentShow.setText(clinicProfile.getPayment().toString());
    }

    private void showProfilePopup() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_create_profile, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Profile Information");

        final AlertDialog builder = dialogBuilder.create();
        builder.show();

        // get dialog's components
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextClinicNameHome);
        final EditText editTextAddress1 = (EditText) dialogView.findViewById(R.id.editTextAddressHome1);
        final EditText editTextAddress2 = (EditText) dialogView.findViewById(R.id.editTextAddressHome2);
        final EditText editTextPhone  = (EditText) dialogView.findViewById(R.id.editTextPhoneHome);
        final TextView textViewPayment = (TextView) dialogView.findViewById(R.id.textViewSelectPayment);
        final Button buttonCreate = (Button) dialogView.findViewById(R.id.buttonCreateProfilePopup);
        final RadioButton insuranceType1 = dialogView.findViewById(R.id.radioButtonInsuranceType1);
        final RadioButton insuranceType2 = dialogView.findViewById(R.id.radioButtonInsuranceType2);
        final RadioButton insuranceType3 = dialogView.findViewById(R.id.radioButtonInsuranceType3);

        if (!isNewUser){
            buttonCreate.setText("Modify");
        }

        // onClick listener for the select payment textView
        textViewPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPaymentChoices();
                // hide key board once clicked
                InputMethodManager manager = (InputMethodManager)getActivity()
                        .getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(textViewPayment.getWindowToken(), 0);
                //create list popup
                createChoiceList(getContext(), textViewPayment, paymentChoices,
                        R.layout.list_payment, R.id.textViewPaymentPopup);
                // Check popup exist or not
                if (dropDownList != null && !dropDownList.isShowing()) {
                    dropDownList.showAsDropDown(textViewPayment, 0, 10);
                }
            }
        });

        // onClick listener for the create profile button
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get user's inputs
                String name = editTextName.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String payment = textViewPayment.getText().toString().trim();
                String address1 = editTextAddress1.getText().toString().trim();
                String address2 = editTextAddress2.getText().toString().trim();

                // validate user's input
                if (name.equals("")){
                    Toast.makeText(getContext(), ("Please fill up the name for your clinic"),
                            Toast.LENGTH_SHORT).show();
                }else if (address1.equals("")){
                    Toast.makeText(getContext(), ("Please fill up the address line"),
                            Toast.LENGTH_SHORT).show();
                }else if (phone.equals("")){
                    Toast.makeText(getContext(), ("Please fill up the phone number"),
                            Toast.LENGTH_SHORT).show();
                }else if (payment.equals("")){
                    Toast.makeText(getContext(), ("Please fill up the payment method"),
                            Toast.LENGTH_SHORT).show();
                }else if (!insuranceType1.isChecked() && !insuranceType2.isChecked() && !insuranceType3.isChecked()){
                    Toast.makeText(getContext(), ("Please select a type of insurance"),
                            Toast.LENGTH_SHORT).show();
                }else{
                    // init clinic profile
                    String tempStartMonday = clinicProfile.getStartTime("Monday");
                    String tempEndMonday = clinicProfile.getEndTime("Monday");
                    String tempStartTuesday = clinicProfile.getStartTime("Tuesday");
                    String tempEndTuesday = clinicProfile.getEndTime("Tuesday");
                    String tempStartWednesday = clinicProfile.getStartTime("Wednesday");
                    String tempEndWednesday = clinicProfile.getEndTime("Wednesday");
                    String tempStartThursday = clinicProfile.getStartTime("Thursday");
                    String tempEndThursday = clinicProfile.getEndTime("Thursday");
                    String tempStartFriday = clinicProfile.getStartTime("Friday");
                    String tempEndFriday = clinicProfile.getEndTime("Friday");
                    Insurance insuranceInstance = null;
                    Payment paymentInstance;
                    String tempAddress;

                    if (!address2.equals("")){
                        tempAddress = address1+" "+address2;
                        addressShow.setText(address1+" "+address2);
                    }else{
                        tempAddress = address1;
                        addressShow.setText(address1);
                    }
                    switch (payment){
                        case "Cash":
                            paymentInstance = Payment.Cash;
                            break;
                        case  "Cheque":
                            paymentInstance = Payment.Cheque;
                            break;
                        case  "Debit Card":
                            paymentInstance = Payment.DebitCard;
                            break;
                        case  "Credit Card":
                            paymentInstance = Payment.CreditCard;
                            break;
                        default:
                            paymentInstance = null;
                    }
                    if (insuranceType1.isChecked()){
                        insuranceInstance = Insurance.Personal;
                    }
                    else if (insuranceType2.isChecked()){
                        insuranceInstance = Insurance.Group;
                    }
                    else if (insuranceType3.isChecked()){
                        insuranceInstance = Insurance.Travel;
                    }
                    clinicProfile = new ClinicProfile(tempAddress, phone, name,
                            paymentInstance, insuranceInstance);
                    if (tempStartMonday != null && tempEndMonday != null
                            && tempStartTuesday != null && tempEndTuesday != null
                            && tempStartWednesday != null && tempEndWednesday != null
                            && tempStartThursday != null && tempEndThursday != null
                            && tempStartFriday != null && tempEndFriday != null){
                        clinicProfile.setStartTime("Monday",tempStartMonday);
                        clinicProfile.setEndTime("Monday",tempEndMonday);
                        clinicProfile.setStartTime("Tuesday",tempStartTuesday);
                        clinicProfile.setEndTime("Tuesday",tempEndTuesday);
                        clinicProfile.setStartTime("Wednesday",tempStartWednesday);
                        clinicProfile.setEndTime("Wednesday",tempEndWednesday);
                        clinicProfile.setStartTime("Thursday",tempStartThursday);
                        clinicProfile.setEndTime("Thursday",tempEndThursday);
                        clinicProfile.setStartTime("Friday",tempStartFriday);
                        clinicProfile.setEndTime("Friday",tempEndFriday);
                    }
                    if (!isNewUser){
                        employee.updateClinic(clinicProfile);
                    }else{
                        employee.setUpClinic(clinicProfile);
                    }
                    nullProfileAlert.setVisibility(View.GONE);
                    isNewUser = false;
                    onRefresh();
                    Toast.makeText(getContext(), ("Profile completed"), Toast.LENGTH_SHORT).show();
                    builder.dismiss();
                }
            }
        });
    }

    private void showSelectedClinic(){
        profileCard.setVisibility(View.VISIBLE);
        workingHourCard.setVisibility(View.VISIBLE);
        selectedServiceCard.setVisibility(View.VISIBLE);
        cardViewRating.setVisibility(View.VISIBLE);
        ratingList = new ArrayList<Rate>();

        clinicProfile = selectedEmployee.getClinicProfile();
        HomeViewModel homeViewModelSelected = new HomeViewModel(selectedEmployee.getId());
        homeViewModelSelected.getSelectedServiceLiveData().observe(this,
                new Observer<ArrayList<Service>>() {
                    @Override
                    public void onChanged(ArrayList<Service> services) {
                        selectedServices = services;
                        onRefresh();
                    }
                });
        homeViewModelSelected.getRatesMutableLiveData().observe(this,
                new Observer<ArrayList<Rate>>() {
                    @Override
                    public void onChanged(ArrayList<Rate> rates) {
                        ratingList = rates;
                        onRefresh();
                    }
                });
        homeViewModel.saveState(selectedEmployee);
    }

    private void showBookingPopup(final Service service){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_booking, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextDate = (EditText) dialogView.findViewById(R.id.editTextPreferDate);

        final Button buttonBooking = (Button) dialogView.findViewById(R.id.bookButtonPopup);
        editTextDate.setFocusableInTouchMode(false);    // do not allow modify to the editText

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(1, editTextDate);
            }
        });

        dialogBuilder.setTitle("Booking");
        final AlertDialog builder = dialogBuilder.create();
        builder.show();

        buttonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextDate.getText().toString().equals("")){
                    Appointment appointment = new Appointment();
                    appointment.setDate(editTextDate.getText().toString());
                    appointment.setEmployeeEmail(selectedEmployee.getEmail());
                    appointment.setPatientEmail(patient.getEmail());
                    appointment.setServiceName(service.getServiceName());
                    appointment.setClinicName(selectedEmployee.getClinicProfile().getClinicName());
                    patient.makeAppointment(appointment);
                    builder.dismiss();
                }else {
                    Toast.makeText(getContext(), ("Please select a date"),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        onRefresh();
    }

    // method to create a drop down list
    private void createChoiceList(Context context, final TextView textView,
                                  final ArrayList<String> choices, int layoutId, int textViewId) {
        ListView listView = new ListView(getContext());
        // set array adapter
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                layoutId, textViewId, choices);
        listView.setAdapter(dataAdapter);

        // set list view's listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get list item's data
                String value = choices.get(position);
                // display data
                textView.setText(value);
                // dismiss popup after item chosen
                dropDownList.dismiss();
            }
        });
        dropDownList = new PopupWindow(listView, textView.getWidth(),
                ActionBar.LayoutParams.WRAP_CONTENT, true);

        // get & set background of the list
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.drop_down_list_bg);
        dropDownList.setBackgroundDrawable(drawable);

        // set dropDownList's attributes
        dropDownList.setFocusable(true);
        dropDownList.setOutsideTouchable(true);
        dropDownList.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // close the list if click outside
                dropDownList.dismiss();
            }
        });
    }

    // onClick method for save working hour button
    private void saveWorkingHour(){
        String startTimeMonday = startTimeMondaySelect.getText().toString().trim();
        String endTimeMonday = endTimeMondaySelect.getText().toString().trim();
        String startTimeTuesday = startTimeTuesdaySelect.getText().toString().trim();
        String endTimeTuesday = endTimeTuesdaySelect.getText().toString().trim();
        String startTimeWednesday = startTimeWednesdaySelect.getText().toString().trim();
        String endTimeWednesday = endTimeWednesdaySelect.getText().toString().trim();
        String startTimeThursday = startTimeThursdaySelect.getText().toString().trim();
        String endTimeThursday = endTimeThursdaySelect.getText().toString().trim();
        String startTimeFriday = startTimeFridaySelect.getText().toString().trim();
        String endTimeFriday = endTimeFridaySelect.getText().toString().trim();
        if (startTimeMonday.equals("-") || endTimeMonday.equals("-")
                || startTimeTuesday.equals("-") || endTimeTuesday.equals("-")
                || startTimeWednesday.equals("-") || endTimeWednesday.equals("-")
                || startTimeThursday.equals("-") || endTimeThursday.equals("-")
                || startTimeFriday.equals("-") || endTimeFriday.equals("-")){
            Toast.makeText(getContext(), ("Please select all start/end time"),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i<startTimeChoices.size(); i++){
            if (startTimeChoices.get(i).equals(startTimeMonday)){
                employee.setStartTime("Monday", startTimeChoices.get(i));
            }
            if (endTimeChoices.get(i).equals(endTimeMonday)){
                employee.setEndTime("Monday", endTimeChoices.get(i));
            }
            if (startTimeChoices.get(i).equals(startTimeTuesday)){
                employee.setStartTime("Tuesday", startTimeChoices.get(i));
            }
            if (endTimeChoices.get(i).equals(endTimeTuesday)){
                employee.setEndTime("Tuesday", endTimeChoices.get(i));
            }
            if (startTimeChoices.get(i).equals(startTimeWednesday)){
                employee.setStartTime("Wednesday", startTimeChoices.get(i));
            }
            if (endTimeChoices.get(i).equals(endTimeWednesday)){
                employee.setEndTime("Wednesday", endTimeChoices.get(i));
            }
            if (startTimeChoices.get(i).equals(startTimeThursday)){
                employee.setStartTime("Thursday", startTimeChoices.get(i));
            }
            if (endTimeChoices.get(i).equals(endTimeThursday)){
                employee.setEndTime("Thursday", endTimeChoices.get(i));
            }
            if (startTimeChoices.get(i).equals(startTimeFriday)){
                employee.setStartTime("Friday", startTimeChoices.get(i));
            }
            if (endTimeChoices.get(i).equals(endTimeFriday)){
                employee.setEndTime("Friday", endTimeChoices.get(i));
            }
        }
        timeSetAlert.setVisibility(View.GONE);
        Toast.makeText(getContext(), ("Successfully set time"), Toast.LENGTH_SHORT).show();
    }

    private void showDatePicker(final int mode, final TextView textView){
        Calendar calendar = Calendar.getInstance();
        int year    = calendar.get(Calendar.YEAR);
        int month   = calendar.get(Calendar.MONTH);
        int day     = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            if (mode == 1){ // for Booking
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                String dateInString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                Date date = formatter.parse(dateInString);
                                chosenDate = formatter.format(date);
                                Log.d("chosenDate", chosenDate+"");
                                textView.setText(chosenDate);
                            }else if (mode == 2){ // for Search
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                Date myDate = formatter.parse((dayOfMonth+2)+"-"+monthOfYear+"-"+year);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                                dayOfWeek = simpleDateFormat.format(myDate);
                                Log.d("chosenDayOfWeek", dayOfWeek+"");
                                searchView.setQuery(dayOfWeek, true);
                            }
                        }catch (Exception e){
                            Log.d("dateChosenException", e.toString());
                        }
                    }
                }, year, month, day);
        Field mDatePickerField;
        try {
            mDatePickerField = datePicker.getClass().getDeclaredField("DatePicker");
            mDatePickerField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    private void showTimePicker(final int mode){
        TimePickerDialog timePicker = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeValue = hourOfDay + ":" + minute;
                        chosenTime = timeValue;
                        // mode 1: set the result to searchView
                        if (mode == 1) {
                            // add employees that working at that time
                            onRefresh();
                            // update adapter to display the time periods
                            searchAdapter = new ArrayAdapter<String>(getContext(),
                                    android.R.layout.simple_list_item_1, employeesTimeStrings);
                            searchResultList.setAdapter(searchAdapter);
                            // set query to the selected time
                            searchView.setQuery(searchView.getQuery() + " "
                                    + chosenTime,true);
                        }
                        searchResultCard.setVisibility(View.VISIBLE);
                        searchView.clearFocus();
                    }
                }, 00, 00, DateFormat.is24HourFormat(getContext())
        );
        timePicker.show();
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount() return number of child
            View listItem = listAdapter.getView(i, null, listView);
            // calculate each list item's height
            listItem.measure(0, 0);
            // calculate all list item's height
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight() get divider's height
        // params.height get the height of listView
        listView.setLayoutParams(params);
    }

    private void addPaymentChoices() {
        paymentChoices = new ArrayList<>();
        paymentChoices.add("Cash");
        paymentChoices.add("Cheque");
        paymentChoices.add("Debit Card");
        paymentChoices.add("Credit Card");
    }

    private void addTimeChoices() {
        startTimeChoices = new ArrayList<>();
        startTimeChoices.add("8:00");
        startTimeChoices.add("8:30");
        startTimeChoices.add("9:00");
        startTimeChoices.add("9:30");
        startTimeChoices.add("10:00");
        startTimeChoices.add("10:30");

        endTimeChoices = new ArrayList<>();
        endTimeChoices.add("16:00");
        endTimeChoices.add("16:30");
        endTimeChoices.add("17:00");
        endTimeChoices.add("17:30");
        endTimeChoices.add("18:00");
        endTimeChoices.add("18:30");
    }
}
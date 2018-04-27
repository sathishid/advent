package com.ara.advent.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.MainActivity;
import com.ara.advent.R;
import com.ara.advent.models.Customer;
import com.ara.advent.models.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripsheetActivity extends AppCompatActivity implements TextWatcher {

    private final static String TAG = "TripSheet";
    private static String url = "http://arasoftwares.in/atnc-app/android_atncfile.php?action=";
    private static String customerUrl = url + "customerList";
    private static String vehicleNoUrl = url + "";
    private static String vehicletypeUrl = url + "vehiclelist";
    private static String placeUrl = url + "";
    private static String bookingUrl = url + "tripsheetentry";
    private static String parCustomerId = "custid";
    private static String parVehicleType = "vehicletype";
    private static String parvehicleNo = "vehicle_no";
    private static String parRoute = "route";
    private static String parstartDAte = "start_date";
    private static String parcloseDAte = "close_date";
    private static String parStartKm = "startkm";
    private static String parendKm = "endkm";
    private static String partotalkm = "totalkm";
    private static String parstartTime = "starttime";
    private static String parcloseTime = "endtime";
    private static String parTotalTime = "totaltime";

    int i1, i2, substract;
    long diffMinutes, diffHours, diffDays;
    String valCusId, valvehicletype, valvehicleno, valroute, valclosedate, valstartdate, valstartkm, valendkm, valtotalkm, valstarttime, valendtime, valtotaltime;
    String vehiclename, vehicleid, customername, customerid;

    @BindView(R.id.Trip_scrollLayout)
    ScrollView tripScroll;
    @BindView(R.id.spinner_Customer)
    Spinner customer_spinner;
    @BindView(R.id.spinner_vehicle_type)
    Spinner vehicleType_spinner;
    @BindView(R.id.input_VehicleNo)
    Spinner vechicleNo_spinner;
    @BindView(R.id.input_PLace)
    Spinner route_spinner;
    @BindView(R.id.input_date)
    TextView startingDate;
    @BindView(R.id.input_default_date)
    TextView closingDate;
    @BindView(R.id.input_start_km)
    EditText startingKm_Edittext;
    @BindView(R.id.input_closing_km)
    EditText closingKm_Edittext;
    @BindView(R.id.input_total_km)
    EditText totalKm_Edittext;
    @BindView(R.id.input_start_time)
    TextView start_time;
    @BindView(R.id.input_closing_time)
    TextView close_time;
    @BindView(R.id.input_total_time)
    TextView total_time;
    @BindView(R.id.btn_booking)
    Button booking_button;

    DatePickerDialog datePickerDialog;
    Calendar calendar;
    TimePickerDialog timepickerdialog;
    String format;


    ArrayList<Customer> customer = new ArrayList<Customer>();
    ArrayList<String> vehicleNo;
    ArrayList<String> vehicletype;
    ArrayList<String> place;
    EditText[] editTexts;
    String vehNo[] = {"5432", "5433", "5434", "5435", "5436", "5437", "5438"};
    String vehroute[] = {"Chennai", "Madurai", "Kovai", "Trichy", "Salem", "Nellai"};
    private int CalendarHour, CalendarMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripsheet);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tripScroll.setFocusableInTouchMode(true);
        tripScroll.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        vehicleNo = new ArrayList<String>();
        vehicletype = new ArrayList<String>();
        place = new ArrayList<String>();


        editTexts = new EditText[]{startingKm_Edittext, closingKm_Edittext};
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i].addTextChangedListener(this);
        }


        initSpinner();

        if (isNetworkAvailable()) {

            getVehicletypejson();
            getCustomerJson();
//            getvehicleNoJson();
//            getPlaceJson();
        } else {
            Snackbar bar = Snackbar.make(tripScroll, "Something went wrong, Check Network connection!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle user action

                        }
                    });

            bar.show();
        }

        closingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDate();
            }
        });
        startingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate();
            }
        });

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startingTimeMethod();
            }
        });

        close_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closingTimeMethod();
            }
        });
        total_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calTimeDiff();
            }
        });


        booking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendDataForBooking();
            }
        });


        vehicleType_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        vechicleNo_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        route_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void calTimeDiff() {

        String dateStarted = startingDate.getText().toString() + " " + start_time.getText().toString();
        String dateClosed = closingDate.getText().toString() + " " + close_time.getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ENGLISH);

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStarted);
            d2 = format.parse(dateClosed);

            long diff = d2.getTime() - d1.getTime();

            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
            diffDays = diff / (24 * 60 * 60 * 1000);

            Log.e(TAG, "diffrence days" + diffDays);
            Log.e(TAG, "diffrence hours" + diffHours);
            Log.e(TAG, "diffrence minutes" + diffMinutes);


        } catch (ParseException e) {
            Log.e(TAG, "parse Exception-" + e);

        }

        total_time.setText(String.valueOf(diffDays) + "days : " + String.valueOf(diffHours)+"hours");

//        Toast.makeText(TripsheetActivity.this, String.valueOf(diffHours)+":"+String.valueOf(diffMinutes), Toast.LENGTH_SHORT).show();

    }

    private void startDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(TripsheetActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String dFrom = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                        startingDate.setText(dFrom);
                        Log.e(TAG, " date ---" + dFrom);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void initSpinner() {

        ArrayAdapter aa = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, vehNo);
        vechicleNo_spinner.setAdapter(aa);

        ArrayAdapter aa1 = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, vehroute);
        route_spinner.setAdapter(aa1);
    }

    private void SendDataForBooking() {

        valvehicletype = vehicleType_spinner.getSelectedItem().toString();
        valvehicleno = vechicleNo_spinner.getSelectedItem().toString();
        valroute = route_spinner.getSelectedItem().toString();
        valstartdate = startingDate.getText().toString();
        valclosedate = closingDate.getText().toString();
        valstartkm = startingKm_Edittext.getText().toString();
        valendkm = closingKm_Edittext.getText().toString();
        valtotalkm = totalKm_Edittext.getText().toString();
        valstarttime = start_time.getText().toString();
        valendtime = close_time.getText().toString();
        valtotaltime = total_time.getText().toString();


        Log.e(TAG, "" + parCustomerId + "---" + valCusId);
        Log.e(TAG, "" + parVehicleType + "---" + valvehicletype);
        Log.e(TAG, "" + parvehicleNo + "---" + valvehicleno);
        Log.e(TAG, "" + parRoute + "---" + valroute);
        Log.e(TAG, "" + parstartDAte + "---" + valstartdate);
        Log.e(TAG, "" + parStartKm + "---" + valstartkm);
        Log.e(TAG, "" + parendKm + "---" + valendkm);
        Log.e(TAG, "" + partotalkm + "---" + valtotalkm);
        Log.e(TAG, "" + parstartTime + "---" + valstarttime);
        Log.e(TAG, "" + parcloseTime + "---" + valendtime);
        Log.e(TAG, "" + parTotalTime + "---" + valtotaltime);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, bookingUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "Booking_DATA_response-" + response);
                if (response.equalsIgnoreCase("success")) {

                    customer_spinner.setSelection(0);
                    vehicleType_spinner.setSelection(0);
                    vechicleNo_spinner.setSelection(0);
                    route_spinner.setSelection(0);
                    startingDate.setText("");
                    closingDate.setText("");
                    startingKm_Edittext.setText("");
                    closingKm_Edittext.setText("");
                    totalKm_Edittext.setText("");
                    start_time.setText("");
                    close_time.setText("");
                    total_time.setText("");

                    Snackbar bar = Snackbar.make(tripScroll, "Data sended successfully", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle user action
                                    startActivity(new Intent(TripsheetActivity.this, MainActivity.class));
                                    finish();

                                }
                            });

                    bar.show();


                } else {


                    Snackbar bar = Snackbar.make(tripScroll, "Data was not sent ." + " please check the details", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle user action

                                }
                            });

                    bar.show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Booking_DATA_error-" + error);
                Snackbar bar = Snackbar.make(tripScroll, "" + error, Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle user action

                            }
                        });

                bar.show();
                startActivity(new Intent(TripsheetActivity.this, MainActivity.class));
                finish();


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map map = new HashMap();
                map.put(parCustomerId, valCusId);
                map.put(parVehicleType, valvehicletype);
                map.put(parvehicleNo, valvehicleno);
                map.put(parRoute, valroute);
                map.put(parstartDAte, valstartdate);
                map.put(parcloseDAte, valclosedate);
                map.put(parStartKm, valstartkm);
                map.put(parendKm, valendkm);
                map.put(partotalkm, valtotalkm);
                map.put(parstartTime, valstarttime);
                map.put(parcloseTime, valendtime);
                map.put(parTotalTime, valtotaltime);
                return map;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    /*private void getPlaceJson() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,placeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "place_response-" + response);

                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                         = jsonObject.getString("");
                         = jsonObject.getString("");

                        Log.e(TAG, "" + );
                        Log.e(TAG, "" + );
//                        customer.add(customerid);
                        place.add();

                    }

                    ArrayAdapter arrayAdapter = new ArrayAdapter(TripsheetActivity.this,android.R.layout.simple_spinner_item,place);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    route_spinner.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "EXCEPTION_place-" + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "place_error-" + error);
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getvehicleNoJson() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,vehicleNoUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "vehicleNo_response-" + response);

                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                         = jsonObject.getString("");
                         = jsonObject.getString("");

                        Log.e(TAG, "" + );
                        Log.e(TAG, "" + );
//                        customer.add(customerid);
                        vehicleNo.add();

                    }

                    ArrayAdapter arrayAdapter = new ArrayAdapter(TripsheetActivity.this,android.R.layout.simple_spinner_item,vehicleNo);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vechicleNo_spinner.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "vehicleNo_place-" + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "vehicleNo_error-" + error);
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }*/

    private void getVehicletypejson() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, vehicletypeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "vehicletype_response-" + response);

                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        vehicleid = jsonObject.getString("vehicle_type_id");
                        vehiclename = jsonObject.getString("vehicle_type_name");

                        Log.e(TAG, "" + vehicleid);
                        Log.e(TAG, "" + vehiclename);
//
//                        vehicletype.add(vehicleid);
                        vehicletype.add(vehiclename);
                    }

                    ArrayAdapter arrayAdapter = new ArrayAdapter(TripsheetActivity.this, R.layout.support_simple_spinner_dropdown_item, vehicletype);
                    vehicleType_spinner.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "EXCEPTION_VEHICLELIST-" + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "vehicleList_error-" + error);
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getCustomerJson() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, customerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "customer_response-" + response);

                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        customerid = jsonObject.getString("customerId");
                        customername = jsonObject.getString("customername");

                        customer.add(new Customer(customerid, customername));
                    }

                    CustomerAdapter customerAdapter = new CustomerAdapter(TripsheetActivity.this, android.R.layout.simple_spinner_item, customer);
                    customer_spinner.setAdapter(customerAdapter);

                    customer_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Customer selectedNAme = customer.get(position);
                            Log.e(TAG, "selected customer name-" + selectedNAme.getCus_name());
                            Log.e(TAG, "selected customer id-" + selectedNAme.getCus_id());

                            valCusId = selectedNAme.getCus_id();
            /*            Toast.makeText(TripsheetActivity.this, ""+selectedNAme.getCus_name(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(TripsheetActivity.this, ""+selectedNAme.getCus_id(), Toast.LENGTH_SHORT).show();
*/

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "EXCEPTION_CUSTOMERLIST-" + e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "VehicleType_error-" + error);
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void closingTimeMethod() {
        calendar = Calendar.getInstance();
        CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
        CalendarMinute = calendar.get(Calendar.MINUTE);

        timepickerdialog = new TimePickerDialog(TripsheetActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                new TimePickerDialog.OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        if (hourOfDay == 0) {

                            hourOfDay += 12;

                            format = "AM";
                        } else if (hourOfDay == 12) {

                            format = "PM";

                        } else if (hourOfDay > 12) {

                            hourOfDay -= 12;

                            format = "PM";

                        } else {

                            format = "AM";
                        }


                        close_time.setText(hourOfDay + ":" + minute);/*
                        total_time.setText(String.valueOf(diffHours)+":"+String.valueOf(diffMinutes));

                        Log.e(TAG, "diffrence days" + diffDays);
                        Log.e(TAG, "diffrence hours" + diffHours);
                        Log.e(TAG, "diffrence minutes" + diffMinutes);
*/
                        calTimeDiff();

                    }
                }, CalendarHour, CalendarMinute, false);
        timepickerdialog.show();

    }

    private void startingTimeMethod() {
        calendar = Calendar.getInstance();
        CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
        CalendarMinute = calendar.get(Calendar.MINUTE);

        timepickerdialog = new TimePickerDialog(TripsheetActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                new TimePickerDialog.OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        if (hourOfDay == 0) {

                            hourOfDay += 12;

                            format = "AM";
                        } else if (hourOfDay == 12) {

                            format = "PM";

                        } else if (hourOfDay > 12) {

                            hourOfDay -= 12;

                            format = "PM";

                        } else {

                            format = "AM";
                        }

                        start_time.setText(hourOfDay + ":" + minute);
                    }
                }, CalendarHour, CalendarMinute, false);
        timepickerdialog.show();
    }

    private void closeDate() {

        Boolean flag = false;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(TripsheetActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String dFrom = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                        closingDate.setText(dFrom);
                        Log.e(TAG, " date ---" + dFrom);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
/*
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;

        if (spinner.getId() == R.id.spinner_Customer) {
            valCusId =customer.get(position).getCus_id();
            Toast.makeText(this, ""+valCusId, Toast.LENGTH_SHORT).show();
            Log.e(TAG,"customerid----------------"+valCusId);

        } else if (spinner.getId() == R.id.spinner_vehicle_type) {


        } else if (spinner.getId() == R.id.input_VehicleNo) {

        } else if (spinner.getId() == R.id.input_PLace) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        /*if (startingKm_Edittext.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please enter starting Km first", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


        totalKm_Edittext.setText(s);

        if (startingKm_Edittext.getText().toString().trim().length() != 0 && closingKm_Edittext.getText().toString().trim().length() > 0) {

            i1 = Integer.parseInt(startingKm_Edittext.getText().toString().trim());
            i2 = Integer.parseInt(closingKm_Edittext.getText().toString().trim());
            substract = i2 - i1;

            totalKm_Edittext.setText(substract + "");
        }


    }

    @Override
    public void afterTextChanged(Editable s) {


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TripsheetActivity.this,MainActivity.class));
        finish();
        super.onBackPressed();
    }
}

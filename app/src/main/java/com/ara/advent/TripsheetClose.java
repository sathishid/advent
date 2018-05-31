package com.ara.advent;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.http.MySingleton;
import com.ara.advent.models.OncallTsModel;
import com.ara.advent.models.TripsheetListModel;
import com.ara.advent.utils.AppConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindBitmap;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.MY_CAMERA_REQUEST_CODE;
import static com.ara.advent.utils.AppConstants.PREFERENCE_NAME;

public class TripsheetClose extends AppCompatActivity {
    public static final String TAG = "Trip sheet close";
    String totaltime;
    private Context context;
    private ProgressDialog progressDialog;
    private String progressMessage;

    @BindView(R.id.Tripclode)
    ScrollView sv;
    @BindView(R.id.textview_tripnoClose)
    TextView trino;
    @BindView(R.id.textview_tripdateclose)
    TextView tripdate;
    @BindView(R.id.textview_customerclose)
    TextView customer;
    @BindView(R.id.textview_closingKmclose)
    EditText closingkM;
    @BindView(R.id.textview_closingtimeHclose)
    EditText closetimeHours;
    @BindView(R.id.textview_closingtimeMclose)
    EditText closetimeMinutes;
    @BindView(R.id.Submitclose)
    Button SubmitClose;
    @BindView(R.id.startingKM_close)
    TextView startingKm_close;
    @BindView(R.id.startingtime_close)
    TextView starting_time_close;
    @BindView(R.id.customer_addressClose)
    TextView addressClose;
    @BindView(R.id.tripVehName1)
    TextView vehicle_name;
    @BindView(R.id.mobileNumberClose)
    TextView mobnoClose;
    String a;
    String f, e;
    String c, stime, ctime;
    OncallTsModel oncallTsModel;
    int skm, ckm, totalkm;
    long diffMinutes, diffHours, diffDays;

    @BindView(R.id.notify_user)
    Button notifying_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripsheet_close);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        sv.setFocusableInTouchMode(true);
        sv.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        SharedPreferences sharedPreferences1 = getSharedPreferences(PREFERENCE_NAME,MODE_PRIVATE);
        String ok = sharedPreferences1.getString("closed","");
        if (ok.equalsIgnoreCase("ok")){
            startActivity(new Intent(TripsheetClose.this,TripsheetImageSubmit.class));
            finish();
        }
        oncallTsModel = new OncallTsModel();


        SharedPreferences sharedPreferences = getSharedPreferences("submit", MODE_PRIVATE);
        a = sharedPreferences.getString("tripsheetid", "");
        String b = sharedPreferences.getString("tripsheetno", "");
        c = sharedPreferences.getString("tripsheetDate", "");
        String d = sharedPreferences.getString("tripsheetcustomername", "");
        e = sharedPreferences.getString("starttime", "");
        f = sharedPreferences.getString("startingkm", "");
        String i = sharedPreferences.getString("tirpsheetcusmobno","");
        String k = sharedPreferences.getString("tripsheetcusadd","");
        String m = sharedPreferences.getString("vehId", "");
        String n = sharedPreferences.getString("vehname", "");
        trino.setText(b);
        tripdate.setText(c);
        customer.setText(d);
        startingKm_close.setText(f);
        starting_time_close.setText(e);
        mobnoClose.setText(i);
        addressClose.setText(k);
        vehicle_name.setText(n);


        if (!isNetworkAvailable()) {
            showSnackbar("PLease check Your network connection");
        }
        SubmitClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMethodd();
            }
        });


        initEdittextFocus();

        notifying_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickToNotifycustomer();

            }
        });

    }

    private void clickToNotifycustomer() {

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.NOTIFYURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "resopnse " + response);
                String curentString = response;
                String[] separated = curentString.split("(?<=\\d)(?=\\D)");
                String code = separated[0];
                String res = separated[1];
                progressDialog.dismiss();
                if (res.contains("success")) {
                    progressDialog.dismiss();
                    showSnackbar("You Notified Customer");
                } else {
                    progressDialog.dismiss();
                    showSnackbar("Message Not sent ,contact support");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Log.e(TAG, "error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map map = new HashMap();
                map.put(AppConstants.TRIPID, a);
                return map;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void initEdittextFocus() {

        EditText[] editTexts2 = new EditText[]{closetimeHours, closetimeMinutes};
        for (int k = 0; k < editTexts2.length; k++) {
            editTexts2[k].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (closetimeHours.getText().toString().length() == 2) {

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (closetimeMinutes.getText().toString().length() == 2) {


                    }
                }
            });
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void submitMethodd() {


        if (!formValid()) {
            progressDialog.dismiss();
            Toast.makeText(this, "please enter all details", Toast.LENGTH_SHORT).show();
            return;

        }
        if (!isNetworkAvailable()) {
            progressDialog.dismiss();
            showSnackbar("PLease Check Your Netwok Connection");
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();

        String closetime = closetimeHours.getText().toString() + " : " + closetimeMinutes.getText().toString();
        String closingKM = closingkM.getText().toString();

        calTimeDiff();
        calculateTotalkmandtime();
        SharedPreferences sharedPreferences = getSharedPreferences("submit", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("closetime", closetime);
        editor.putString("closekm", closingKM);
        editor.putString("totkm", String.valueOf(totalkm));
        editor.putString("tottime", totaltime);
        editor.commit();
        SharedPreferences sharedPreferences1 = getSharedPreferences(PREFERENCE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.putString("closed","ok");
        editor1.commit();
        progressDialog.dismiss();
        startActivity(new Intent(TripsheetClose.this, TripsheetImageSubmit.class));
        finish();

    }

    public void calculateTotalkmandtime() {

        skm = Integer.parseInt(f);
        ckm = Integer.parseInt(closingkM.getText().toString());

        totalkm = ckm - skm;


    }

    public boolean formValid() {

        boolean error = true;


        if (closetimeHours.getText().toString().isEmpty() || Integer.parseInt(closetimeHours.getText().toString()) > 24) {
            closetimeHours.setError("hours not valid");
            error = false;
        } else {
            closetimeHours.setError(null);
        }

        if (closetimeHours.getText().toString().isEmpty() || Integer.parseInt(closetimeHours.getText().toString()) > 60) {
            closetimeHours.setError("minutes not valid");
            error = false;
        } else {
            closetimeHours.setError(null);
        }

/*
        if (closingkM.getText().toString().isEmpty()) {
            closingkM.setError("km not valid");
            error = false;
        } else {
            closetimeHours.setError(null);
        }*/

        if (closingkM.getText().toString().isEmpty() || Integer.parseInt(closingkM.getText().toString()) < Integer.parseInt(startingKm_close.getText().toString())) {
            closingkM.setError("km not valid");
            error = false;
        } else {
            closingkM.setError(null);
        }

        return error;
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(TripsheetClose.this, TripSheetList.class));
        finish();


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private void showSnackbar(String message) {
        final Snackbar snackbar = Snackbar.make(sv, message,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.text_ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void showSnackbar(String message, final boolean finishApp) {
        final Snackbar snackbar = Snackbar.make(sv, message,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.text_ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                if (finishApp)
                    finish();

            }
        });
        snackbar.show();
    }

    private void calTimeDiff() {

        stime = e;
        ctime = closetimeHours.getText().toString() + ":" + closetimeMinutes.getText().toString();
        String dateStarted = c + " " + e;
        String dateClosed = c + " " + ctime;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

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

        totaltime = diffHours + ":" + diffMinutes;


    }

}

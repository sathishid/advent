package com.ara.advent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.http.MySingleton;
import com.ara.advent.utils.AppConstants;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripsheetSubmit extends AppCompatActivity {

    @BindView(R.id.scrol)
    ScrollView sv;
    @BindView(R.id.textview_tripno)
    TextView trino;
    @BindView(R.id.textview_tripdate)
    TextView tripdate;
    @BindView(R.id.textview_customer)
    TextView customer;
    @BindView(R.id.textview_customermc)
    TextView customermc;
    @BindView(R.id.textview_startingkm)
    TextView startkm;
    @BindView(R.id.textview_startingtime)
    TextView starttime;
    @BindView(R.id.textview_customerREPORT)
    TextView cusreportto;
    @BindView(R.id.textview_closingkm)
    TextView closekm;
    @BindView(R.id.textview_closingtimeM)
    TextView closetimem;
    @BindView(R.id.textview_closingtimeH)
    TextView closetimeh;
    String a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripsheet_submit);
        ButterKnife.bind(this);


        SharedPreferences sharedPreferences = getSharedPreferences("submit",MODE_PRIVATE);
        a = sharedPreferences.getString("tripsheetid","");
        String b = sharedPreferences.getString("tripsheetno","");
        String c = sharedPreferences.getString("tripsheetDate","");
        String d = sharedPreferences.getString("tripsheetcustomername","");
        String e = sharedPreferences.getString("tripsheetMCname","");
        String f = sharedPreferences.getString("tripsheetreportto","");
        String j = sharedPreferences.getString("tripshetstartingkm","");
        String h = sharedPreferences.getString("tripsheetstartingtie","");

        trino.setText(b);
        tripdate.setText(c);
        customer.setText(d);
        customermc.setText(e);
        cusreportto.setText(f);
        startkm.setText(j);
        starttime.setText(h);
        if (isNetworkAvailable()) {
            submitMethodd();
        } else {

            showSnackbar("No connection");
        }

    }
    public boolean formValid() {

        boolean error = true;


        if (closetimeh.getText().toString().isEmpty()) {
            closetimeh.setError("hours not valid");
            error = false;
        } else {
            closetimeh.setError(null);
        }

        if (closetimem.getText().toString().isEmpty()) {
            closetimem.setError("minutes not valid");
            error = false;
        } else {
            closetimem.setError(null);
        }


        if (closekm.getText().toString().isEmpty()) {
            closekm.setError("km not valid");
            error = false;
        } else  {
            closekm.setError(null);
        }

        return error;
    }


    private void submitMethodd() {

        if (!formValid()) {
            Toast.makeText(this, "please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        final String closing_time = closetimeh.getText().toString()+":"+closetimem.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.SUBMITURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("TAG","response--------"+response);
                if (response.equalsIgnoreCase("success")) {
                    SharedPreferences sharedPreferences = getSharedPreferences("submit",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    startActivity(new Intent(TripsheetSubmit.this,TripSheetList.class)
                    .putExtra("name","TripSheet Added successfully"));
                    finish();

                } else{
                    Toast.makeText(TripsheetSubmit.this, "data was not sent", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","error--------"+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map map = new HashMap();
                map.put(AppConstants.TRIPID,a);
                map.put(AppConstants.CLOSINGKM,closekm.getText().toString());
                map.put(AppConstants.CLOSINTIME,closing_time);
                return map;

            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TripsheetSubmit.this, TripSheetList.class));
        finish();
        super.onBackPressed();
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
}

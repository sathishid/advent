package com.ara.advent;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.http.MySingleton;
import com.ara.advent.utils.AppConstants;

import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripsheetHistory extends AppCompatActivity {

    private static final String TAG= "TripsheetHistory";
    @BindView(R.id.Linear)
    RelativeLayout linear;
    @BindView(R.id.tripHistory)
    ListView TripHistory;
    @BindView(R.id.swipeto)
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripsheet_history);
        ButterKnife.bind(this);

        if (isNetworkAvailable()) {
            initViews();
        } else {
            showSnackbar("Please check your network connection");

        }

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {
                    showSnackbar("Please check your network connection");
                }
                initViews();
                swipe.setRefreshing(false);
            }
        });
    }

    private void initViews() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.HISTORYURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG,"response"+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG,"error"+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map map = new HashMap();
                map.put("","");
                return map;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void showSnackbar(String message) {
        final Snackbar snackbar = Snackbar.make(linear, message,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.text_ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TripsheetHistory.this,TripSheetList.class));
        finish();
    }
}

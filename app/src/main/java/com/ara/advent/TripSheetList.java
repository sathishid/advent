package com.ara.advent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.Adapter.TripsheetListAdapter;
import com.ara.advent.http.MySingleton;
import com.ara.advent.models.TripsheetListModel;
import com.ara.advent.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.TBCMCNAME;
import static com.ara.advent.utils.AppConstants.TBCNAME;
import static com.ara.advent.utils.AppConstants.TBCSSKM;
import static com.ara.advent.utils.AppConstants.TBCSTIME;
import static com.ara.advent.utils.AppConstants.TBDATE;
import static com.ara.advent.utils.AppConstants.TBID;
import static com.ara.advent.utils.AppConstants.TBNO;
import static com.ara.advent.utils.AppConstants.TBREPORTTO;

public class TripSheetList extends AppCompatActivity {
    private static final String TAG = "TRIPSHEETLIST";
    @BindView(R.id.li)
    LinearLayout li;

    @BindView(R.id.list)
    ListView Trip_list;
    ArrayList<TripsheetListModel> triplistArray = new ArrayList<TripsheetListModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripsheetlist);
        ButterKnife.bind(this);
        if (isNetworkAvailable()) {
            populateTripSheetData();
        } else {
            showSnackbar("Please check your networ connection");
        }
        Intent in = getIntent();
        String text = in.getStringExtra("name");
        if (text != null) {
            Snackbar bar = Snackbar.make(li, "" + text, Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle user action
                        }
                    });

            bar.show();
        }

        Trip_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String tripsheetid = triplistArray.get(i).getTripBooking_id();
                String tripsheetno = triplistArray.get(i).getTripBooking_no();
                String tripsheetDate = triplistArray.get(i).getTripBooking_date();
                String tripsheetcustomername = triplistArray.get(i).getCustomer_name();
                String tripsheetMCname = triplistArray.get(i).getCustomerMultiContact_name();
                String tripsheetreportto = triplistArray.get(i).getTripBookingReport_to();
                String tripshetstartingkm = triplistArray.get(i).getTripcustomer_startingkm();
                String tripsheetstartingtie = triplistArray.get(i).getTripcustomer_startingtime();

                Log.e(TAG, "tripsheetid" + tripsheetid);
                Log.e(TAG, "tripsheetno" + tripsheetno);
                Log.e(TAG, "tripsheetDate" + tripsheetDate);
                Log.e(TAG, "tripsheetcustomername" + tripsheetcustomername);
                Log.e(TAG, "tripsheetMCname" + tripsheetMCname);
                Log.e(TAG, "tripsheetreportto" + tripsheetreportto);
                Log.e(TAG, "tripshetstartingkm" + tripshetstartingkm);
                Log.e(TAG, "tripsheetstartingtie" + tripsheetstartingtie);


                SharedPreferences sharedPreferences = getSharedPreferences("submit", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("tripsheetid", tripsheetid);
                editor.putString("tripsheetno", tripsheetno);
                editor.putString("tripsheetDate", tripsheetDate);
                editor.putString("tripsheetcustomername", tripsheetcustomername);
                editor.putString("tripsheetMCname", tripsheetMCname);
                editor.putString("tripsheetreportto", tripsheetreportto);
                editor.putString("tripshetstartingkm", tripshetstartingkm);
                editor.putString("tripsheetstartingtie", tripsheetstartingtie);
                editor.commit();

                startActivity(new Intent(TripSheetList.this, TripsheetSubmit.class));
                finish();
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public void populateTripSheetData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.TBURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "populate trip sheet data " + response);
                JSONArray jsonArray = null;
                JSONObject jsonObject = null;
                triplistArray = new ArrayList<>();
                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String tbid = jsonObject.getString(TBID);
                        String tbbno = jsonObject.getString(TBNO);
                        String tbdate = jsonObject.getString(TBDATE);
                        String cname = jsonObject.getString(TBCNAME);
                        String cmcname = jsonObject.getString(TBCMCNAME);
                        String tbreeportto = jsonObject.getString(TBREPORTTO);
                        String tcstartingkm = jsonObject.getString(TBCSSKM);
                        String tcstartingtime = jsonObject.getString(TBCSTIME);
                        TripsheetListModel t = new TripsheetListModel();
                        t.setTripBooking_id(tbid);
                        t.setTripBooking_no(tbbno);
                        t.setTripBooking_date(tbdate);
                        t.setCustomer_name(cname);
                        t.setCustomerMultiContact_name(cmcname);
                        t.setTripBookingReport_to(tbreeportto);
                        t.setTripcustomer_startingkm(tcstartingkm);
                        t.setTripcustomer_startingtime(tcstartingtime);

                        triplistArray.add(t);
                    }


                    TripsheetListAdapter tripsheet = new TripsheetListAdapter(getApplicationContext(), R.layout.listitems, triplistArray);
                    Trip_list.setAdapter(tripsheet);
                } catch (JSONException json) {
                    Log.e(TAG, "jsonexception" + json);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "populate trip sheet data error " + error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map map = new HashMap();
                map.put(AppConstants.PARAM_VEHICLE_ID, ""+AppConstants.getUser().getId());

                return map;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TripSheetList.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }


    private void showSnackbar(String message) {
        final Snackbar snackbar = Snackbar.make(li, message,
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

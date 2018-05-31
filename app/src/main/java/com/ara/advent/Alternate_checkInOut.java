package com.ara.advent;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ara.advent.models.Attendance;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Alternate_checkInOut extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private final static String TAG = "CheckInOut";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final int GPS_REQUEST_RESULT = 104;
    @BindView(R.id.input_your_avatar)
    ImageView imageViewAvatar;
    @BindView(R.id.input_text_your_name)
    TextView input_yout_name;
    @BindView(R.id.input_view_place)
    TextView input_place;
    @BindView(R.id.input_view_date)
    TextView input_view_date;
    @BindView(R.id.input_view_check_in)
    TextView input_view_checkIn;
    @BindView(R.id.input_view_check_out)
    TextView input_view_checkOut;
    @BindView(R.id.btn_check_in)
    Button btn_check_in;
    @BindView(R.id.btn_check_out)
    Button btn_check_out;
    @BindView(R.id.view_scroll_root)
    ScrollView scrollView;
    SharedPreferences sharedPreferences;
    private LocationCallback mLocationCallback;
    private boolean mLocationPermissionGranted = false;
    private boolean mLocationHistoryGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private Attendance attendance;
    String cIn, cOut;
    String username, pasword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternate_check_in_out);
        ButterKnife.bind(this);


    }
}

package com.ara.advent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ScrollView;

import com.ara.advent.models.Attendance;
import com.ara.advent.utils.AppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = "Main Activity";
    @BindView(R.id.layout_scroll_view)
    ScrollView rootLayout;

    private LocationCallback mLocationCallback;
    private boolean mLocationPermissionGrandted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private Attendance attendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        attendance = new Attendance();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, AppConstants.MAIN_REQUEST_CODE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGoogleService()) {
            if (mLocationPermissionGrandted) {
                startLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationPermissionGrandted) {
            stopLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        try {
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        } catch (SecurityException securityException) {
            Log.e(TAG, securityException.getMessage());
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private boolean checkGoogleService() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int apiAvailableStatusCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailableStatusCode == ConnectionResult.SUCCESS) {
            Log.i(TAG, "Play API Available");
            return true;
        } else {
            if (googleApiAvailability.isUserResolvableError(apiAvailableStatusCode)) {
                googleApiAvailability.showErrorNotification(this, apiAvailableStatusCode);
            }
        }
        return false;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission Granted.");
            mLocationPermissionGrandted = true;

            initLocationPermission();
            startLocationUpdates();

        } else {
            Log.e(TAG, "This App requires Location Service.");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

            }, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void initLocationPermission() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.e(TAG, "Still Location null");
                    return;
                }
                String strLocation = "";
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    updateAddress(location);
                }
                Log.i(TAG, strLocation + " Locations");
            }

            ;
        };
    }

    private void updateAddress(Location location) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);

        } catch (IOException ioException) {
            Log.e(TAG, ioException.getMessage(), ioException);
        } catch (IllegalArgumentException illegalArgumentException) {

            Log.e(TAG, illegalArgumentException.getMessage() + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {

        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            attendance.setAddressFraments(addressFragments);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    private void logout() {
        //TODO: Logout logic goes here.
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_logout_id:
                logout();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            Log.e(TAG, "Location Permission");
            mLocationPermissionGrandted = true;
            startLocationUpdates();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.MAIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar snackbar = Snackbar.make(rootLayout, "Login Successful", Snackbar.LENGTH_SHORT);
                snackbar.show();
                if (checkGoogleService()) {
                    checkLocationPermission();
                }
            }
        }
    }


}

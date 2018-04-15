package com.ara.advent;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ara.advent.models.Attendance;
import com.ara.advent.models.User;
import com.ara.advent.utils.AppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.MY_CAMERA_REQUEST_CODE;
import static com.ara.advent.utils.AppConstants.PARAM_USER_ID;
import static com.ara.advent.utils.AppConstants.PARAM_USER_NAME;
import static com.ara.advent.utils.AppConstants.PREFERENCE_NAME;
import static com.ara.advent.utils.AppConstants.REQUEST_IMAGE_CAPTURE;

public class CheckInOut extends AppCompatActivity {

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

    @BindView(R.id.view_scroll_root)
    ScrollView scrollView;
    private LocationCallback mLocationCallback;
    private boolean mLocationPermissionGranted = false;
    private boolean mLocationHistoryGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private Attendance attendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);
        attendance = new Attendance();
        ButterKnife.bind(this);


        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        if (sharedPreferences.contains(PARAM_USER_NAME)) {
            User user = new User();
            user.setId(sharedPreferences.getInt(PARAM_USER_ID, -1));
            user.setUserName(sharedPreferences.getString(PARAM_USER_NAME, null));
            AppConstants.setUser(user);
            input_yout_name.setText(user.getUserName());
            input_view_date.setText(AppConstants.todayAsString());
            requestGPSPermission(true);

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, AppConstants.MAIN_REQUEST_CODE);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageViewAvatar.setImageBitmap(imageBitmap);
        } else if (requestCode == AppConstants.MAIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                input_yout_name.setText(AppConstants.getUser().getUserName());
                requestGPSPermission(true);
            }
        } else if (requestCode == GPS_REQUEST_RESULT) {
            requestGPSPermission(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    showSnackbar("This App needs Camera",true);
                }
            }
            break;
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Location Permitted.");
                    mLocationPermissionGranted = true;
                    updateLocationFromHistory();
                } else {
                    showSnackbar("Location Service is required to use this App.",true);
                }

                break;
        }

    }


    public void addPhoto_OnClick(View view) {
        requestPermissionForCamera();
    }


    private void requestPermissionForCamera() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                showSnackbar("This App needs Camera",true);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_REQUEST_CODE);
            }
        } else {
            dispatchTakePictureIntent();
        }
    }


    private void showSnackbar(String message, final boolean finishApp) {
        final Snackbar snackbar = Snackbar.make(scrollView, message,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.text_ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                if(finishApp)
                    finish();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public void requestGPSPermission(boolean isFirstTime) {
        if (!hasGoogleService()) {
            //TODO: Need to provide option to install the Google Services.
            Log.e(TAG, "Google Play Service is required to use this App.");
            showSnackbar("Google Play Server is required.",true);
            return;
        }

        if (hasLocationEnabled()) {
            if (hasLocationPermission()) {
                Log.i(TAG, "Permission Granted.");
                mLocationPermissionGranted = true;
                updateLocationFromHistory();
            } else {
                requestLocationPermission();
            }
        } else {
            if (isFirstTime) {
                showEnableGPSAlertToUser();
            } else {
                showSnackbar("You must enable the Location Service!",true);
            }
        }
    }

    public boolean hasLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showEnableGPSAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?" +
                "system Need GPS To Open This Application")
                .setCancelable(false)
                .setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent, GPS_REQUEST_RESULT);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void updateLocationFromHistory() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task<Location> locationTask = mFusedLocationClient.getLastLocation();
            locationTask.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.i(TAG, (location == null) + "");
                    if (location == null) {
                        mLocationHistoryGranted = false;
                        startLocationUpdates();
                    } else {
                        mLocationHistoryGranted = true;
                        updateAddress(location);
                    }
                }
            });
            locationTask.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage(), e);

                }
            });
        } catch (SecurityException securityException) {
            Log.e(TAG, securityException.getMessage(), securityException);
            startLocationUpdates();
        }

    }

    private boolean canStartLocationService() {
        if (!mLocationPermissionGranted)
            return false;
        if (mLocationHistoryGranted)
            return false;
        return true;
    }

    private void startLocationUpdates() {
        if (!canStartLocationService()) {
            return;
        }

        initiateLocationServices();
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
            showSnackbar("Something went wrong, contact Support",true);
        }
    }

    private void stopLocationUpdates() {
        if (!mLocationPermissionGranted || mLocationHistoryGranted) {
            return;
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private boolean hasGoogleService() {
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

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        Log.e(TAG, "This App requires Location Service.");
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void initiateLocationServices() {
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
                    2);

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
            attendance.setCheckInAddress(AppConstants.getAddressAsString(addressFragments));
            input_place.setText(attendance.getCheckInAddress());

        }

    }


}

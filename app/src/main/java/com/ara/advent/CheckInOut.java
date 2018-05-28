package com.ara.advent;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.http.HttpCaller;
import com.ara.advent.http.HttpRequest;
import com.ara.advent.http.HttpResponse;
import com.ara.advent.http.MySingleton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.CHECK_IN_DATE;
import static com.ara.advent.utils.AppConstants.MY_CAMERA_REQUEST_CODE;
import static com.ara.advent.utils.AppConstants.PARAM_CHECK_IN;
import static com.ara.advent.utils.AppConstants.PARAM_CHECK_OUT;
import static com.ara.advent.utils.AppConstants.PARAM_PASSWORD;
import static com.ara.advent.utils.AppConstants.PARAM_PERAMT;
import static com.ara.advent.utils.AppConstants.PARAM_TOLLAMT;
import static com.ara.advent.utils.AppConstants.PARAM_TYPE;
import static com.ara.advent.utils.AppConstants.PARAM_USER_ID;
import static com.ara.advent.utils.AppConstants.PARAM_USER_NAME;
import static com.ara.advent.utils.AppConstants.PREFERENCE_NAME;
import static com.ara.advent.utils.AppConstants.REQUEST_IMAGE_CAPTURE;
import static com.ara.advent.utils.AppConstants.calendarAsString;
import static com.ara.advent.utils.AppConstants.isToday;
import static com.ara.advent.utils.AppConstants.timeAsString;
import static com.ara.advent.utils.AppConstants.user;

public class CheckInOut extends AppCompatActivity {

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
        setContentView(R.layout.activity_check_in_out);
        ButterKnife.bind(this);
        attendance = new Attendance();
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        User user = new User();
        user.setId(sharedPreferences.getInt(PARAM_USER_ID, -1));
        user.setUserName(sharedPreferences.getString(PARAM_USER_NAME, null));
        AppConstants.setUser(user);
        SharedPreferences sharedPreferences = getSharedPreferences("logindetails", MODE_PRIVATE);
        username = sharedPreferences.getString("user", "");
        pasword = sharedPreferences.getString("pass", "");
        Log.e(TAG, "username = " + username);
        Log.e(TAG, "password = " + pasword);


        if (isNetworkAvailable()) {

            method();
        } else {
            showSnackbar("something went wrong ,PLease check your network connection");
        }
        String checkIn = sharedPreferences.getString("checkin","");
        String checkOut = sharedPreferences.getString("checkOut","");
        if (!checkIn.contains("") && !checkOut.contains("")) {
            showSnackbar("you Are Already Check IN Today ,Please Try again tommorrow");
        } else if (checkIn.contains("") && checkOut.contains("")){
            showSnackbar("please check In");

        } else if (checkIn.contains("") && checkOut.contains("")) {
            showSnackbar("Please Check Out");
        }
        requestGPSPermission(true);


    }

    private boolean updateFromPreference() {

        if (sharedPreferences.contains(PARAM_USER_NAME)) {
            User user = new User();
            user.setId(sharedPreferences.getInt(PARAM_USER_ID, -1));
            user.setUserName(sharedPreferences.getString(PARAM_USER_NAME, null));
            String date = sharedPreferences.getString(CHECK_IN_DATE, null);
            if (date != null && isToday(date)) {
                if (sharedPreferences.contains(PARAM_CHECK_IN)) {
                    String time = sharedPreferences.getString(PARAM_CHECK_IN, null);
                    attendance.setCheckInTime(AppConstants.timeStringToCalendar(time));

                    if (sharedPreferences.contains(PARAM_CHECK_OUT)) {
                        time = sharedPreferences.getString(PARAM_CHECK_OUT, null);
                        attendance.setCheckOutTime(AppConstants.timeStringToCalendar(time));
                    }
                    attendance.setAttendanceDate(AppConstants.calendarStringAsCalendar(date));
                    if (AppConstants.isToday(attendance.getAttendanceDate())) {
                        attendance.setAlreadyCheckedIn(true);
                    } else {
                        attendance.setAlreadyCheckedIn(false);
                    }
                }
            } else {
                attendance.setCheckOutTime(null);
                attendance.setCheckInTime(null);
                attendance.setAlreadyCheckedIn(false);
                changeButtonState(btn_check_out, false);
            }
            AppConstants.setUser(user);
            return true;
        }
        return false;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getMessage(), ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.ara.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(attendance.getImageFileName());

            compressImageFile(imageBitmap, attendance.getImageFileName());

            imageViewAvatar.setImageBitmap(imageBitmap);

            updateDetails();
        } else if (requestCode == AppConstants.MAIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                updateFromPreference();
                updateDetails();
                requestGPSPermission(true);
            }
        } else if (requestCode == GPS_REQUEST_RESULT) {
            requestGPSPermission(false);
        }
    }

    public void compressImageFile(Bitmap bitmap, String fileName) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);


            File file = new File(fileName);
            file.deleteOnExit();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] array = outputStream.toByteArray();
            fileOutputStream.write(array);
            fileOutputStream.close();

        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage(), exception);
        }

    }

    private void updateDetails() {


        attendance.setUser(AppConstants.getUser());
        input_yout_name.setText(attendance.getUser().getUserName());

        attendance.setAttendanceDate(Calendar.getInstance());
        input_view_date.setText(AppConstants.calendarAsString(attendance.getAttendanceDate()));
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        boolean hasAlreadyCheckedOut = sharedPreferences.contains(PARAM_CHECK_OUT);
        if (attendance.hasCheckedIn()) {
            if (!AppConstants.isNotHalfAnHourDifference(attendance.getCheckInTime())) {
                changeButtonState(btn_check_in, false);
            } else {
                changeButtonState(btn_check_in, true);

            }


            if (hasAlreadyCheckedOut) {
                if (!AppConstants.isNotHalfAnHourDifference(attendance.getCheckOutTime())) {
                    changeButtonState(btn_check_out, false);
                } else {
                    changeButtonState(btn_check_out, true);
                }
            } else {
                attendance.setCheckOutTime(Calendar.getInstance());
                changeButtonState(btn_check_out, true);
            }
            input_view_checkIn.setText(AppConstants.timeAsString(attendance.getCheckInTime()));

            input_view_checkOut.setText(AppConstants.timeAsString(attendance.getCheckOutTime()));
        } else {
            attendance.setCheckInTime(Calendar.getInstance());
            input_view_checkIn.setText(AppConstants.timeAsString(attendance.getCheckInTime()));

            changeButtonState(btn_check_out, false);
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
                    showSnackbar("This App needs Camera", true);
                }
            }
            break;
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Location Permitted.");
                    mLocationPermissionGranted = true;
                    updateLocationFromHistory();
                } else {
                    showSnackbar("Location Service is required to use this App.", true);
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
                showSnackbar("This App needs Camera", true);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_CAMERA_REQUEST_CODE);
            }
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void showSnackbar(String message) {
        showSnackbar(message, false);
    }

    private void showSnackbar(String message, final boolean finishApp) {
        final Snackbar snackbar = Snackbar.make(scrollView, message,
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
            showSnackbar("Google Play Server is required.", true);
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
                showSnackbar("You must enable the Location Service!", true);
            }
        }
    }

    public boolean hasLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showEnableGPSAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
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

    private void changeButtonState(Button btnControl, boolean state) {
        btnControl.setEnabled(state);
        if (state) {
            //Enable state
            btnControl.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.primary_dark, null));
        } else {
            btnControl.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.gray, null));
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
            showSnackbar("Something went wrong, contact Support", true);
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
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        attendance.setLatitude(latitude);
        attendance.setLongitude(longitude);

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
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

    private boolean validate(boolean checkOut) {
        if (attendance == null) {
            showSnackbar("Check In entry not found", false);
            return false;
        }
        if (attendance.getImageFileName() == null) {
            showSnackbar("Photo not updated.", false);
            return false;
        }
        if (attendance.getCheckInAddress() == null) {
            showSnackbar("Address not updated.", false);
            return false;
        }
        if (attendance.getCheckInTime() == null) {
            showSnackbar("Check-In time not found", false);
            return false;
        }
        if (checkOut) {
            if (attendance.getCheckOutAddress() == null) {
                showSnackbar("Check-Out address not found", false);
                return false;
            }
            if (attendance.getCheckOutTime() == null) {
                showSnackbar("Check-Out time not found", false);
                return false;
            }
        }
        return true;
    }

    private void showAlertDialog(final boolean isCheckIn) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
        alertDialogBuilder.setMessage("You have already checked In, do want to overwrite the exiting check-in?")
                .setCancelable(false)
                .setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                attendance.setCheckInTime(Calendar.getInstance());
                                pushToServer(isCheckIn);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void btnCheckInOnClick(View view) {
        if (!validate(false)) {
            Log.i(TAG, "Validation Failed");
            return;
        }
        if (!isNetworkAvailable()) {
            showSnackbar("PLease Check Your Netwok Connection");
            return;
        }
        if (attendance.hasCheckedIn()) {
            showAlertDialog(true);
        } else {
            pushToServer(true);
        }

    }

    public void method() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.getLoginAction(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "rsponse for " + response);
                JSONArray jsonArray = null;
                JSONObject jsonObject = null;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        cIn = jsonObject.getString("checkin");
                        cOut = jsonObject.getString("chec   Qkout");
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("checkin",cIn);
                    editor.putString("checkout",cOut);
                    editor.commit();


                    /* if (cIn.equalsIgnoreCase(null)) {

                        showSnackbar("Please check In");
                    } else if (cOut.equalsIgnoreCase(null)) {

                        showSnackbar("Please Check Out");
                    } else if (cIn.equalsIgnoreCase(null) && cOut.equalsIgnoreCase(null)) {

                        showSnackbar("PLease both check In and Check Out");
                    } else {

                        showSnackbar("Your daily limit is finished . PLease try again tommorrow ");
                    }*/
                   /* if (cIn != null && cOut != null ) {
                        showSnackbar("you are already check in please try again tommorrow");
                    }*/


                } catch (JSONException ex) {
                    Log.e(TAG, "exception json " + ex);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "error for " + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map map = new HashMap();
                map.put(PARAM_USER_NAME, username);
                map.put(PARAM_PASSWORD, pasword);
                map.put(PARAM_TYPE, String.valueOf(1));
                return map;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void pushToServer(final boolean isCheckIn) {

        HttpRequest httpRequest = new HttpRequest(AppConstants.getSaveAction());
        httpRequest.setRequestBody(attendance.toMultiPartBody(isCheckIn));
        try {
            new HttpCaller(this, (isCheckIn) ? "Checking In" : "Checking Out") {
                @Override
                public void onResponse(HttpResponse response) {
                    super.onResponse(response);
                    if (response.getStatus() == HttpResponse.ERROR) {
                        onFailed(response);
                    } else {

                        onSuccess(response, isCheckIn);
                    }
                }
            }.execute(httpRequest);

        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage(), exception);
            showSnackbar("Something went wrong, contact Ara software", false);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" +
                "" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        String mCurrentPhotoPath;
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        attendance.setImageFileName(mCurrentPhotoPath);


        return image;
    }

    private void onSuccess(HttpResponse response, boolean isCheckIn) {
        try {
            String message = response.getMesssage();
            if (message.compareToIgnoreCase(AppConstants.SUCCESS_MESSAGE) != 0) {
                showSnackbar(response.getMesssage(), false);
                return;
            }
            Log.i(TAG, response.getMesssage());


            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            if (isCheckIn) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(PARAM_USER_ID, user.getId());
                editor.putString(PARAM_CHECK_IN, timeAsString(attendance.getCheckInTime()));
                editor.putString(CHECK_IN_DATE, calendarAsString(Calendar.getInstance()));
                editor.putString(PARAM_USER_NAME, user.getUserName());
                editor.commit();
                attendance.setAlreadyCheckedIn(true);
                imageViewAvatar.setImageResource(R.drawable.camera_icon);
                attendance.setImageFileName(null);
                updateDetails();
                showSnackbar("Checked In Successfully.");
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(PARAM_USER_ID, user.getId());
                editor.putString(PARAM_USER_NAME, user.getUserName());
                editor.putString(PARAM_CHECK_OUT, timeAsString(attendance.getCheckOutTime()));
                editor.commit();
                showSnackbar("Checked Out Successfully.");
                attendance.setAlreadyCheckedIn(false);
                attendance.setCheckInTime(null);
                attendance.setCheckOutTime(null);
                attendance.setImageFileName(null);
                changeButtonState(btn_check_out, false);
                changeButtonState(btn_check_in, false);

                imageViewAvatar.setImageResource(R.drawable.camera_icon);
                attendance.setImageFileName(null);

            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            showSnackbar("Something went wrong, contact support");
        }

    }

    private void onFailed(HttpResponse response) {
        if (response != null) {
            showSnackbar("Something went wrong, Check Network connection!");
            Log.e(TAG, response.getMesssage());
        }
    }

    public void btnCheckOutOnClick(View view) {
        if (!validate(true)) {
            Log.i(TAG, "Validation Failed");
            return;
        }
        if (!isNetworkAvailable()) {
            showSnackbar("PLease Check Your Netwok Connection");
            return;
        }
        pushToServer(false);


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CheckInOut.this, MainActivity.class));
        finish();
    }
}

package com.ara.advent;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.Image;
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
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.Adapter.CustomerAdapter;
import com.ara.advent.http.HttpCaller;
import com.ara.advent.http.HttpRequest;
import com.ara.advent.http.HttpResponse;
import com.ara.advent.http.MySingleton;
import com.ara.advent.models.Customer;
import com.ara.advent.models.OncallTsModel;
import com.ara.advent.utils.AppConstants;

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
import java.util.IllegalFormatCodePointException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.MY_CAMERA_REQUEST_CODE;
import static com.ara.advent.utils.AppConstants.REQUEST_IMAGE_CAPTURE;

public class OncallTripsheet extends AppCompatActivity {


    private static final String TAG = "Oncall TripSheet";
    private static final int REQUEST_TAKE_IMAGE_ONE = 1;
    private static final int REQUEST_TAKE_IMAGE_TWO = 2;
    private static final int REQUEST_TAKE_IMAGE_THREE = 3;

    @BindView(R.id.OncallTripsheet_layout)
    ScrollView OntripLayout;
    @BindView(R.id.OntripsheetId)
    EditText OnTripsheetId;
    @BindView(R.id.Oncall_spinnerCustomer)
    Spinner OnTripsheet_cusSpinner;
    @BindView(R.id.OntripsheetDate)
    TextView OntripDate;
    @BindView(R.id.OnSubmit)
    Button OnSubmit_button;
    @BindView(R.id.cardview1)
    CardView CardInput_image1;
    @BindView(R.id.cardview2)
    CardView CardInput_image2;
    @BindView(R.id.cardview3)
    CardView CardInput_image3;
    @BindView(R.id.input_cameraImage1)
    ImageView input_image1;
    @BindView(R.id.input_cameraImage2)
    ImageView input_image2;
    @BindView(R.id.input_cameraImage3)
    ImageView input_image3;
    ArrayList<Customer> customer;
    DatePickerDialog datePickerDialog;
    String valCusId;
    OncallTsModel oncallTsModel;
    Boolean date_is_changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oncall_tripsheet);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        OntripLayout.setFocusableInTouchMode(true);
        OntripLayout.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);


        customer = new ArrayList<Customer>();
        oncallTsModel = new OncallTsModel();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        OntripDate.setText(date);

        if (isNetworkAvailable()) {
            getCustomerJson();
        } else {
            showSnackbar("Something Went Wrong,Check Internet Connection");
        }

        OntripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OntripDateMethod();
            }
        });
        OnSubmit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSubmit_buttonMethod();
            }
        });

        CardInput_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionForCamera(REQUEST_TAKE_IMAGE_ONE);
            }
        });
        CardInput_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionForCamera(REQUEST_TAKE_IMAGE_TWO);
            }
        });
        CardInput_image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionForCamera(REQUEST_TAKE_IMAGE_THREE);
            }
        });

    }

    private void requestPermissionForCamera(int request) {

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
            dispatchTakePictureIntent(request);
        }
    }

    private void showSnackbar(String message, final boolean finishApp) {
        final Snackbar snackbar = Snackbar.make(OntripLayout, message,
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

    private void OnSubmit_buttonMethod() {


        if (!formValid()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }
        PushtoServer();
    }

    private void PushtoServer() {
        HttpRequest httpRequest = new HttpRequest(AppConstants.ONCALL_BOOKING_URL);
        httpRequest.setRequestBody(oncallTsModel.multipartRequest());
        try {
            new HttpCaller(this,"Processing..."){
                @Override
                public void onResponse(HttpResponse response) {
                    super.onResponse(response);
                    if (response.getStatus() == HttpResponse.ERROR) {
                        Log.e(TAG,"Error response when pushto server "+response.getMesssage());
                        onBookingFailure(response);
                    } else {

                        Log.e(TAG,"Booking Ride Success"+ response.getMesssage());
                       startActivity(new Intent(OncallTripsheet.this,MainActivity.class)
                       .putExtra("OncallBooked","Trip Sheet Added Successfully"));
                       finish();
                    }
                }
            }.execute(httpRequest);

        } catch (Exception execption ) {
            Log.e(TAG,"Exception on submit dat to server"+execption);
        }
    }
    private void onBookingFailure(HttpResponse response) {
        if (response != null) {
            Toast.makeText(OncallTripsheet.this, "Something Went Wrong! Please check the network connection.", Toast.LENGTH_SHORT).show();
        }
    }
    private void OntripDateMethod() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(OncallTripsheet.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String dend = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        OntripDate.setText(dend);
                        date_is_changed = true;
                        Log.e(TAG, " date ---" + dend);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void getCustomerJson() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConstants.CUSTOMER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "customer_response-" + response);

                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String customerid = jsonObject.getString("customerId");
                        String customername = jsonObject.getString("customername");

                        customer.add(new Customer(customerid, customername));
                    }

                    CustomerAdapter customerAdapter = new CustomerAdapter(OncallTripsheet.this, android.R.layout.simple_spinner_item, customer);
                    OnTripsheet_cusSpinner.setAdapter(customerAdapter);

                    OnTripsheet_cusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Customer selectedNAme = customer.get(position);

                            valCusId = selectedNAme.getCus_id();
                            Log.e(TAG, "value for customer id" + valCusId);
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
                showSnackbar("Something went wrong ,PLease Contact Suppport");
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void dispatchTakePictureIntent(int REQUEST_TAKE_PHOTO) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(REQUEST_TAKE_PHOTO);
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
        if (requestCode == REQUEST_TAKE_IMAGE_ONE && resultCode == RESULT_OK) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(oncallTsModel.getImage_file_one());
            compressImageFile(imageBitmap, oncallTsModel.getImage_file_one());
            input_image1.setImageBitmap(BitmapFactory.decodeFile(oncallTsModel.getImage_file_one()));

        } else if (requestCode == REQUEST_TAKE_IMAGE_TWO && resultCode == RESULT_OK) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(oncallTsModel.getImage_file_two());
            compressImageFile(imageBitmap, oncallTsModel.getImage_file_two());
            input_image2.setImageBitmap(BitmapFactory.decodeFile(oncallTsModel.getImage_file_one()));

        } else if (requestCode == REQUEST_TAKE_IMAGE_THREE && resultCode == RESULT_OK) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(oncallTsModel.getImage_file_three());
            compressImageFile(imageBitmap, oncallTsModel.getImage_file_three());
            input_image3.setImageBitmap(BitmapFactory.decodeFile(oncallTsModel.getImage_file_one()));
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


    private File createImageFile(int REQUEST) throws IOException {
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
        if (REQUEST == 1) {
            oncallTsModel.setImage_file_one(mCurrentPhotoPath);
        } else if (REQUEST == 2) {
            oncallTsModel.setImage_file_two(mCurrentPhotoPath);
        } else if (REQUEST == 3) {
            oncallTsModel.setImage_file_three(mCurrentPhotoPath);
        }

        return image;
    }
    public boolean formValid() {

        boolean error = true;
        String edit_tripId = OnTripsheetId.getText().toString();

        if (edit_tripId.isEmpty()) {
            OnTripsheetId.setError("TripSheet Id not valid");
            error = false;
        } else {
            OnTripsheetId.setError(null);
        }
        if (date_is_changed = false) {
            showSnackbar("Date is required");
        }

        return error;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private void showSnackbar(String message) {
        final Snackbar snackbar = Snackbar.make(OntripLayout, message,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.text_ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OncallTripsheet.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }
}

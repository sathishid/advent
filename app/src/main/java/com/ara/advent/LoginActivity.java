package com.ara.advent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ara.advent.http.HttpCaller;
import com.ara.advent.http.HttpRequest;
import com.ara.advent.http.HttpResponse;
import com.ara.advent.models.User;
import com.ara.advent.utils.AppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.PARAM_USER_ID;
import static com.ara.advent.utils.AppConstants.PARAM_USER_NAME;
import static com.ara.advent.utils.AppConstants.PREFERENCE_NAME;

public class LoginActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = "LoginActivity";
    @BindView(R.id.input_login_userName)
    EditText _login_userName;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.scroll_view_login)
    ScrollView _rootScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed(null);
            return;
        }

        _loginButton.setEnabled(false);

        User user = new User();
        user.setUserName(_login_userName.getText().toString());
        user.setPassword(_passwordText.getText().toString());

        _loginButton.setEnabled(true);
        HttpRequest request = new HttpRequest(AppConstants.getLoginAction());

        request.setRequestBody(user.toRequestBody());
        try {
            new HttpCaller(this, "Validating User") {
                @Override
                public void onResponse(HttpResponse response) {
                    super.onResponse(response);
                    if (response.getStatus() == HttpResponse.ERROR) {
                        onLoginFailed(response);
                    } else {

                        onLoginSuccess(response);
                    }
                }
            }.execute(request);
        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage(), exception);
            showSnackbar(exception.getMessage());
        }
    }

    public boolean validate() {
        boolean valid = true;

        String mobile = _login_userName.getText().toString();
        String password = _passwordText.getText().toString();

        if (mobile.isEmpty() || mobile.length() <= 4) {
            _login_userName.setError("Enter the Valid Name");
            valid = false;
        } else {
            _login_userName.setError(null);
        }

        if (password.isEmpty() || password.length() < 3 || password.length() > 10) {
            _passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private boolean hasGoogleServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int isAvailable = apiAvailability.isGooglePlayServicesAvailable(this);
        switch (isAvailable) {
            case ConnectionResult.SUCCESS:
                return true;
            default:
                apiAvailability.showErrorNotification(this, isAvailable);
                return false;
        }
    }

    public void onLoginSuccess(HttpResponse response) {

        try {
            JSONArray jsonArray = new JSONArray(response.getMesssage());
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            _loginButton.setEnabled(true);
            if (jsonObject.getString(AppConstants.LOGIN_RESULT)
                    .compareToIgnoreCase(AppConstants.SUCCESS_MESSAGE) != 0) {
                Toast.makeText(getBaseContext(), "Invalid Mobile or Password!", Toast.LENGTH_LONG).show();
                return;
            }
            Log.i("LoginSuccess", response.getMesssage());


            User user = AppConstants.toUser(jsonObject);
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PARAM_USER_ID, user.getId());
            editor.putString(PARAM_USER_NAME, user.getUserName());
            editor.commit();

            Intent result = new Intent();
            result.putExtra("result", "success");
            setResult(RESULT_OK, result);
            finish();

        } catch (Exception e) {
            Log.e("On Login Success", e.getMessage());
            Toast.makeText(LoginActivity.this, "Something Went Wrong,Contact Support", Toast.LENGTH_LONG);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            Log.e(TAG, "Permission Granted");
        }
    }

    public void onLoginFailed(HttpResponse response) {
        _loginButton.setEnabled(true);
        if (response != null) {
            showSnackbar("Something went wrong, Check Network connection!");
            Log.e("Customer Login Failed", response.getMesssage());
        }
    }

    public void login_onClick(View view) {

        login();

    }

    private void showSnackbar(String message) {
        final Snackbar snackbar = Snackbar.make(_rootScrollView, message,
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

package com.ara.advent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import com.ara.advent.utils.AppConstants;
import com.ara.advent.utils.TripsheetActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.PARAM_USER_ID;
import static com.ara.advent.utils.AppConstants.PREFERENCE_NAME;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    @BindView(R.id.layout_scroll_view)
    ScrollView rootLayout;

    @BindView(R.id.goto_CheckInOut)
    Button gotoCheckInOut;

    @BindView(R.id.goto_TripSheet)
    Button gotoTripSheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        if (sharedPreferences.contains(PARAM_USER_ID)) {

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, AppConstants.MAIN_REQUEST_CODE);
        }
        if (isNetworkAvailable()) {

        } else {
            Snackbar bar = Snackbar.make(rootLayout, "Something went wrong, Check Network connection!", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle user action
                        }
                    });

            bar.show();
        }

        gotoCheckInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CheckInOut.class));
                finish();
            }
        });
        gotoTripSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TripsheetActivity.class));
                finish();
            }
        });
    }

    /* @Override
     protected void onResume() {
         super.onResume();

     }

     @Override
     protected void onPause() {
         super.onPause();

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
 */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.MAIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}

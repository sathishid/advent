package com.ara.advent.utils;

import android.util.Log;

import com.ara.advent.models.User;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AppConstants {
    public static String PREFERENCE_NAME = "amaze_advent.ara";
    public static final int MAIN_REQUEST_CODE = 0;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int MY_CAMERA_REQUEST_CODE = 100;

    private static final String URL = "http://arasoftwares.in/atnc-app/android_atncfile.php?action=";
    private static final String SAVE_ACTION = "save";
    private static final String LOGIN_ACTION = "login_details";


    public static String getLoginAction() {
        return URL + LOGIN_ACTION;
    }

    public static String getSaveAction() {
        return URL + SAVE_ACTION;
    }

    public static final String PARAM_USER_ID = "userid";
    public static final String PARAM_USER_NAME = "username";
    public static final String PARAM_LOGIN = "login";
    public static final String PARAM_PASSWORD = "password";
    public static final String LOGIN_RESULT = "login";
    public static String SUCCESS_MESSAGE = "success";


    public static User user;

    public static void setUser(User tmpUser) {
        user = tmpUser;
    }

    public static User getUser() {
        return user;
    }

    public static User toUser(JSONObject jsonObject) {
        try {

            int id = jsonObject.getInt(PARAM_USER_ID);
            String name = jsonObject.getString(AppConstants.PARAM_USER_NAME);
            user = new User(id, name);
        } catch (Exception e) {
            Log.e("Register Login User", e.getMessage());
        }
        return user;
    }

    public static String todayAsString() {
        Calendar calendar = Calendar.getInstance();

        return calendar.get(Calendar.DATE)+"/"
                + calendar.get(Calendar.MONTH) +"/"
                + calendar.get(Calendar.YEAR);
    }

    public static String getAddressAsString(ArrayList<String> addressFraments) {
        String strAdddress = "";
        for (String address : addressFraments) {
            strAdddress += address + "\n\r";
        }
        return strAdddress;
    }

}

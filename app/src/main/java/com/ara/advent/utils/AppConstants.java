package com.ara.advent.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ara.advent.models.Attendance;
import com.ara.advent.models.User;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

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

    public static final int CHECK_IN = 1;
    public static final int CHECK_OUT = 2;


    public static final String PARAM_IMAGE = "userimage";
    public static final String PARAM_ID = "id";
    public static final String PARAM_LOCATION = "location";
    public static final String PARAM_LATTITUDE = "lattitude";
    public static final String PARAM_LONGITUDE = "langitude";
    public static final String PARAM_TYPE = "type";

    public static final String CHECK_IN_TIME = "CheckInTime";


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

    public static String timeAsString(Calendar calendar) {
        String am_pm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
        int hour=Math.abs(12-calendar.get(Calendar.HOUR));
        return hour + ":" +
                calendar.get(Calendar.MINUTE) + " " + am_pm;

    }

    public static String calendarAsString(Calendar calendar) {

        return calendar.get(Calendar.DATE) + "/"
                + calendar.get(Calendar.MONTH) + "/"
                + calendar.get(Calendar.YEAR);
    }

    public static String getAddressAsString(ArrayList<String> addressFraments) {
        String strAdddress = "";
        for (String address : addressFraments) {
            strAdddress += address + "\n\r";
        }
        return strAdddress;
    }

    public static boolean isNotHalfAnHourDifference(Calendar inTime) {
        Calendar currentTime = Calendar.getInstance();
        if (inTime.get(Calendar.HOUR) != currentTime.get(Calendar.HOUR))
            return false;
        if (inTime.get(Calendar.AM_PM) != currentTime.get(Calendar.AM_PM))
            return false;
        if ((currentTime.get(Calendar.MINUTE) - inTime.get(Calendar.MINUTE)) > 30)
            return false;

        return true;
    }

    public static Bitmap compressImage(Bitmap bitmap, Attendance attendance) {
        Bitmap imageBitmap = bitmap;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            imageBitmap = null;
            byte[] imageAsArray = outputStream.toByteArray();
            attendance.setImage(imageAsArray);
            imageBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(outputStream.toByteArray()));
            return imageBitmap;
        } catch (Exception exception) {
            Log.e("AppConstants", exception.getMessage(), exception);
        }
        return imageBitmap;
    }

    public static Calendar timeStringToCalendar(String time) {
        String[] hrsMin = time.split(":");
        int hours = Integer.parseInt(hrsMin[0]);
        String[] minAm_Pm = hrsMin[1].split(" ");
        int minutes = Integer.parseInt(minAm_Pm[0]);
        int am_pm = Calendar.AM;
        if (minAm_Pm[1] == "PM") {
            am_pm = Calendar.PM;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.AM_PM, am_pm);

        return calendar;

    }
}

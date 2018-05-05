package com.ara.advent.models;

import java.io.File;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ara.advent.utils.AppConstants.CHECK_IN;
import static com.ara.advent.utils.AppConstants.CHECK_OUT;
import static com.ara.advent.utils.AppConstants.PARAM_ID;
import static com.ara.advent.utils.AppConstants.PARAM_IMAGE;
import static com.ara.advent.utils.AppConstants.PARAM_LATTITUDE;
import static com.ara.advent.utils.AppConstants.PARAM_LOCATION;
import static com.ara.advent.utils.AppConstants.PARAM_LONGITUDE;
import static com.ara.advent.utils.AppConstants.PARAM_TYPE;

public class Attendance {
    private double longitude;
    private double latitude;
    private Calendar attendanceDate;
    private User user;
    private String checkInOutAddress;

    private Calendar checkInTime;
    private Calendar checkOutTime;


    private String imageFileName;


    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    private boolean hasAlreadyCheckIn;


    public boolean hasCheckedIn() {
        return hasAlreadyCheckIn;
    }

    public void setAlreadyCheckedIn(boolean checkedIn) {
        hasAlreadyCheckIn = checkedIn;
    }

    public Calendar getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Calendar checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Calendar getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Calendar checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Calendar getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Calendar attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getCheckOutAddress() {
        return checkInOutAddress;
    }


    public void setCheckOutAddress(String checkOutAddress) {
        this.checkInOutAddress = checkOutAddress;
    }

    public String getCheckInAddress() {
        return checkInOutAddress;
    }

    public void setCheckInAddress(String checkInAddress) {
        this.checkInOutAddress = checkInAddress;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MultipartBody toMultiPartBody(boolean isCheckIn) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        MediaType mediaType = MediaType.parse("image/jpeg");

        // String base64Image = Base64.encodeToString(getImage(), Base64.DEFAULT);
        builder.addFormDataPart(PARAM_IMAGE, getImageFileName(),
                RequestBody.create(mediaType, new File(getImageFileName())));
        builder.addFormDataPart(PARAM_ID, user.getId() + "");
        if (isCheckIn) {
            builder.addFormDataPart(PARAM_LOCATION, getCheckInAddress());
            builder.addFormDataPart(PARAM_TYPE, CHECK_IN + "");

        } else {
            builder.addFormDataPart(PARAM_LOCATION, getCheckOutAddress());
            builder.addFormDataPart(PARAM_TYPE, CHECK_OUT + "");
        }

        builder.addFormDataPart(PARAM_LATTITUDE, latitude + "");
        builder.addFormDataPart(PARAM_LONGITUDE, longitude + "");

        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }

    public String getImageFileName() {
        return imageFileName;
    }


    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    private String imageFilePath;

}

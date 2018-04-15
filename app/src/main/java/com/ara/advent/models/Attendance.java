package com.ara.advent.models;

import java.util.ArrayList;

public class Attendance {
    private float longitude;
    private float latitude;
    private String checkInPlace;
    private String checkOutPlace;
    private String User;
    private String checkInAddress;
    private String checkOutAddress;

    public String getCheckOutAddress() {
        return checkOutAddress;
    }

    public void setCheckOutAddress(String checkOutAddress) {
        this.checkOutAddress = checkOutAddress;
    }

    public String getCheckInAddress() {
        return checkInAddress;
    }

    public void setCheckInAddress(String checkInAddress) {
        this.checkInAddress = checkInAddress;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getCheckInPlace() {
        return checkInPlace;
    }

    public void setCheckInPlace(String checkInPlace) {
        this.checkInPlace = checkInPlace;
    }

    public String getCheckOutPlace() {
        return checkOutPlace;
    }

    public void setCheckOutPlace(String checkOutPlace) {
        this.checkOutPlace = checkOutPlace;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }
}

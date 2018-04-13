package com.ara.advent.models;

import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

public class Attendance {
    private float longitude;
    private float latitude;
    private String checkInPlace;
    private String checkOutPlace;
    private String User;
    private ArrayList<String> addressFraments;

    public ArrayList<String> getAddressFraments() {
        return addressFraments;
    }

    public void setAddressFraments(ArrayList<String> addressFraments) {
        this.addressFraments = addressFraments;
    }
    public String getAddressAsString(){
        String strAdddress="";
        for (String address:addressFraments) {
            strAdddress+=address+"\n\r";
        }
        return strAdddress;
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

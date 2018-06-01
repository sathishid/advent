package com.ara.advent.models;

import android.util.Log;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ara.advent.utils.AppConstants.IP;
import static com.ara.advent.utils.AppConstants.PARAM_CLOSING_KM_ONCALL;
import static com.ara.advent.utils.AppConstants.PARAM_CLOSIN_TIME_ONCALL;
import static com.ara.advent.utils.AppConstants.PARKIGNBILL;
import static com.ara.advent.utils.AppConstants.PARAM_TRIPSHEETFRONT;
import static com.ara.advent.utils.AppConstants.PARAM_TRIPSHEETBACK;
import static com.ara.advent.utils.AppConstants.TOLLGAETEBILL;
import static com.ara.advent.utils.AppConstants.PARAM_PERMITBILL;
import static com.ara.advent.utils.AppConstants.PARAM_PARKAMT;
import static com.ara.advent.utils.AppConstants.PARAM_PERAMT;
import static com.ara.advent.utils.AppConstants.PARAM_TOLLAMT;
import static com.ara.advent.utils.AppConstants.PARAM_TRIPSHEET_ID;
import static com.ara.advent.utils.AppConstants.TOTALKM;
import static com.ara.advent.utils.AppConstants.TOTALTIME;
import static com.ara.advent.utils.AppConstants.USERID;
import static com.ara.advent.utils.AppConstants.user;

/**
 * Created by User on 08-May-18.
 */

public class OncallTsModel {

    String trip_Id;
    String customer_id;
    String Def_date;
    String image_file_one;
    String image_file_two;
    String image_file_three;
    String image_file_four;

    public String getPark_amount() {
        return park_amount;
    }

    public void setPark_amount(String park_amount) {
        this.park_amount = park_amount;
    }

    public String getPemit_amount() {
        return pemit_amount;
    }

    public void setPemit_amount(String pemit_amount) {
        this.pemit_amount = pemit_amount;
    }

    public String getToll_amout() {
        return toll_amout;
    }

    public void setToll_amout(String toll_amout) {
        this.toll_amout = toll_amout;
    }

    String park_amount;
    String pemit_amount;
    String toll_amout;

    public String getImage_file_one_back() {
        return image_file_one_back;
    }

    public void setImage_file_one_back(String image_file_one_back) {
        this.image_file_one_back = image_file_one_back;
    }

    String image_file_one_back;
    String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTotalkilometer() {
        return totalkilometer;
    }

    public void setTotalkilometer(String totalkilometer) {
        this.totalkilometer = totalkilometer;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    String totalkilometer;
    String totalTime;

    public String getClosingkilometer() {
        return closingkilometer;
    }

    public void setClosingkilometer(String closingkilometer) {
        this.closingkilometer = closingkilometer;
    }

    public String getClosingtime() {
        return closingtime;
    }

    public void setClosingtime(String closingtime) {
        this.closingtime = closingtime;
    }

    String closingkilometer;
    String closingtime;

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }


    public String getImage_file_four() {
        return image_file_four;
    }

    public void setImage_file_four(String image_file_four) {
        this.image_file_four = image_file_four;
    }

    public String getImage_file_two() {
        return image_file_two;
    }

    public void setImage_file_two(String image_file_two) {
        this.image_file_two = image_file_two;
    }

    public String getImage_file_three() {
        return image_file_three;
    }

    public void setImage_file_three(String image_file_three) {
        this.image_file_three = image_file_three;
    }

    public String getTrip_Id() {
        return trip_Id;
    }

    public void setTrip_Id(String trip_Id) {
        this.trip_Id = trip_Id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getDef_date() {
        return Def_date;
    }

    public void setDef_date(String def_date) {
        Def_date = def_date;
    }

    public String getImage_file_one() {
        return image_file_one;
    }

    public void setImage_file_one(String image_file_one) {
        this.image_file_one = image_file_one;
    }

    public MultipartBody multipartRequest() {


        Log.e("TAg", "message string id " + customer_id);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        MediaType mediaType = MediaType.parse("image/jpeg");
        builder.addFormDataPart(PARAM_TRIPSHEETFRONT, getImage_file_one(),
                RequestBody.create(mediaType, new File(getImage_file_one())));
        if (getImage_file_two() != null)
            builder.addFormDataPart(PARAM_PERMITBILL, getImage_file_two(),
                    RequestBody.create(mediaType, new File(getImage_file_two())));
        if (getImage_file_three() != null)
            builder.addFormDataPart(PARKIGNBILL, getImage_file_three(),
                    RequestBody.create(mediaType, new File(getImage_file_three())));
        if (getImage_file_four() != null)
            builder.addFormDataPart(TOLLGAETEBILL, getImage_file_four(),
                    RequestBody.create(mediaType, new File(getImage_file_four())));

        builder.addFormDataPart(PARAM_TRIPSHEETBACK, getImage_file_one_back(),
                RequestBody.create(mediaType, new File(getImage_file_one_back())));
        builder.addFormDataPart(PARAM_TRIPSHEET_ID, getTrip_Id());
        builder.addFormDataPart(USERID, String.valueOf(user.getId()));
        builder.addFormDataPart(PARAM_CLOSING_KM_ONCALL, closingkilometer);
        builder.addFormDataPart(PARAM_CLOSIN_TIME_ONCALL, closingtime);
        builder.addFormDataPart(TOTALKM, getTotalkilometer());
        builder.addFormDataPart(TOTALTIME, getTotalTime());
        builder.addFormDataPart(IP, getLocalIpAddress());
        builder.addFormDataPart(PARAM_PARKAMT, getPark_amount());
        builder.addFormDataPart(PARAM_PERAMT, getPemit_amount());
        builder.addFormDataPart(PARAM_TOLLAMT, getToll_amout());
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }

    @Override
    public String toString() {
        return PARAM_TRIPSHEET_ID + "-" + getTrip_Id() + "\n" +
                USERID + "-" + user.getId() + "\n" +
                PARAM_CLOSING_KM_ONCALL + "-" + closingkilometer + "\n" +
                PARAM_CLOSIN_TIME_ONCALL + "-" + closingtime + "\n" +
                TOTALKM + "-" + getTotalkilometer() + "\n" +
                TOTALTIME + "-" + getTotalTime() + "\n" +
                IP + "-" + getLocalIpAddress() + "\n"
                + PARAM_PARKAMT + "-" + getPark_amount() + "\n"
                + PARAM_PERAMT + "-" + getPemit_amount() + "\n" +
                PARAM_TOLLAMT + "-" + getToll_amout();
    }
}

package com.ara.advent.models;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import com.ara.advent.utils.AppConstants;

import java.io.File;

import static com.ara.advent.utils.AppConstants.PARAM_CUSTOMER_ID;
import static com.ara.advent.utils.AppConstants.PARAM_DEF_DATE;
import static com.ara.advent.utils.AppConstants.PARAM_IMG_ONE;
import static com.ara.advent.utils.AppConstants.PARAM_IMG_THREE;
import static com.ara.advent.utils.AppConstants.PARAM_IMG_TWO;
import static com.ara.advent.utils.AppConstants.PARAM_TRIPSHEET_ID;

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

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        MediaType mediaType = MediaType.parse("image/jpeg");
        builder.addFormDataPart(PARAM_IMG_ONE,getImage_file_one(),
                RequestBody.create(mediaType,new File(getImage_file_one())));
        builder.addFormDataPart(PARAM_IMG_TWO,getImage_file_one(),
                RequestBody.create(mediaType,new File(getImage_file_two())));
        builder.addFormDataPart(PARAM_IMG_THREE,getImage_file_one(),
                RequestBody.create(mediaType,new File(getImage_file_three())));
        builder.addFormDataPart(PARAM_TRIPSHEET_ID,trip_Id);
        builder.addFormDataPart(PARAM_CUSTOMER_ID,customer_id);
        builder.addFormDataPart(PARAM_DEF_DATE,Def_date);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }

}

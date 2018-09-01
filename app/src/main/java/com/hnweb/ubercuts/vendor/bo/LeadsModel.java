package com.hnweb.ubercuts.vendor.bo;

import java.io.Serializable;

public class LeadsModel implements Serializable {

    private String my_task_id;
    private String my_task_message;
    private String my_task_price;
    private String my_task_job_location;
    private String u_name;
    private String category_id;
    private String category_name;
    private String sub_category_id;
    private String sub_category_name;
    private String latitude;
    private String longitude;
    private String u_img;

    public String getU_img() {
        return u_img;
    }

    public void setU_img(String u_img) {
        this.u_img = u_img;
    }

    public String getMy_task_id() {
        return my_task_id;
    }

    public void setMy_task_id(String my_task_id) {
        this.my_task_id = my_task_id;
    }

    public String getMy_task_message() {
        return my_task_message;
    }

    public void setMy_task_message(String my_task_message) {
        this.my_task_message = my_task_message;
    }

    public String getMy_task_price() {
        return my_task_price;
    }

    public void setMy_task_price(String my_task_price) {
        this.my_task_price = my_task_price;
    }

    public String getMy_task_job_location() {
        return my_task_job_location;
    }

    public void setMy_task_job_location(String my_task_job_location) {
        this.my_task_job_location = my_task_job_location;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }
}

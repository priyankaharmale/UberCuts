package com.hnweb.ubercuts.user.bo;

import java.io.Serializable;

public class MyTaskModel implements Serializable{

    private String message_code;
    private String message;
    private String my_task_id;
    private String category_name;
    private String category_id;
    private String sub_category_name;
    private String sub_category_id;
    private String job_location_name;
    private String beautician;
    private String beautician_id;
    private String vendor_name;
    private String date;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String status;

    public MyTaskModel(String message_code, String message, String my_task_id, String category_name, String category_id, String beautician, String date) {
        this.message_code = message_code;
        this.message = message;
        this.my_task_id = my_task_id;
        this.category_name = category_name;
        this.category_id = category_id;
        this.beautician = beautician;
        this.date = date;
    }

    public MyTaskModel() {

    }

    public String getMessage_code() {
        return message_code;
    }

    public void setMessage_code(String message_code) {
        this.message_code = message_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMy_task_id() {
        return my_task_id;
    }

    public void setMy_task_id(String my_task_id) {
        this.my_task_id = my_task_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getBeautician() {
        return beautician;
    }

    public void setBeautician(String beautician) {
        this.beautician = beautician;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public String getJob_location_name() {
        return job_location_name;
    }

    public void setJob_location_name(String job_location_name) {
        this.job_location_name = job_location_name;
    }

    public String getBeautician_id() {
        return beautician_id;
    }

    public void setBeautician_id(String beautician_id) {
        this.beautician_id = beautician_id;
    }
}

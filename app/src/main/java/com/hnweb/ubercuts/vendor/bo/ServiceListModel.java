package com.hnweb.ubercuts.vendor.bo;

import java.io.Serializable;

/**
 * Created by PC-21 on 09-Apr-18.
 */

public class ServiceListModel implements Serializable{

    private int service_message_code;
    private String service_message;
    private String service_service_id;
    private String service_default_price;
    private String service_todays_offer;
    private String service_category_id;
    private String service_category_name;
    private String service_sub_category_id;
    private String service_sub_category_name;

    public int getService_message_code() {
        return service_message_code;
    }

    public void setService_message_code(int service_message_code) {
        this.service_message_code = service_message_code;
    }

    public String getService_message() {
        return service_message;
    }

    public void setService_message(String service_message) {
        this.service_message = service_message;
    }

    public String getService_service_id() {
        return service_service_id;
    }

    public void setService_service_id(String service_service_id) {
        this.service_service_id = service_service_id;
    }

    public String getService_default_price() {
        return service_default_price;
    }

    public void setService_default_price(String service_default_price) {
        this.service_default_price = service_default_price;
    }

    public String getService_todays_offer() {
        return service_todays_offer;
    }

    public void setService_todays_offer(String service_todays_offer) {
        this.service_todays_offer = service_todays_offer;
    }

    public String getService_category_id() {
        return service_category_id;
    }

    public void setService_category_id(String service_category_id) {
        this.service_category_id = service_category_id;
    }

    public String getService_category_name() {
        return service_category_name;
    }

    public void setService_category_name(String service_category_name) {
        this.service_category_name = service_category_name;
    }

    public String getService_sub_category_id() {
        return service_sub_category_id;
    }

    public void setService_sub_category_id(String service_sub_category_id) {
        this.service_sub_category_id = service_sub_category_id;
    }

    public String getService_sub_category_name() {
        return service_sub_category_name;
    }

    public void setService_sub_category_name(String service_sub_category_name) {
        this.service_sub_category_name = service_sub_category_name;
    }
}

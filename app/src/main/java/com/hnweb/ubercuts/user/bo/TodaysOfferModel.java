package com.hnweb.ubercuts.user.bo;

import java.io.Serializable;

public class TodaysOfferModel implements Serializable {

    private String today_service_id;
    private String today_default_price;
    private String today_today_offer;
    private String today_category_id;

    public String getToday_sub_category_id() {
        return today_sub_category_id;
    }

    public void setToday_sub_category_id(String today_sub_category_id) {
        this.today_sub_category_id = today_sub_category_id;
    }

    public String getToday_sub_category_name() {
        return today_sub_category_name;
    }

    public void setToday_sub_category_name(String today_sub_category_name) {
        this.today_sub_category_name = today_sub_category_name;
    }

    private String today_category_name;
    private String today_sub_category_id;
    private String today_sub_category_name;

    private String today_u_name;

    public String getToday_service_id() {
        return today_service_id;
    }

    public void setToday_service_id(String today_service_id) {
        this.today_service_id = today_service_id;
    }

    public String getToday_default_price() {
        return today_default_price;
    }

    public void setToday_default_price(String today_default_price) {
        this.today_default_price = today_default_price;
    }

    public String getToday_today_offer() {
        return today_today_offer;
    }

    public void setToday_today_offer(String today_today_offer) {
        this.today_today_offer = today_today_offer;
    }

    public String getToday_category_id() {
        return today_category_id;
    }

    public void setToday_category_id(String today_category_id) {
        this.today_category_id = today_category_id;
    }

    public String getToday_category_name() {
        return today_category_name;
    }

    public void setToday_category_name(String today_category_name) {
        this.today_category_name = today_category_name;
    }


    public String getToday_u_name() {
        return today_u_name;
    }

    public void setToday_u_name(String today_u_name) {
        this.today_u_name = today_u_name;
    }
}

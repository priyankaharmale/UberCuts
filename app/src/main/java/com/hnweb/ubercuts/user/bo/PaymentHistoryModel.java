package com.hnweb.ubercuts.user.bo;

import java.io.Serializable;

public class PaymentHistoryModel implements Serializable {

    private String pay_booking_amount;
    private String pay_payment_date;
    private String pay_beautician_name;
    private String pay_category_name;
    private String pay_category_id;
    private String pay_date;
    private String pay_time;

    public String getU_img() {
        return u_img;
    }

    public void setU_img(String u_img) {
        this.u_img = u_img;
    }

    private String sub_service_id;
    private String u_img;
    private String sub_service_name;

    public String getPay_booking_amount() {
        return pay_booking_amount;
    }

    public void setPay_booking_amount(String pay_booking_amount) {
        this.pay_booking_amount = pay_booking_amount;
    }

    public String getPay_payment_date() {
        return pay_payment_date;
    }

    public void setPay_payment_date(String pay_payment_date) {
        this.pay_payment_date = pay_payment_date;
    }

    public String getPay_beautician_name() {
        return pay_beautician_name;
    }

    public void setPay_beautician_name(String pay_beautician_name) {
        this.pay_beautician_name = pay_beautician_name;
    }

    public String getPay_category_name() {
        return pay_category_name;
    }

    public void setPay_category_name(String pay_category_name) {
        this.pay_category_name = pay_category_name;
    }

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getSub_service_id() {
        return sub_service_id;
    }

    public void setSub_service_id(String sub_service_id) {
        this.sub_service_id = sub_service_id;
    }

    public String getSub_service_name() {
        return sub_service_name;
    }

    public void setSub_service_name(String sub_service_name) {
        this.sub_service_name = sub_service_name;
    }

    public String getPay_category_id() {
        return pay_category_id;
    }

    public void setPay_category_id(String pay_category_id) {
        this.pay_category_id = pay_category_id;
    }
}

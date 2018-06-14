package com.hnweb.ubercuts.contants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Priyanka H on 13/06/2018.
 */
public class AppConstant {


    /*============================================Register==================================================*/
    public static final String KEY_U_NAME = "u_name";
    public static final String KEY_U_EMAIL = "u_email";
    public static final String KEY_U_PHONE = "u_phone";
    public static final String KEY_U_STREET = "u_street";
    public static final String KEY_U_CITY = "u_city";
    public static final String KEY_U_STATE = "u_state";
    public static final String KEY_U_COUNTRY = "u_country";
    public static final String KEY_U_ZIPCODE = "u_zipcode";
    public static final String KEY_U_PASSWORD = "u_password";
    public static final String KEY_U_CARDNO = "card_no";
    public static final String KEY_U_MON = "exp_mon";
    public static final String KEY_U_YEAR = "exp_year";
    public static final String KEY_U_CVV = "cvv";
    public static final String KEY_U_IMAGE = "img";
    public static final String KEY_U_DEVICETYPE = "device_type";
    public static final String KEY_U_DEVICETOKEN = "devicetoken";

    public static final String BASE_URL = "http://tech599.com/tech599.com/johnaks/Ubercuts/api/";

    /*=================================================Register User=========================================================*/
    public static final String API_REGISTER_USER = "register_user.php";

    /*=================================================Login User=========================================================*/
    public static final String API_LOGIN_USER = BASE_URL +"login.php";

    /*=================================================Forgot Password User=========================================================*/
    public static final String API_FORGOTPWD_USER = BASE_URL +"login.php";
    /*=================================================Country List=========================================================*/
    public static final String API_GETLIST_COUNTRIES = BASE_URL + "get_countries.php";
    public static final String API_GETLIST_STATE = BASE_URL + "get_states.php";
    public static final String API_GETLIST_CITY = BASE_URL + "get_cities.php";

    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


}

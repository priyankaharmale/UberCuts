package com.hnweb.ubercuts.contants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Priyanka H on 13/06/2018.
 */
public class AppConstant {


    public static final String KEY_PASSWORD = "pwd";
    public static final String KEY_CONFRIMPASSWORD = "pwd";
    /*============================================Register==================================================*/
    public static final String KEY_PHONE = "mobno";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";

    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "long";

    public static final String USER_ID = "reg_id";
    public static final String CHURCH_ID = "cid";
    public static final String EVENT_ID = "eid";
    public static final String FAV_STATUS = "fav_status";


    public static final String BASE_URL = "http://tech599.com/tech599.com/johnaks/Ubercuts/api/";

    /*=================================================Login User=========================================================*/

    /*=================================================Country List=========================================================*/
    public static final String API_GETLIST_COUNTRIES = BASE_URL + "get_countries.php";


    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


}

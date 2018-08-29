package com.hnweb.ubercuts.contants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Priyanka H on 13/06/2018.
 */
public class AppConstant {


    /*============================================Register==================================================*/
    public static final String KEY_NAME = "u_name";
    public static final String KEY_ID = "u_id";
    public static final String KEY_EMAIL = "u_email";
    public static final String KEY_PHONE = "u_phone";
    public static final String KEY_STREET = "u_street";
    public static final String KEY_CITY = "u_city";
    public static final String KEY_STATE = "u_state";
    public static final String KEY_COUNTRY = "u_country";
    public static final String KEY_ZIPCODE = "u_zipcode";
    public static final String KEY_PASSWORD = "u_password";
    public static final String KEY_TYPE = "type";
    public static final String KEY_USERTYPE = "user_type";
    public static final String KEY_ISCREDIT = "is_credit_card_added";
    public static final String KEY_CARDNO = "card_no";
    public static final String KEY_MON = "exp_mon";
    public static final String KEY_YEAR = "exp_year";
    public static final String KEY_CVV = "cvv";
    public static final String KEY_IMAGE = "img";
    public static final String KEY_DEVICETYPE = "device_type";
    public static final String KEY_DEVICETOKEN = "devicetoken";
    public static final String KEY_EXPERINCE = "experience";
    public static final String KEY_BUSINESSNAME = "u_business_name";
    public static final String KEY_ABOUTME = "u_bio";
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_VENDOR_ID = "vendor_id";
    public static final String KEY_SUBCATEGORY_ID = "sub_category_id";
    public static final String KEY_DEFAULT_PRICE = "default_price";
    public static final String KEY_TODAYS_OFFER = "todays_offer";


    public static final String BASE_URL = "http://tech599.com/tech599.com/johnaks/Ubercuts/api/";

    /*******************************User***************************************************************************************************/
    /*=================================================Register User=========================================================*/
    public static final String API_REGISTER_USER = BASE_URL + "register_user.php";
    /*=================================================Login User=========================================================*/
    public static final String API_LOGIN_USER = BASE_URL + "login.php";
    /*=================================================Forgot Password User=========================================================*/
    public static final String API_FORGOTPWD_USER = BASE_URL + "forgot_password.php";
    /*=================================================Country List=========================================================*/
    public static final String API_GETLIST_COUNTRIES = BASE_URL + "get_countries.php";
    /*=================================================State List=========================================================*/
    public static final String API_GETLIST_STATE = BASE_URL + "get_states.php";
    /*=================================================City List=========================================================*/
    public static final String API_GETLIST_CITY = BASE_URL + "get_cities.php";
    /*=================================================Email Exists=========================================================*/
    public static final String API_EMAIL_EXISTS = BASE_URL + "email_checking.php";
    /*=================================================Get Services List=========================================================*/
    public static final String API_GET_SERVICELIST = BASE_URL + "get_sub_category.php";
    /*=================================================Get Services List=========================================================*/
    public static final String API_GET_VENDORLISTNEARBY = BASE_URL + "get_service_provider_near_by_me.php";
    /*=================================================Get Vendor  Details=========================================================*/
    public static final String API_GET_VENDORDETAILS = BASE_URL + "get_beautician_details.php";
    /*=================================================Get Vendor  SErvices=========================================================*/
    public static final String API_GET_VENDORSERVICES = BASE_URL + "get_beautician_services.php";
    /*=================================================Get Vendor  SErvices=========================================================*/
    public static final String API_GET_VENDORREVIEWS = BASE_URL + "get_beautician_reviews.php";
    /*=================================================Add Vendor  Fav=========================================================*/
    public static final String API_ADD_VENDORFAV = BASE_URL + "add_favorite_vendor.php";
    /*=================================================Remove Vendor  Fav=========================================================*/
    public static final String API_REMOVE_VENDORFAV = BASE_URL + "remove_favorite_vendor.php";
    /*=================================================Listing of Vendor  Fav=========================================================*/
    public static final String API_GETALL_VENDORFAV = BASE_URL + "my_favorite_vendor_listing.php";
    /*=================================================Get Vendor Work =========================================================*/
    public static final String API_GET_VENDORWORK = BASE_URL + "get_vendor_work.php";
    /*=================================================Get Today's  Offer =========================================================*/
    public static final String API_GET_TODAYSOFFER = BASE_URL + "todays_offers.php";
    /*=================================================Get Today's  Offer =========================================================*/
    public static final String API_GET_CREDITCARDDETAILS = BASE_URL + "get_saved_card_details.php";
    /*=================================================Reguslar booking =========================================================*/
    public static final String API_REGULAR_BOOKING = BASE_URL + "regular_booking.php";
    /*=================================================Reguslar booking =========================================================*/
    public static final String API_POSTYOURTASK_BOOKING = BASE_URL + "post_task_booking.php";
    /*=================================================Reguslar booking =========================================================*/
    public static final String API_MYTASKLISTING = BASE_URL + "my_task_list.php";
    /*=================================================Reguslar booking =========================================================*/
    public static final String API_MYTASKDETAILS = BASE_URL + "my_task_details.php";
    /*=================================================GET PROFILE USER  =========================================================*/
    public static final String API_GETUSERPROFILEDETAILS = BASE_URL + "get_user.php";
    /*================================================= PROFILE USER  UPDATE=========================================================*/
    public static final String API_UPDATEUSERPROFILE = BASE_URL + "update_user.php";

    /*******************************Vendor***************************************************************************************************/
    /*=================================================Register Vendor=========================================================*/
    public static final String API_REGISTER_VENDOR = BASE_URL + "register_vendor.php";
    /*=================================================MY Services and Offers Vendor=========================================================*/
    public static final String API_VENDOR_OFFERS = BASE_URL + "my_service_list.php";
    /*=================================================ADD Services Vendor=========================================================*/
    public static final String API_VENDOR_ADDSERVICES = BASE_URL + "post_my_service.php";
    /*=================================================Update Services Vendor=========================================================*/
    public static final String API_VENDOR_UPDATESERVICES = BASE_URL + "update_my_service.php";
    /*=================================================Get Vendor Services List=========================================================*/
    public static final String API_GET_SERVICELIST_VENDOR = BASE_URL + "vendor_get_sub_category.php";
    /*=================================================Get Vendor My profile Details=========================================================*/
    public static final String API_GET_MYPROFILE_VENDOR = BASE_URL + "vendor_get_profile.php";
    /*=================================================Get Vendor My profile Details=========================================================*/
    public static final String API_GET_BUSINESS_VENDOR = BASE_URL + "vendor_get_business_prof.php";
    /*=================================================Get Vendor My profile Details=========================================================*/
    public static final String API_GET_MYWORK_VENDOR = BASE_URL + "vendor_get_work.php";
    /*=================================================Save Vendor AboutMe Data=========================================================*/
    public static final String API_SAVE_ABOUTME_VENDOR = BASE_URL + "vendor_update_profile.php";
    /*=================================================Save Vendor Business Data=========================================================*/
    public static final String API_SAVE_BUSINESS_VENDOR = BASE_URL + "vendor_update_business_prof.php";
    /*=================================================Save Vendor Work Images=========================================================*/
    public static final String API_SAVE_VENDOR_IMAGES = BASE_URL + "add_my_work_images.php";
    /*=================================================Save Vendor Work Video=========================================================*/
    public static final String API_SAVE_VENDOR_VIDEO = BASE_URL + "add_my_work_video.php";
    /*=================================================Remove Image Vendor Work =========================================================*/
    public static final String API_REMOVE_VENDOR_IMAGE = BASE_URL + "delete_my_work_images.php";
    /*=================================================Remove Video Vendor Work =========================================================*/
    public static final String API_REMOVE_VENDOR_VIDEO = BASE_URL + "delete_my_work_video.php";
    /*=================================================SAve Profile Image of  Vendor  =========================================================*/
    public static final String API_SAVEPROFILE_VENDOR = BASE_URL + "change_profile_picture.php";
    /*=================================================SAve Profile Image of  Vendor  =========================================================*/
    public static final String API_DELETESERVICE_VENDOR = BASE_URL + "delete_my_service.php";


    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


}

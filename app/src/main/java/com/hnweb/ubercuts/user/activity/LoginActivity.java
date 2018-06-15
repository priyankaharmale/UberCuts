package com.hnweb.ubercuts.user.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.utils.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyanka H on 12/06/2018.
 */
public class LoginActivity extends AppCompatActivity {
    Button btn_createaccount;
    TextView tv_forgotpwd;
    Dialog dialog;
    EditText et_emailId;
    EditText et_password, et_email;
    Button btn_signIn;
    LoadingDialog loadingDialog;
    SharedPreferences prefUser;
    SharedPreferences.Editor editorUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_createaccount = (Button) findViewById(R.id.btn_createaccount);
        tv_forgotpwd = (TextView) findViewById(R.id.tv_forgotpwd);
        btn_signIn = (Button) findViewById(R.id.btn_signIn);
        et_password = (EditText) findViewById(R.id.et_password);
        et_email = (EditText) findViewById(R.id.et_email);
        loadingDialog = new LoadingDialog(LoginActivity.this);
        prefUser = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        editorUser = prefUser.edit();

        btn_createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivityStepOne.class);
                startActivity(intent);
            }
        });
        tv_forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation1()) {

                    if (Utils.isNetworkAvailable(LoginActivity.this)) {

                        String password = et_password.getText().toString();
                        String email = et_email.getText().toString();

                        login(email, password);
                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }
            }
        });
    }

    public void dialog() {
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_forgotpwd);
        //   final EditText et_exprydate = (EditText) dialog.findViewById(R.id.et_exprydate);
        TextView tv_submit = (TextView) dialog.findViewById(R.id.tv_submit);
        TextView tv_cancle = (TextView) dialog.findViewById(R.id.tv_cancle);
        et_emailId = (EditText) dialog.findViewById(R.id.et_emailId);

        dialog.show();
        dialog.setCancelable(true);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidation()) {
                    if (Utils.isNetworkAvailable(LoginActivity.this)) {
                        forgotpwd(et_emailId.getText().toString());
                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }

            }
        });

    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validations.hasText(et_emailId, "Please Enter Email ID "))
            ret = false;

        if (!Validations.isEmailAddress(et_emailId, true, "Please Enter Valid Email ID"))
            ret = false;
        return ret;

    }

    private boolean checkValidation1() {
        boolean ret = true;
        if (!Validations.hasText(et_email, "Please Enter Email ID "))
            ret = false;
        if (!Validations.isEmailAddress(et_email, true, "Please Enter Valid Email ID"))
            ret = false;
        if (!Validations.hasText(et_password, "Please Enter RE-Enter Password"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_password, "Password atleast 7 characters"))
            ret = false;
        return ret;
    }

    private void login(final String email, final String password) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_LOGIN_USER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                try {
                                                    JSONArray jsonArray = j.getJSONArray("details");

                                                    for (int i = 0; i < jsonArray.length(); i++) {

                                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                        String user_id = jsonObject.getString("u_id");
                                                        String user_name = jsonObject.getString("u_name");
                                                        String user_email = jsonObject.getString("u_email");
                                                        String user_phone = jsonObject.getString("u_phone");
                                                        String user_image = jsonObject.getString("u_img");
                                                        String device_type = jsonObject.getString("device_type");
                                                        String user_street = jsonObject.getString("u_street");
                                                        String user_city = jsonObject.getString("u_city");
                                                        String user_state = jsonObject.getString("u_state");
                                                        String user_country = jsonObject.getString("u_country");
                                                        String user_zipcode = jsonObject.getString("u_zipcode");
                                                        String user_is_card = jsonObject.getString("is_credit_card_added");
                                                        String user_type = jsonObject.getString("type");

                                                        /*    if (user_type.equals("0")) {*/
                                                        //user_type_value = "User";
                                                        editorUser.putString(AppConstant.KEY_U_ID, user_id);
                                                        editorUser.putString(AppConstant.KEY_U_NAME, user_name);
                                                        editorUser.putString(AppConstant.KEY_U_EMAIL, user_email);
                                                        editorUser.putString(AppConstant.KEY_U_PHONE, user_phone);
                                                        editorUser.putString(AppConstant.KEY_U_IMAGE, user_image);
                                                        editorUser.putString(AppConstant.KEY_U_DEVICETYPE, user_type);
                                                        editorUser.putString(AppConstant.KEY_U_STREET, user_street);
                                                        editorUser.putString(AppConstant.KEY_U_CITY, user_city);
                                                        editorUser.putString(AppConstant.KEY_U_STATE, user_state);
                                                        editorUser.putString(AppConstant.KEY_U_COUNTRY, user_country);
                                                        editorUser.putString(AppConstant.KEY_U_ZIPCODE, user_zipcode);
                                                        editorUser.commit();
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        finish();
                                                        startActivity(intent);
                                                        // }
                                                        /*else {
                                                            //user_type_value = "Vendor";
                                                            editorVendor.putString("user_id_vendor", user_id);
                                                            editorVendor.apply();
                                                            editorVendor.putString("Username", user_name);
                                                            editorVendor.putString("Email", user_email);
                                                            editorVendor.putString("UserPhone", user_phone);
                                                            editorVendor.putString("UserDob", user_dob);
                                                            editorVendor.putString("UserImage", user_image);
                                                            editorVendor.putString("UserType", user_type);
                                                            editorVendor.putString("UserStreet", user_street);
                                                            editorVendor.putString("UserCity", user_city);
                                                            editorVendor.putString("UserState", user_state);
                                                            editorVendor.putString("UserCountry", user_country);
                                                            editorVendor.putString("UserZipcode", user_zipcode);
                                                            editorVendor.commit();
                                                            Intent intent = new Intent(LoginActivityUser.this, MainActivityVendor.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            finish();
                                                            startActivity(intent);
                                                        }*/


                            /*Intent intent = new Intent(LoginActivityUser.this, MainActivityUser.class);
                            intent.putExtra("UserIDS",user_id);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intent);*/

                                                    }
                                                } catch (JSONException e) {
                                                    System.out.println("jsonexeption" + e.toString());
                                                }
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                        AlertUtility.showAlert(LoginActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.KEY_U_EMAIL, email);
                    params.put(AppConstant.KEY_U_PASSWORD, password);
                    params.put(AppConstant.KEY_U_DEVICETYPE, "Android");
                    params.put(AppConstant.KEY_U_DEVICETOKEN, "122");

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void forgotpwd(final String email) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_FORGOTPWD_USER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog1, int id) {

                                                dialog1.dismiss();
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                        AlertUtility.showAlert(LoginActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.KEY_U_EMAIL, email);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}


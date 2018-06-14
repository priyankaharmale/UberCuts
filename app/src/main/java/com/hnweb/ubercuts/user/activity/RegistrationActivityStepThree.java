package com.hnweb.ubercuts.user.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.multipartrequest.MultiPart_Key_Value_Model;
import com.hnweb.ubercuts.multipartrequest.MultipartFileUploaderAsync;
import com.hnweb.ubercuts.multipartrequest.OnEventListener;
import com.hnweb.ubercuts.user.adaptor.MonthAdaptor;
import com.hnweb.ubercuts.user.adaptor.YearAdaptor;
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.utils.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyanka H on 13/06/2018.
 */

public class RegistrationActivityStepThree extends AppCompatActivity implements OnCallBack {
    Button btn_proceed;
    OnCallBack onCallBack;
    EditText et_mm, et_yyyy, et_cvv, et_cardNo;
    LoadingDialog loadingDialog;
    String profilepic, fullname, emailId, mobileNo, password, country, state, city, street, zipcode, cardNo, month, year, cvv;
    ImageView iv_profilepic;
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerstepthree);

        onCallBack = RegistrationActivityStepThree.this;
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        et_mm = (EditText) findViewById(R.id.et_mm);
        et_yyyy = (EditText) findViewById(R.id.et_yyyy);
        et_cvv = (EditText) findViewById(R.id.et_cvv);
        iv_profilepic = (ImageView) findViewById(R.id.iv_profilepic);
        et_cardNo = (EditText) findViewById(R.id.et_cardNo);
        drawable = ContextCompat.getDrawable(RegistrationActivityStepThree.this, R.drawable.user_register);
        loadingDialog = new LoadingDialog(RegistrationActivityStepThree.this);

        getSavedData();


        if (!profilepic.equals("")) {
            try {
                Glide.with(RegistrationActivityStepThree.this)
                        .load(profilepic)
                        .error(drawable)
                        .centerCrop()
                        .crossFade()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(iv_profilepic);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        } else {
            iv_profilepic.setImageResource(R.drawable.user_register);
        }
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_cardNo.getText().toString().equals("")) {
                    et_cardNo.setError("Please Enter the Card Number");
                } else if (et_mm.getText().toString().equals("")) {
                    et_mm.setError("Please Select the Month");
                } else if (et_yyyy.getText().toString().equals("")) {
                    et_yyyy.setError("Please Select the Year");
                } else if (et_cvv.getText().toString().equals("")) {
                    et_cvv.setError("Please Enter CVV Number");
                } else {
                    cardNo = et_cardNo.getText().toString();
                    month = et_mm.getText().toString();
                    year = et_yyyy.getText().toString();
                    cvv = et_cvv.getText().toString();

                    register(cardNo, month, year, cvv);
                }

            }
        });
        et_yyyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogYear();
            }
        });
        et_mm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMonth();
            }
        });
        et_cardNo.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            boolean isDelete = true;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0)
                    isDelete = false;
                else
                    isDelete = true;
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String source = editable.toString();
                int length = source.length();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(source);
                if (length > 0 && length % 5 == 0) {
                    if (isDelete)
                        stringBuilder.deleteCharAt(length - 1);
                    else
                        stringBuilder.insert(length - 1, " ");

                    et_cardNo.setText(stringBuilder);
                    et_cardNo.setSelection(et_cardNo.getText().length());
                    }
            }
        });
    }

    public void dialogYear() {
        Dialog dialog = new Dialog(RegistrationActivityStepThree.this);
        dialog.setContentView(R.layout.dialog_month);
        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Year");
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i <= 2060; i++) {
            years.add(Integer.toString(i));
        }
        YearAdaptor adapter = new YearAdaptor(RegistrationActivityStepThree.this, years, onCallBack, dialog);
        lv.setAdapter(adapter);

        dialog.show();
    }

    public void dialogMonth() {
        Dialog dialog = new Dialog(RegistrationActivityStepThree.this);
        dialog.setContentView(R.layout.dialog_month);
        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Month");
        ArrayList<String> years = new ArrayList<String>();
        years.add("01");
        years.add("02");
        years.add("03");
        years.add("04");
        years.add("05");
        years.add("06");
        years.add("07");
        years.add("08");
        years.add("09");
        years.add("10");
        years.add("11");
        years.add("12");
        MonthAdaptor adapter = new MonthAdaptor(RegistrationActivityStepThree.this, years, onCallBack, dialog);
        lv.setAdapter(adapter);
        dialog.show();
    }

    @Override
    public void callback(String count) {
        et_mm.setText(count);
    }

    @Override
    public void callbackYear(String count) {
        et_yyyy.setText(count);

    }

    @Override
    public void callcountryList(String id, String name) {

    }

    @Override
    public void callstateList(String id, String name) {

    }

    @Override
    public void callcityList(String id, String name) {

    }

    public void register(String cardNo, String month, String year, String cvv) {
        loadingDialog.show();

        MultiPart_Key_Value_Model OneObject = new MultiPart_Key_Value_Model();
        Map<String, String> fileParams = new HashMap<>();
        if (profilepic.equals("")) {
            //  fileParams.put("profile_pic", "");
        } else {
            fileParams.put(AppConstant.KEY_U_IMAGE, profilepic);
        }

        Map<String, String> stringparam = new HashMap<>();

        stringparam.put(AppConstant.KEY_U_NAME, fullname);
        stringparam.put(AppConstant.KEY_U_EMAIL, emailId);
        stringparam.put(AppConstant.KEY_U_PHONE, mobileNo);
        stringparam.put(AppConstant.KEY_U_PASSWORD, password);
        stringparam.put(AppConstant.KEY_U_COUNTRY, country);
        stringparam.put(AppConstant.KEY_U_STATE, state);
        stringparam.put(AppConstant.KEY_U_CITY, city);
        stringparam.put(AppConstant.KEY_U_STREET, street);
        stringparam.put(AppConstant.KEY_U_ZIPCODE, zipcode);
        stringparam.put(AppConstant.KEY_U_CARDNO, cardNo);
        stringparam.put(AppConstant.KEY_U_MON, month);
        stringparam.put(AppConstant.KEY_U_YEAR, year);
        stringparam.put(AppConstant.KEY_U_CVV, cvv);

        OneObject.setUrl(AppConstant.API_REGISTER_USER);
        OneObject.setFileparams(fileParams);
        System.out.println("file" + fileParams);
        System.out.println("UTL" + OneObject.toString());
        OneObject.setStringparams(stringparam);
        System.out.println("string" + stringparam);

        MultipartFileUploaderAsync someTask = new MultipartFileUploaderAsync(RegistrationActivityStepThree.this, OneObject, new OnEventListener<String>() {
            @Override
            public void onSuccess(String object) {
                loadingDialog.dismiss();
                System.out.println("Result" + object);
                try {
                    JSONObject jsonObject1response = new JSONObject(object);
                    int flag = jsonObject1response.getInt("message_code");
                    if (flag == 1) {
                        String message = jsonObject1response.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivityStepThree.this);
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent = new Intent(RegistrationActivityStepThree.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        String message = jsonObject1response.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivityStepThree.this);
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("JSONException" + e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("onFailure" + e);
            }
        });
        someTask.execute();
        return;
    }

    private void getSavedData() {
        SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        profilepic = settings.getString(AppConstant.KEY_U_IMAGE, null);
        fullname = settings.getString(AppConstant.KEY_U_NAME, null);
        emailId = settings.getString(AppConstant.KEY_U_EMAIL, null);
        mobileNo = settings.getString(AppConstant.KEY_U_PHONE, null);
        password = settings.getString(AppConstant.KEY_U_PASSWORD, null);
        country = settings.getString(AppConstant.KEY_U_COUNTRY, null);
        state = settings.getString(AppConstant.KEY_U_STATE, null);
        city = settings.getString(AppConstant.KEY_U_CITY, null);
        street = settings.getString(AppConstant.KEY_U_STREET, null);
        zipcode = settings.getString(AppConstant.KEY_U_ZIPCODE, null);
    }
}

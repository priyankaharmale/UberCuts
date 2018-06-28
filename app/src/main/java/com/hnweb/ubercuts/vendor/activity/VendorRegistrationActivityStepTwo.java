package com.hnweb.ubercuts.vendor.activity;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.multipartrequest.MultiPart_Key_Value_Model;
import com.hnweb.ubercuts.multipartrequest.MultipartFileUploaderAsync;
import com.hnweb.ubercuts.multipartrequest.OnEventListener;
import com.hnweb.ubercuts.user.adaptor.CityListAdaptor;
import com.hnweb.ubercuts.user.adaptor.CountryListAdaptor;
import com.hnweb.ubercuts.user.adaptor.StateListAdaptor;
import com.hnweb.ubercuts.user.bo.City;
import com.hnweb.ubercuts.user.bo.Country;
import com.hnweb.ubercuts.user.bo.State;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.utils.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyanka H on 13/06/2018.
 */

public class VendorRegistrationActivityStepTwo extends AppCompatActivity implements OnCallBack {
    Button btn_proceed;
    EditText et_country, et_state, et_city, et_steet, et_zip, et_businessName, et_aboutme;
    LoadingDialog loadingDialog;
    OnCallBack onCallBack;
    ArrayList<Country> countryArrayList = new ArrayList<>();
    ArrayList<State> stateArrayList = new ArrayList<>();
    ;
    ArrayList<City> cityArrayList = new ArrayList<>();
    ;
    CountryListAdaptor countryListAdaptor;
    StateListAdaptor stateListAdaptor;
    CityListAdaptor cityListAdaptor;
    ImageView iv_profilepic;
    String profilepic;
    Button btn_signIn;
    Drawable drawable;
    TextView textView_address;
    String fullname, emailId, mobileNo, password, experience;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registersteptwo);
        getSavedData();
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        et_country = (EditText) findViewById(R.id.et_country);
        et_state = (EditText) findViewById(R.id.et_state);
        et_city = (EditText) findViewById(R.id.et_city);
        et_zip = (EditText) findViewById(R.id.et_zip);
        et_steet = (EditText) findViewById(R.id.et_steet);
        iv_profilepic = (ImageView) findViewById(R.id.iv_profilepic);
        btn_signIn = (Button) findViewById(R.id.btn_signIn);
        et_businessName = (EditText) findViewById(R.id.et_businessName);
        textView_address = (TextView) findViewById(R.id.textView_address);
        et_aboutme = (EditText) findViewById(R.id.et_aboutme);

        et_aboutme.setVisibility(View.VISIBLE);

        drawable = ContextCompat.getDrawable(VendorRegistrationActivityStepTwo.this, R.drawable.user_register);

        onCallBack = VendorRegistrationActivityStepTwo.this;
        loadingDialog = new LoadingDialog(VendorRegistrationActivityStepTwo.this);

        textView_address.setText("Business Address");
        et_businessName.setVisibility(View.VISIBLE);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation1()) {
                    if (Utils.isNetworkAvailable(VendorRegistrationActivityStepTwo.this)) {
                        String country = et_country.getText().toString();
                        String state = et_state.getText().toString();
                        String city = et_city.getText().toString();
                        String street = et_steet.getText().toString();
                        String zipcode = et_zip.getText().toString();
                        String businessName = et_businessName.getText().toString();
                        String aboutMe = et_aboutme.getText().toString();
                        register(country, state, city, street, zipcode, businessName, aboutMe);

                    } else {
                        Utils.myToast1(VendorRegistrationActivityStepTwo.this);
                    }
                }

            }
        });
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendorRegistrationActivityStepTwo.this, VendorLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        et_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getCountryList();


            }
        });
        et_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countryArrayList.size() == 0) {
                    Toast.makeText(VendorRegistrationActivityStepTwo.this, "Please Select Country", Toast.LENGTH_LONG).show();
                } else {
                    dialogState();
                }
            }
        });
        et_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stateArrayList.size() == 0) {
                    Toast.makeText(VendorRegistrationActivityStepTwo.this, "Please Select State", Toast.LENGTH_LONG).show();
                } else {
                    dialogCity();
                }
            }
        });
        SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        profilepic = settings.getString(AppConstant.KEY_IMAGE, null);
        if (!profilepic.equals("")) {
            try {
                Glide.with(VendorRegistrationActivityStepTwo.this)
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
    }

    public void dialogContry() {
        Dialog dialog = new Dialog(VendorRegistrationActivityStepTwo.this);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialogbox_list);

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(VendorRegistrationActivityStepTwo.this);
        recyclerView.setLayoutManager(mLayoutManager);

        TextView textView_header = (TextView) dialog.findViewById(R.id.textView_custom_view);
        final EditText searchView = (EditText) dialog.findViewById(R.id.search_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select Country");
        }
        dialog.setCancelable(true);

        countryListAdaptor = new CountryListAdaptor(countryArrayList, VendorRegistrationActivityStepTwo.this, onCallBack, dialog);
        recyclerView.setAdapter(countryListAdaptor);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCountry(searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialog.show();
    }

    public void dialogState() {
        Dialog dialog = new Dialog(VendorRegistrationActivityStepTwo.this);
        dialog.setContentView(R.layout.dialogbox_list);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(VendorRegistrationActivityStepTwo.this);
        recyclerView.setLayoutManager(mLayoutManager);
        TextView textView_header = (TextView) dialog.findViewById(R.id.textView_custom_view);
        final EditText searchView = (EditText) dialog.findViewById(R.id.search_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select State");
        }
        dialog.setCancelable(true);
        stateListAdaptor = new StateListAdaptor(stateArrayList, VendorRegistrationActivityStepTwo.this, onCallBack, dialog);
        recyclerView.setAdapter(stateListAdaptor);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterState(searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialog.show();
    }

    public void dialogCity() {
        Dialog dialog = new Dialog(VendorRegistrationActivityStepTwo.this);
        dialog.setContentView(R.layout.dialogbox_list);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(VendorRegistrationActivityStepTwo.this);
        recyclerView.setLayoutManager(mLayoutManager);
        TextView textView_header = (TextView) dialog.findViewById(R.id.textView_custom_view);
        final EditText searchView = (EditText) dialog.findViewById(R.id.search_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select City");
        }
        dialog.setCancelable(true);
        cityListAdaptor = new CityListAdaptor(cityArrayList, VendorRegistrationActivityStepTwo.this, onCallBack, dialog);
        recyclerView.setAdapter(cityListAdaptor);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCity(searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
    }

    private void getCountryList() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETLIST_COUNTRIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_fav" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            //  countryArrayList = new ArrayList<>();
                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("details");
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        final Country country = new Country();
                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                        country.setId(jsonObjectpostion.getString("country_id"));
                                        country.setCountyName(jsonObjectpostion.getString("country_name"));

                                        countryArrayList.add(country);
                                    }
                                    dialogContry();

                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(VendorRegistrationActivityStepTwo.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }


                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(VendorRegistrationActivityStepTwo.this, error);
                        AlertUtility.showAlert(VendorRegistrationActivityStepTwo.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(VendorRegistrationActivityStepTwo.this);
        requestQueue.add(stringRequest);

    }

    private void getStateList(final String countryId) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETLIST_STATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_fav" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            //stateArrayList = new ArrayList<>();
                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("details");
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        final State state = new State();
                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                        state.setId(jsonObjectpostion.getString("state_id"));
                                        state.setStateName(jsonObjectpostion.getString("state_name"));

                                        stateArrayList.add(state);
                                    }

                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(VendorRegistrationActivityStepTwo.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }


                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(VendorRegistrationActivityStepTwo.this, error);
                        AlertUtility.showAlert(VendorRegistrationActivityStepTwo.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //user_id
                    params.put("country_id", countryId);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                    Log.e("Exception", e.getMessage());
                }


                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(VendorRegistrationActivityStepTwo.this);
        requestQueue.add(stringRequest);

    }

    private void getCityList(final String state) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETLIST_CITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_fav" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            //   cityArrayList = new ArrayList<>();
                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("details");
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        final City city = new City();
                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                        city.setId(jsonObjectpostion.getString("city_id"));
                                        city.setCityName(jsonObjectpostion.getString("city_name"));

                                        cityArrayList.add(city);
                                    }


                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(VendorRegistrationActivityStepTwo.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }


                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(VendorRegistrationActivityStepTwo.this, error);
                        AlertUtility.showAlert(VendorRegistrationActivityStepTwo.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //user_id
                    params.put("state_id", state);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                    Log.e("Exception", e.getMessage());
                }


                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(VendorRegistrationActivityStepTwo.this);
        requestQueue.add(stringRequest);

    }

    private boolean checkValidation1() {
        boolean ret = true;
        if (!Validations.hasText(et_businessName, "Please Enter Business Name"))
            ret = false;
        if (!Validations.hasText(et_country, "Please Select Country"))
            ret = false;
        if (!Validations.hasText(et_state, "Please Select State"))
            ret = false;
        if (!Validations.hasText(et_city, "Please Select City"))
            ret = false;
        if (!Validations.hasText(et_steet, "Please Enter Street"))
            ret = false;
        if (!Validations.hasText(et_zip, "Please Enter Zip Code"))
            ret = false;
        if (!Validations.hasText(et_aboutme, "Please Enter Abou Me"))
            ret = false;
        return ret;
    }

    @Override
    public void callback(String count) {

    }

    @Override
    public void callbackYear(String count) {

    }

    @Override
    public void callcountryList(String id, String name) {
        et_country.setText(name);
        et_city.setText("");
        et_state.setText("");
        et_steet.setText("");
        et_zip.setText("");

        et_city.setHint("City");
        et_state.setHint("State");
        et_steet.setHint("Street");
        et_zip.setHint("Zip Code");

        stateArrayList.clear();
        cityArrayList.clear();

        getStateList(id);
    }

    @Override
    public void callstateList(String id, String name) {
        et_state.setText(name);
        et_city.setText("");
        et_steet.setText("");
        et_zip.setText("");
        cityArrayList.clear();
        et_city.setHint("City");
        et_steet.setHint("Street");
        et_zip.setHint("Zip Code");
        getCityList(id);
    }

    @Override
    public void callcityList(String id, String name) {
        et_city.setText(name);

    }

    @Override
    public void callrefresh() {

    }

    private void filterCountry(String text) {
        //new array list that will hold the filtered data
        ArrayList<Country> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (Country s : countryArrayList) {
            //if the existing elements contains the search input
            if (s.getCountyName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        countryListAdaptor.filterList(filterdNames);
    }

    private void filterState(String text) {
        //new array list that will hold the filtered data
        ArrayList<State> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (State s : stateArrayList) {
            //if the existing elements contains the search input
            if (s.getStateName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        stateListAdaptor.filterList(filterdNames);
    }

    private void filterCity(String text) {
        //new array list that will hold the filtered data
        ArrayList<City> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (City s : cityArrayList) {
            //if the existing elements contains the search input
            if (s.getCityName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        cityListAdaptor.filterList(filterdNames);
    }

    private void getSavedData() {
        SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        profilepic = settings.getString(AppConstant.KEY_IMAGE, null);
        fullname = settings.getString(AppConstant.KEY_NAME, null);
        emailId = settings.getString(AppConstant.KEY_EMAIL, null);
        mobileNo = settings.getString(AppConstant.KEY_PHONE, null);
        password = settings.getString(AppConstant.KEY_PASSWORD, null);
        experience = settings.getString(AppConstant.KEY_EXPERINCE, null);

    }

    public void register(String country, String state, String city, String street, String zipcode, String businessName, String aboutMe) {
        loadingDialog.show();

        MultiPart_Key_Value_Model OneObject = new MultiPart_Key_Value_Model();
        Map<String, String> fileParams = new HashMap<>();
        if (profilepic.equals("")) {
            //  fileParams.put("profile_pic", "");
        } else {
            fileParams.put(AppConstant.KEY_IMAGE, profilepic);
        }

        Map<String, String> stringparam = new HashMap<>();

        stringparam.put(AppConstant.KEY_NAME, fullname);
        stringparam.put(AppConstant.KEY_EMAIL, emailId);
        stringparam.put(AppConstant.KEY_PHONE, mobileNo);
        stringparam.put(AppConstant.KEY_PASSWORD, password);
        stringparam.put(AppConstant.KEY_COUNTRY, country);
        stringparam.put(AppConstant.KEY_STATE, state);
        stringparam.put(AppConstant.KEY_CITY, city);
        stringparam.put(AppConstant.KEY_STREET, street);
        stringparam.put(AppConstant.KEY_ZIPCODE, zipcode);
        stringparam.put(AppConstant.KEY_EXPERINCE, experience);
        stringparam.put(AppConstant.KEY_ABOUTME, aboutMe);
        stringparam.put(AppConstant.KEY_BUSINESSNAME, businessName);


        OneObject.setUrl(AppConstant.API_REGISTER_VENDOR);
        OneObject.setFileparams(fileParams);
        System.out.println("file" + fileParams);
        System.out.println("UTL" + OneObject.toString());
        OneObject.setStringparams(stringparam);
        System.out.println("string" + stringparam);

        MultipartFileUploaderAsync someTask = new MultipartFileUploaderAsync(VendorRegistrationActivityStepTwo.this, OneObject, new OnEventListener<String>() {
            @Override
            public void onSuccess(String object) {
                loadingDialog.dismiss();
                System.out.println("Result" + object);
                try {
                    JSONObject jsonObject1response = new JSONObject(object);
                    int flag = jsonObject1response.getInt("message_code");
                    if (flag == 1) {
                        String message = jsonObject1response.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(VendorRegistrationActivityStepTwo.this);
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent = new Intent(VendorRegistrationActivityStepTwo.this, VendorLoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        String message = jsonObject1response.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(VendorRegistrationActivityStepTwo.this);
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
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


}

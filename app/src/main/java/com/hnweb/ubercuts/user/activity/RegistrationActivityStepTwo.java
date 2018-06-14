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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.SharedPreference;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.utils.Validations;
import com.hnweb.ubercuts.user.adaptor.CityListAdaptor;
import com.hnweb.ubercuts.user.adaptor.CountryListAdaptor;
import com.hnweb.ubercuts.user.adaptor.StateListAdaptor;
import com.hnweb.ubercuts.user.bo.City;
import com.hnweb.ubercuts.user.bo.Country;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.user.bo.State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyanka H on 13/06/2018.
 */
public class RegistrationActivityStepTwo extends AppCompatActivity implements OnCallBack {
    Button btn_proceed;
    EditText et_country, et_state, et_city, et_steet, et_zip;
    LoadingDialog loadingDialog;
    OnCallBack onCallBack;
    ArrayList<Country> countryArrayList = new ArrayList<>();
    ArrayList<State> stateArrayList = new ArrayList<>();
    ArrayList<City> cityArrayList = new ArrayList<>();
    ImageView iv_profilepic;
    String profilepic;
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registersteptwo);
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        et_country = (EditText) findViewById(R.id.et_country);
        et_state = (EditText) findViewById(R.id.et_state);
        et_city = (EditText) findViewById(R.id.et_city);
        et_zip = (EditText) findViewById(R.id.et_zip);
        et_steet = (EditText) findViewById(R.id.et_steet);
        iv_profilepic = (ImageView) findViewById(R.id.iv_profilepic);
        drawable = ContextCompat.getDrawable(RegistrationActivityStepTwo.this, R.drawable.user_register);

        onCallBack = RegistrationActivityStepTwo.this;
        loadingDialog = new LoadingDialog(RegistrationActivityStepTwo.this);

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation1()) {
                    if (Utils.isNetworkAvailable(RegistrationActivityStepTwo.this)) {
                        String country = et_country.getText().toString();
                        String state = et_state.getText().toString();
                        String city = et_city.getText().toString();
                        String street = et_steet.getText().toString();
                        String zipcode = et_zip.getText().toString();
                        Intent intent = new Intent(RegistrationActivityStepTwo.this, RegistrationActivityStepThree.class);
                        startActivity(intent);
                        SharedPreference.addressSave(getApplicationContext(), country, state, city, street, zipcode);

                    } else {
                        Utils.myToast1(RegistrationActivityStepTwo.this);
                    }
                }

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
                dialogState();
            }
        });
        et_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCity();
            }
        });
        SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        profilepic = settings.getString(AppConstant.KEY_U_IMAGE, null);
        if (!profilepic.equals("")) {
            try {
                Glide.with(RegistrationActivityStepTwo.this)
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
        Dialog dialog = new Dialog(RegistrationActivityStepTwo.this);
        dialog.setContentView(R.layout.dialog_list);

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(RegistrationActivityStepTwo.this);
        recyclerView.setLayoutManager(mLayoutManager);
        dialog.setCancelable(true);
        dialog.setTitle("Select Country");


        CountryListAdaptor adapter = new CountryListAdaptor(countryArrayList, RegistrationActivityStepTwo.this, onCallBack, dialog);
        recyclerView.setAdapter(adapter);

        dialog.show();
    }

    public void dialogState() {
        Dialog dialog = new Dialog(RegistrationActivityStepTwo.this);
        dialog.setContentView(R.layout.dialog_list);

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(RegistrationActivityStepTwo.this);
        recyclerView.setLayoutManager(mLayoutManager);
        dialog.setCancelable(true);
        dialog.setTitle("Select Country");


        StateListAdaptor adapter = new StateListAdaptor(stateArrayList, RegistrationActivityStepTwo.this, onCallBack, dialog);
        recyclerView.setAdapter(adapter);

        dialog.show();
    }

    public void dialogCity() {
        Dialog dialog = new Dialog(RegistrationActivityStepTwo.this);
        dialog.setContentView(R.layout.dialog_list);

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(RegistrationActivityStepTwo.this);
        recyclerView.setLayoutManager(mLayoutManager);
        dialog.setCancelable(true);
        dialog.setTitle("Select Country");

        CityListAdaptor adapter = new CityListAdaptor(cityArrayList, RegistrationActivityStepTwo.this, onCallBack, dialog);
        recyclerView.setAdapter(adapter);

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
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivityStepTwo.this);
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
                        String reason = AppUtils.getVolleyError(RegistrationActivityStepTwo.this, error);
                        AlertUtility.showAlert(RegistrationActivityStepTwo.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivityStepTwo.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivityStepTwo.this);
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
                        String reason = AppUtils.getVolleyError(RegistrationActivityStepTwo.this, error);
                        AlertUtility.showAlert(RegistrationActivityStepTwo.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivityStepTwo.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivityStepTwo.this);
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
                        String reason = AppUtils.getVolleyError(RegistrationActivityStepTwo.this, error);
                        AlertUtility.showAlert(RegistrationActivityStepTwo.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivityStepTwo.this);
        requestQueue.add(stringRequest);

    }

    private boolean checkValidation1() {
        boolean ret = true;
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
        getStateList(id);
    }

    @Override
    public void callstateList(String id, String name) {
        et_state.setText(name);
        getCityList(id);
    }

    @Override
    public void callcityList(String id, String name) {
        et_city.setText(name);

    }

}

package com.hnweb.ubercuts.vendor.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.user.adaptor.CityListAdaptor;
import com.hnweb.ubercuts.user.adaptor.CountryListAdaptor;
import com.hnweb.ubercuts.user.adaptor.StateListAdaptor;
import com.hnweb.ubercuts.user.bo.City;
import com.hnweb.ubercuts.user.bo.Country;
import com.hnweb.ubercuts.user.bo.State;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.DataUtility;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.RequestInfo;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.vendor.activity.ProfileEditSaveVendorFragment;
import com.hnweb.ubercuts.vendor.activity.VendorRegistrationActivityStepTwo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ProfileVenodorBusiness extends Fragment  implements OnCallBack{

    private SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    Button btnSaveInfo;
    EditText etBusinessName, etStreet, etCity, etState, etCountry, etZipcode;
    TextView tvBusinessName, tvStreet, tvCity, tvState, tvCountry, tvZipCode, tvExperiences;
    ImageView imageViewEdit, imageViewSave;
    RelativeLayout relativeLayoutEdit, relativeLayoutSave;
    Button btnCountry, btnState, btnCity, btnExperience;

    private String countryId = "", stateId = "", cityId = "";
    private String countryName = "", stateName = "", cityName = "";
    private boolean selectedCountry = false, selectedState = false, selectedCity = false;
    String experience_value = "";
    OnCallBack onCallBack;
    ArrayList<Country> countryArrayList = new ArrayList<>();
    ArrayList<State> stateArrayList = new ArrayList<>();
    ;
    ArrayList<City> cityArrayList = new ArrayList<>();
    ;
    CountryListAdaptor countryListAdaptor;
    StateListAdaptor stateListAdaptor;
    CityListAdaptor cityListAdaptor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_business, container, false);

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);

        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        onCallBack = this;

        if (connectionDetector.isConnectingToInternet()) {
            getVendorBusinessDetails();
            //  getCountryList();
        } else {
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

        initViewById(view);

        tvExperiences = ((ProfileEditSaveVendorFragment) getActivity()).textViewExperience;

        return view;
    }

    private void initViewById(View view) {

        etBusinessName = view.findViewById(R.id.editText_business_name_vendor);
        //etState = view.findViewById(R.id.editText_state_vendor);
        //etCity = view.findViewById(R.id.editText_city_vendor);
        etStreet = view.findViewById(R.id.editText_street_vendor);
        etZipcode = view.findViewById(R.id.editText_zipcode_vendor);
        //etCountry = view.findViewById(R.id.editText_country_vendor);

        tvBusinessName = view.findViewById(R.id.textView_business_name_vendor);
        tvCountry = view.findViewById(R.id.textView_country_name);
        tvState = view.findViewById(R.id.textView_state_name);
        tvCity = view.findViewById(R.id.textView_city_name);
        tvStreet = view.findViewById(R.id.textView_street);
        tvZipCode = view.findViewById(R.id.textView_zip_code);

        relativeLayoutEdit = view.findViewById(R.id.relative_ly_business_edit);
        relativeLayoutSave = view.findViewById(R.id.relative_ly_business_save);

        imageViewEdit = view.findViewById(R.id.imageView_business_info_edit);
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutSave.setVisibility(View.VISIBLE);
                relativeLayoutEdit.setVisibility(View.GONE);
            }
        });
        imageViewSave = view.findViewById(R.id.button_save_vendor_business_info);

        btnExperience = view.findViewById(R.id.button_select_experience_business_info);
        btnCountry = view.findViewById(R.id.button_select_country_business_info);
        btnState = view.findViewById(R.id.button_select_state_business_info);
        btnCity = view.findViewById(R.id.button_select_city_business_info);
      /*  btnExperience.setOnClickListener(this);
        btnCountry.setOnClickListener(this);
        btnState.setOnClickListener(this);
        btnCity.setOnClickListener(this);*/

        btnExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExperienceDialog();
            }
        });
        btnCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getCountryList();


            }
        });
        btnState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countryArrayList.size() == 0) {
                    Toast.makeText(getActivity(), "Please Select Country", Toast.LENGTH_LONG).show();
                } else {
                    dialogState();
                }
            }
        });
        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stateArrayList.size() == 0) {
                    Toast.makeText(getActivity(), "Please Select State", Toast.LENGTH_LONG).show();
                } else {
                    dialogCity();
                }
            }
        });
        //btnSaveInfo = view.findViewById(R.id.button_business_info_save);
        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String business_name = etBusinessName.getText().toString().trim();
                String street = etStreet.getText().toString().trim();
                String zipcode = etZipcode.getText().toString().trim();
                String country = btnCountry.getText().toString();
                String state = btnState.getText().toString();
                String city = btnCity.getText().toString();

                if (!business_name.matches("")) {
                    if (!country.equals("")) {
                        if (!state.equals("")) {
                            if (!city.equals("")) {
                                if (!street.matches("")) {
                                    if (!zipcode.matches("")) {
                                        saveBusinessData(country,state,city,business_name, street, zipcode);
                                    } else {
                                        Toast.makeText(getActivity(), "Please Enter Zipcode!!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Please Enter Street", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Please select City!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please select State!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please select Country!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Enter Business Name!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getVendorBusinessDetails() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_BUSINESS_VENDOR,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "GETDetails= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONObject jsonObject = jobj.getJSONObject("details");


                                String vendor_id = jsonObject.getString("u_id");
                                String vendor_name = jsonObject.getString("u_name");
                                String vendor_experience = jsonObject.getString("experience");
                                String vendor_street = jsonObject.getString("u_street");
                                String vendor_city = jsonObject.getString("u_city");
                                String vendor_state = jsonObject.getString("u_state");
                                String vendor_country = jsonObject.getString("u_country");
                                String vendor_zipcode = jsonObject.getString("u_zipcode");

                                String vendor_business_name = jsonObject.getString("u_business_name");


                                etBusinessName.setText(vendor_business_name);
                                btnState.setText(vendor_state);
                                btnCity.setText(vendor_city);
                                etStreet.setText(vendor_street);
                                etZipcode.setText(vendor_zipcode);
                                btnCountry.setText(vendor_country);

                                tvBusinessName.setText(vendor_business_name);
                                tvState.setText(vendor_state);
                                tvCity.setText(vendor_city);
                                tvStreet.setText(vendor_street);
                                tvZipCode.setText(vendor_zipcode);
                                tvCountry.setText(vendor_country);

                                countryName = vendor_country;
                                stateName = vendor_state;
                                cityName = vendor_city;

                                if (vendor_experience.equals("Starter")) {
                                    tvExperiences.setText("Starter");
                                    btnExperience.setText("Starter");
                                } else if (vendor_experience.equals("1")) {
                                    tvExperiences.setText("0 - 1 Years");
                                    btnExperience.setText("0 - 1 Years");
                                } else if (vendor_experience.equals("2")) {
                                    tvExperiences.setText("1 - 5 Years");
                                    btnExperience.setText("1 - 5 Years");
                                } else if (vendor_experience.equals("3")) {
                                    tvExperiences.setText("5 - 10 Years");
                                    btnExperience.setText("5 - 10 Years");
                                }


                            } else {
                                Utils.AlertDialog(getActivity(), msg);
                                //displayAlertDialog(msg);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("vendor_id", user_id);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }
    private void saveBusinessData(final String country,final String state, final String city,final String business_name, final String street, final String zipcode) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_SAVE_BUSINESS_VENDOR,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "EditProfile= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                getVendorBusinessDetails();
                                relativeLayoutEdit.setVisibility(View.VISIBLE);
                                relativeLayoutSave.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.AlertDialog(getActivity(), msg);
                                //displayAlertDialog(msg);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                try {
                    params.put("vendor_id", user_id);
                    params.put("u_business_name", business_name);
                    params.put("u_street", street);
                    params.put("u_city", city);
                    params.put("u_state", state);
                    params.put("u_country", country);
                    params.put("u_zipcode", zipcode);
                    params.put("experience", experience_value);


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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }
    public void dialogContry() {
        Dialog dialog = new Dialog(getActivity());
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialogbox_list);

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        TextView textView_header = (TextView) dialog.findViewById(R.id.textView_custom_view);
        final EditText searchView = (EditText) dialog.findViewById(R.id.search_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select Country");
        }
        dialog.setCancelable(true);

        countryListAdaptor = new CountryListAdaptor(countryArrayList, getActivity(), onCallBack, dialog);
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
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogbox_list);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        TextView textView_header = (TextView) dialog.findViewById(R.id.textView_custom_view);
        final EditText searchView = (EditText) dialog.findViewById(R.id.search_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select State");
        }
        dialog.setCancelable(true);
        stateListAdaptor = new StateListAdaptor(stateArrayList, getActivity(), onCallBack, dialog);
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
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogbox_list);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        TextView textView_header = (TextView) dialog.findViewById(R.id.textView_custom_view);
        final EditText searchView = (EditText) dialog.findViewById(R.id.search_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select City");
        }
        dialog.setCancelable(true);
        cityListAdaptor = new CityListAdaptor(cityArrayList, getActivity(), onCallBack, dialog);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    @Override
    public void callback(String count) {

    }

    @Override
    public void callbackYear(String count) {

    }

    @Override
    public void callcountryList(String id, String name) {
        btnCountry.setText(name);
        btnCity.setText("");
        btnState.setText("");
        etStreet.setText("");
        etZipcode.setText("");

        btnCity.setHint("City");
        btnState.setHint("State");
        etStreet.setHint("Street");
        etZipcode.setHint("Zip Code");

        stateArrayList.clear();
        cityArrayList.clear();

        getStateList(id);
    }

    @Override
    public void callstateList(String id, String name) {
        btnState.setText(name);
        btnCity.setText("");
        etStreet.setText("");
        etZipcode.setText("");
        cityArrayList.clear();
        btnCity.setHint("City");
        etStreet.setHint("Street");
        etZipcode.setHint("Zip Code");
        getCityList(id);
    }

    @Override
    public void callcityList(String id, String name) {
        btnCity.setText(name);

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
    private void showExperienceDialog() {
        final Dialog driverForgotPassdialog = new Dialog(getActivity());
        driverForgotPassdialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        driverForgotPassdialog.setContentView(R.layout.layout_experience);

        Button sendBtn = (Button) driverForgotPassdialog.findViewById(R.id.cancel_apply);
        Button cancel = (Button) driverForgotPassdialog.findViewById(R.id.cancel_dialog);

        final RadioGroup radioGroup = (RadioGroup) driverForgotPassdialog.findViewById(R.id.radioGroupEx);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverForgotPassdialog.dismiss();
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioSexButton = (RadioButton) driverForgotPassdialog.findViewById(selectedId);
                String rd_button = radioSexButton.getText().toString();
                if (rd_button.equals("Starter")) {
                    experience_value = "0";
                    btnExperience.setText(rd_button);
                } else if (rd_button.equals("0 - 1 Years")) {
                    experience_value = "1";
                    btnExperience.setText(rd_button);
                } else if (rd_button.equals("1 - 5 Years")) {
                    experience_value = "2";
                    btnExperience.setText(rd_button);
                } else if (rd_button.equals("5 - 10 Years")) {
                    experience_value = "3";
                    btnExperience.setText(rd_button);
                }

                //Toast.makeText(RegisterVenDorActivity.this, radioSexButton.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverForgotPassdialog.dismiss();
            }
        });

        driverForgotPassdialog.show();
    }

}

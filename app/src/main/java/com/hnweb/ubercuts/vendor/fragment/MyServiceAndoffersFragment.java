package com.hnweb.ubercuts.vendor.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.vendor.activity.MainActivityVendor;
import com.hnweb.ubercuts.vendor.adaptor.ServiceListAdapter;
import com.hnweb.ubercuts.vendor.bo.ServiceListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PC-21 on 09-Apr-18.
 */
public class MyServiceAndoffersFragment extends Fragment implements OnCallBack {

    private SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String category_id;
    ArrayList<ServiceListModel> serviceListModels = new ArrayList<ServiceListModel>();
    ArrayList<Services> catgoryModels;
    RecyclerView recyclerViewServiceList;
    TextView textViewTotal;
    Button btnAddService;
    String selected_ids;
    OnCallBack onCallBack;
    FloatingActionButton fb_btn_add_service_nails;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // load data here
            category_id = "1";

        } else {
            // fragment is no longer visible
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myservice_vendor, container, false);

        loadingDialog = new LoadingDialog(getActivity());
        connectionDetector = new ConnectionDetector(getActivity());
        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);

        onCallBack = MyServiceAndoffersFragment.this;
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        category_id = "1";
        initViewById(view);
        return view;
    }

    private void initViewById(View view) {


        if (connectionDetector.isConnectingToInternet()) {
            getServiceList();
            getServices();
        } else {
            Snackbar snackbar = Snackbar
                    .make(((MainActivityVendor) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();
            //Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

        recyclerViewServiceList = view.findViewById(R.id.recylerview_service_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewServiceList.setLayoutManager(mLayoutManager);
        recyclerViewServiceList.setItemAnimator(new DefaultItemAnimator());
        fb_btn_add_service_nails = view.findViewById(R.id.fb_btn_add_service_nails);
        textViewTotal = view.findViewById(R.id.textView_total_length);

        //   btnAddService = view.findViewById(R.id.button_add_service_nails);
        fb_btn_add_service_nails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

    }


    private void getServiceList() {

        loadingDialog.show();
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_VENDOR_OFFERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "ServiceList: " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONArray userdetails = jobj.getJSONArray("details");
                                int total_size = userdetails.length();
                                textViewTotal.setText(String.valueOf(total_size));
                                serviceListModels.clear();
                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);

                                    ServiceListModel serviceListModel = new ServiceListModel();
                                    serviceListModel.setService_service_id(jsonObject.getString("service_id"));
                                    serviceListModel.setService_default_price(jsonObject.getString("default_price"));
                                    serviceListModel.setService_todays_offer(jsonObject.getString("todays_offer"));
                                    serviceListModel.setService_category_id(jsonObject.getString("category_id"));
                                    serviceListModel.setService_category_name(jsonObject.getString("category_name"));
                                    serviceListModel.setService_sub_category_id(jsonObject.getString("sub_category_id"));
                                    serviceListModel.setService_sub_category_name(jsonObject.getString("sub_category_name"));
                                    serviceListModels.add(serviceListModel);
                                }
                                ServiceListAdapter mAdapter = new ServiceListAdapter(getActivity(), serviceListModels, connectionDetector, loadingDialog, catgoryModels, onCallBack);
                                recyclerViewServiceList.setAdapter(mAdapter);

                            } else {
                                Utils.AlertDialog(getActivity(), msg);
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
                    params.put("category_id", "1");
                    params.put("user_id", user_id);

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


    private void getServices() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_SERVICELIST_VENDOR,
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
                            catgoryModels = new ArrayList<>();
                            if (message_code == 1) {
                                fb_btn_add_service_nails.setVisibility(View.VISIBLE);


                             /*   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
*/
                                try {
                                    JSONArray jsonArray = j.getJSONArray("details");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        Services catgoryModel = new Services();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String serviceId = jsonObject.getString("sub_category_id");
                                        String serviceName = jsonObject.getString("sub_category_name");
                                        catgoryModel.setId(serviceId);
                                        catgoryModel.setServicesName(serviceName);

                                        catgoryModels.add(catgoryModel);

                                    }

                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                                //  dialog.dismiss();
                                          /*  }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();*/
                            } else {
                                message = j.getString("message");
                               /* AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                fb_btn_add_service_nails.setVisibility(View.GONE);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();*/
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
                    params.put(AppConstant.KEY_CATEGORY_ID, "1");
                    params.put(AppConstant.KEY_VENDOR_ID, user_id);

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

    private void showAlertDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_service);

        Button sendBtn = (Button) dialog.findViewById(R.id.btn_post_add_service);
        Button btnCancel = dialog.findViewById(R.id.cancel_add_service);
        final EditText price = (EditText) dialog.findViewById(R.id.et_default_price);
        final EditText todays_offers = (EditText) dialog.findViewById(R.id.et_todays_offer);

        final Spinner spCatgory = dialog.findViewById(R.id.spinner_category);
        spCatgory.setPrompt("Select category");
        ArrayAdapter adapter = new ArrayAdapter<Services>(getActivity(), android.R.layout.simple_spinner_item, catgoryModels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCatgory.setAdapter(adapter);
        spCatgory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Services mSelected = (Services) adapterView.getItemAtPosition(i);
                Log.i("Id:", mSelected.getId());
                selected_ids = mSelected.getId();
                //Toast.makeText(getActivity(), "Company Name: " + companyName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String default_price = price.getText().toString();
                String todays_offer = todays_offers.getText().toString();

                if (!default_price.matches("")) {
                    if (!todays_offer.matches("")) {
                        if (selected_ids.equals("")) {
                            Toast.makeText(getActivity(), "Please Select Category", Toast.LENGTH_SHORT).show();
                        } else {
                            if (connectionDetector.isConnectingToInternet()) {
                                addServices(dialog, selected_ids, default_price, todays_offer, price, todays_offers, spCatgory, dialog);
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(((MainActivityVendor) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        }
                    } else {
                        todays_offers.setError("Please Enter Today's Offer");
                    }
                } else {
                    price.setError("Please Enter Default Price");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addServices(final Dialog dialogs, final String selected_ids, final String default_price, final String todays_offer, final EditText price, final EditText todays_offers, final Spinner spCatgory, final Dialog dialog) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_VENDOR_ADDSERVICES,
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                dialogs.dismiss();
                                                getServiceList();
                                                getServices();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
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


                    params.put(AppConstant.KEY_USER_ID, user_id);
                    params.put(AppConstant.KEY_CATEGORY_ID, "1");
                    params.put(AppConstant.KEY_SUBCATEGORY_ID, selected_ids);
                    params.put(AppConstant.KEY_DEFAULT_PRICE, default_price);
                    params.put(AppConstant.KEY_TODAYS_OFFER, todays_offer);


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

    @Override
    public void callback(String count) {
    }

    @Override
    public void callbackYear(String count) {

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

    @Override
    public void callrefresh() {
        getServiceList();
        getServices();
    }


}

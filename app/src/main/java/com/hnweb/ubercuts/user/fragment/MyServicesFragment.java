package com.hnweb.ubercuts.user.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.hnweb.ubercuts.user.activity.BeauticianDetailsActivity;
import com.hnweb.ubercuts.user.activity.RegularBookNowYourTask;
import com.hnweb.ubercuts.user.adaptor.NewServicesAdaptor;
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyServicesFragment extends Fragment implements OnCallBack {

    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    TextView textView_empty_service_nails;
    ListView listview_services;
    String beautician_id;
    ArrayList<Services> serivcesList;
    OnCallBack onCallBack;
    String serviceId;
    String seviceprice="";
    NewServicesAdaptor newServicesAdaptor;

    Button btnBookNow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_service, container, false);

        onCallBack = this;
        beautician_id = getArguments().getString("BeauticianIds");
        btnBookNow = ((BeauticianDetailsActivity) getActivity()).btn_booknow;

        initViewById(view);
        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seviceprice.equals("")) {
                    Toast.makeText(getActivity(), "Please select at least one service", Toast.LENGTH_SHORT).show();
                } else {
                    //((BeauticianDetailsActivity)getActivity()).finish();
                  /*  Fragment fragment = new PostYourTaskFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("BookingPayment",selected_price);
                    fragment.setArguments(bundle);
                    changeFragment(fragment);*/
                    Intent intent = new Intent(getActivity(), RegularBookNowYourTask.class);
                    intent.putExtra("SelectedPrice", seviceprice);
                    intent.putExtra("VendorIds", beautician_id);
                    intent.putExtra("SelectedSubCate",serviceId);
                    startActivity(intent);
                    //Toast.makeText(getActivity(), "Selected :: "+selected_price, Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    private void initViewById(View view) {


        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        listview_services = (ListView) view.findViewById(R.id.listview_services);
        textView_empty_service_nails = (TextView) view.findViewById(R.id.textView_empty_service_nails);
        if (connectionDetector.isConnectingToInternet()) {
            getServices();
        } else {

            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }


    }


    private void getServices() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_VENDORSERVICES,
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
                            serivcesList = new ArrayList<Services>();
                            if (message_code == 1) {
                                try {
                                    JSONArray jsonArray = j.getJSONArray("details");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        Services services = new Services();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String serviceId = jsonObject.getString("service_id");
                                        String serviceName = jsonObject.getString("sub_category_name");
                                        String ref_id_category = jsonObject.getString("ref_id_category");
                                        String ref_id_sub_category = jsonObject.getString("ref_id_sub_category");
                                        String default_price = jsonObject.getString("default_price");
                                        String todays_offer = jsonObject.getString("todays_offer");
                                        String category_name = jsonObject.getString("category_name");
                                        services.setId(serviceId);
                                        services.setServicesName(serviceName);
                                        services.setRef_id_category(ref_id_category);
                                        services.setRef_id_sub_category(ref_id_sub_category);
                                        services.setDefault_price(default_price);
                                        services.setTodays_offer(todays_offer);
                                        services.setCategory_name(category_name);

                                        serivcesList.add(services);
                                        newServicesAdaptor = new NewServicesAdaptor(getActivity(), serivcesList, onCallBack);
                                        listview_services.setAdapter(newServicesAdaptor);
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
                                                listview_services.setVisibility(View.GONE);
                                                textView_empty_service_nails.setVisibility(View.VISIBLE);
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
                    params.put("beautician_id", beautician_id);

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
        serviceId = id;
        seviceprice = name;
    }

    @Override
    public void callrefresh() {

    }



}

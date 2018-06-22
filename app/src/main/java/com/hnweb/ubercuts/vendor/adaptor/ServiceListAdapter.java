package com.hnweb.ubercuts.vendor.adaptor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.icu.util.ULocale;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.vendor.activity.MainActivityVendor;
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

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {

    private ArrayList<ServiceListModel> serviceList;
    private Context context;
    String selected_ids;
    String selected_sub_category;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    ArrayList<Services> catgoryModels;
    String user_id;
    SharedPreferences prefs;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_service_name, tv_default_price, tv_todays_offer;
        ImageView iv_edit;

        public MyViewHolder(View view) {
            super(view);
            tv_service_name = view.findViewById(R.id.tv_serviceName);
            tv_default_price = view.findViewById(R.id.tv_default);
            tv_todays_offer = view.findViewById(R.id.tv_offer);
            iv_edit = view.findViewById(R.id.iv_edit);


        }
    }


    public ServiceListAdapter(FragmentActivity activity, ArrayList<ServiceListModel> serviceList, ConnectionDetector connectionDetector, LoadingDialog loadingDialog, ArrayList<Services> catgoryModels) {
        this.serviceList = serviceList;
        this.context = activity;
        //this.subCategoriesList = subCategoriesList;
        this.connectionDetector = connectionDetector;
        this.catgoryModels = catgoryModels;
        this.loadingDialog = loadingDialog;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptor_vendor_services, parent, false);

        prefs = context.getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);


        user_id = prefs.getString(AppConstant.KEY_ID, null);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ServiceListModel serviceListModel = serviceList.get(position);
        holder.tv_service_name.setText(serviceListModel.getService_sub_category_name());
        holder.tv_default_price.setText("$ " + serviceListModel.getService_default_price());
        holder.tv_todays_offer.setText(serviceListModel.getService_todays_offer() + "% OFF");

        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAlertDailog(serviceListModel.getService_service_id(), serviceListModel.getService_default_price(), serviceListModel.getService_todays_offer(), serviceListModel.getService_sub_category_name());
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    private void updateAlertDailog(final String service_id, String service_default_price, String service_todays_offer, String service_sub_category_name) {

        //getServices();

        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_service);

        Button sendBtn = (Button) dialog.findViewById(R.id.btn_post_add_service);
        Button btnCancel = dialog.findViewById(R.id.cancel_add_service);
        final EditText price = (EditText) dialog.findViewById(R.id.et_default_price);
        final EditText todays_offers = (EditText) dialog.findViewById(R.id.et_todays_offer);
        final TextView tv_serviceName = (TextView) dialog.findViewById(R.id.tv_serviceName);

        String value_btn = sendBtn.getText().toString().trim();
        if (value_btn.equals("Add")) {
            sendBtn.setText("Update");
        }


        if (service_default_price != null) {
            price.setText(service_default_price);
        } else {
            price.setText("");

        }

        if (todays_offers != null) {
            todays_offers.setText(service_todays_offer);
        } else {
            todays_offers.setText("");

        }
        tv_serviceName.setText(service_sub_category_name);


        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String default_price = price.getText().toString();
                String todays_offer = todays_offers.getText().toString();

                if (!default_price.matches("")) {
                    if (!todays_offer.matches("")) {
                        if (selected_ids.equals("")) {
                            Toast.makeText(context, "Please Select Category", Toast.LENGTH_SHORT).show();
                        } else {
                            if (connectionDetector.isConnectingToInternet()) {
                                updateervices(selected_ids, default_price, todays_offer, price, todays_offers, dialog, service_id);
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(((MainActivityVendor) context).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

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

/*
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
                            catgoryModels = new ArrayList<Services>();
                            if (message_code == 1) {

                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

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

                                                dialog.dismiss();
                                            }
                                        });
                                android.support.v7.app.AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                android.support.v7.app.AlertDialog alert = builder.create();
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
                        String reason = AppUtils.getVolleyError(context, error);
                        AlertUtility.showAlert(context, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
*/

    private void updateervices(final String selected_ids, final String default_price, final String todays_offer, final EditText price, final EditText todays_offers, final Dialog dialog, final String service_id) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_VENDOR_UPDATESERVICES,
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

                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog1, int id) {
                                                dialog1.dismiss();
                                                dialog.dismiss();

                                                //getServices();
                                            }
                                        });
                                android.support.v7.app.AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                android.support.v7.app.AlertDialog alert = builder.create();
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
                        String reason = AppUtils.getVolleyError(context, error);
                        AlertUtility.showAlert(context, reason);
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
                    params.put("service_id", service_id);


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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

}

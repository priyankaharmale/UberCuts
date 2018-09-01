package com.hnweb.ubercuts.vendor.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.interfaces.FragmentCommunicator;
import com.hnweb.ubercuts.user.activity.HomeActivity;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("Registered")
public class LeadsDetailsActivity extends AppCompatActivity implements View.OnClickListener, AdapterCallback {

    private SharedPreferences prefs;
    String vendor_id;
    TextView tvServiceName, tvCustomerName,tv_date,tv_time, tvCustomerPrice, tvYourPrice, tvJobLocation, tvDescription;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String my_task_ids;
    Button btnAcceptLead, btnRejectLead;
    AdapterCallback adapterCallback;
    ImageView imageViewCustomer;
    public FragmentCommunicator fragmentCommunicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_details);

        Intent intent = getIntent();
        my_task_ids = intent.getStringExtra("VendorLeadIds").toString();
        if (my_task_ids != null) {
            Log.d("UserMyTask", my_task_ids);
        }

        adapterCallback = LeadsDetailsActivity.this;

        prefs = getApplicationContext().getSharedPreferences("MyPrefVendor", MODE_PRIVATE);
        vendor_id = prefs.getString("user_id_vendor", null);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViewById();
    }

    private void initViewById() {

        tvServiceName = findViewById(R.id.tv_serviceName);
        tvCustomerName = findViewById(R.id.tv_vendroName);
        tvCustomerPrice = findViewById(R.id.tv_customerprice);
        tvJobLocation = findViewById(R.id.tv_location);
        tvDescription = findViewById(R.id.tv_description);
        imageViewCustomer=findViewById(R.id.iv_vendorimage);
        tv_date=findViewById(R.id.tv_date);
        tv_time=findViewById(R.id.tv_time);
        connectionDetector = new ConnectionDetector(LeadsDetailsActivity.this);
        loadingDialog = new LoadingDialog(LeadsDetailsActivity.this);
        if (connectionDetector.isConnectingToInternet()) {
            getLeadsDetailsRequest();
        } else {
          /*  Snackbar snackbar = Snackbar.make(((MainActivityVendor) getApplicationContext()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);
            snackbar.show();*/
        }

        btnAcceptLead = findViewById(R.id.btn_sendyouroffer);
        btnRejectLead = findViewById(R.id.btn_rejectoffer);
        btnAcceptLead.setOnClickListener(this);
        btnRejectLead.setOnClickListener(this);
    }

    private void getLeadsDetailsRequest() {

        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_LEADS_DEATAILS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "LeadsList= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONObject jsonObject = jobj.getJSONObject("details");
                                String my_task_id = jsonObject.getString("my_task_id");
                                String my_task_message = jsonObject.getString("my_task_message");
                                String booking_date = jsonObject.getString("booking_date");
                                String booking_time = jsonObject.getString("booking_time");
                               // String discount_amt = jsonObject.getString("discount_amt");
                                String my_task_price = jsonObject.getString("my_task_price");
                                String booking_amount = jsonObject.getString("booking_amount");
                                String my_task_job_location = jsonObject.getString("my_task_job_location");
                                String u_name = jsonObject.getString("u_name");
                                String category_id = jsonObject.getString("category_id");
                                String category_name = jsonObject.getString("category_name");
                                String sub_category_id = jsonObject.getString("sub_category_id");
                                String sub_category_name = jsonObject.getString("sub_category_name");
                                String uimage = jsonObject.getString("u_img");

                                tvServiceName.setText(sub_category_name);
                                //   tvSubServiceName.setText(sub_category_name);
                                tvCustomerName.setText(u_name);
                                tvCustomerPrice.setText("$"+my_task_price);

                                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                                Date date_format = null;
                                try {
                                    date_format = inputFormat.parse(booking_date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String outputDateFormat = outputFormat.format(date_format);
                                Log.d("DateFormatClass", outputDateFormat);

                                SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm:ss");
                                SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm aa");
                                String time_format = null;
                                try {
                                    time_format = outFormat.format(inFormat.parse(booking_time));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                tv_date.setText(outputDateFormat);
                                tv_time.setText(time_format);
                                if (uimage.equals("")) {
                                    Glide.with(getApplicationContext()).load(R.drawable.user_register).into(imageViewCustomer);
                                } else {
                                    Glide.with(getApplicationContext()).load(uimage).into(imageViewCustomer);
                                }

                                if (my_task_job_location.equals("1")) {
                                    String customer_place = "Customer Place";
                                    tvJobLocation.setText(customer_place);
                                } else {
                                    String vendor_place = "Vendor Place";
                                    tvJobLocation.setText(vendor_place);
                                }
                                //tvJobLocation.setText(my_task_job_location);
                                tvDescription.setText(my_task_message);


                            } else {

                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(LeadsDetailsActivity.this, error);
                        AlertUtility.showAlert(LeadsDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("my_task_id", my_task_ids);


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_rejectoffer:
                String value_reject = "Reject";
                String set_title_reject = "Lead Reject!!";
                String set_message_reject = "Are you sure you want to lead rejected?";
                showAlertDialog(value_reject, set_title_reject, set_message_reject);
                break;

            case R.id.btn_sendyouroffer:
                String value_accept = "Accept";
                String set_title_accept = "Lead Accept!!";
                String set_message_accept = "Are you sure you want to lead accepted?";
                showAlertDialog(value_accept, set_title_accept, set_message_accept);
                break;
        }
    }

    private void showAlertDialog(final String value_accept_reject, String set_title_accept, final String set_message_accept_reject) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LeadsDetailsActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle(set_title_accept);

        // Setting Dialog Message
        alertDialog.setMessage(set_message_accept_reject);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(value_accept_reject, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
                if (value_accept_reject.equals("Accept")) {
               //     postAcceptedRejectedRequest(value_accept_reject);
                } else {
               //     postAcceptedRejectedRequest(value_accept_reject);
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

/*
    private void postAcceptedRejectedRequest(String value_accept_reject) {
        loadingDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("my_task_id", my_task_ids);
        params.put("vendor_id", vendor_id);
        Log.e("ParamsLeadDetails", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("leadsDetails");
        if (value_accept_reject.equals("Accept")){
            request_info.setUrl(WebsServiceURLVendor.VENDOR_LEADS_DETAILS_ACCEPT);
        }else {
            request_info.setUrl(WebsServiceURLVendor.VENDOR_LEADS_DETAILS_REJECT);
        }
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, LeadsDetailsActivity.this, request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "LeadsListAcceptedRejected= " + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");
                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {
                        finish();
                        Toast.makeText(LeadsDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        //fragmentCommunicator.passDataToFragment();

                    } else {
                        Toast.makeText(LeadsDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    System.out.println("jsonexeption" + e.toString());
                }
            }

            @Override
            public void OnError(String message) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                AlertUtility.showAlert(getApplicationContext(), false, "Network Error,Please Check Internet Connection");
            }
        });

    }
*/

    @Override
    public void onMethodCallPosted() {

    }
}

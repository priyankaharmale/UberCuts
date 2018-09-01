package com.hnweb.ubercuts.vendor.activity;

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
import android.widget.LinearLayout;
import android.widget.TextView;


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
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyJobsBookedDetailsActivity extends AppCompatActivity {

    SharedPreferences prefs;
    String my_task_id, user_id, user_name = "", user_image = "";
    String vendor_id;
    TextView tvServiceName, tvDate, tvTime, tvJobLocation, tvDescription,
            tvCustomerName, tvCustomerAddress, tvAmount;
    Button btnMarkAsComplete;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    ImageView iv_cutsomer;
    LinearLayout linearLayout, linearLayoutMessage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_bookedjob_details);

        Intent intent = getIntent();
        my_task_id = intent.getStringExtra("my_task_id").toString();

        prefs = getSharedPreferences("MyPrefVendor", MODE_PRIVATE);
        vendor_id = prefs.getString("user_id_vendor", null);
        Log.d("GetData", vendor_id + " :: " + my_task_id);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViewById();
    }

    private void initViewById() {

        tvServiceName = findViewById(R.id.tv_serviceName);

        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvJobLocation = findViewById(R.id.textView_job_location_booked);
        tvDescription = findViewById(R.id.textView_description_booked);
        tvCustomerName = findViewById(R.id.textView_beautician_details);
        tvCustomerAddress = findViewById(R.id.textView_customer_address_book);
        tvAmount = findViewById(R.id.textView_amount_received_book);

        btnMarkAsComplete = findViewById(R.id.button_mark_as_complete);
        iv_cutsomer = findViewById(R.id.iv_cutsomer);
        btnMarkAsComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        connectionDetector = new ConnectionDetector(MyJobsBookedDetailsActivity.this);
        loadingDialog = new LoadingDialog(MyJobsBookedDetailsActivity.this);

        if (connectionDetector.isConnectingToInternet()) {
            getBookedDetailsJobsRequest();
        } else {
            Snackbar snackbar = Snackbar.make(((MainActivityVendor) getApplicationContext()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();
            //Toast.makeText(MyJobsBookedDetailsActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

     /*   linearLayout = findViewById(R.id.linearLayout_show_on_map);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyJobsBookedDetailsActivity.this, ShowOnMapActivity.class);
                intent.putExtra("MyTaskID",my_task_id);
                intent.putExtra("Beautician_id",user_id);
                intent.putExtra("BeauticianImage",user_image);
                intent.putExtra("BeauticianName",user_name);
                intent.putExtra("MessageFlag","Vendor");
                startActivity(intent);
            }
        });

        linearLayoutMessage = findViewById(R.id.linearLayoutMsg);
        linearLayoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyJobsBookedDetailsActivity.this, MessageChatActivity.class);
                intent.putExtra("MessageFlag","Booked");
                intent.putExtra("MyTaskID",my_task_id);
                intent.putExtra("Beautician_id",user_id);
                startActivity(intent);
            }
        });*/
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyJobsBookedDetailsActivity.this);
        // set title
        alertDialogBuilder.setTitle("Mark As Complete!!");
        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to mark as completed?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //   postMarkAsCompleteRequest(dialog);
                        //Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

/*
    private void postMarkAsCompleteRequest(final DialogInterface dialog) {
        //loadingDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("my_task_id", my_task_id);
        Log.d("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("markascomplete");
        request_info.setUrl(WebsServiceURLVendor.VENDOR_MARK_AS_COMPLETED);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, this, request_info, false, new DataUtility.OnDataCallbackListner() {
            @SuppressLint("SetTextI18n")
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "PostDetails :" + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {
                        Toast.makeText(MyJobsBookedDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        finish();
                    } else {
                        dialog.cancel();
                        Toast.makeText(MyJobsBookedDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                AlertUtility.showAlert(MyJobsBookedDetailsActivity.this, false, "Network Error,Please Check Internet Connection");
            }
        });
    }
*/

    private void getBookedDetailsJobsRequest() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_VENDOR_JOB_DETAILS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("MyJobsResponse :", response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONArray jsonArray = jobj.getJSONArray("details");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String category_id = jsonObject.getString("category_id");
                                    String category_name = jsonObject.getString("category_name");
                                    String sub_category_name = jsonObject.getString("sub_category_name");
                                    String sub_category_id = jsonObject.getString("sub_category_id");
                                    String date = jsonObject.getString("created_date");
                                    String time = jsonObject.getString("created_time");
                                    String job_location = jsonObject.getString("my_task_job_location");
                                    String task_msg = jsonObject.getString("my_task_message");

                                    String add_street = jsonObject.getString("u_street");
                                    String add_city = jsonObject.getString("u_city");
                                    String add_state = jsonObject.getString("u_state");
                                    String add_country = jsonObject.getString("u_country");
                                    String add_zipcode = jsonObject.getString("u_zipcode");
                                    user_id = jsonObject.getString("user_id");
                                    user_name = jsonObject.getString("user_name");
                                    user_image = jsonObject.getString("user_image");

                                    String amount_received = jsonObject.getString("amount_paid");

                                    String booking_date = jsonObject.getString("booking_date");
                                    String booking_time = jsonObject.getString("booking_time");

                                    tvServiceName.setText(category_name);
                                    //  tvSubServiceName.setText(sub_category_name);
                                    tvCustomerName.setText(user_name);
                                    tvJobLocation.setText(job_location);
                                    tvDescription.setText(task_msg);
                                    tvCustomerAddress.setText(add_street + ", " + add_city + ", " + add_state + ", " + add_country + ", " + add_zipcode);

                                    String input_date_format = "yyyy-MM-dd";
                                    String output_date_format = "dd MMM yyyy";
                                    Utils utils = new Utils();
                                    String outputDateFormat = utils.dateFormats(booking_date, input_date_format, output_date_format);
                                    Log.e("dateFormatsConverts", outputDateFormat);

                                    tvDate.setText(outputDateFormat);

                                    String input_time_format = "hh:mm:ss";
                                    String output_time_format = "HH:mm aa";
                                    String outputTimeFormat = utils.timeFormats(booking_time, input_time_format, output_time_format);
                                    Log.e("timeFormatsConverts", outputTimeFormat);
                                    tvTime.setText(outputTimeFormat);

                                    if (tvAmount.equals("")) {
                                        tvAmount.setText(" - ");
                                    } else {
                                        tvAmount.setText("$ " + amount_received);
                                    }
                                    if (user_image.equals("")) {
                                        Glide.with(getApplicationContext()).load(R.drawable.user_register).into(iv_cutsomer);
                                    } else {
                                        Glide.with(getApplicationContext()).load(user_image).into(iv_cutsomer);
                                    }
                                }

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
                        String reason = AppUtils.getVolleyError(MyJobsBookedDetailsActivity.this, error);
                        AlertUtility.showAlert(MyJobsBookedDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("my_task_id", my_task_id);
                    params.put("status", "1");


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

}

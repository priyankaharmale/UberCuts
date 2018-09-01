package com.hnweb.ubercuts.vendor.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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

public class MyJobsCompletedDetailsActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    SharedPreferences prefs;
    String my_task_id;
    String vendor_id;
    TextView tvServiceName, tvDate, tvTime, tvJobLocation, tvDescription,
            tvCustomerName, tvCustomerAddress, tvAmount;
    Button btnMarkAsComplete, btnReceivedFeedBack;
    EditText etReceivedFeedback;
   // RatingBar ratingBarFeedback;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    //Float ratingbar;
    String receiver_id = "";

    ImageView iv_cutsomer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_completededjob_details);

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

        tvServiceName = findViewById(R.id.textView_service_names_complete);

        tvDate = findViewById(R.id.textView_date_completed);
        tvTime = findViewById(R.id.textView_time_completed);
        tvJobLocation = findViewById(R.id.textView_job_location_completed);
        tvDescription = findViewById(R.id.textView_description_completed);
        tvCustomerName = findViewById(R.id.textView_customer_name_completed);
        tvCustomerAddress = findViewById(R.id.textView_customer_address_completed);
        tvAmount = findViewById(R.id.textView_amount_received_completed);
        iv_cutsomer=findViewById(R.id.iv_cutsomer);

      //  etReceivedFeedback = findViewById(R.id.editText_feedback_comment_completed);
        btnReceivedFeedBack = findViewById(R.id.button_feedback_post_complete);
        btnReceivedFeedBack.setOnClickListener(this);
       /* ratingBarFeedback = findViewById(R.id.rtb_feedback_star_completed);
        ratingBarFeedback.setOnRatingBarChangeListener(this);
*/
        connectionDetector = new ConnectionDetector(MyJobsCompletedDetailsActivity.this);
        loadingDialog = new LoadingDialog(MyJobsCompletedDetailsActivity.this);
        if (connectionDetector.isConnectingToInternet()) {
            getCompletedDetailsJobsRequest();
        } else {
            Snackbar snackbar = Snackbar.make(((MainActivityVendor) getApplicationContext()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();
            //Toast.makeText(MyJobsCompletedDetailsActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_feedback_post_complete:
                String feedback = etReceivedFeedback.getText().toString();
                if (!feedback.equals("")){
               //     postReceivedFeedBack(feedback,etReceivedFeedback);
                }else {
                    etReceivedFeedback.setError("Please Enter Feedback");
                }
                break;
            default:
                break;
        }
    }
    

    private void getCompletedDetailsJobsRequest() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_VENDOR_JOB_DETAILS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "PostDetails :" + response);

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
                                    String user_id = jsonObject.getString("user_id");
                                    String user_name = jsonObject.getString("user_name");
                                    String user_image = jsonObject.getString("user_image");
                                    String amount_received = jsonObject.getString("amount_paid");

                                    String booking_date = jsonObject.getString("booking_date");
                                    String booking_time = jsonObject.getString("booking_time");

                                    receiver_id = user_id;

                                    tvServiceName.setText(category_name);
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
                        String reason = AppUtils.getVolleyError(MyJobsCompletedDetailsActivity.this, error);
                        AlertUtility.showAlert(MyJobsCompletedDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("my_task_id", my_task_id);
                    params.put("status", "2");


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
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        //ratingbar = rating;
        //Toast.makeText(MyJobsCompletedDetailsActivity.this, "rating \n" + rating, Toast.LENGTH_SHORT).show();
    }

/*
    private void postReceivedFeedBack(String feedback, final EditText etReceivedFeedback) {

        //loadingDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("review_content", feedback);
        params.put("rating", String.valueOf(ratingbar));
        params.put("sender_id", vendor_id);
        params.put("receiver_id", receiver_id);
        params.put("my_task_id", my_task_id);

        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("mytask");
        request_info.setUrl(WebsServiceURLUser.USER_POST_YOUR_FEEDBACK);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, this, request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "Feedback Post :" + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {
                        etReceivedFeedback.setText("");
                        finish();
                        Toast.makeText(MyJobsCompletedDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyJobsCompletedDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                AlertUtility.showAlert(MyJobsCompletedDetailsActivity.this, false, "Network Error,Please Check Internet Connection");
            }
        });
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}

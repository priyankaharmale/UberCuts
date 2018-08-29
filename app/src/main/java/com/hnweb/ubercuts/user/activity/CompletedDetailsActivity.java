package com.hnweb.ubercuts.user.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
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
public class CompletedDetailsActivity extends AppCompatActivity {

    SharedPreferences prefs;
    String user_id;
    TextView tvServices, tvSubServices, tvJobLocation, tvDate, tvTime, tvDescription,
            tvBeautician, tvAmountPaid, tvUserNames,tvReceivedFeedback,tv_yourprice;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String my_task_ids;
    Button btnPostFeedback;
    ImageView imageViewBeautician;
    RatingBar ratingBarStar;
    Float ratingbar;
    EditText etFeedback;
    String receiver_id = "";
    float format_rating_bar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completedtask_details);

        prefs = getSharedPreferences("MyPrefUser", MODE_PRIVATE);
        user_id = prefs.getString("user_id_user", null);

        Intent intent = getIntent();
        my_task_ids = intent.getStringExtra("my_task_id").toString();
        Log.d("UserMyTask", my_task_ids);
        initViewByIds();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initViewByIds() {

       /* tvServices = findViewById(R.id.textView_completed_name);
        tvSubServices = findViewById(R.id.textView_sub_service_complete_name);

        tvJobLocation = findViewById(R.id.textView_job_location_complete);
        tvDescription = findViewById(R.id.textView_description_complete);
        tvBeautician = findViewById(R.id.textView_beautician_address_complete);
        tvAmountPaid = findViewById(R.id.textView_amount_paid_complete);
        tvDate = findViewById(R.id.textView_completed_date);
        tvTime = findViewById(R.id.textView_completed_time);
        tvUserNames = findViewById(R.id.textView_completed_names);
        imageViewBeautician = findViewById(R.id.profile_image_beautician_complete);
*/

        tvServices = findViewById(R.id.tv_serviceName);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tv_yourprice=findViewById(R.id.tv_yourprice);
        tvJobLocation = findViewById(R.id.textView_job_location_book);
        tvDescription = findViewById(R.id.tv_description);
        tvUserNames = findViewById(R.id.textView_beautician_details);
        tvBeautician=findViewById(R.id.textView_beautician_address);
        imageViewBeautician=findViewById(R.id.overlapImage);


      //  tvReceivedFeedback = findViewById(R.id.tv_received_feedback);
       /* linearLayout = findViewById(R.id.linear_received_feedback);
        ratingBarStar = findViewById(R.id.rtb_feedback_star);
        ratingBarStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                //txtRatingValue.setText(String.valueOf(rating));
                ratingbar = rating;
                format_rating_bar = Math.round(ratingbar);
                // Toast.makeText(CompletedDetailsActivity.this, "rating" + rating + " :: " + format_rating_bar, Toast.LENGTH_SHORT).show();

            }
        });

        etFeedback = findViewById(R.id.editText_feedback_comment);
*/

        connectionDetector = new ConnectionDetector(CompletedDetailsActivity.this);
        loadingDialog = new LoadingDialog(CompletedDetailsActivity.this);

        if (connectionDetector.isConnectingToInternet()) {
            getCompletedDetailsData();
         //   getReceivedFeedBack();
        } else {
            /*Snackbar snackbar = Snackbar.make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(CompletedDetailsActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

     /*   btnPostFeedback = findViewById(R.id.button_comment_post);
        btnPostFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String get_feedback = etFeedback.getText().toString().trim();

                if (!get_feedback.matches("")) {
                    if (ratingBarStar.getRating() == 0.0) {
                        //set what you want\
                        Toast.makeText(CompletedDetailsActivity.this, "Please Rating Star", Toast.LENGTH_SHORT).show();
                    } else {
                        //give error if ratingbar not changed
                        postFeedbackRequest(get_feedback, ratingbar);
                    }
                } else {
                    Toast.makeText(CompletedDetailsActivity.this, "Please Enter Review Comment!", Toast.LENGTH_SHORT).show();
                }


                //Toast.makeText(CompletedDetailsActivity.this, "Rating Bar"+ratingbar, Toast.LENGTH_SHORT).show();

                //getPostTaskCancel(my_task_ids);
            }
        });*/
    }

  /*  private void getReceivedFeedBack() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, WebsServiceURLUser.USER_RECEIVED_FEEDBACK,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("RecivedFeedback", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("message_code");
                            Log.d("message_code:- ", String.valueOf(code));

                            if (code == 1) {
                                linearLayout.setVisibility(View.VISIBLE);
                                String msg = jsonObject.getString("message");
                                Log.d("message ", msg);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("details");
                                String review_content = jsonObject1.getString("review_content");
                                if (review_content.equals("")) {
                                    tvReceivedFeedback.setText(" - ");
                                } else {
                                    tvReceivedFeedback.setText(review_content);
                                }

                               // Toast.makeText(CompletedDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();

                            } else {
                                linearLayout.setVisibility(View.GONE);
                                String msg = jsonObject.getString("message");
                                Log.d("message:- ", msg);

                                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error: ", error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("u_id", user_id);
                params.put("my_task_id", my_task_ids);

                Log.e("RecevedFeedBack ", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);

    }

    private void postFeedbackRequest(String get_feedback, Float ratingbar) {
        prefs = getSharedPreferences("MyPrefUser", MODE_PRIVATE);
        String user_id = prefs.getString("user_id_user", null);
        //loadingDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("review_content", get_feedback);
        params.put("rating", String.valueOf(ratingbar));
        params.put("sender_id", user_id);
        params.put("receiver_id", receiver_id);
        params.put("my_task_id", my_task_ids);

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
                        etFeedback.setText("");
                        finish();
                        Toast.makeText(CompletedDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CompletedDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                AlertUtility.showAlert(CompletedDetailsActivity.this, false, "Network Error,Please Check Internet Connection");
            }
        });
    }
*/
    private void getCompletedDetailsData() {

        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_MYTASKDETAILS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "CompletedDetails :" + response);

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
                                    String date = jsonObject.getString("booking_date");
                                    String time = jsonObject.getString("booking_time");
                                    String job_location = jsonObject.getString("my_task_job_location");
                                    String task_msg = jsonObject.getString("my_task_message");
                                    String beautician_image = jsonObject.getString("beautician_image");
                                    String beautician_name = jsonObject.getString("beautician_name");
                                    String beautician_id = jsonObject.getString("beautician_id");
                                    String amount_paid = jsonObject.getString("amount_paid");

                                    String add_street = jsonObject.getString("u_street");
                                    String add_city = jsonObject.getString("u_city");
                                    String add_state = jsonObject.getString("u_state");
                                    String add_country = jsonObject.getString("u_country");
                                    String add_zipcode = jsonObject.getString("u_zipcode");

                                    receiver_id = beautician_id;
                                    tv_yourprice.setText("$"+amount_paid);
                                    tvServices.setText(sub_category_name);
                                    tvUserNames.setText(beautician_name);
                                    tvJobLocation.setText(job_location);
                                    tvDescription.setText(task_msg);
                                    tvBeautician.setText(add_street + ", " + add_city + ", " + add_state + ", " + add_country + ", " + add_zipcode);

                                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                                    Date date_format = null;
                                    try {
                                        date_format = inputFormat.parse(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String outputDateFormat = outputFormat.format(date_format);
                                    Log.d("DateFormatClass", outputDateFormat);

                                    SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm:ss");
                                    SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm aa");
                                    String time_format = null;
                                    try {
                                        time_format = outFormat.format(inFormat.parse(time));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    tvDate.setText(outputDateFormat);
                                    tvTime.setText(time_format);

                                    if (beautician_image.equals("")) {
                                        Glide.with(getApplicationContext()).load(R.drawable.user_register).into(imageViewBeautician);
                                    } else {
                                        Glide.with(getApplicationContext()).load(beautician_image).into(imageViewBeautician);
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
                        String reason = AppUtils.getVolleyError(CompletedDetailsActivity.this, error);
                        AlertUtility.showAlert(CompletedDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("my_task_id", my_task_ids);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}

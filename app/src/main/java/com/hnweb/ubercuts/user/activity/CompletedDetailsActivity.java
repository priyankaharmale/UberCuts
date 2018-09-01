package com.hnweb.ubercuts.user.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.hnweb.ubercuts.vendor.activity.MainActivityVendor;

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
            tvBeautician, tvAmountPaid, tvUserNames, tvReceivedFeedback, tv_yourprice;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String my_task_ids;
    Button button_give_feedback;
    ImageView imageViewBeautician;
    Float ratingbar;
    String receiver_id = "";
    float format_rating_bar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completedtask_details);

        prefs = getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);


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
        tv_yourprice = findViewById(R.id.tv_yourprice);
        tvJobLocation = findViewById(R.id.textView_job_location_book);
        tvDescription = findViewById(R.id.tv_description);
        tvUserNames = findViewById(R.id.textView_beautician_details);
        tvBeautician = findViewById(R.id.textView_beautician_address);
        imageViewBeautician = findViewById(R.id.overlapImage);


        button_give_feedback = findViewById(R.id.button_give_feedback);

        button_give_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAlertDailog();
            }
        });

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

    }


    private void postFeedbackRequest(final String get_feedback, final Float ratingbar,final  Dialog dialog) {

        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GIVEFEEDBACK,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "Feedback Post :" + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                dialog.dismiss();
                                Toast.makeText(CompletedDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CompletedDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                    params.put("review_content", get_feedback);
                    params.put("rating", String.valueOf(ratingbar));
                    params.put("sender_id", user_id);
                    params.put("receiver_id", receiver_id);
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
                                    tv_yourprice.setText("$" + amount_paid);
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


    private void updateAlertDailog() {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rating_reviw);

        Button btnPostFeedback = (Button) dialog.findViewById(R.id.btn_post_add_service);
        Button btnCancel = dialog.findViewById(R.id.btn_cancle);
        final EditText editText_feedback_comment = (EditText) dialog.findViewById(R.id.editText_feedback_comment);
       final   RatingBar ratingBarStar = dialog.findViewById(R.id.rtb_reviews_rating);


        ratingBarStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                //txtRatingValue.setText(String.valueOf(rating));
                ratingbar = rating;
                format_rating_bar = Math.round(ratingbar);
                // Toast.makeText(CompletedDetailsActivity.this, "rating" + rating + " :: " + format_rating_bar, Toast.LENGTH_SHORT).show();

            }
        });



        btnPostFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String get_feedback = editText_feedback_comment.getText().toString().trim();

                if (!get_feedback.matches("")) {
                    if (ratingBarStar.getRating() == 0.0) {
                        //set what you want\
                        Toast.makeText(CompletedDetailsActivity.this, "Please Rating Star", Toast.LENGTH_SHORT).show();
                    } else {
                        //give error if ratingbar not changed
                        postFeedbackRequest(get_feedback, ratingbar,dialog);
                    }
                } else {
                    Toast.makeText(CompletedDetailsActivity.this, "Please Enter Review Comment!", Toast.LENGTH_SHORT).show();
                }


                //Toast.makeText(CompletedDetailsActivity.this, "Rating Bar"+ratingbar, Toast.LENGTH_SHORT).show();

                //getPostTaskCancel(my_task_ids);
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

}

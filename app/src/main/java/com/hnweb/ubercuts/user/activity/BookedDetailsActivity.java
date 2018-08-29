package com.hnweb.ubercuts.user.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

@SuppressLint("SimpleDateFormat")
public class BookedDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences prefs;
    String user_id;
    TextView tvServices, tvDate, tvTime, tvJobLocation, tvDescription, tvBeautician, textView_beautician_address, tv_yourprice;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String my_task_ids, beautician_id;
    String beautician_image, beautician_name, beautician_name_value;
    Button btnCancelTask;
    ImageView imageViewBeautician;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookedtask_details);

        Intent intent = getIntent();
        my_task_ids = intent.getStringExtra("my_task_id").toString();
//        beautician_id = intent.getStringExtra("beautician_id").toString();

        Log.d("UserMyTask", my_task_ids);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViewByIds();
    }

    private void initViewByIds() {

        tvServices = findViewById(R.id.tv_serviceName);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);

        tvJobLocation = findViewById(R.id.textView_job_location_book);
        tvDescription = findViewById(R.id.tv_description);
        tvBeautician = findViewById(R.id.textView_beautician_details);
        textView_beautician_address = findViewById(R.id.textView_beautician_address);
        imageViewBeautician = findViewById(R.id.overlapImage);
        tv_yourprice = findViewById(R.id.tv_yourprice);

        connectionDetector = new ConnectionDetector(BookedDetailsActivity.this);
        loadingDialog = new LoadingDialog(BookedDetailsActivity.this);

        if (connectionDetector.isConnectingToInternet()) {
            getPostedDetailsData();
        } else {
            /*Snackbar snackbar = Snackbar.make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(BookedDetailsActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

        btnCancelTask = findViewById(R.id.button_cancel_task);
        btnCancelTask.setOnClickListener(this);
    }

    private void getPostedDetailsData() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_MYTASKDETAILS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "bookedDetails :" + response);

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
                                    beautician_image = jsonObject.getString("beautician_image");
                                    beautician_name = jsonObject.getString("beautician_name");
                                    String amount_paid = jsonObject.getString("amount_paid");

                                    String add_street = jsonObject.getString("u_street");
                                    String add_city = jsonObject.getString("u_city");
                                    String add_state = jsonObject.getString("u_state");
                                    String add_country = jsonObject.getString("u_country");
                                    String add_zipcode = jsonObject.getString("u_zipcode");
                                    beautician_name_value = beautician_name;

                                    tvServices.setText(sub_category_name);
                                    tvBeautician.setText(beautician_name);
                                    tvJobLocation.setText(job_location);
                                    tvDescription.setText(task_msg);
                                    tv_yourprice.setText("$"+amount_paid);
                                    textView_beautician_address.setText(add_street + ", " + add_city + ", " + add_state + ", " + add_country + ", " + add_zipcode);

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
                        String reason = AppUtils.getVolleyError(BookedDetailsActivity.this, error);
                        AlertUtility.showAlert(BookedDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("my_task_id", my_task_ids);
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
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_recived_offers:

              /*  Intent intent = new Intent(PostedDetailsActivity.this, ReceivedViewOffersActivity.class);
                startActivity(intent);*/
                break;

            case R.id.button_cancel_task:
                showAlertDialog();
                break;

            default:
                break;
        }
    }

    private void showAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BookedDetailsActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Cancel This Task...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want cancel this task?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //  getPostTaskCancel(my_task_ids);
                // Write your code here to invoke YES event
                //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
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
    private void getPostTaskCancel(String my_task_ids) {
        loadingDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("my_task_id", my_task_ids);

        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("mytask");
        request_info.setUrl(WebsServiceURLUser.USER_CANCEL_MY_TASK);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, this, request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "BookCancelTaskDetails :" + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {
                        finish();
                        getPostRefresh();
                    } else {

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
                AlertUtility.showAlert(PostedDetailsActivity.this, false, "Network Error,Please Check Internet Connection");
            }
        });
    }
*/

    private void getPostRefresh() {
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

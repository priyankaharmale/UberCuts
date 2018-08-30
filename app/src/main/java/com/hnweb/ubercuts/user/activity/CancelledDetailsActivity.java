package com.hnweb.ubercuts.user.activity;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CancelledDetailsActivity extends AppCompatActivity {

    SharedPreferences prefs;
    String user_id;
    TextView tvServices, tvJobLocation, tvDate, tv_yourprice, tvTime, tvDescription, tvBeautician, tvAmountPaid, tvUserNames;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String my_task_ids;
    Button btnCancelTask;
    ImageView imageViewBeautician;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canclededtask_details);

        Intent intent = getIntent();
        my_task_ids = intent.getStringExtra("my_task_id").toString();
        Log.d("UserMyTask", my_task_ids);
        initViewByIds();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViewByIds() {

    /*    tvServices = findViewById(R.id.textView_canceled_name);
        tvSubServices = findViewById(R.id.textView_sub_service_cancel_name);

        tvJobLocation = findViewById(R.id.textView_job_location_cancel);
        tvDescription = findViewById(R.id.textView_description_cancel);
        tvBeautician = findViewById(R.id.textView_beautician_address_cancel);
        tvAmountPaid = findViewById(R.id.textView_amount_paid_cancel);
        tvDate = findViewById(R.id.textView_canceled_date);
        tvTime = findViewById(R.id.textView_cancelled_time);
        tvUserNames = findViewById(R.id.textView_cancelled_names);
        imageViewBeautician = findViewById(R.id.profile_image_beautician_cancel);
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


        connectionDetector = new ConnectionDetector(CancelledDetailsActivity.this);
        loadingDialog = new LoadingDialog(CancelledDetailsActivity.this);

        if (connectionDetector.isConnectingToInternet()) {
            getCancelDetailsData();
        } else {
            /*Snackbar snackbar = Snackbar.make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(CancelledDetailsActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

        btnCancelTask = findViewById(R.id.button_reschedule_task);
        btnCancelTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getPostTaskCancel(my_task_ids);
                showAlertDialog();
            }
        });
    }

    private void getCancelDetailsData() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_MYTASKDETAILS,
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
                                    String date = jsonObject.getString("booking_date");
                                    String time = jsonObject.getString("booking_time");
                                    String job_location = jsonObject.getString("my_task_job_location");
                                    String task_msg = jsonObject.getString("my_task_message");
                                    String beautician_image = jsonObject.getString("beautician_image");
                                    String beautician_name = jsonObject.getString("beautician_name");
                                    String amount_paid = jsonObject.getString("amount_paid");

                                    String add_street = jsonObject.getString("u_street");
                                    String add_city = jsonObject.getString("u_city");
                                    String add_state = jsonObject.getString("u_state");
                                    String add_country = jsonObject.getString("u_country");
                                    String add_zipcode = jsonObject.getString("u_zipcode");

                                    tvServices.setText(sub_category_name);
                                    tvUserNames.setText(beautician_name);
                                    tvJobLocation.setText(job_location);
                                    tvDescription.setText(task_msg);
                                    tvDate.setText(date);
                                    tvTime.setText(time);
                                    tv_yourprice.setText("$" + amount_paid);

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
                        String reason = AppUtils.getVolleyError(CancelledDetailsActivity.this, error);
                        AlertUtility.showAlert(CancelledDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("my_task_id", my_task_ids);
                    params.put("status", "3");


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

    private void showAlertDialog() {

        final Dialog dialog = new Dialog(CancelledDetailsActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_row_cancelled);

        Button sendBtn = (Button) dialog.findViewById(R.id.btn_post_add_service);
        Button btnCancel = dialog.findViewById(R.id.cancel_add_service);

        ImageView imageViewDate = dialog.findViewById(R.id.imageView_date);
        ImageView imageViewTime = dialog.findViewById(R.id.imageView_time);

        Date today = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yyyy");
        String dateToStr = formatDate.format(today);

        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm aa");
        String timeToStr = formatTime.format(today);

        final TextView tvPostedDate = dialog.findViewById(R.id.textView_posted_date);
        final TextView tvPostedTime = dialog.findViewById(R.id.textView_posted_time);
        tvPostedDate.setText(dateToStr);
        tvPostedTime.setText(timeToStr);
        imageViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePicker(tvPostedDate);
            }
        });
        imageViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimePicker(tvPostedTime);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date_selected = tvPostedDate.getText().toString().trim();
                String time_selected = tvPostedTime.getText().toString().trim();
                if (!date_selected.matches("")) {
                    if (!time_selected.matches("")) {

                        dialog.cancel();
                        String input_date_format = "dd MMM yyyy";
                        String output_date_format = "yyyy-MM-dd";
                        Utils utils = new Utils();
                        String outputDateFormat = utils.dateFormats(date_selected, input_date_format, output_date_format);

                        //String input_time_format = "hh:mm:ss";
                        // String output_time_format = "HH:mm aa";
                        //String outputTimeFormat = utils.timeFormats(time_selected, input_time_format, output_time_format);

                        showAlertDialogConfirm(outputDateFormat, time_selected, dialog);
                    } else {
                        Toast.makeText(CancelledDetailsActivity.this, "Please select Time", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CancelledDetailsActivity.this, "Please select Date", Toast.LENGTH_SHORT).show();
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

    private void showAlertDialogConfirm(final String date_selected, final String time_selected, Dialog dialog) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CancelledDetailsActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Reschedule Task");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to reschedule task?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
                dialog.cancel();
                postResceduleTaskRequest(date_selected, time_selected, dialog);

            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    private void postResceduleTaskRequest(final String date_selected, final String time_selected, final DialogInterface dialog) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_RESHEDULETASK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("RescheduleTask", "RescheduleTask :" + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                dialog.cancel();
                                finish();
                                Toast.makeText(CancelledDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.cancel();
                                Toast.makeText(CancelledDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(CancelledDetailsActivity.this, error);
                        AlertUtility.showAlert(CancelledDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("my_task_id", my_task_ids);
                    params.put("booking_date", date_selected);
                    params.put("booking_time", time_selected);

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


    private void getDatePicker(final TextView tvPostedDate) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(CancelledDetailsActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String list_of_count = String.format("%02d", (monthOfYear + 1));

                        String date = dayOfMonth + "-" + list_of_count + "-" + year;
                        Log.e("DateFormatChange", date);

                        String input_date_format = "dd-MM-yyyy";
                        String output_date_format = "dd MMM yyyy";
                        Utils utils = new Utils();
                        String outputDateFormat = utils.dateFormats(date, input_date_format, output_date_format);

                        Log.d("DateFormatClass", outputDateFormat);

                        tvPostedDate.setText(outputDateFormat);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void getTimePicker(final TextView tvPostedTime) {

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        //final String finalAm_pm = am_pm;
        TimePickerDialog timePickerDialog = new TimePickerDialog(CancelledDetailsActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        boolean isPM = (hourOfDay >= 12);
                        tvPostedTime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
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

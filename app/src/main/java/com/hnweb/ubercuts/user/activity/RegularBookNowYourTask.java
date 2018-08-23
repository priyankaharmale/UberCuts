package com.hnweb.ubercuts.user.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegularBookNowYourTask extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences prefs;
    String user_id;
    Button btnPostTask, btnSelectJobs, btnCate, btnSubCate, btnCancel;
    EditText etTaskyourMessage;
    TextView textViewPrice;
    Spinner spJob, spCategory, spSubCategory;
    private String[] job_location = {"Customer Place", "Beauty Parlor"};
    private ListView listView;
    private ArrayAdapter<String> jobsArrayAdapter;

    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private boolean selectedCategory = false;

    private String selectedJobs = "";
    private String selectedCategoryId = "";
    private String selectedSubCategoryId = "";
    ImageView imageViewDate, imageViewTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView tvPostedDate, tvPostedTime;
    private String selected_date = "";
    private String selected_time = "";
    private String selected_price, selected_cate, selected_sub_cate;
    private String vendor_ids;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        initViewById();
        Intent intent = getIntent();
        selected_price = intent.getStringExtra("SelectedPrice");
        vendor_ids = intent.getStringExtra("VendorIds");
        selected_sub_cate = intent.getStringExtra("SelectedSubCate");
        textViewPrice = findViewById(R.id.et_your_price);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewPrice.setText("$ " + selected_price);


        tvPostedDate = findViewById(R.id.textView_posted_date);
        tvPostedTime = findViewById(R.id.textView_posted_time);

        prefs = getSharedPreferences("MyPrefUser", MODE_PRIVATE);
        user_id = prefs.getString("user_id_user", null);

        Date today = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yyyy");
        String dateToStr = formatDate.format(today);

        String input_date_format = "dd MMM yyyy";
        String output_date_format = "yyyy-MM-dd";
        Utils utils = new Utils();
        String outputDateFormat = utils.dateFormats(dateToStr, input_date_format, output_date_format);
        Log.e("dateFormatsConverts", outputDateFormat);
        selected_date = outputDateFormat;

        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm aa");
        String timeToStr = formatTime.format(today);
        tvPostedDate.setText(dateToStr);
        tvPostedTime.setText(timeToStr);

        String input_time_format = "hh:mm aa";
        String output_time_format = "HH:mm:ss";
        String outputTimeFormat = utils.timeFormats(timeToStr, input_time_format, output_time_format);
        Log.e("timeFormatsConverts", outputTimeFormat);

        selected_time = outputTimeFormat;

        loadingDialog = new LoadingDialog(RegularBookNowYourTask.this);
    }

    private void initViewById() {

        btnPostTask = findViewById(R.id.btn_post_task);
        btnSelectJobs = findViewById(R.id.button_select_jobs);
        btnCate = findViewById(R.id.button_category);
        btnSubCate = findViewById(R.id.button_sub_category);
        btnCancel = findViewById(R.id.btn_cancel);

        etTaskyourMessage = findViewById(R.id.et_task_your_message);


        imageViewDate = findViewById(R.id.imageView_date);
        imageViewTime = findViewById(R.id.imageView_time);
        imageViewDate.setOnClickListener(this);
        imageViewTime.setOnClickListener(this);

        btnPostTask.setOnClickListener(this);
        btnSelectJobs.setOnClickListener(this);
        btnCate.setOnClickListener(this);
        btnSubCate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        connectionDetector = new ConnectionDetector(RegularBookNowYourTask.this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_post_task:
                inputFieldValidation();
                break;

            case R.id.btn_cancel:
                finish();
                break;

            case R.id.button_select_jobs:
                try {
                    InputMethodManager imm = (InputMethodManager) RegularBookNowYourTask.this.getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(RegularBookNowYourTask.this.getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                onClickedSelectedJobs();
                break;

            case R.id.imageView_date:
                getDatePicker();
                break;

            case R.id.imageView_time:
                getTimePicker();
                break;

            default:
                break;
        }
    }

    private void inputFieldValidation() {

        String your_messsage = etTaskyourMessage.getText().toString().trim();
        //String your_price = etYourPrice.getText().toString().trim();
        String job_location = btnSelectJobs.getText().toString().trim();
        String cate = btnCate.getText().toString().trim();
        String sub_cate = btnSubCate.getText().toString().trim();

        if (!your_messsage.matches("")) {
            if (!selected_date.matches("")) {
                if (!selectedJobs.matches("")) {
                    if (!selected_time.matches("")) {
                        Intent intent = new Intent(RegularBookNowYourTask.this, BookNowPayment.class);
                        intent.putExtra("YourMessage", your_messsage);
                        intent.putExtra("SelectedPrice", selected_price);
                        intent.putExtra("SelectedJobs", selectedJobs);
                        intent.putExtra("SelectedSubCate", selected_sub_cate);
                        intent.putExtra("SelectedDate", selected_date);
                        intent.putExtra("SelectedTime", selected_time);
                        intent.putExtra("VendorIds", vendor_ids);
                        startActivity(intent);
                        etTaskyourMessage.setText("");
                        selectedJobs = "";

                    } else {
                        Toast.makeText(RegularBookNowYourTask.this, "Please Select Time!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegularBookNowYourTask.this, "Please Select Job Location!!", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(RegularBookNowYourTask.this, "Please Select Date!!", Toast.LENGTH_SHORT).show();

            }
        } else {
            etTaskyourMessage.setError("Please Enter Your Message!!");
        }
    }

    private void getDatePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(RegularBookNowYourTask.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        @SuppressLint("DefaultLocale") String list_of_count = String.format("%02d", (monthOfYear + 1));

                        String date = dayOfMonth + "-" + list_of_count + "-" + year;
                        Log.e("DateFormatChange", date);

                        String input_date_format = "dd-MM-yyyy";
                        String output_date_format = "dd MMM yyyy";
                        Utils utils = new Utils();
                        String outputDateFormat = utils.dateFormats(date, input_date_format, output_date_format);
                        Log.e("dateFormatsConverts", outputDateFormat);

                        String input_date_format1 = "dd-MM-yyyy";
                        String output_date_format1 = "yyyy-MM-dd";
                        String outputDateFormat1 = utils.dateFormats(date, input_date_format1, output_date_format1);
                        Log.e("dateFormatsConverts", outputDateFormat1);
                        selected_date = outputDateFormat1;

                        tvPostedDate.setText(outputDateFormat);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void getTimePicker() {

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        Log.d("TimeFormat", String.valueOf(mHour + " :: " + mMinute));

        // Launch Time Picker Dialog
        //final String finalAm_pm = am_pm;
        TimePickerDialog timePickerDialog = new TimePickerDialog(RegularBookNowYourTask.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        boolean isPM = (hourOfDay >= 12);
                        tvPostedTime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                        String time_get = mHour + ":" + mMinute + ":" + "00";

                        String format = String.format("%02d:%02d", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute);


                        selected_time = time_get;
                        Log.e("TimeFormat", time_get + " :: " + format);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    private void onClickedSelectedCategory() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegularBookNowYourTask.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.common_list, null);
        alertDialog.setView(dialogView);

        TextView textView_header = (TextView) dialogView.findViewById(R.id.textView_custom_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select Category");
        }
        Button btnCancel = (Button) dialogView.findViewById(R.id.button_cancel);
        listView = (ListView) dialogView.findViewById(R.id.listView_cate);
        final AlertDialog ad = alertDialog.create();
        ad.show();

        ImageView imageView = dialogView.findViewById(R.id.imageViewBackArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });

    }


    private void onClickedSelectedJobs() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegularBookNowYourTask.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.common_list, null);
        alertDialog.setView(dialogView);

        TextView textView_header = (TextView) dialogView.findViewById(R.id.textView_custom_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select Job Location");
        }
        Button btnCancel = (Button) dialogView.findViewById(R.id.button_cancel);
        listView = (ListView) dialogView.findViewById(R.id.listView_cate);
        final AlertDialog ad = alertDialog.create();
        ad.show();

        ImageView imageView = dialogView.findViewById(R.id.imageViewBackArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });
        jobsArrayAdapter = new ArrayAdapter<String>(RegularBookNowYourTask.this, R.layout.spinner_text, job_location);
        listView.setAdapter(jobsArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stringText;
                //in normal case
                stringText = ((TextView) view).getText().toString();
                btnSelectJobs.setText(stringText);
                if (stringText.equals("Customer Place")) {
                    selectedJobs = "0";
                } else {
                    selectedJobs = "1";
                }
                ad.dismiss();
                if (stringText != null) {
                    //selectedChildCate = true;
                } else {
                    Toast.makeText(RegularBookNowYourTask.this, "Please Select Parent Category...!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

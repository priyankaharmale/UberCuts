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
import android.widget.Button;
import android.widget.TextView;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentDetailsActivity extends AppCompatActivity {

    SharedPreferences prefs;
    String user_id;

    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String userPaymentAmount,paymentMode,paymentStatus,paymentRefNo,paymentDate,paymentTime,paymentAmount;
    TextView textViewAmount,tvMode,tvStatus,tvRefNo,tvDate,tvTime;
    Button btnProcced;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_payment_details);

        //overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        Intent intent = getIntent();
        userPaymentAmount = intent.getStringExtra("PaymentDetails").toString();
        paymentMode = intent.getStringExtra("PaymentMode").toString();
        paymentStatus = intent.getStringExtra("PaymentStatus").toString();
        paymentRefNo = intent.getStringExtra("PaymentRefNo").toString();

        paymentDate = intent.getStringExtra("PaymentDate").toString();
        paymentTime = intent.getStringExtra("PaymentTime").toString();
        paymentAmount = intent.getStringExtra("PaymentAmount").toString();

        Log.d("UserDetailsIds", userPaymentAmount);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViewById();
    }

    @SuppressLint("SetTextI18n")
    private void initViewById() {
        textViewAmount = findViewById(R.id.textView_total_price);

        tvMode = findViewById(R.id.textView_debit);
        tvStatus = findViewById(R.id.textView_payment_status);
        tvRefNo = findViewById(R.id.textView_ref_number);
        tvDate = findViewById(R.id.textView_date);
        tvTime = findViewById(R.id.textView_time);

        btnProcced = findViewById(R.id.button_payment_procced);

        textViewAmount.setText("$ "+userPaymentAmount);
        tvMode.setText(paymentMode);
        tvStatus.setText(paymentStatus);
        tvRefNo.setText(paymentRefNo);

        DateFormat inputFormat = new SimpleDateFormat( "yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date_format = null;
        try {
            date_format = inputFormat.parse(paymentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateFormat = outputFormat.format(date_format);
        Log.d("DateFormatBook", outputDateFormat);

        SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat outFormat = new SimpleDateFormat("hh:mm aa");
        String time_format = null;
        try {
            time_format = outFormat.format(inFormat.parse(paymentTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("TimeFormatBook", time_format);

        tvDate.setText(outputDateFormat);
        tvTime.setText(time_format);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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

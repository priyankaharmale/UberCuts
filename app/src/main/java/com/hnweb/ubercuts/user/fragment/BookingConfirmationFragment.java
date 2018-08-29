package com.hnweb.ubercuts.user.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;


import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.user.activity.PostYourTaskPayNowActivity;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class BookingConfirmationFragment extends Fragment implements View.OnClickListener {

    String my_task_message, my_task_price, my_task_job_location, ref_id_category, ref_category_name, ref_id_sub_category, ref_sub_category_name, status;
    String bids_id;
    SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    TextView tvServices, tvSubServices, tvJobLocation, tvDescription, tvBeautician, tvBeauticianCharge,
            tvOtherCharge, tvPromoCode, tvTotalCharge, tvPostedDate, tvPostedTime;
    RadioButton radioButtonPromoCode;
    EditText etPromocode;
    Button btnApplyPromocode, btnPayNow;
    CheckBox checkBox;
    LinearLayout linearLayout;
    //String paymentValue = "";
    String vendorId = "";
    String myTaskId = "";
    String promoCodeApplyFinalPrice = "";
    ImageView imageViewDate, imageViewTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    boolean checkbox_value = false;
    String promo_code = "", discountAmount = "", discountTotalAmount = "";
    boolean discount_value = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_book_confimation, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            my_task_message = bundle.getString("my_task_message");
            my_task_price = bundle.getString("my_task_price");
            my_task_job_location = bundle.getString("my_task_job_location");
            ref_id_category = bundle.getString("ref_id_category");
            ref_id_sub_category = bundle.getString("ref_id_sub_category");
            ref_category_name = bundle.getString("ref_category_name");
            ref_sub_category_name = bundle.getString("ref_sub_category_name");
            status = bundle.getString("status");
            Log.d("GetBundle", "\n" + my_task_message + "\n" + my_task_price + "\n" +
                    my_task_job_location + "\n" + ref_id_category + "\n" + ref_id_sub_category + "\n" + ref_category_name + "\n" + ref_sub_category_name);
        }

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);

        user_id = prefs.getString(AppConstant.KEY_ID, null);
        Log.e("PostedUserIds", user_id);

        tvPostedDate = view.findViewById(R.id.textView_posted_date);
        tvPostedTime = view.findViewById(R.id.textView_posted_time);

        Date today = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yyyy");
        String dateToStr = formatDate.format(today);

        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm aa");
        String timeToStr = formatTime.format(today);
        tvPostedDate.setText(dateToStr);
        tvPostedTime.setText(timeToStr);
        initViewById(view);


        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initViewById(View view) {

        tvServices = view.findViewById(R.id.textView_service_name_book);
        tvSubServices = view.findViewById(R.id.textView_sub_service_name_book);
        tvJobLocation = view.findViewById(R.id.textView_job_location_book);
        tvDescription = view.findViewById(R.id.textView_description_book);

        tvServices.setText(ref_category_name);
        tvSubServices.setText(ref_sub_category_name);
        //tvJobLocation.setText(my_task_job_location);

        tvBeauticianCharge = view.findViewById(R.id.textView_beautician_charge);
        tvOtherCharge = view.findViewById(R.id.textView_other_charge);
        tvPromoCode = view.findViewById(R.id.textView_promo_code_charge);
        tvTotalCharge = view.findViewById(R.id.textView_total_charges);

        tvBeauticianCharge.setText("$ " + my_task_price);
        tvTotalCharge.setText("$ " + my_task_price);

        imageViewDate = view.findViewById(R.id.imageView_date);
        imageViewTime = view.findViewById(R.id.imageView_time);
        imageViewDate.setOnClickListener(this);
        imageViewTime.setOnClickListener(this);

        checkBox = view.findViewById(R.id.radioButton_promo_code);
        etPromocode = view.findViewById(R.id.editText_promo_code);
        btnPayNow = view.findViewById(R.id.button_pay_now);
        btnApplyPromocode = view.findViewById(R.id.button_promo_code_apply);
        btnPayNow.setOnClickListener(this);
        btnApplyPromocode.setOnClickListener(this);


        linearLayout = view.findViewById(R.id.LLayout);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.GONE);

                    int payment_beautician = Integer.parseInt(my_task_price);
                    int payment_promocode = Integer.parseInt("0");

                    int Total_discount_price = payment_beautician - payment_promocode;

                    promoCodeApplyFinalPrice = String.valueOf(Total_discount_price);

                    tvPromoCode.setText("$ 0");

                    tvTotalCharge.setText("$ " + Total_discount_price);
                }
            }
        });

        loadingDialog = new LoadingDialog(getActivity());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_promo_code_apply:
                String et_promo_code = etPromocode.getText().toString();
                //Log.d("PaymentValueApply",paymentValue);
                //Toast.makeText(this, "Payment "+paymentValue, Toast.LENGTH_SHORT).show();
                if (!et_promo_code.matches("")) {
                    //postApplyPromoCode(et_promo_code);
                } else {
                    etPromocode.setError("Please Enter Promo Code");
                }
                //Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_pay_now:
                String tvDate = tvPostedDate.getText().toString().trim();
                String tvTime = tvPostedTime.getText().toString().trim();

                String input_date_format = "dd MMM yyyy";
                String output_date_format = "yyyy-MM-dd";
                Utils utils = new Utils();
                String outputDateFormat = utils.dateFormats(tvDate, input_date_format, output_date_format);
                Log.e("dateFormatsConverts", outputDateFormat);

                String input_time_format = "hh:mm aa";
                String output_time_format = "hh:mm:ss";
                String outputTimeFormat = utils.timeFormats(tvTime, input_time_format, output_time_format);
                Log.e("timeFormatsConverts", outputTimeFormat);

                Intent intent = new Intent(getActivity(), PostYourTaskPayNowActivity.class);
                intent.putExtra("PaymentAmount", promoCodeApplyFinalPrice);
                intent.putExtra("BookingDate", outputDateFormat);
                intent.putExtra("BookingTime", outputTimeFormat);
                intent.putExtra("VendorPrice", my_task_price);
                intent.putExtra("VendorId", vendorId);
                intent.putExtra("MyTaskId", myTaskId);

                intent.putExtra("my_task_message", my_task_message);
                intent.putExtra("my_task_price", my_task_price);
                intent.putExtra("my_task_job_location", my_task_job_location);
                intent.putExtra("ref_id_category", ref_id_category);
                intent.putExtra("ref_id_sub_category", ref_id_sub_category);
                intent.putExtra("status", status);
                intent.putExtra("promoCodeUsed", promo_code);
                intent.putExtra("discountAmount", discountAmount);
                if (discount_value == true) {
                    intent.putExtra("discountTotalAmount", discountTotalAmount);
                } else {
                    intent.putExtra("discountTotalAmount", my_task_price);
                }
                //Toast.makeText(getActivity(), "Data " + "\n" + promo_code + " ::\n " + discountAmount + " ::\n " + discountTotalAmount, Toast.LENGTH_SHORT).show();
                startActivity(intent);
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

    private void getDatePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String list_of_count = String.format("%02d", (monthOfYear + 1));

                        String date = dayOfMonth + "-" + list_of_count + "-" + year;
                        Log.e("DateFormatChange", date);

                        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                        Date date_format = null;
                        try {
                            date_format = inputFormat.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateFormat = outputFormat.format(date_format);
                        Log.d("DateFormatClass", outputDateFormat);


                        tvPostedDate.setText(outputDateFormat);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void getTimePicker() {

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        //final String finalAm_pm = am_pm;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        boolean isPM = (hourOfDay >= 12);
                        tvPostedTime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


/*
    private void postApplyPromoCode(final String et_promo_code) {
        loadingDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("promo_code", et_promo_code);

        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("promocode");
        request_info.setUrl(WebsServiceURLUser.USER_PROMO_CODE_APPLY);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, getActivity(), request_info, false, new DataUtility.OnDataCallbackListner() {
            @SuppressLint("SetTextI18n")
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "Promocode :" + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {

                        promo_code = et_promo_code;
                        JSONObject jsonObject = jobj.getJSONObject("details");

                        String promocode_value = jsonObject.getString("sDiscount");
                        Log.d("PaymentValue", my_task_price + promocode_value);
                        int value = 10;

                        double amount = Double.parseDouble(promocode_value);
                        //double tip_per = Double.parseDouble(tip.toString());
                        //double tip_cal = (amount * tip_per) / 100;

                        double percent_discount = amount / 100;
                        Log.d("PaymentValueDiscount", String.valueOf(percent_discount));
                        double payment_beautician = Double.parseDouble(my_task_price);
                        Log.e("PaymentValueDiscount", String.valueOf(payment_beautician));
                        Double beautician_amount_percent = payment_beautician * percent_discount;
                        Log.d("PaymentValue", String.valueOf(beautician_amount_percent));
                        Double Total_discount_price = payment_beautician - beautician_amount_percent;
                        Log.e("PaymentValue", String.valueOf(Total_discount_price));

                        String value_discount = new DecimalFormat("##.##").format(Total_discount_price);

                        promoCodeApplyFinalPrice = String.valueOf(value_discount);
                        String value_discount_amount = new DecimalFormat("##.##").format(beautician_amount_percent);
                        tvPromoCode.setText("$ " + String.valueOf(value_discount_amount));
                        discountAmount = value_discount_amount;
                        discountTotalAmount = value_discount;
                        //tvBeauticianCharge.setText("$ "+String.valueOf(value_discount));
                        discount_value = true;
                        tvTotalCharge.setText("$ " + String.valueOf(value_discount));

                        etPromocode.setText("");

                        //checkBox.setChecked(false);

                    } else {
                        Utils.AlertDialog(getActivity(), msg);
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

                AlertUtility.showAlert(getActivity(), false, "Network Error,Please Check Internet Connection");
            }
        });
    }
*/
}

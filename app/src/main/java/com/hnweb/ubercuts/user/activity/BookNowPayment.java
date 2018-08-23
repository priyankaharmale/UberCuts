package com.hnweb.ubercuts.user.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookNowPayment extends AppCompatActivity {

    public static final String PUBLISHABLE_KEY = "pk_test_dC5cjS6xNiCLr68WRFjrV0HN";
    SharedPreferences prefs;
    String user_id, user_email_id;

    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String userPaymentAmount;
    TextView textViewAmount;
    Button btnProcced;
    CheckBox checkBoxCardNumber;
    String cvv_value = "", cardNumber = "", expiryMonth = "", expiryYear = "", expiryYearfourDigit = "";
    String radiobuttonFlag = "";
    private EditText etCardNumber, etMonth, etExpiryDate, etCVV;
    String yourMeassage, selectedJobs, selectedCate, selectedSubCate, bookingDate, bookingTime, vendorIds;
    boolean get_cvv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_now);

        Intent intent = getIntent();
        userPaymentAmount = intent.getStringExtra("SelectedPrice").toString();
        yourMeassage = intent.getStringExtra("YourMessage");
        selectedJobs = intent.getStringExtra("SelectedJobs");
        selectedCate = intent.getStringExtra("SelectedCate");
        selectedSubCate = intent.getStringExtra("SelectedSubCate");
        bookingDate = intent.getStringExtra("SelectedDate");
        bookingTime = intent.getStringExtra("SelectedTime");
        vendorIds = intent.getStringExtra("VendorIds");
        Log.d("UserDetailsIds", userPaymentAmount + " : " + yourMeassage + " : " + selectedJobs + " : " + selectedCate + " : " + selectedSubCate + " : " + bookingDate + " :: " + bookingTime);

        prefs = getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);

        user_email_id = prefs.getString("Email", null);
        Log.e("PayNowUserIds", user_id + " : " + user_email_id);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViewById();
    }

    @SuppressLint("SetTextI18n")
    private void initViewById() {

        textViewAmount = findViewById(R.id.textView_total_price);

        etCardNumber = (EditText) findViewById(R.id.et_cardNo);
        etMonth = (EditText) findViewById(R.id.et_mm);
        etExpiryDate = (EditText) findViewById(R.id.et_yyyy);
        etCVV = (EditText) findViewById(R.id.et_cvv);

        etCardNumber.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            boolean isDelete = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0)
                    isDelete = false;
                else
                    isDelete = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String source = editable.toString();
                int length = source.length();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(source);

                if (length > 0 && length % 5 == 0) {
                    if (isDelete)
                        stringBuilder.deleteCharAt(length - 1);
                    else
                        stringBuilder.insert(length - 1, " ");

                    etCardNumber.setText(stringBuilder);
                    etCardNumber.setSelection(etCardNumber.getText().length());

                }
            }
        });

        btnProcced = findViewById(R.id.btn_proceed);
        btnProcced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cvv_number = etCVV.getText().toString().trim();
                Log.d("CvvData", String.valueOf(get_cvv));
                if (get_cvv == true) {
                    Log.d("CvvData", String.valueOf(cvv_value));
                    if (!cvv_number.matches("")) {
                        if (cvv_value.equals(cvv_number)) {

                            Log.e("cardDeatils", cardNumber + " : " + cvv_value + " :: " + expiryMonth + " :: " + expiryYearfourDigit);
                            getStripePaymentToken(cardNumber, expiryMonth, expiryYear, cvv_value, expiryYearfourDigit);
                        } else {
                            Toast.makeText(BookNowPayment.this, "Please Correct Card Details", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        etCVV.setError("Please Enter CVV Number!!");
                    }

                } else {
                    //Toast.makeText(BookNowPayment.this, "Else False", Toast.LENGTH_SHORT).show();
                    String strCardNumber = etCardNumber.getText().toString().trim();
                    String strMonth = etMonth.getText().toString().trim();
                    String strExpiryDate = etExpiryDate.getText().toString().trim();
                    String strCVV = etCVV.getText().toString().trim();

                    String ste_year = strExpiryDate.substring(strExpiryDate.length() - 2);
                    Log.e("YearFormat", ste_year);

                    if (!strCardNumber.matches("")) {
                        if (!strMonth.matches("")) {
                            if (!strExpiryDate.matches("")) {
                                if (!strCVV.matches("")) {
                                    getStripePaymentToken(strCardNumber, strMonth, ste_year, strCVV, strExpiryDate);
                                } else {
                                    etCVV.setError("Please Enter CVV!!");
                                }
                            } else {
                                etExpiryDate.setError("Please Enter Expiry Date (MM/DD)!!");
                            }
                        } else {
                            etMonth.setError("Please Enter Month!!");
                        }
                    } else {
                        etCardNumber.setError("Please Enter Card Number!!");
                    }
                }
            }
        });

        textViewAmount.setText("$ " + userPaymentAmount);

        connectionDetector = new ConnectionDetector(BookNowPayment.this);
        loadingDialog = new LoadingDialog(BookNowPayment.this);

        if (connectionDetector.isConnectingToInternet()) {
            getCardDetailsInfo();
        } else {
           /* Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(BookNowPayment.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void getCardDetailsInfo() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_CREDITCARDDETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "CardDetails :" + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("Message_Code", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONArray jsonArray = jobj.getJSONArray("details");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String card_no = jsonObject.getString("card_no");
                                    //String exp_date = jsonObject.getString("exp_date");
                                    String cvv = jsonObject.getString("cvv");
                                    String exp_month = jsonObject.getString("exp_mon");
                                    String exp_year = jsonObject.getString("exp_year");
                                    String str_year_last_two_digit = exp_year.substring(exp_year.length() - 2); // str_year=="23";
                                    cvv_value = cvv;
                                    cardNumber = card_no;
                                    expiryMonth = exp_month;
                                    expiryYear = str_year_last_two_digit;
                                    expiryYearfourDigit = exp_year;
                                    String card_no1 = jsonObject.getString("card_no");
                                    String card_number_format = "XXXX XXXX XXXX " + card_no.substring(card_no.length() - 4);
                                    //String card_number_format1 = "XXXX XXXX XXXX " + card_no1.substring(card_no1.length() + 4);
                                    //Log.e("FormatNumber",card_number_format1);
                                    etCardNumber.setText(card_number_format);
                                    etExpiryDate.setText(exp_year);
                                    etMonth.setText(expiryMonth);
                                    etCVV.setText(cvv);
                                    get_cvv = true;
                                }

                            } else {
                                //Utils.AlertDialog(BookNowPayment.this, msg);
                                get_cvv = false;
                                etCardNumber.setText("");
                                etExpiryDate.setText("");
                                etMonth.setText("");
                                etCVV.setText("");
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(BookNowPayment.this, error);
                        AlertUtility.showAlert(BookNowPayment.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("u_id", user_id);
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
        RequestQueue requestQueue = Volley.newRequestQueue(BookNowPayment.this);
        requestQueue.add(stringRequest);

    }

    private void getStripePaymentToken(final String cardNumber, final String expiryMonth, String expiryYear, final String cvv_value, final String expiryYearfourDigit) {
        loadingDialog.show();
        //final String month_year = etExpiryDate.getText().toString().trim();
        //String str_year = strExpiryDate.substring(strExpiryDate.length() - 2); // str_year=="23";
        Log.e("PaymetDatas", cardNumber + " : " + expiryMonth + " :: " + expiryYear + " : " + cvv_value + " " + expiryYearfourDigit);

        Log.d("PaymetDatas", cardNumber + " " + cvv_value + " " + expiryYearfourDigit);

        final String strExpiryDate = expiryMonth + expiryYear;

        Card card = new Card(cardNumber,
                Integer.valueOf(expiryMonth),
                Integer.valueOf(expiryYear),
                cvv_value);

        Stripe stripe = new Stripe();

        stripe.createToken(card, PUBLISHABLE_KEY, new TokenCallback() {
            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                //Toast.makeText(getActivity(), "Token created: " + token.getId(), Toast.LENGTH_LONG).show();
                Log.d("StripeSuccessPayNow", token.getId());
                String token_id = token.getId();

                //Toast.makeText(UserStripePaymentActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
                if (connectionDetector.isConnectingToInternet()) {
                    newStripePaymentAPIRequest(token_id, cardNumber, strExpiryDate, cvv_value, expiryMonth, expiryYearfourDigit);
                } else {
                    Toast.makeText(BookNowPayment.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                }

            }

            public void onError(Exception error) {

                Log.d("StripeFail", error.getLocalizedMessage());
                Toast.makeText(BookNowPayment.this, "Please Enter Correct Card Details!!", Toast.LENGTH_SHORT).show();
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }


    private void newStripePaymentAPIRequest(final String token_id, final String cardNumber, final String strExpiryDate, final String cvv_value, final String expiryMonth, final String expiryYearfourDigit) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_REGULAR_BOOKING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "Login= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONObject jsonObject = jobj.getJSONObject("details");
                                String payment_mode = jsonObject.getString("payment_mode");
                                String payment_status = jsonObject.getString("payment_status");
                                String payment_reference_number = jsonObject.getString("transaction_id");
                                String payment_date = jsonObject.getString("date");
                                String payment_time = jsonObject.getString("time");
                                String payment_amount = jsonObject.getString("amount");

                                Intent intent = new Intent(BookNowPayment.this, PaymentDetailsActivity.class);
                                intent.putExtra("PaymentDetails", userPaymentAmount);

                                intent.putExtra("PaymentMode", payment_mode);
                                intent.putExtra("PaymentStatus", payment_status);
                                intent.putExtra("PaymentRefNo", payment_reference_number);
                                intent.putExtra("PaymentDate", payment_date);
                                intent.putExtra("PaymentTime", payment_time);
                                intent.putExtra("PaymentAmount", payment_amount);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                etCVV.setText("");
                                etCardNumber.setText("");
                                etMonth.setText("");
                                etExpiryDate.setText("");
                                //etCvvNumber.setText("");
                                Toast.makeText(BookNowPayment.this, "Payment Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.AlertDialog(BookNowPayment.this, "User not registered.");
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(BookNowPayment.this, error);
                        AlertUtility.showAlert(BookNowPayment.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("my_task_message", yourMeassage);
                    params.put("my_task_job_location", "1");
                    params.put("user_id", user_id);
                    params.put("ref_id_category", "1");
                    params.put("ref_id_sub_category", selectedSubCate);
                    params.put("booking_date", bookingDate);
                    params.put("booking_time", bookingTime);
                    params.put("card_no", cardNumber);
                    params.put("exp_mon", expiryMonth);
                    params.put("exp_year", expiryYearfourDigit);
                    params.put("cvv", cvv_value);
                    params.put("stripeToken", token_id);
                    params.put("vendor_id", vendorIds);
                    params.put("total_price", userPaymentAmount);

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
        RequestQueue requestQueue = Volley.newRequestQueue(BookNowPayment.this);
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

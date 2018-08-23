package com.hnweb.ubercuts.user.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
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
import com.hnweb.ubercuts.user.adaptor.NewServicesAdaptor;
import com.hnweb.ubercuts.user.bo.BeauticianDetailsModel;
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.user.fragment.AboutMeFragment;
import com.hnweb.ubercuts.user.fragment.MyReviewsFragment;
import com.hnweb.ubercuts.user.fragment.MyServicesFragment;
import com.hnweb.ubercuts.user.fragment.VendorMyWorkFragment;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.DataUtility;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.RequestInfo;
import com.hnweb.ubercuts.utils.Utils;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.hnweb.ubercuts.utils.MainApplication.TAG;

public class BeauticianDetailsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String user_details_task_ids;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView iv_fav;
    public TextView textViewListCount;
    ViewPagerBeauticianAdapter adapter;
    private ArrayList<BeauticianDetailsModel> beauticianDeatilsModels = null;
    String beautician_id = "", about_us = "";
    ImageView imageViewBeautician;
    RatingBar ratingBarReviwes;
    String str_Fav;
    String isFavClick = "1";
  public   Button btn_booknow;
    private int mYear, mMonth, mDay, mHour, mMinute;

    TextView tv_date , tv_time;
    TextView textViewBeauticianName, textViewExperience;
    Button btnBookYourTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_detailsnew);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ratingBarReviwes = (RatingBar) findViewById(R.id.rtb_reviews_rating);
        ratingBarReviwes.setFocusable(false);
        prefs = getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        Intent intent = getIntent();
        user_details_task_ids = intent.getStringExtra("user_details_ids").toString();
        Log.d("UserDetailsIds", user_details_task_ids);
        imageViewBeautician = findViewById(R.id.iv_profile);
        textViewBeauticianName = findViewById(R.id.tv_vendroName);
        textViewExperience = findViewById(R.id.tv_experience);
        btn_booknow = findViewById(R.id.btn_booknow);

        btn_booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookNowDialog();
            }
        });
        initViewByIds();
        viewPager = findViewById(R.id.viewpager);
        //setupViewPager(viewPager);
        iv_fav = (ImageView) findViewById(R.id.imageView_fav);
        adapter = new ViewPagerBeauticianAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        textViewListCount = findViewById(R.id.textView_list_count);
        tabLayout.setOnTabSelectedListener(this);
        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavClick.equals("0")) {

                    removeFav();
                } else {

                    addFav();
                }
            }
        });


    }

    private void initViewByIds() {

     /*   btnBookYourTask = findViewById(R.id.button_book_now);
        btnBookYourTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeauticianDetailsActivity.this,BookYourTaskActivity.class);
                startActivity(intent);
            }
        });*/

        connectionDetector = new ConnectionDetector(BeauticianDetailsActivity.this);
        loadingDialog = new LoadingDialog(BeauticianDetailsActivity.this);
        if (connectionDetector.isConnectingToInternet()) {
            getServices(user_details_task_ids);
        } else {

            Toast.makeText(BeauticianDetailsActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

    }


    private void getServices(final String user_details_task_ids) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_VENDORDETAILS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "ServiceListDeatils= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONObject jsonObject = jobj.getJSONObject("details");
                                beauticianDeatilsModels = new ArrayList<BeauticianDetailsModel>();

                                BeauticianDetailsModel beauticianDetailsModel = new BeauticianDetailsModel();

                                beauticianDetailsModel.setBeautician_id(jsonObject.getString("u_id"));
                                beauticianDetailsModel.setBeautician_name(jsonObject.getString("u_name"));
                                beauticianDetailsModel.setBeautician_email(jsonObject.getString("u_email"));
                                beauticianDetailsModel.setBeautician_business_name(jsonObject.getString("u_business_name"));
                                beauticianDetailsModel.setBeautician_img(jsonObject.getString("u_img"));
                                beauticianDetailsModel.setBeautician_experience(jsonObject.getString("experience"));
                                beauticianDetailsModel.setBeautician_street(jsonObject.getString("u_street"));
                                beauticianDetailsModel.setBeautician_city(jsonObject.getString("u_city"));
                                beauticianDetailsModel.setBeautician_state(jsonObject.getString("u_state"));
                                beauticianDetailsModel.setBeautician_country(jsonObject.getString("u_country"));
                                beauticianDetailsModel.setBeautician_zipcode(jsonObject.getString("u_zipcode"));
                                beauticianDetailsModel.setBeautician_about_us(jsonObject.getString("u_bio"));
                                beauticianDetailsModel.setBeautician_IsFav(jsonObject.getString("fav_flag"));
                                beauticianDetailsModel.setBeautician_rating(jsonObject.getString("rating"));

                                beautician_id = jsonObject.getString("u_id");
                                about_us = jsonObject.getString("u_bio");
                                str_Fav = jsonObject.getString("fav_flag");
                                if (str_Fav.equals("Y")) {
                                    iv_fav.setImageResource(R.drawable.fav_selected);
                                    isFavClick = "0";
                                } else {
                                    iv_fav.setImageResource(R.drawable.fav_unselected);
                                    isFavClick = "1";
                                }

                                if(jsonObject.getString("rating").equals("null"))
                                {

                                    ratingBarReviwes.setRating(0);
                                }else
                                {

                                    ratingBarReviwes.setRating(Float.parseFloat(jsonObject.getString("rating")));

                                }
                                beauticianDeatilsModels.add(beauticianDetailsModel);

                                String beautician_image = jsonObject.getString("u_img");

                                if (beautician_image.equals("")) {

                                    Glide.with(BeauticianDetailsActivity.this).load(R.drawable.user_register).into(imageViewBeautician);
                                } else {
                                    Glide.with(BeauticianDetailsActivity.this).load(beautician_image).into(imageViewBeautician);
                                }

                                String beautician_name = jsonObject.getString("u_name");
                                if (beautician_name.equals("")) {
                                    textViewBeauticianName.setText("No Name");
                                } else {
                                    textViewBeauticianName.setText(beautician_name);
                                }

                                String beautician_experience = jsonObject.getString("experience");
                                if (beautician_experience.equals("")) {
                                    textViewExperience.setText("No Name");
                                } else {
                                    textViewExperience.setText(beautician_experience + " " + "Years");
                                }


                                Log.d("ArraySize", String.valueOf(beauticianDeatilsModels.size()));


                            } else {
                                Utils.AlertDialog(BeauticianDetailsActivity.this, msg);

                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(BeauticianDetailsActivity.this, error);
                        AlertUtility.showAlert(BeauticianDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("beautician_id", user_details_task_ids);
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
        RequestQueue requestQueue = Volley.newRequestQueue(BeauticianDetailsActivity.this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private class ViewPagerBeauticianAdapter extends FragmentPagerAdapter {
        String[] title = new String[]{"ABOUT ME", "SERVICES", "REVIEWS","MY WORK"};


        public ViewPagerBeauticianAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new AboutMeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("BeauticianIds", user_details_task_ids);
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment = new MyServicesFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("BeauticianIds", user_details_task_ids);

                    fragment.setArguments(bundle1);
                    break;
                case 2:
                    fragment = new MyReviewsFragment();
                    Bundle bundleReviews = new Bundle();
                    bundleReviews.putString("BeauticianIds", user_details_task_ids);
                    fragment.setArguments(bundleReviews);
                    break;
                case 3:
                    fragment = new VendorMyWorkFragment();
                    Bundle bundl1e = new Bundle();
                    bundl1e.putString("BeauticianIds", user_details_task_ids);
                    fragment.setArguments(bundl1e);
                    break;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

        @Override
        public int getCount() {
            return title.length;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        onBackPressed();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addFav() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_ADD_VENDORFAV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "ServiceListDeatils= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                Utils.AlertDialog(BeauticianDetailsActivity.this, msg);
                                iv_fav.setImageResource(R.drawable.fav_selected);
                                isFavClick = "0";
                            } else {
                                Utils.AlertDialog(BeauticianDetailsActivity.this, msg);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(BeauticianDetailsActivity.this, error);
                        AlertUtility.showAlert(BeauticianDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("vendor_id", user_details_task_ids);
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
        RequestQueue requestQueue = Volley.newRequestQueue(BeauticianDetailsActivity.this);
        requestQueue.add(stringRequest);

    }

    private void removeFav() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_REMOVE_VENDORFAV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "ServiceListDeatils= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                iv_fav.setImageResource(R.drawable.fav_unselected);
                                isFavClick = "1";
                                Utils.AlertDialog(BeauticianDetailsActivity.this, msg);


                            } else {
                                Utils.AlertDialog(BeauticianDetailsActivity.this, msg);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(BeauticianDetailsActivity.this, error);
                        AlertUtility.showAlert(BeauticianDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("vendor_id", user_details_task_ids);
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
        RequestQueue requestQueue = Volley.newRequestQueue(BeauticianDetailsActivity.this);
        requestQueue.add(stringRequest);

    }


    private void bookNowDialog() {
        Intent intent= new Intent(BeauticianDetailsActivity.this,RegularBookNowYourTask.class);
        startActivity(intent);
    }


}

package com.hnweb.ubercuts.vendor.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.hnweb.ubercuts.user.activity.HomeActivity;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.vendor.activity.ProfileEditSaveVendorFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ProfileVendorAbout extends Fragment {

    private SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    Button btnSaveInfo;
    EditText etName, etEmailId, etPhone, etAboutMe;
    TextView tvName,tvEmail,tvPhoneNumber,tvAboutMe,tvVendorName;
    ImageView imageViewEdit,imageViewSave;
    boolean editValue = false;
    RelativeLayout relativeLayout,relativeLayout1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about_me_vendor, container, false);

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());

        if (connectionDetector.isConnectingToInternet()) {
            getVendorDetails();
        } else {
             Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

        initViewById(view);

        tvVendorName = ((ProfileEditSaveVendorFragment) getActivity()).textViewVendorName;

        return view;
    }

    private void initViewById(View view) {

        etName = view.findViewById(R.id.editText_first_name_vendor);
        etEmailId = view.findViewById(R.id.editText_email_id_vendor);
        etPhone = view.findViewById(R.id.editText_phone_number_vendor);
       // etdob = view.findViewById(R.id.editText_dob_vendor);
        etAboutMe = view.findViewById(R.id.editText_about_me_vendor);

        tvName = view.findViewById(R.id.textView_full_name);
        tvEmail = view.findViewById(R.id.textView_email_id);
        tvPhoneNumber = view.findViewById(R.id.textView_phone_number);
        tvAboutMe = view.findViewById(R.id.textView_about_me);

        relativeLayout = view.findViewById(R.id.relative_layout_data);
        relativeLayout1 = view.findViewById(R.id.relative_layout_data1);

        imageViewEdit = view.findViewById(R.id.imageView_edit);
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout1.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.GONE);
            }
        });
        imageViewSave = view.findViewById(R.id.button_save_vendor_info);
        //btnSaveInfo = view.findViewById(R.id.button_save_vendor_info);
        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String full_name = etName.getText().toString().trim();
                String email_id = etEmailId.getText().toString().trim();
                String phone_number = etPhone.getText().toString().trim();
               // String date_of_birth = etdob.getText().toString().trim();
                String about_me = etAboutMe.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (!full_name.matches("")) {
                    if (!email_id.matches("")) {
                        if (email_id.matches(emailPattern)) {
                            if (!phone_number.matches("")) {

                                    if (!about_me.matches("")) {
                                        saveaboutMeData(full_name,email_id,phone_number,about_me);
                                    } else {
                                        Toast.makeText(getActivity(), "Please Enter About Me!!", Toast.LENGTH_SHORT).show();
                                    }

                            } else {
                                Toast.makeText(getActivity(), "Please Enter Phone Number!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please Enter Valid Email Id!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Email Id!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Enter Full Name!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void getVendorDetails() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_MYPROFILE_VENDOR,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "GETDetails= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONObject jsonObject = jobj.getJSONObject("details");


                                String vendor_id = jsonObject.getString("u_id");
                                String vendor_name = jsonObject.getString("u_name");
                                String vendor_image = jsonObject.getString("u_img");
                                String vendor_email = jsonObject.getString("u_email");
                                String about_me = jsonObject.getString("about_me");
                                String vendor_phone = jsonObject.getString("u_phone");
                                String vendor_experience = jsonObject.getString("experience");

                                Log.d("UserImageEdit", vendor_image);

                                etName.setText(vendor_name);
                                etEmailId.setText(vendor_email);
                                etPhone.setText(vendor_phone);
                                etAboutMe.setText(about_me);

                                tvName.setText(vendor_name);
                                tvEmail.setText(vendor_email);
                                tvPhoneNumber.setText(vendor_phone);
                                tvAboutMe.setText(about_me);

                                tvVendorName.setText(vendor_name);

                            } else {
                                Utils.AlertDialog(getActivity(), msg);
                                //displayAlertDialog(msg);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("vendor_id", user_id);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void saveaboutMeData(final String full_name, final String email_id, final String phone_number, final String about_me) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_SAVE_ABOUTME_VENDOR,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "EditProfile= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                getVendorDetails();
                                relativeLayout.setVisibility(View.VISIBLE);
                                relativeLayout1.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.AlertDialog(getActivity(), msg);
                                //displayAlertDialog(msg);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                    },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                try {
                    params.put("vendor_id", user_id);
                    params.put("u_name", full_name);
                    params.put("u_email", email_id);
                    params.put("u_phone", phone_number);
                    params.put("u_bio", about_me);


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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

}

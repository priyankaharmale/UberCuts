package com.hnweb.ubercuts.user.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.helper.VolleyMultipartRequest;
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.user.activity.HomeActivity;
import com.hnweb.ubercuts.user.activity.UserRegistrationActivityStepTwo;
import com.hnweb.ubercuts.user.adaptor.CityListAdaptor;
import com.hnweb.ubercuts.user.adaptor.CountryListAdaptor;
import com.hnweb.ubercuts.user.adaptor.StateListAdaptor;
import com.hnweb.ubercuts.user.bo.City;
import com.hnweb.ubercuts.user.bo.Country;
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.user.bo.State;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.vendor.bo.ProfileUpdateModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PC-21 on 04-Apr-18.
 */

public class ProfileEditFragment extends android.support.v4.app.Fragment implements View.OnClickListener , OnCallBack {

    private SharedPreferences prefs;
    String user_id, profile_image;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    Button  btnSaveInfo;
    EditText etName, etEmailId, etPhone, etStreet, etCity, etState, etCountry, etZipcode;
    ImageView ivProfileImage, ivTakePhoto;
    private String image_path_selected = "";
    private Uri imageUri;
    Calendar myCalendar;
    ArrayList<Country> countryArrayList = new ArrayList<>();
    ArrayList<State> stateArrayList = new ArrayList<>();
    ;
    OnCallBack onCallBack;
    CountryListAdaptor countryListAdaptor;
    StateListAdaptor stateListAdaptor;
    CityListAdaptor cityListAdaptor;
    ArrayList<City> cityArrayList = new ArrayList<>();

    private String countryId = "", stateId = "", cityId = "";
    private String countryName = "", stateName = "", cityName = "";
    private boolean selectedCountry = false, selectedState = false, selectedCity = false;

    SharedPreferences.Editor editorUser;
    Bitmap thumbnail;

    @SuppressLint("CommitPrefEdits")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);


        myCalendar = Calendar.getInstance();

        onCallBack=this;
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        profile_image = prefs.getString(AppConstant.KEY_IMAGE, null);
        editorUser = prefs.edit();

        ivProfileImage = view.findViewById(R.id.profile_image_edit);
        if (profile_image.equals("")) {
            Glide.with(ProfileEditFragment.this).load(R.drawable.user_register).into(ivProfileImage);
        } else {
            Glide.with(ProfileEditFragment.this).load(profile_image).into(ivProfileImage);
        }

        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());

        if (connectionDetector.isConnectingToInternet()) {
            getUserDeatilsInfo();
           // getCountryList();
        } else {
            /*Snackbar snackbar = Snackbar
                    .make(((HomeActivity) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

        initViewById(view);

        return view;
    }

    private void initViewById(View view) {

        etName = view.findViewById(R.id.et_name);
        etEmailId = view.findViewById(R.id.et_email_id);
        etPhone = view.findViewById(R.id.et_phone_no);
        etStreet = view.findViewById(R.id.et_street);
        etState = view.findViewById(R.id.et_state);
        etCity = view.findViewById(R.id.et_city);
        etCountry = view.findViewById(R.id.et_country);
        etZipcode = view.findViewById(R.id.et_zipcode);

        //btnDateOfBirth = view.findViewById(R.id.btn_dateof_birth);
        btnSaveInfo = view.findViewById(R.id.button_update_profile);


        ivTakePhoto = view.findViewById(R.id.profile_image_edit_upload);
        btnSaveInfo.setOnClickListener(this);
        ivTakePhoto.setOnClickListener(this);
        etCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getCountryList();


            }
        });
        etState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countryArrayList.size() == 0) {
                    Toast.makeText(getActivity(), "Please Select Country", Toast.LENGTH_LONG).show();
                } else {
                    dialogState();
                }
            }
        });
        etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stateArrayList.size() == 0) {
                    Toast.makeText(getActivity(), "Please Select State", Toast.LENGTH_LONG).show();
                } else {
                    dialogCity();
                }
            }
        });


    }


    private void getUserDeatilsInfo() {

        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETUSERPROFILEDETAILS,
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



                                    JSONObject jsonObject = jobj.getJSONObject("details");


                                    String user_id = jsonObject.getString("u_id");
                                    String user_name = jsonObject.getString("u_name");
                                    String user_email = jsonObject.getString("u_email");
                                    String user_phone = jsonObject.getString("u_phone");
                                    String user_image = jsonObject.getString("u_img");
                                    String user_street = jsonObject.getString("u_street");
                                    String user_city = jsonObject.getString("u_city");
                                    String user_state = jsonObject.getString("u_state");
                                    String user_zipcode = jsonObject.getString("u_zipcode");
                                    String user_country = jsonObject.getString("u_country");
                                    Log.d("UserImageEdit", user_image);

                                    etName.setText(user_name);
                                    etEmailId.setText(user_email);
                                    etPhone.setText(user_phone);

                                    etStreet.setText(user_street);
                                    etState.setText(user_state);
                                    etCity.setText(user_city);
                                    etCountry.setText(user_country);
                                    etZipcode.setText(user_zipcode);

                                    etCountry.setText(user_country);
                                    etState.setText(user_state);
                                    etCity.setText(user_city);


                                    countryName = user_country;
                                    stateName = user_state;
                                    cityName = user_city;

                                    etCountry.setText(user_country);
                                    etState.setText(user_state);
                                    etCity.setText(user_city);

                                    if (user_image.equals("")) {
                                        Glide.with(ProfileEditFragment.this).load(R.drawable.user_register).into(ivProfileImage);
                                    } else {
                                        Glide.with(ProfileEditFragment.this).load(user_image).into(ivProfileImage);


                                    }

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_update_profile:
                inputvalidationField();
                break;

            case R.id.profile_image_edit_upload:
                selectedImage();
                break;




            default:
                break;
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    }

    private void inputvalidationField() {
        String user_name = etName.getText().toString().trim();
        String user_email = etEmailId.getText().toString().trim();
        String user_phone = etPhone.getText().toString().trim();
        String user_street = etStreet.getText().toString().trim();
        String user_city = etCity.getText().toString().trim();
        String user_state = etState.getText().toString().trim();
        String user_country = etCountry.getText().toString().trim();
        String user_zipcode = etZipcode.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        if (!user_name.matches("")) {
            if (!user_email.matches("")) {
                if (user_email.matches(emailPattern)) {
                    if (!user_phone.matches("")) {

                            if (!user_country.equals("")) {
                                if (!user_state.equals("")) {
                                    if (!user_city.equals("")) {
                                        if (!user_street.matches("")) {
                                            if (!user_zipcode.matches("")) {
                                                postUpdatePrifleApi(user_name, user_email, user_phone, user_street, cityName, stateName, countryName, user_zipcode);
                                            } else {
                                                etZipcode.setError("Please Enter Zipcode");
                                            }
                                        } else {
                                            etStreet.setError("Please Enter Street");
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Please select City!!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Please select State!!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Please select Country!!", Toast.LENGTH_SHORT).show();
                            }

                    } else {
                        etPhone.setError("Please Enter Phone Number");
                    }
                } else {
                    etEmailId.setError("Please Enter valid Email Id.");
                }
            } else {
                etEmailId.setError("Please Enter Email Id.");
            }
        } else {
            etName.setError("Please Enter Full Name!");
        }
    }

    private void selectedImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    //imageViewPost.setImageBitmap(thumbnail);
                    String imageurl = getRealPathFromURI(imageUri);
                    image_path_selected = imageurl;
                    //Imageview imageView = (ImageView) findViewById(R.id.image_view);
                    Glide.with(this).load(imageurl).into(ivProfileImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                try {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    thumbnail = (BitmapFactory.decodeFile(picturePath));
                    image_path_selected = picturePath;
                    Log.w("pathofimagefromgallery", picturePath + "" + thumbnail);

                    Glide.with(this).load(picturePath).into(ivProfileImage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri imageUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(imageUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void postUpdatePrifleApi(final String user_name, final String user_email, final String
            user_phone, final String user_street, final String user_city, final String user_state, final String
                                             user_country, final String user_zipcode) {

        loadingDialog.show();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,AppConstant.API_UPDATEUSERPROFILE,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if (loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        try {
                            JSONObject jobj = new JSONObject(new String(response.data));
                            //Toast.makeText(getApplicationContext(), jobj.getString("message"), Toast.LENGTH_SHORT).show();
                            int message_code = jobj.getInt("message_code");
                            String message = jobj.getString("message");
                            if (message_code == 1) {
                                //image_path_selected = "";
                                    JSONObject jsonObject = jobj.getJSONObject("details");
                                    String userProfile = jsonObject.getString("u_img");

                                       // editorUser.putString("UserImage", userProfile);


                                        String user_id = jsonObject.getString("u_id");
                                        String user_name = jsonObject.getString("u_name");
                                        String user_email = jsonObject.getString("u_email");
                                        String user_phone = jsonObject.getString("u_phone");
                                        String user_image = jsonObject.getString("u_img");
                                        String user_street = jsonObject.getString("u_street");
                                        String user_city = jsonObject.getString("u_city");
                                        String user_state = jsonObject.getString("u_state");
                                        String user_country = jsonObject.getString("u_country");
                                        String user_zipcode = jsonObject.getString("u_zipcode");

                                        editorUser.putString(AppConstant.KEY_ID, user_id);
                                        editorUser.putString(AppConstant.KEY_NAME, user_name);
                                        editorUser.putString(AppConstant.KEY_EMAIL, user_email);
                                        editorUser.putString(AppConstant.KEY_PHONE, user_phone);
                                        editorUser.putString(AppConstant.KEY_IMAGE, user_image);
                                        editorUser.putString(AppConstant.KEY_STREET, user_street);
                                        editorUser.putString(AppConstant.KEY_CITY, user_city);
                                        editorUser.putString(AppConstant.KEY_STATE, user_state);
                                        editorUser.putString(AppConstant.KEY_COUNTRY, user_country);
                                        editorUser.putString(AppConstant.KEY_ZIPCODE, user_zipcode);
                                        editorUser.commit();

                                        editorUser.apply();
                                        editorUser.commit();
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                                        Glide.with(ProfileEditFragment.this).load(userProfile).into(ivProfileImage);
                                        //Glide.with(ProfileEditFragment.this).load(userProfile).into(((MainActivityUser) getActivity()).imageViewProfile);



                                ProfileUpdateModel.getInstance().changeState(true);
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                Utils.AlertDialog(getActivity(), message);
                                //Toast.makeText(RegisterVenDorActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("u_id", user_id);
                params.put("u_name", user_name);
                params.put("u_email", user_email);
                params.put("u_phone", user_phone);
                params.put("u_street", user_street);
                params.put("u_city", user_city);
                params.put("u_state", user_state);
                params.put("u_country", user_country);
                params.put("u_zipcode", user_zipcode);
                Log.e("ParamsImage",params.toString());
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();

                if (thumbnail==null)
                {

                }else
                {
                    params.put("img", new DataPart(imagename + ".png", getFileDataFromDrawable(thumbnail)));

                }
                Log.d("ParamsImage",params.toString());
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);

    }

    protected byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void getCountryList() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETLIST_COUNTRIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_fav" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            //  countryArrayList = new ArrayList<>();
                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("details");
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        final Country country = new Country();
                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                        country.setId(jsonObjectpostion.getString("country_id"));
                                        country.setCountyName(jsonObjectpostion.getString("country_name"));

                                        countryArrayList.add(country);
                                    }
                                    dialogContry();

                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }


                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
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
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void getStateList(final String countryId) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETLIST_STATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_fav" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            //stateArrayList = new ArrayList<>();
                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("details");
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        final State state = new State();
                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                        state.setId(jsonObjectpostion.getString("state_id"));
                                        state.setStateName(jsonObjectpostion.getString("state_name"));

                                        stateArrayList.add(state);
                                    }

                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }


                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
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
                    //user_id
                    params.put("country_id", countryId);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                    Log.e("Exception", e.getMessage());
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

    private void getCityList(final String state) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETLIST_CITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_fav" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            //   cityArrayList = new ArrayList<>();
                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("details");
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        final City city = new City();
                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                        city.setId(jsonObjectpostion.getString("city_id"));
                                        city.setCityName(jsonObjectpostion.getString("city_name"));

                                        cityArrayList.add(city);
                                    }


                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }


                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
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
                    //user_id
                    params.put("state_id", state);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                    Log.e("Exception", e.getMessage());
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
    @Override
    public void callback(String count) {

    }

    @Override
    public void callbackYear(String count) {

    }

    @Override
    public void callcountryList(String id, String name) {
        etCountry.setText(name);
        etCity.setText("");
        etState.setText("");
        etStreet.setText("");
        etState.setText("");


        etCity.setHint("City");
        etState.setHint("State");
        etStreet.setHint("Street");
        etZipcode.setHint("Zip Code");

        stateArrayList.clear();
        cityArrayList.clear();

        getStateList(id);
    }

    @Override
    public void callstateList(String id, String name) {
        etState.setText(name);
        etCity.setText("");
        etStreet.setText("");
        etZipcode.setText("");
        cityArrayList.clear();
        etCity.setHint("City");
        etStreet.setHint("Street");
        etZipcode.setHint("Zip Code");
        getCityList(id);
    }

    @Override
    public void callcityList(String id, String name) {
        etCity.setText(name);

    }

    @Override
    public void callrefresh() {

    }

    private void filterCountry(String text) {
        //new array list that will hold the filtered data
        ArrayList<Country> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (Country s : countryArrayList) {
            //if the existing elements contains the search input
            if (s.getCountyName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        countryListAdaptor.filterList(filterdNames);
    }

    private void filterState(String text) {
        //new array list that will hold the filtered data
        ArrayList<State> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (State s : stateArrayList) {
            //if the existing elements contains the search input
            if (s.getStateName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        stateListAdaptor.filterList(filterdNames);
    }

    private void filterCity(String text) {
        //new array list that will hold the filtered data
        ArrayList<City> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (City s : cityArrayList) {
            //if the existing elements contains the search input
            if (s.getCityName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        cityListAdaptor.filterList(filterdNames);
    }

    public void dialogContry() {
        final Dialog dialog = new Dialog(getActivity());
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialogbox_list);

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        TextView textView_header = (TextView) dialog.findViewById(R.id.textView_custom_view);
        final EditText searchView = (EditText) dialog.findViewById(R.id.search_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select Country");
        }
        dialog.setCancelable(true);

        countryListAdaptor = new CountryListAdaptor(countryArrayList, getActivity(), onCallBack, dialog);
        recyclerView.setAdapter(countryListAdaptor);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCountry(searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        Button button_cancel = (Button) dialog.findViewById(R.id.button_cancel);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void dialogState() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogbox_list);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        TextView textView_header = (TextView) dialog.findViewById(R.id.textView_custom_view);
        final EditText searchView = (EditText) dialog.findViewById(R.id.search_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select State");
        }
        dialog.setCancelable(true);

        stateListAdaptor = new StateListAdaptor(stateArrayList, getActivity(), onCallBack, dialog);
        recyclerView.setAdapter(stateListAdaptor);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterState(searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        Button button_cancel = (Button) dialog.findViewById(R.id.button_cancel);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void dialogCity() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogbox_list);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.lv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        TextView textView_header = (TextView) dialog.findViewById(R.id.textView_custom_view);
        final EditText searchView = (EditText) dialog.findViewById(R.id.search_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select City");
        }
        dialog.setCancelable(true);
        cityListAdaptor = new CityListAdaptor(cityArrayList, getActivity(), onCallBack, dialog);
        recyclerView.setAdapter(cityListAdaptor);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCity(searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        Button button_cancel = (Button) dialog.findViewById(R.id.button_cancel);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

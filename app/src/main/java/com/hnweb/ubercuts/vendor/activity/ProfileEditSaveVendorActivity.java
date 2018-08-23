package com.hnweb.ubercuts.vendor.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentValues;
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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.vendor.bo.ProfileUpdateModel;
import com.hnweb.ubercuts.vendor.fragment.ProfileVendorAbout;
import com.hnweb.ubercuts.vendor.fragment.ProfileVendorMyWork;
import com.hnweb.ubercuts.vendor.fragment.ProfileVenodorBusiness;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

public class ProfileEditSaveVendorActivity extends AppCompatActivity implements View.OnClickListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private SharedPreferences prefs;
    SharedPreferences.Editor editor;
    public TextView textViewVendorName, textViewExperience;
    ImageView ivProfileImage, ivSaveProfileImage, ivTakePhoto;
    private String image_path_selected = "";
    private Uri imageUri;
    Bitmap thumbnail;
    String user_id;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_save_vendor_profile);
        // initViewById();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        editor = prefs.edit();

        viewPager = findViewById(R.id.view_pager_profile);
        tabLayout = findViewById(R.id.tabs);

        setupViewPager(viewPager);

        connectionDetector = new ConnectionDetector(ProfileEditSaveVendorActivity.this);
        loadingDialog = new LoadingDialog(ProfileEditSaveVendorActivity.this);


        textViewVendorName = findViewById(R.id.textView_vendor_name);
        textViewExperience = findViewById(R.id.textView_vendor_experience);

        ivProfileImage = findViewById(R.id.profile_image);
        ivSaveProfileImage = findViewById(R.id.imageView_save);
        ivTakePhoto = findViewById(R.id.profile_image_photoupload);
        //btnSaveInfo.setOnClickListener(this);
        ivTakePhoto.setOnClickListener(this);
        ivSaveProfileImage.setOnClickListener(this);

        if (connectionDetector.isConnectingToInternet()) {
            getVendorDetails();
        } else {
            Toast.makeText(ProfileEditSaveVendorActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileVendorAbout(), "ABOUT ME");
        adapter.addFragment(new ProfileVenodorBusiness(), "BUSINESS");
        adapter.addFragment(new ProfileVendorMyWork(), "MY WORK");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.profile_image_photoupload:
                selectImage();
                break;

            case R.id.imageView_save:
                if (connectionDetector.isConnectingToInternet()) {
                    uploadImageToServer();
                } else {

                    Toast.makeText(this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditSaveVendorActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
                    thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    //imageViewPost.setImageBitmap(thumbnail);
                    String imageurl = getRealPathFromURI(imageUri);
                    image_path_selected = imageurl;
                    //Imageview imageView = (ImageView) findViewById(R.id.image_view);
                    Glide.with(this).load(imageurl).into(ivProfileImage);
                    ivSaveProfileImage.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                try {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getApplicationContext().getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    thumbnail = (BitmapFactory.decodeFile(picturePath));
                    image_path_selected = picturePath;
                    Log.w("pathofimagefromgallery", picturePath + "" + thumbnail);
                    ivSaveProfileImage.setVisibility(View.VISIBLE);
                    Glide.with(this).load(picturePath).into(ivProfileImage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri imageUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(imageUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    protected byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
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


                                String user_name = jsonObject.getString("u_name");
                                String user_image = jsonObject.getString("u_img");
                                String experience = jsonObject.getString("experience");

                                Log.d("UserImageEdit", user_image);

                                textViewVendorName.setText(user_name);
                                textViewExperience.setText(experience + " Years");

                                if (user_image.equals("")) {
                                    Glide.with(getApplicationContext()).load(R.drawable.user_register).into(ivProfileImage);
                                    //Glide.with(getApplicationContext()).load(R.drawable.user_register).into(((MainActivityVendor) getApplicationContext()).imageViewProfile);
                                } else {
                                      editor.putString(AppConstant.KEY_IMAGE, user_image);
                                    editor.apply();
                                      editor.commit();
                                    Glide.with(getApplicationContext()).load(user_image).into(ivProfileImage);
                                    //Glide.with(getApplicationContext()).load(user_image).into(((MainActivityVendor) getApplicationContext()).imageViewProfile);


                                }
                            } else {
                                Utils.AlertDialog(ProfileEditSaveVendorActivity.this, msg);
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
                        String reason = AppUtils.getVolleyError(ProfileEditSaveVendorActivity.this, error);
                        AlertUtility.showAlert(ProfileEditSaveVendorActivity.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileEditSaveVendorActivity.this);
        requestQueue.add(stringRequest);

    }

    private void uploadImageToServer() {

        loadingDialog.show();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppConstant.API_SAVEPROFILE_VENDOR,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if (loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            int message_code = obj.getInt("message_code");
                            String message = obj.getString("message");
                            if (message_code == 1) {
                                //String image_profile = obj.getString("u_img");
                             //   editor.putString("UserImage", image_profile);
                              //  editor.apply();
                             //   editor.commit();
                                Log.e("ErrorMessage", message);
                            //    Glide.with(ProfileEditSaveVendorActivity.this).load(image_profile).into(ivProfileImage);
                                //ImageView imageView = ((MainActivityVendor)getApplicationContext()).imageViewProfile;
                                //Glide.with(ProfileEditSaveVendorFragment.this).load(image_profile).into(imageView);
                                ivSaveProfileImage.setVisibility(View.GONE);
                                image_path_selected = "";
                                ProfileUpdateModel.getInstance().changeState(true);
                                getVendorDetails();

                            } else {
                                Utils.AlertDialog(ProfileEditSaveVendorActivity.this, message);
                                Log.d("ErrorMessage", message);
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

      /*       * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("u_id",user_id);
                Log.e("ParamsImage",params.toString());
                return params;
            }
/*

             * Here we are passing image by renaming it with a unique name
             *
*/

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("u_img", new DataPart(imagename + ".png", getFileDataFromDrawable(thumbnail)));
                Log.d("ParamsImage",params.toString());
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

}

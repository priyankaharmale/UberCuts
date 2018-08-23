package com.hnweb.ubercuts.vendor.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hnweb.ubercuts.MainActivity;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.user.activity.HomeActivity;
import com.hnweb.ubercuts.user.activity.UserLoginActivity;
import com.hnweb.ubercuts.user.activity.UserRegistrationActivityStepOne;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.SharedPreference;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.utils.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyanka H on 13/06/2018.
 */
public class VendorRegistrationActivityStepOne extends AppCompatActivity {
    Button btn_proceed;
    EditText et_fullname, et_email, et_mobile, et_password, et_confrimpassword, et_experience;
    ImageView iv_profilepic;
    private int GALLERY = 1, CAMERA = 2;
    public static final int REQUEST_CAMERA = 5;
    public static File destination;
    protected static final int REQUEST_STORAGE_ACCESS_PERMISSION = 102;
    String camImage = "", imagePath12, experience_value;
    Drawable drawable;
    Button btn_signIn;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerstepone);
        loadingDialog = new LoadingDialog(VendorRegistrationActivityStepOne.this);

        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        et_fullname = (EditText) findViewById(R.id.et_fullname);
        et_email = (EditText) findViewById(R.id.et_email);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_password = (EditText) findViewById(R.id.et_password);
        et_experience = (EditText) findViewById(R.id.et_experience);
        et_confrimpassword = (EditText) findViewById(R.id.et_confrimpassword);
        iv_profilepic = (ImageView) findViewById(R.id.iv_profilepic);
        btn_signIn = (Button) findViewById(R.id.btn_signIn);

        et_experience.setVisibility(View.VISIBLE);
        et_experience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExperienceDialog();
            }
        });
        drawable = ContextCompat.getDrawable(VendorRegistrationActivityStepOne.this, R.drawable.user_register);
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendorRegistrationActivityStepOne.this, UserLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidation()) {
                    if (Utils.isNetworkAvailable(VendorRegistrationActivityStepOne.this)) {
                        String email = et_email.getText().toString();
                        if (!et_password.getText().toString().equals(et_confrimpassword.getText().toString())) {
                            Toast.makeText(VendorRegistrationActivityStepOne.this, "Password Not matching ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                        {
                            emailexists(email);
                        }

                    } else {
                        Utils.myToast1(VendorRegistrationActivityStepOne.this);
                    }
                }

            }
        });
        iv_profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
    }

    private boolean checkValidation() {

        boolean ret = true;
        if (!Validations.hasText(et_fullname, "Please Enter Name"))
            ret = false;
        if (!Validations.hasText(et_email, "Please Enter Email ID "))
            ret = false;
        if (!Validations.hasText(et_mobile, "Please Enter the Mobile No."))
            ret = false;
        if (!Validations.hasText(et_experience, "Please Select the Experience"))
            ret = false;
        if (!Validations.hasText(et_password, "Please Enter Password"))
            ret = false;
        if (!Validations.hasText(et_confrimpassword, "Please Enter Confirm Password"))
            ret = false;
        if (!Validations.isEmailAddress(et_email, true, "Please Enter Valid Email ID"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_password, "Password atleast 7 characters"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_confrimpassword, "Password atleast 7 characters"))
            ret = false;
        return ret;
    }

    private void showPictureDialog() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(VendorRegistrationActivityStepOne.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                isPermissionGrantedImageGallery();
                                break;
                            case 1:
                                isPermissionGrantedImage();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void isPermissionGrantedImageGallery() {

        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(VendorRegistrationActivityStepOne.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            choosePhotoFromGallary();
        }

    }

    public void isPermissionGrantedImage() {
        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(VendorRegistrationActivityStepOne.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            camerImage();
        }

    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(VendorRegistrationActivityStepOne.this, permission)) {
            new android.app.AlertDialog.Builder(VendorRegistrationActivityStepOne.this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(VendorRegistrationActivityStepOne.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(VendorRegistrationActivityStepOne.this, new String[]{permission}, requestCode);
        }
    }

    public void camerImage() {
        System.out.println("Click Image11");
        String name = AppConstant.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".png");


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(VendorRegistrationActivityStepOne.this, getApplicationContext().getPackageName() + ".my.package.name.provider", destination));
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    FileOutputStream fo;
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    imagePath12 = destination.getAbsolutePath();

                    camImage = imagePath12;

                    try {
                        Glide.with(VendorRegistrationActivityStepOne.this)
                                .load(camImage)
                                .error(drawable)
                                .centerCrop()
                                .crossFade()
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(iv_profilepic);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(VendorRegistrationActivityStepOne.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == REQUEST_CAMERA) {

            System.out.println("REQUEST_CAMERA");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            String imagePath = destination.getAbsolutePath();
            Log.i("Path", imagePath);
            camImage = imagePath;
            Toast.makeText(VendorRegistrationActivityStepOne.this, camImage, Toast.LENGTH_SHORT).show();

            try {
                Glide.with(VendorRegistrationActivityStepOne.this)
                        .load(camImage)
                        .error(drawable)
                        .centerCrop()
                        .crossFade()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(iv_profilepic);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
    }

    private void showExperienceDialog() {
        final Dialog driverForgotPassdialog = new Dialog(VendorRegistrationActivityStepOne.this);
        driverForgotPassdialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        driverForgotPassdialog.setContentView(R.layout.dialog_experience);

        Button sendBtn = (Button) driverForgotPassdialog.findViewById(R.id.cancel_apply);
        Button cancel = (Button) driverForgotPassdialog.findViewById(R.id.cancel_dialog);

        final RadioGroup radioGroup = (RadioGroup) driverForgotPassdialog.findViewById(R.id.radioGroupEx);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverForgotPassdialog.dismiss();
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioSexButton = (RadioButton) driverForgotPassdialog.findViewById(selectedId);
                String rd_button = radioSexButton.getText().toString();
                if (rd_button.equals("Starter")) {
                    experience_value = "0";
                    et_experience.setText(rd_button);
                } else if (rd_button.equals("0 - 1 Years")) {
                    experience_value = "1";
                    et_experience.setText(rd_button);
                } else if (rd_button.equals("1 - 5 Years")) {
                    experience_value = "2";
                    et_experience.setText(rd_button);
                } else if (rd_button.equals("5 - 10 Years")) {
                    experience_value = "3";
                    et_experience.setText(rd_button);
                }

                //Toast.makeText(RegisterVenDorActivity.this, radioSexButton.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverForgotPassdialog.dismiss();
            }
        });

        driverForgotPassdialog.show();
    }

    private void emailexists(final String email) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_EMAIL_EXISTS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(VendorRegistrationActivityStepOne.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String password = et_password.getText().toString();
                                                String email = et_email.getText().toString();
                                                String phoneNo = et_mobile.getText().toString();
                                                String name = et_fullname.getText().toString();
                                                String experience = et_experience.getText().toString();
                                                /*if (!et_password.getText().toString().equals(et_confrimpassword.getText().toString())) {
                                                    Toast.makeText(VendorRegistrationActivityStepOne.this, "Password Not matching ", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }*/
                                                if (!camImage.equals("")) {
                                                    SharedPreference.profileSave(getApplicationContext(), name, email, phoneNo, password, camImage, experience);

                                                } else {
                                                    SharedPreference.profileSave(getApplicationContext(), name, email, phoneNo, password, "", experience);

                                                }
                                                Intent intent = new Intent(VendorRegistrationActivityStepOne.this, VendorRegistrationActivityStepTwo.class);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(VendorRegistrationActivityStepOne.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(VendorRegistrationActivityStepOne.this, error);
                        AlertUtility.showAlert(VendorRegistrationActivityStepOne.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.KEY_EMAIL, email);
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

}

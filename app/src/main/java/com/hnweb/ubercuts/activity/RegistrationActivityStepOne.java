package com.hnweb.ubercuts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.Utils.Utils;
import com.hnweb.ubercuts.Utils.Validations;

/**
 * Created by Priyanka H on 13/06/2018.
 */
public class RegistrationActivityStepOne extends AppCompatActivity {
    Button btn_proceed;
    EditText et_fullname, et_email, et_mobile, et_password, et_confrimpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerstepone);
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        et_fullname = (EditText) findViewById(R.id.et_fullname);
        et_email = (EditText) findViewById(R.id.et_email);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_password = (EditText) findViewById(R.id.et_password);
        et_confrimpassword = (EditText) findViewById(R.id.et_confrimpassword);

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidation()) {

                    if (Utils.isNetworkAvailable(RegistrationActivityStepOne.this)) {


                        String password = et_password.getText().toString();
                        String email = et_email.getText().toString();
                        String phoneNo = et_mobile.getText().toString();
                        String name = et_fullname.getText().toString();

                        if (!et_password.getText().toString().equals(et_confrimpassword.getText().toString())) {
                            Toast.makeText(RegistrationActivityStepOne.this, "Password Not matching ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(RegistrationActivityStepOne.this, RegistrationActivityStepTwo.class);
                        startActivity(intent);
                    } else {
                        Utils.myToast1(RegistrationActivityStepOne.this);
                    }
                }

            }
        });
    }

    private boolean checkValidation() {

        boolean ret = true;
        if (!Validations.hasText(et_fullname, "Please Enter Name"))
            ret = false;
        if (!Validations.hasText(et_mobile, "Please Enter the Mobile No."))
        ret = false;
        if (!Validations.hasText(et_email, "Please Enter Email ID "))
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

}

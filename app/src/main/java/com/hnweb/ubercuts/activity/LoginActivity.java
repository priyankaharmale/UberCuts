package com.hnweb.ubercuts.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.Utils.Utils;
import com.hnweb.ubercuts.Utils.Validations;

/**
 * Created by Priyanka H on 12/06/2018.
 */
public class LoginActivity extends AppCompatActivity {
    Button btn_createaccount;
    TextView tv_forgotpwd;
    Dialog dialog;
    EditText et_emailId;
    EditText et_password, et_email;
    Button btn_signIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_createaccount = (Button) findViewById(R.id.btn_createaccount);
        tv_forgotpwd = (TextView) findViewById(R.id.tv_forgotpwd);
        btn_signIn = (Button) findViewById(R.id.btn_signIn);
        et_password=(EditText) findViewById(R.id.et_password);
        et_email=(EditText) findViewById(R.id.et_email);
        btn_createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivityStepOne.class);
                startActivity(intent);
            }
        });
        tv_forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation1()) {

                    if (Utils.isNetworkAvailable(LoginActivity.this)) {

                        String password = et_password.getText().toString();
                        String email = et_email.getText().toString();
                        // login(email, password);
                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }
            }
        });
    }

    public void dialog() {
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_forgotpwd);
        //   final EditText et_exprydate = (EditText) dialog.findViewById(R.id.et_exprydate);
        TextView tv_submit = (TextView) dialog.findViewById(R.id.tv_submit);
        TextView tv_cancle = (TextView) dialog.findViewById(R.id.tv_cancle);
        et_emailId = (EditText) dialog.findViewById(R.id.et_emailId);

        dialog.show();
        dialog.setCancelable(true);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidation()) {

                    if (Utils.isNetworkAvailable(LoginActivity.this)) {
                        String email = et_emailId.getText().toString();
                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }
                // start progress dialog
                /*-------------------------------------------------strip-------------------------------------*/

            }
        });

    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validations.hasText(et_emailId, "Please Enter Email ID ")) ;
        ret = false;

        if (!Validations.isEmailAddress(et_emailId, true, "Please Enter Valid Email ID"))
            ret = false;
        return ret;

    }

    private boolean checkValidation1() {
        boolean ret = true;
        if (!Validations.hasText(et_email, "Please Enter Email ID ")) ;
        ret = false;

        if (!Validations.isEmailAddress(et_email, true, "Please Enter Valid Email ID"))
            ret = false;
        if (!Validations.hasText(et_password, "Please Enter RE-Enter Password")) ;
        ret = false;
        if (!Validations.check_text_length_7_text_layout(et_password, "Password atleast 7 characters"))
            ret = false;

        return ret;
    }
}


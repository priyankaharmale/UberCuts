package com.hnweb.ubercuts.user.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.SharedPreference;

/**
 * Created by Priyanka H on 12/06/2018.
 */
public class MainActivity extends AppCompatActivity {
    Button btn_user, btn_vendor;
    SharedPreferences pref;
    String useridUser;
    ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        connectionDetector = new ConnectionDetector(MainActivity.this);


        useridUser = pref.getString(AppConstant.KEY_U_ID, null);
        btn_user = (Button) findViewById(R.id.btn_user);
        btn_vendor = (Button) findViewById(R.id.btn_vendor);
       /* btn_vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });*/
        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionDetector.isConnectingToInternet()) {
                    if (useridUser != null) {
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                        intentLogin.putExtra("UserVendorFlag", "USER");
                        intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentLogin);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

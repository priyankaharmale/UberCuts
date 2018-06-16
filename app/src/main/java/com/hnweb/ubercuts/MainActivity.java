package com.hnweb.ubercuts;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.helper.SharedPrefManager;
import com.hnweb.ubercuts.user.activity.HomeActivity;
import com.hnweb.ubercuts.user.activity.LoginActivity;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.PermissionUtility;

import java.util.ArrayList;

/**
 * Created by Priyanka H on 12/06/2018.
 */
public class MainActivity extends AppCompatActivity {
    Button btn_user, btn_vendor;
    SharedPreferences pref;
    String useridUser;
    ConnectionDetector connectionDetector;
    private PermissionUtility putility;
    ArrayList<String> permission_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        connectionDetector = new ConnectionDetector(MainActivity.this);

        runTimePermission();
        getdeviceToken();
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

    private void runTimePermission() {

        putility = new PermissionUtility(this);
        permission_list = new ArrayList<String>();
        permission_list.add(android.Manifest.permission.INTERNET);
        permission_list.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
        permission_list.add(android.Manifest.permission.WAKE_LOCK);
        permission_list.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permission_list.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        permission_list.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permission_list.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permission_list.add(Manifest.permission.CAMERA);
        putility.setListner(new PermissionUtility.OnPermissionCallback() {
            @Override
            public void OnComplete(boolean is_granted) {
                Log.i("OnPermissionCallback", "is_granted = " + is_granted);
                if (is_granted) {

                } else {
                    putility.checkPermission(permission_list);
                }
            }
        });
        putility.checkPermission(permission_list);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (putility != null) {
            putility.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getdeviceToken() {
        try {
            String t = SharedPrefManager.getInstance(this).getDeviceToken();

            if (t.equals("")) {
                Log.d("Tokan", "t-NULL");
            } else {
                Log.d("Tokan", t);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


}

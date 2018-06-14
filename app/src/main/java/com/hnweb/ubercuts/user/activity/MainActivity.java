package com.hnweb.ubercuts.user.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hnweb.ubercuts.R;
/**
 * Created by Priyanka H on 12/06/2018.
 */
public class MainActivity extends AppCompatActivity {
Button btn_user, btn_vendor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        btn_user=(Button) findViewById(R.id.btn_user);
        btn_vendor=(Button) findViewById(R.id.btn_vendor);
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
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}

package com.hnweb.ubercuts.vendor.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.MainActivity;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.user.activity.HomeActivity;
import com.hnweb.ubercuts.user.fragment.NailsFragment;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.vendor.fragment.LeadsFragment;
import com.hnweb.ubercuts.vendor.fragment.MyServiceAndoffersFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyanka H on 21-June-2018
 */

public class MainActivityVendor extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prefs;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String user_id, profile_image, user_name, user_street, user_city;
    private View navHeader;
    ImageView imageViewProfile, imageViewClose, imageViewUpload;
    TextView textViewUserName, textViewAdrress;
    public MenuItem liveitemList, liveitemMap;
    public CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_vendor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);


        user_id = prefs.getString(AppConstant.KEY_ID, null);
        profile_image = prefs.getString(AppConstant.KEY_IMAGE, null);
        user_name = prefs.getString(AppConstant.KEY_NAME, null);
        user_street = prefs.getString(AppConstant.KEY_STREET, null);
        user_city = prefs.getString(AppConstant.KEY_CITY, null);

        Log.d("UserIDVendor", "Vendor UserId :: " + user_id);

        connectionDetector = new ConnectionDetector(MainActivityVendor.this);
        loadingDialog = new LoadingDialog(MainActivityVendor.this);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader = navigationView.getHeaderView(0);
        textViewUserName = navHeader.findViewById(R.id.tv_user_name);
        textViewAdrress = navHeader.findViewById(R.id.textView_address);
        imageViewProfile = navHeader.findViewById(R.id.profile_image);
        imageViewClose = navHeader.findViewById(R.id.imageView_close);
        imageViewUpload = navHeader.findViewById(R.id.profile_image_photoupload);

        textViewUserName.setText(user_name);
        textViewAdrress.setText(user_street + ", " + user_city);

        if (profile_image.equals("")) {
            Glide.with(this).load(R.drawable.user_register).into(imageViewProfile);
        } else {
            Glide.with(this).load(profile_image).into(imageViewProfile);
        }

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

       /* imageViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Fragment fragment = null;
                fragment = new ();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();
            }
        })*/
        ;

        if (savedInstanceState == null) {

            Fragment fragment = null;
            fragment = new LeadsFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame_layout, fragment);
            transaction.commit();
            //toolbarmiddletext.setText("Home");
            //getSupportActionBar().setTitle("Find Craves");
            //drawerFragment.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vendor_main, menu);


        liveitemList = menu.findItem(R.id.action_list);
        liveitemMap = menu.findItem(R.id.action_map);
        SpannableString sList = new SpannableString(liveitemList.getTitle().toString());
        SpannableString sMap = new SpannableString(liveitemMap.getTitle().toString());
        sList.setSpan(new ForegroundColorSpan(Color.WHITE), 0, sList.length(), 0);
        sMap.setSpan(new ForegroundColorSpan(Color.WHITE), 0, sMap.length(), 0);
        liveitemList.setTitle(sList);
        liveitemMap.setTitle(sMap);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_leads) {
            // Handle the camera action
            fragment = new LeadsFragment();
        } else if (id == R.id.nav_my_service) {
            fragment = new MyServiceAndoffersFragment();
        }else
            if (id == R.id.nav_myprofile) {
            Intent intent = new Intent(MainActivityVendor.this, ProfileEditSaveVendorFragment.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            showLogoutAlert();
        }
       /* } else if (id == R.id.nav_my_bids) {
            fragment = new MyBidsFragment();
        } else if (id == R.id.nav_my_jobs) {
            fragment = new MyJobsFragment();
        } else if (id == R.id.nav_vendor_message) {

        } else if (id == R.id.nav_payment_history) {

        } else if (id == R.id.nav_my_service) {
            fragment = new MyServiceAndOfferFragment();
        } else if (id == R.id.nav_rating_reviws) {

        } else if (id == R.id.nav_change_password) {
            fragment = new ChangePasswordVendor();
        } else if (id == R.id.nav_logout) {
            showLogoutAlert();
        }*/

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Logout Successfully");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                prefs.edit().clear().apply();
                showLogoutAlert();
                dialog.cancel();
                Intent in = new Intent(MainActivityVendor.this, MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
}


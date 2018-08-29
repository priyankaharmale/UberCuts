package com.hnweb.ubercuts.user.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.MainActivity;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.user.fragment.FavouritesFragment;
import com.hnweb.ubercuts.user.fragment.HomeFragment;
import com.hnweb.ubercuts.user.fragment.MyTaskFragment;
import com.hnweb.ubercuts.user.fragment.ProfileEditFragment;
import com.hnweb.ubercuts.user.fragment.TodaysOfferFragment;
import com.hnweb.ubercuts.utils.LoadingDialog;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    LoadingDialog loadingDialog;
    DrawerLayout drawer;
    private View navHeader;
    public MenuItem liveitemList, liveitemMap;
    String profile_image, user_name, user_street, user_city, user_id;

    public Toolbar toolbar;
    ImageView imageViewProfile, imageViewClose, imageViewUpload;
    TextView textViewUserName, textViewAdrress;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getdeviceToken();

        loadingDialog = new LoadingDialog(HomeActivity.this);

        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        profile_image = prefs.getString(AppConstant.KEY_IMAGE, null);
        user_name = prefs.getString(AppConstant.KEY_NAME, null);
        user_street = prefs.getString(AppConstant.KEY_STREET, null);
        user_city = prefs.getString(AppConstant.KEY_CITY, null);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);
        textViewUserName = navHeader.findViewById(R.id.tv_user_name);
        textViewAdrress = navHeader.findViewById(R.id.textView_address);
        imageViewProfile = navHeader.findViewById(R.id.profile_image);
        imageViewClose = navHeader.findViewById(R.id.imageView_close);
        imageViewUpload = navHeader.findViewById(R.id.profile_image_photoupload);

        imageViewUpload.setVisibility(View.VISIBLE);

        textViewUserName.setText(user_name);
        textViewAdrress.setText(user_street + ", " + user_city);

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

       imageViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Fragment fragment = null;
                fragment = new ProfileEditFragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();
            }
        });

        if (profile_image == null || profile_image.equals("")) {
            Glide.with(this).load(R.drawable.user_register).into(imageViewProfile);
        } else {
            Glide.with(this).load(profile_image).into(imageViewProfile);
        }

        if (savedInstanceState == null) {

            Fragment fragment = null;
            fragment = new HomeFragment();
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

   /* private void getdeviceToken() {
        try {
            String token = SharedPrefManager.getInstance(this).getDeviceToken();

            if (token.equals("")) {
                Log.d("Tokan", "t-NULL");
                deviceToken = token;
            } else {
                Log.d("Tokan", token);
                deviceToken = token;
            }

            if (token.equals("")) {

            } else {
                updateDeviceToken(deviceToken);
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }


    }*/

    private void updateDeviceToken(String deviceToken) {

        /*//loadingDialog.show();
        String get_user_id = prefs.getString("user_id_user", null);
        Map<String, String> params = new HashMap<>();
        params.put("u_id", get_user_id);
        params.put("devicetoken", deviceToken);
        params.put("device_type", "Android");
        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("login");
        request_info.setUrl(WebsServiceURLUser.USER_UPDATE_TOKEN_DEVICE);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, MainActivityUser.this, request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "MainActivityUser" + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);
                    if (message_code == 1) {
                        Toast.makeText(MainActivityUser.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        //Utils.AlertDialog(MainActivityUser.this, msg);

                    }
                } catch (JSONException e) {
                    System.out.println("jsonexeption" + e.toString());
                }
            }

            @Override
            public void OnError(String message) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                AlertUtility.showAlert(MainActivityUser.this, false, "Network Error,Please Check Internet Connection");
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                finish();
            } else {
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                    //getFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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

        if (id == R.id.nav_home) {
            // Handle the camera action
            fragment = new HomeFragment();
        } else if (id == R.id.nav_logout) {
            showLogoutAlert();
        } else if (id == R.id.nav_favourites) {
            fragment = new FavouritesFragment();
            //toolbar.setTitle("FAVOURITES");
        } else if (id == R.id.nav_my_tasks) {
            fragment = new MyTaskFragment();
        } else if (id == R.id.nav_todays_offer) {
            fragment = new TodaysOfferFragment();
        }
        /* else if (id == R.id.nav_payment_history) {
            fragment = new PaymentFragment();
        } else if (id == R.id.nav_message) {

        } else if (id == R.id.nav_change_password) {
            fragment = new ChangePasswordUser();
        } */

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
                Intent in = new Intent(HomeActivity.this, MainActivity.class);
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

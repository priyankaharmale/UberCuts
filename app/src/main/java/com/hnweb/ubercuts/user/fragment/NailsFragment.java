package com.hnweb.ubercuts.user.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.user.adaptor.BeauticianDetailsAdapter;
import com.hnweb.ubercuts.user.adaptor.ServicesAdaptor;
import com.hnweb.ubercuts.user.bo.Details;
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.DataUtility;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.LocationSet;
import com.hnweb.ubercuts.utils.MyLocationListener;
import com.hnweb.ubercuts.utils.RequestInfo;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.vendor.activity.MainActivityVendor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PC-21 on 03-Apr-18.
 */

@SuppressLint("ValidFragment")
public class NailsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static final int REQUEST_PLACE_PICKER = 1001;
    MapView mMapView;
    private GoogleMap googleMap;
    String category_id;
    private SharedPreferences prefs;
    String user_id, serviceId;
    LocationSet locationSet = new LocationSet();

    Button btnPostTask, btnPostTaskList, btnRegularBooking;
    ConnectionDetector connectionDetector;
    ImageView image_filter;
    LoadingDialog loadingDialog;
    double latitude = 0.0d;
    double longitude = 0.0d;
    ConnectionDetector conDetector;

    boolean doubleBackToExitPressedOnce = false;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    MenuItem liveitemList, liveitemMap;
    LinearLayout linearLayoutMap, linearLayoutList;
    private ArrayList<Details> detailsArrayList = null;
    String arrayListCategory;
    String replaceArrayListCategory;
    RecyclerView rvBeauticianList;
    BeauticianDetailsAdapter beauticianDetailsAdapter;
    TextView textViewEmpty;
    EditText etSearchPlace;
    private Circle mCircle;
    private Marker mMarker;
    Context context;
    MyLocationListener myLocationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        liveitemList = menu.findItem(R.id.action_list);
        liveitemMap = menu.findItem(R.id.action_map);
        liveitemList.setVisible(true);
        liveitemMap.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;


        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.fragment_nails, null);
        myLocationListener = new MyLocationListener(getActivity());
        if (locationSet.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getActivity(), getActivity())) {
            fetchLocationData();
        } else {
            locationSet.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getContext(), getActivity());
        }

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);


        user_id = prefs.getString(AppConstant.KEY_ID, null);
        serviceId = prefs.getString("SubcategoryId", null);

        loadingDialog = new LoadingDialog(getActivity());

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 50, 180);

        initViewById(view);


        connectionDetector = new ConnectionDetector(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            //  getNailsList();
            getServices();
        } else {
            Snackbar snackbar = Snackbar
                    .make(((MainActivityVendor) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();
            //Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    private void googleMapView(final String getData) {

        if (getData.equals("1")) {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    // For showing a move to my location button
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);


                    for (int i = 0; i < detailsArrayList.size(); i++) {

                        Double latitude = Double.valueOf(detailsArrayList.get(i).getLatitude());
                        Double longitude = Double.valueOf(detailsArrayList.get(i).getLatitude());

                    /*BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nails_icon_map);
                    LatLng sydney = new LatLng(-34, 151);
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description")).setIcon(icon);

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/


                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_map);
                        LatLng lati_long_position = new LatLng(latitude, longitude);
                        googleMap.addMarker(new MarkerOptions().position(lati_long_position).title(detailsArrayList.get(i).getUName() + "," + detailsArrayList.get(i).getUId()).snippet(detailsArrayList.get(i).getSubCategoryName())).setIcon(icon);

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(lati_long_position).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        /* else {
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nails_icon_map);
                            LatLng lati_long_position = new LatLng(latitude, longitude);
                            googleMap.addMarker(new MarkerOptions().position(lati_long_position).title(detailsArrayList.get(i).getCategoryName() + "," + detailsArrayList.get(i).getUId()).snippet(detailsArrayList.get(i).getSubCategoryName())).setIcon(icon);

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(lati_long_position).zoom(12).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
*/
                    }



                /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(getActivity(), "Clicked marker", Toast.LENGTH_SHORT).show();
                    }
                });*/

               /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        String marker_value = marker.getTitle();
                        Log.d("Title",marker_value);

                        String[] separated = marker_value.split(",");
                        String title = separated[0];
                        String user_id = separated[1];

                        Log.d("Title",marker_value);
                        Log.e("Title",title);
                        Log.d("Title",user_id);
                        Bundle bundle = new Bundle();
                        //fragment = new NailsFragment();
                        Fragment fragment = new BeauticianDetailsFragment();
                        bundle.putString("Nails","2");
                        fragment.setArguments(bundle);
                        changeFragment(fragment);
                        return false;
                    }
                });*/
                }
            });
        } else {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;


                    // For showing a move to my location button
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);

                    LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    double longitudeCurrent = location.getLongitude();
                    double latitudeCurrent = location.getLatitude();
                    Log.d("CurrentLocation", longitudeCurrent + " :: " + latitudeCurrent);

                   /* Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    String city;
                    String state;*/

                        /*addresses = geocoder.getFromLocation(longitudeCurrent, latitudeCurrent, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        Log.d("Address", city + " :: " + state + " :: " + country + " :: " + postalCode + " :: " + knownName);*/
                    // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nails_icon_map);
                    LatLng lati_long_position = new LatLng(latitudeCurrent, longitudeCurrent);
                    //googleMap.addMarker(new MarkerOptions().position(lati_long_position)).setIcon(icon);

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(lati_long_position).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            if (mCircle == null || mMarker == null) {
                                drawMarkerWithCircle(latLng);
                            } else {
                                updateMarkerWithCircle(latLng);
                            }
                        }
                    });

                }



                /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(getActivity(), "Clicked marker", Toast.LENGTH_SHORT).show();
                    }
                });*/

               /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        String marker_value = marker.getTitle();
                        Log.d("Title",marker_value);

                        String[] separated = marker_value.split(",");
                        String title = separated[0];
                        String user_id = separated[1];

                        Log.d("Title",marker_value);
                        Log.e("Title",title);
                        Log.d("Title",user_id);
                        Bundle bundle = new Bundle();
                        //fragment = new NailsFragment();
                        Fragment fragment = new BeauticianDetailsFragment();
                        bundle.putString("Nails","2");
                        fragment.setArguments(bundle);
                        changeFragment(fragment);
                        return false;
                    }
                });*/

            });
        }
    }

    private void updateMarkerWithCircle(LatLng latLng) {
        mCircle.setCenter(latLng);
        mMarker.setPosition(latLng);
    }

    private void drawMarkerWithCircle(LatLng latLng) {
        double radiusInMeters = 3000.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = googleMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMarker = googleMap.addMarker(markerOptions);
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
    }

    private void initViewById(View view) {
        btnPostTask = view.findViewById(R.id.button_post_your_task);
        btnPostTask.setOnClickListener(this);
        image_filter = view.findViewById(R.id.image_filter);
        btnRegularBooking = view.findViewById(R.id.button_regular_post_your_task);
        btnRegularBooking.setOnClickListener(this);
        image_filter.setOnClickListener(this);
/*
        btnPostTaskList = view.findViewById(R.id.button_post_your_task_list);
        btnPostTaskList.setOnClickListener(this);*/

        linearLayoutMap = view.findViewById(R.id.LinearLayoutMap);
        linearLayoutList = view.findViewById(R.id.LinearLayoutList);

        rvBeauticianList = view.findViewById(R.id.recylerview_beatician_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvBeauticianList.setLayoutManager(mLayoutManager);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);

        etSearchPlace = view.findViewById(R.id.address_place);
        etSearchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(getActivity());
                    // Start the intent by requesting a result,
                    // identified by a request code.
                    startActivityForResult(intent, REQUEST_PLACE_PICKER);

                } catch (GooglePlayServicesRepairableException e) {
                    // ...
                } catch (GooglePlayServicesNotAvailableException e) {
                    // ...
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, getActivity());

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }

            etSearchPlace.setText(name);
            etSearchPlace.setText(address);
            //mViewAttributions.setText(Html.fromHtml(attributions));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

        if (id == R.id.action_list) {
            liveitemList.setVisible(false);
            liveitemMap.setVisible(true);
            linearLayoutMap.setVisibility(View.GONE);
            linearLayoutList.setVisibility(View.VISIBLE);

            return true;
        }

        if (id == R.id.action_map) {
            //liveitemList.setVisible(true);
            liveitemMap.setVisible(false);
            linearLayoutMap.setVisibility(View.VISIBLE);
            linearLayoutList.setVisibility(View.GONE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {
/*
            case R.id.button_post_your_task:
                fragment = new PostYourTaskFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();

                break;*/

            /*case R.id.button_post_your_task_list:

                fragment = new PostYourTaskFragment();
                FragmentManager manager1 = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction1 = manager1.beginTransaction();
                transaction1.addToBackStack(null);
                transaction1.replace(R.id.frame_layout, fragment);
                transaction1.commit();

                break;*/
/*
            case R.id.button_regular_post_your_task:

                fragment = new RegularBookingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Nails", category_id);
                bundle.putString("ArrayList", String.valueOf(arrayListCategory));
                fragment.setArguments(bundle);
                FragmentManager managerRG = getActivity().getSupportFragmentManager();
                FragmentTransaction transactionRG = managerRG.beginTransaction();
                transactionRG.addToBackStack(null);
                transactionRG.replace(R.id.frame_layout, fragment);
                transactionRG.commit();

                break;*/

            case R.id.image_filter:
                filterDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void fetchLocationData() {
        // check if myLocationListener enabled
        if (myLocationListener.canGetLocation()) {
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();

            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            myLocationListener.showSettingsAlert();
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();

            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        }

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        StringBuilder result = new StringBuilder();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(1)).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filterDialog() {
        final Dialog dailofFilter = new Dialog(getActivity());
        dailofFilter.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dailofFilter.setContentView(R.layout.dialog_filter);
        dailofFilter.setCancelable(true);

        dailofFilter.show();
    }

    private void getServices() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_VENDORLISTNEARBY,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("ServiceList", "ServiceList= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONArray userdetails = jobj.getJSONArray("details");
                                detailsArrayList = new ArrayList<Details>();

                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);


                                    Details details = new Details();
                                    details.setUId(jsonObject.getString("u_id"));
                                    details.setUName(jsonObject.getString("u_name"));
                                    details.setUEmail(jsonObject.getString("u_email"));
                                    details.setUBusinessName(jsonObject.getString("u_business_name"));
                                    details.setUImg(jsonObject.getString("u_img"));
                                    details.setExperience(jsonObject.getString("experience"));
                                    details.setUStreet(jsonObject.getString("u_street"));
                                    details.setUCity(jsonObject.getString("u_city"));
                                    details.setUState(jsonObject.getString("u_state"));
                                    details.setUCountry(jsonObject.getString("u_country"));
                                    details.setUZipcode(jsonObject.getString("u_zipcode"));
                                    details.setLatitude(jsonObject.getString("latitude"));
                                    details.setLongitude(jsonObject.getString("longitude"));
                                    details.setDistance(jsonObject.getString("distance"));
                                    details.setTodaysOffer(jsonObject.getString("todays_offer"));

                                    detailsArrayList.add(details);
                                    Log.d("ArraySize", String.valueOf(detailsArrayList.size()));

                                }
                                beauticianDetailsAdapter = new BeauticianDetailsAdapter(getActivity(), detailsArrayList);
                                rvBeauticianList.setAdapter(beauticianDetailsAdapter);
                                String str_data = "1";
                                googleMapView(str_data);
                                textViewEmpty.setVisibility(View.GONE);
                            } else {
                                Utils.AlertDialog(getActivity(), msg);
                                textViewEmpty.setVisibility(View.VISIBLE);
                                rvBeauticianList.setVisibility(View.GONE);
                                String getData = "0";
                                googleMapView(getData);
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
                    params.put("category_id", "1");
                    params.put("sub_category_id", serviceId);
                  /*  params.put("lat", String.valueOf(latitude));
                    params.put("long", String.valueOf(longitude));*/
                    params.put("lat", "25.7617");
                    params.put("long", "-80.1918");
                    params.put("range", "20");
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

}

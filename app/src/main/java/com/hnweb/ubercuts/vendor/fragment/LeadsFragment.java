package com.hnweb.ubercuts.vendor.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.vendor.activity.MainActivityVendor;
import com.hnweb.ubercuts.vendor.adaptor.LeadsListAdapter;
import com.hnweb.ubercuts.vendor.bo.LeadsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PC-21 on 09-Apr-18.
 */

public class LeadsFragment extends android.support.v4.app.Fragment {

    MenuItem liveitemList, liveitemMap;
    LinearLayout linearLayoutMap, linearLayoutList;
    private static final int REQUEST_PLACE_PICKER = 1001;
    MapView mMapView;
    private GoogleMap googleMap;

    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private SharedPreferences prefs;
    String user_id;
    private ArrayList<LeadsModel> leadsModelArrayList = null;
    LeadsListAdapter leadsListAdapter;
    RecyclerView recyclerViewLeadsList;
    TextView textViewEmpty;

    private Circle mCircle;
    private Marker mMarker;

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

        View view = inflater.inflate(R.layout.fragment_leads, container, false);

        prefs = getActivity().getSharedPreferences("MyPrefVendor", MODE_PRIVATE);
        user_id = prefs.getString("user_id_vendor", null);

        /*mMapView = (MapView) view.findViewById(R.id.mapView_leads);
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
        rlp.setMargins(0, 0, 50, 180);*/

        initViewByID(view);
        loadingDialog = new LoadingDialog(getActivity());
        connectionDetector = new ConnectionDetector(getActivity());

        if (connectionDetector.isConnectingToInternet()) {
            //  getLeadsList();
        } else {
            Snackbar snackbar = Snackbar.make(((MainActivityVendor) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);
            snackbar.show();
            //Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void initViewByID(View view) {

        linearLayoutMap = view.findViewById(R.id.LinearLayoutMap);
        linearLayoutList = view.findViewById(R.id.LinearLayoutList);

        recyclerViewLeadsList = view.findViewById(R.id.recylerview_beatician_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewLeadsList.setLayoutManager(layoutManager);

        textViewEmpty = view.findViewById(R.id.textViewEmpty);

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
            liveitemList.setVisible(true);
            liveitemMap.setVisible(false);
            linearLayoutMap.setVisibility(View.VISIBLE);
            linearLayoutList.setVisibility(View.GONE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //  private void getLeadsList() {





    private void googleMapView(String getData) {

        /*if (getData.equals("1")) {

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


                    for (int i = 0; i < leadsModelArrayList.size(); i++) {

                        Double latitude = Double.valueOf(leadsModelArrayList.get(i).getLatitude());
                        Double longitude = Double.valueOf(leadsModelArrayList.get(i).getLatitude());

                    *//*BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nails_icon_map);
                    LatLng sydney = new LatLng(-34, 151);
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description")).setIcon(icon);

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*//*

                        String leads_category = leadsModelArrayList.get(i).getCategory_id();
                        if (leads_category.equals("1")) {
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.hair_icon_map);
                            LatLng lati_long_position = new LatLng(latitude, longitude);
                            googleMap.addMarker(new MarkerOptions().position(lati_long_position).title(leadsModelArrayList.get(i).getCategory_name() + "," + leadsModelArrayList.get(i).getMy_task_id())).setIcon(icon);

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(lati_long_position).zoom(12).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        } else {
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nails_icon_map);
                            LatLng lati_long_position = new LatLng(latitude, longitude);
                            googleMap.addMarker(new MarkerOptions().position(lati_long_position).title(leadsModelArrayList.get(i).getCategory_name() + "," + leadsModelArrayList.get(i).getMy_task_id())).setIcon(icon);

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(lati_long_position).zoom(12).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }


                *//*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(getActivity(), "Clicked marker", Toast.LENGTH_SHORT).show();
                    }
                });*//*

               *//* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
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
                });*//*
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
                    LatLng lati_long_position;
                    final double longitudeCurrent;
                    final double latitudeCurrent;
                    LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        longitudeCurrent = location.getLongitude();
                        latitudeCurrent = location.getLatitude();
                        Log.d("CurrentLocation", longitudeCurrent + " :: " + latitudeCurrent);
                        lati_long_position = new LatLng(latitudeCurrent, longitudeCurrent);

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(lati_long_position).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(Location location) {
                                LatLng latLng = new LatLng(latitudeCurrent, longitudeCurrent);
                                if (mCircle == null || mMarker == null) {
                                    drawMarkerWithCircle(latLng);
                                } else {
                                    updateMarkerWithCircle(latLng);
                                }
                            }
                        });
                    } else {
                        //Toast.makeText(getActivity(), "Null", Toast.LENGTH_SHORT).show();
                    }


                    //googleMap.addMarker(new MarkerOptions().position(lati_long_position)).setIcon(icon);

                    // For zooming automatically to the location of the marker



                    *//*Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    String city;
                    String state;*//*
                        *//*addresses = geocoder.getFromLocation(longitudeCurrent, latitudeCurrent, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        Log.d("Address", city + " :: " + state + " :: " + country + " :: " + postalCode + " :: " + knownName);*//*
                    // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nails_icon_map);


                }



                *//*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(getActivity(), "Clicked marker", Toast.LENGTH_SHORT).show();
                    }
                });*//*

               *//* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
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
                });*//*

            });
        }*/
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

   /* @Override
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
    }*/
}

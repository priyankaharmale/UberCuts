package com.hnweb.ubercuts.vendor.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.LocationSet;
import com.hnweb.ubercuts.utils.MyLocationListener;
import com.hnweb.ubercuts.vendor.activity.MainActivityVendor;
import com.hnweb.ubercuts.R;

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
 * Created by PC-21 on 09-Apr-18.
 */

public class LeadsFragment extends android.support.v4.app.Fragment implements AdapterCallback, View.OnClickListener {

    MenuItem liveitemList, liveitemMap;
    LinearLayout linearLayoutMap, linearLayoutList;
    private static final int REQUEST_PLACE_PICKER = 1001;
    MapView mMapView;
    private GoogleMap googleMap;
    String radiusRangeValue = "";
    double latitude = 0.0d;
    double longitude = 0.0d;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private SharedPreferences prefs;
    String user_id;
    // private ArrayList<LeadsModel> leadsModelArrayList = new ArrayList<LeadsModel>();;
    //LeadsListAdapter leadsListAdapter;
    RecyclerView recyclerViewLeadsList;
    TextView textViewEmpty, textViewTotalCountMap, textViewTotalCountList;

    private Circle mCircle;
    private Marker mMarker;
    public AdapterCallback adapterCallback;
    ImageView imageViewMapFilter, imageViewMapFilterList, imageViewSearchList;
    ArrayList<String> selectedArrayList = new ArrayList<>();
    String replaceArrayListCategory = "";
    String category_id = "";
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    String sub_category_id = "";
    Toolbar toolbar;
    LinearLayout linearLayoutListSearch;
    SearchView searchView;
    LocationSet locationSet = new LocationSet();

    ImageView mCloseButton;
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

        View view = inflater.inflate(R.layout.fragment_leads, container, false);

        //toolbar = ((MainActivityVendor) getActivity()).toolbar;
        //toolbar.setTitle("Leads");

        prefs = getActivity().getSharedPreferences("MyPrefVendor", MODE_PRIVATE);
        user_id = prefs.getString("user_id_vendor", null);


        myLocationListener = new MyLocationListener(getActivity());
        if (locationSet.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getActivity(), getActivity())) {
            fetchLocationData();
        } else {
            locationSet.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getContext(), getActivity());
        }

        mMapView = (MapView) view.findViewById(R.id.mapView_leads);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterCallback = this;

        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 180, 20);

        initViewByID(view);
        loadingDialog = new LoadingDialog(getActivity());
        connectionDetector = new ConnectionDetector(getActivity());


        if (connectionDetector.isConnectingToInternet()) {
            radiusRangeValue = "20";
            //  getLeadsList(radiusRangeValue, category_id, sub_category_id);
            googleMapView("1");
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
        textViewTotalCountMap = view.findViewById(R.id.textView_list_count_map);
        textViewTotalCountList = view.findViewById(R.id.textView_list_count_list);

        imageViewMapFilter = view.findViewById(R.id.imageView_filter_map);
        imageViewMapFilter.setOnClickListener(this);
        imageViewMapFilterList = view.findViewById(R.id.imageView_filter_list);
        imageViewMapFilterList.setOnClickListener(this);
        imageViewSearchList = view.findViewById(R.id.imageView_search_list);
        imageViewSearchList.setOnClickListener(this);

        linearLayoutListSearch = view.findViewById(R.id.leads);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = view.findViewById(R.id.searchView_leads);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Add Text Change Listener to EditText
        mCloseButton = searchView.findViewById(R.id.search_close_btn);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.toString().trim().length() == 0) {
                    ///linearLayout.setVisibility(View.VISIBLE);
                    //searchView.setVisibility(View.GONE);
                    mCloseButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                    //mCloseButton.setVisibility(View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayoutListSearch.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.GONE);
                        }
                    });
                }
                // leadsListAdapter.getFilter().filter(query.toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(), "New Text "+newText, Toast.LENGTH_SHORT).show();
                if (newText.toString().trim().length() == 0) {
                    //linearLayout.setVisibility(View.VISIBLE);
                    mCloseButton.setVisibility(newText.isEmpty() ? View.GONE : View.VISIBLE);
                    mCloseButton.setVisibility(View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayoutListSearch.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.GONE);
                        }
                    });
                }
                //   leadsListAdapter.getFilter().filter(newText.toString());

                return true;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                linearLayoutListSearch.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_filter_map:
              //  showFiterDialog();
                break;

            case R.id.imageView_filter_list:
                // showFiterDialog();
                break;
            default:
                break;

            case R.id.imageView_search_list:
                linearLayoutListSearch.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                break;


        }
    }

/*
    private void showFiterDialog() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_filter_dialog, null);
        alertDialog.setView(dialogView);

        //IndicatorSeekBar rangeSeekbarAge = (IndicatorSeekBar) dialogView.findViewById(R.id.range_seekbar_indicator);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup_filter);
        RadioButton radioButtonNail = dialogView.findViewById(R.id.radioButton_hair);
        RadioButton radioButtonHair = dialogView.findViewById(R.id.radioButton_nail);

        final RecyclerView recyclerViewCate = dialogView.findViewById(R.id.recylerview_list_filter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewCate.setLayoutManager(layoutManager);
        Button btnReset = dialogView.findViewById(R.id.btn_reset_filter);
        Button btnApply = dialogView.findViewById(R.id.btn_apply_filter);
        final TextView textView = dialogView.findViewById(R.id.textView_sub_service);
        final View view = dialogView.findViewById(R.id.view_filter);
        final AlertDialog ad = alertDialog.create();
        ad.show();
        final TextView textViewkm = dialogView.findViewById(R.id.textView_radius_range);
        CrystalSeekbar seekbar = dialogView.findViewById(R.id.rangeSeekbar_range);
        // set listener
        seekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue) {
                textViewkm.setText(String.valueOf(minValue) + " km");

            }
        });

        // set final value listener
        seekbar.setOnSeekbarFinalValueListener(new OnSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number value) {
                Log.d("CRS=>", String.valueOf(value));
                radiusRangeValue = String.valueOf(value);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton_hair) {
                    category_id = "1";
                    getSubCategoryList(category_id, recyclerViewCate, textView, view);
                    //Toast.makeText(getContext(), "choice: Hair", Toast.LENGTH_SHORT).show();
                } else {
                    category_id = "2";
                    getSubCategoryList(category_id, recyclerViewCate, textView, view);
                    //Toast.makeText(getActivity(), "choice: Nail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();

                if (subCategoriesList.size() == 0 || subCategoriesList == null) {
                    Toast.makeText(getActivity(), "Please Wait....", Toast.LENGTH_SHORT).show();
                } else {
                    selectedArrayList.clear();
                    for (int i = 0; i < subCategoriesList.size(); i++) {
                        if (subCategoriesList.get(i).isSelected()) {
                            //params.put("service_ids[" + i + "]", vendorServicesAL.get(i).getCatID());
                            Log.d("ListSize", subCategoriesList.get(i).getSub_category_id());
                            selectedArrayList.add(subCategoriesList.get(i).getSub_category_id());
                        } else {
                            selectedArrayList.remove(subCategoriesList.get(i).getSub_category_id());
                        }
                    }
                    Log.d("ListSize1", String.valueOf(selectedArrayList.size()));

                    if (selectedArrayList.size() != 0) {
                        Log.d("SelecetedArrayList", selectedArrayList.toString());
                        String result = selectedArrayList.toString().replace("[", "").replace("]", "");
                        replaceArrayListCategory = result.replaceAll(" ", "");

                    } else {
                        Toast.makeText(getActivity(), "Please selected at least one service", Toast.LENGTH_SHORT).show();
                    }

                    if (connectionDetector.isConnectingToInternet()) {
                        getLeadsList(radiusRangeValue, category_id, replaceArrayListCategory);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

                        snackbar.show();
                        //Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
                category_id = "";
                sub_category_id = "";
                radiusRangeValue = "20";
                if (connectionDetector.isConnectingToInternet()) {
                    getLeadsList(radiusRangeValue, category_id, replaceArrayListCategory);
                } else {
                    Snackbar snackbar = Snackbar.make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    //Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
*/

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

        if (id == R.id.action_map) {
            liveitemList.setVisible(true);
            liveitemMap.setVisible(false);
            linearLayoutMap.setVisibility(View.VISIBLE);
            linearLayoutList.setVisibility(View.GONE);
            return true;
        }

        if (id == R.id.action_list) {
            liveitemList.setVisible(false);
            liveitemMap.setVisible(true);
            linearLayoutMap.setVisibility(View.GONE);
            linearLayoutList.setVisibility(View.VISIBLE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*
    private void getLeadsList(String radiusRangeValue, String category_id, String sub_category_id) {
        Map<String, String> params = new HashMap<>();

        params.put("u_id", user_id);
        params.put("category_id", category_id);
        params.put("sub_category_id", sub_category_id);
        params.put("range", radiusRangeValue);


        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("leads");
        request_info.setUrl(WebsServiceURLVendor.VENDOR_SERVICE_NEAR_BY_LIST);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, getActivity(), request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "LeadsList= " + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {
                        recyclerViewLeadsList.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = jobj.getJSONArray("details");

                        int total_size = jsonArray.length();
                        @SuppressLint("DefaultLocale") String s = String.format("%02d", total_size);
                        textViewTotalCountMap.setText(String.valueOf(s));
                        textViewTotalCountList.setText(String.valueOf(s));
                        leadsModelArrayList.clear();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(j);

                            LeadsModel leadsModel = new LeadsModel();
                            leadsModel.setMy_task_id(jsonObject.getString("my_task_id"));
                            leadsModel.setMy_task_message(jsonObject.getString("my_task_message"));
                            leadsModel.setMy_task_price(jsonObject.getString("my_task_price"));
                            leadsModel.setMy_task_job_location(jsonObject.getString("my_task_job_location"));
                            leadsModel.setU_name(jsonObject.getString("u_name"));
                            leadsModel.setCategory_id(jsonObject.getString("category_id"));
                            leadsModel.setCategory_name(jsonObject.getString("category_name"));
                            leadsModel.setLatitude(jsonObject.getString("latitude"));
                            leadsModel.setLongitude(jsonObject.getString("longitude"));
                            leadsModel.setSub_category_id(jsonObject.getString("sub_category_id"));
                            leadsModel.setSub_category_name(jsonObject.getString("sub_category_name"));

                            leadsModelArrayList.add(leadsModel);

                            Log.d("ArraySize", String.valueOf(leadsModelArrayList.size()));

                        }
                        leadsListAdapter = new LeadsListAdapter(getActivity(), leadsModelArrayList, adapterCallback);
                        recyclerViewLeadsList.setAdapter(leadsListAdapter);
                        String getData = "1";
                        googleMapView(getData);
                        textViewEmpty.setVisibility(View.GONE);
                    } else {
                        Utils.AlertDialog(getActivity(), msg);
                        textViewEmpty.setVisibility(View.VISIBLE);
                        recyclerViewLeadsList.setVisibility(View.GONE);
                        String getData = "0";
                        textViewTotalCountList.setText("00");
                        textViewTotalCountMap.setText("00");
                        googleMapView(getData);
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
                AlertUtility.showAlert(getActivity(), false, "Network Error,Please Check Internet Connection");
            }
        });

    }
*/

    private void googleMapView(String getData) {

        if (getData.equals("1")) {

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    mMap.clear();

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


                   /* for (int i = 0; i < leadsModelArrayList.size(); i++) {

                        Double latitude = Double.valueOf(leadsModelArrayList.get(i).getLatitude());
                        Double longitude = Double.valueOf(leadsModelArrayList.get(i).getLongitude());
                        Log.d("Lati", latitude + " :: " + longitude);
*/
                    /*BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nails_icon_map);
                    LatLng sydney = new LatLng(-34, 151);
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description")).setIcon(icon);

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

                    //  String leads_category = leadsModelArrayList.get(i).getCategory_id();
                     /*   if (leads_category.equals("1")) {
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
                        }*/
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
        } else {

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    mMap.clear();

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
                    double longitudeCurrent = latitude;
                    double latitudeCurrent = longitude;
                    Log.d("CurrentLocation", longitudeCurrent + " :: " + latitudeCurrent);

                    LatLng lati_long_position = new LatLng(longitudeCurrent, latitudeCurrent);
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

    @Override
    public void onMethodCallPosted() {
        //getLeadsList(radiusRangeValue, category_id, sub_category_id);
    }

  /*  @Override
    public void passDataToFragment() {
        getLeadsList(radiusRangeValue, category_id, sub_category_id);
    }
*/

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


}

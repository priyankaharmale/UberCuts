package com.hnweb.ubercuts.user.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyServicesFragment extends Fragment {
    
    ImageView imageViewNails,imageViewHairs;
    RecyclerView recyclerViewNails,recyclerViewHairs;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    TextView textViewEmptyNails,textViewEmptyHairs;
   // private ArrayList<ServiceNailsModels> serviceNailsModels = null;

 ///   private ArrayList<ServiceHairsModels> serviceHairsModels = null;

 //   NailsServiceAdapter nailsServiceAdapter;
 //   HairsServiceAdapter hairsServiceAdapter;
    String beautician_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_service,container,false);

        beautician_id= getArguments().getString("BeauticianIds");

        initViewById(view);

        return view;
    }

    private void initViewById(View view) {



        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            //getServiceNailsList();
            //getServiceHairsList();
        } else {
           /* Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

    }



   /* private void getServiceNailsList() {

        //loadingDialog.show();
        Map<String, String> params = new HashMap<>();

        params.put("u_id", "17");
        params.put("category_id", "1");
        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("login");
        request_info.setUrl(WebsServiceURLUser.USER_VIEW_BEAUTICIAN_LIST);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, getActivity(), request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "NailsResponse= " + data);
                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {

                        JSONArray userdetails = jobj.getJSONArray("details");
                        serviceNailsModels = new ArrayList<ServiceNailsModels>();
                        Log.d("ArrayLengthNails", String.valueOf(userdetails.length()));

                        for (int j = 0; j < userdetails.length(); j++) {
                            JSONObject jsonObject = userdetails.getJSONObject(j);

                            ServiceNailsModels serviceNailsModel = new ServiceNailsModels();

                            serviceNailsModel.setNails_service_id(jsonObject.getString("service_id"));
                            serviceNailsModel.setNails_service_name(jsonObject.getString("service_name"));
                            serviceNailsModel.setNails_default_price(jsonObject.getString("default_price"));

                            serviceNailsModels.add(serviceNailsModel);

                            Log.d("ArraySize", String.valueOf(serviceNailsModels.size()));

                        }
                        nailsServiceAdapter = new NailsServiceAdapter(getActivity(), serviceNailsModels);
                        recyclerViewNails.setAdapter(nailsServiceAdapter);

                    } else {
                        //Utils.AlertDialog(getActivity(), msg);
                        //textViewEmptyNails.setVisibility(View.VISIBLE);
                        recyclerViewNails.setVisibility(View.GONE);
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

    private void getServiceHairsList() {

        Map<String, String> params = new HashMap<>();

        params.put("u_id", beautician_id);
        params.put("category_id", "1");
        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("login");
        request_info.setUrl(WebsServiceURLUser.USER_VIEW_BEAUTICIAN_LIST);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, getActivity(), request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "HairsList= " + data);
                //recyclerViewHairs.setVisibility(View.VISIBLE);
                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {

                        JSONArray userdetails = jobj.getJSONArray("details");
                        serviceHairsModels = new ArrayList<ServiceHairsModels>();
                        Log.d("ArrayLength", String.valueOf(userdetails.length()));

                        for (int j = 0; j < userdetails.length(); j++) {
                            JSONObject jsonObject = userdetails.getJSONObject(j);

                            ServiceHairsModels serviceHairsModel = new ServiceHairsModels();

                            serviceHairsModel.setHairs_service_id(jsonObject.getString("service_id"));
                            serviceHairsModel.setHairs_service_name(jsonObject.getString("service_name"));
                            serviceHairsModel.setHairs_default_price(jsonObject.getString("default_price"));

                            serviceHairsModels.add(serviceHairsModel);

                            Log.d("ArraySize", String.valueOf(serviceHairsModels.size()));

                        }
                        hairsServiceAdapter = new HairsServiceAdapter(getActivity(), serviceHairsModels);
                        recyclerViewHairs.setAdapter(hairsServiceAdapter);
                        //textViewEmptyHairs.setVisibility(View.GONE);
                    } else {
                        //Utils.AlertDialog(getActivity(), msg);
                        //textViewEmptyHairs.setVisibility(View.VISIBLE);
                        recyclerViewHairs.setVisibility(View.GONE);


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
    }*/
}

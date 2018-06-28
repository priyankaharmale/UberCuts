package com.hnweb.ubercuts.user.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.DataUtility;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.RequestInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class AboutMeFragment extends Fragment {

    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String beautician_id,about_us;
    TextView textViewAboutUs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about_me, container, false);

        beautician_id= getArguments().getString("BeauticianIds");
        //Toast.makeText(getActivity(), "IDS "+beautician_id, Toast.LENGTH_SHORT).show();

        //Log.d("UserAboutMe",beautician_id);

        initViewByIds(view);

        return view;
    }

    private void initViewByIds(View view) {

        textViewAboutUs = view.findViewById(R.id.textView_about_me);

        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            //getBeaticianDeatailsList(beautician_id);
        } else {
            /*Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) BeauticianDetailsActivity.this).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
    }
/*
    private void getBeaticianDeatailsList(String beautician_id) {

        Map<String, String> params = new HashMap<>();

        params.put("u_id", beautician_id);

        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("login");
        request_info.setUrl(WebsServiceURLUser.USER_GET_BEAUTICIAN_DEATAILS);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, getActivity(), request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "ServiceList= " + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {

                        JSONArray userdetails = jobj.getJSONArray("details");

                        Log.d("ArrayLength", String.valueOf(userdetails.length()));

                        for (int j = 0; j < userdetails.length(); j++) {
                            JSONObject jsonObject = userdetails.getJSONObject(j);

                            about_us = jsonObject.getString("about_us");
                            textViewAboutUs.setText(about_us);

                        }
                    } else {
                        Utils.AlertDialog(getActivity(), msg);

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

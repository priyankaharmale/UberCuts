package com.hnweb.ubercuts.user.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.user.adaptor.NewServicesAdaptor;
import com.hnweb.ubercuts.user.adaptor.ReviewsListAdapter;
import com.hnweb.ubercuts.user.bo.ReviewsListModel;
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyReviewsFragment extends Fragment {

    RecyclerView recyclerViewReviews;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    TextView textViewEmptyReviews;

    String beautician_id;
    private ArrayList<ReviewsListModel> reviewsListModels = null;
    ReviewsListAdapter reviewsListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_reviews, container, false);

        beautician_id = getArguments().getString("BeauticianIds");

        initViewById(view);

        return view;
    }

    private void initViewById(View view) {

        //  textViewEmptyReviews= view.findViewById(R.id.textView_empty_service_hairs);

        recyclerViewReviews = view.findViewById(R.id.recylerview_my_reviews);

        RecyclerView.LayoutManager layoutManagerNails = new GridLayoutManager(getActivity(), 1);
        recyclerViewReviews.setLayoutManager(layoutManagerNails);

        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            getReviews();
        } else {

            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

    }


    private void getReviews() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_VENDORREVIEWS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_reviews" + response);
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "NailsResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONArray userdetails = jobj.getJSONArray("details");
                                reviewsListModels = new ArrayList<ReviewsListModel>();
                                Log.d("ArrayLengthNails", String.valueOf(userdetails.length()));

                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);

                                    ReviewsListModel reviewsListModel = new ReviewsListModel();

                                    reviewsListModel.setReviews_id(jsonObject.getString("review_id"));
                                    reviewsListModel.setReviews(jsonObject.getString("review_content"));
                                    reviewsListModel.setRating(jsonObject.getString("rating"));
                                    reviewsListModel.setCreated_date(jsonObject.getString("created_date"));
                                    reviewsListModel.setUser_name(jsonObject.getString("u_name"));
                                    reviewsListModel.setSender_id(jsonObject.getString("sender_id"));
                                   reviewsListModel.setUser_image(jsonObject.getString("u_img"));

                                    reviewsListModels.add(reviewsListModel);

                                    Log.d("ArraySize", String.valueOf(reviewsListModels.size()));

                                }
                                reviewsListAdapter = new ReviewsListAdapter(getActivity(), reviewsListModels);
                                recyclerViewReviews.setAdapter(reviewsListAdapter);

                            } else {
                                //Utils.AlertDialog(getActivity(), msg);
                                //textViewEmptyNails.setVisibility(View.VISIBLE);
                                recyclerViewReviews.setVisibility(View.GONE);
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
                    params.put("beautician_id", beautician_id);

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

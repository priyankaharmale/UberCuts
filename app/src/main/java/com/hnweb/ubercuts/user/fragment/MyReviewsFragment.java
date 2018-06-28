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

public class MyReviewsFragment extends Fragment {

    RecyclerView recyclerViewReviews;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    TextView textViewEmptyReviews;
   // private ArrayList<ReviewsListModel> reviewsListModels = null;

    //ReviewsListAdapter reviewsListAdapter;
    String beautician_id;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_reviews,container,false);

        beautician_id= getArguments().getString("BeauticianIds");

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
          //  getReviewsList();
        } else {
           /* Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

    }

    /*private void getReviewsList() {

        //loadingDialog.show();
        Map<String, String> params = new HashMap<>();

        params.put("u_id", beautician_id);
        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("login");
        request_info.setUrl(WebsServiceURLUser.USER_MY_REVIEWS_LIST);
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
                        reviewsListModels = new ArrayList<ReviewsListModel>();
                        Log.d("ArrayLengthNails", String.valueOf(userdetails.length()));

                        for (int j = 0; j < userdetails.length(); j++) {
                            JSONObject jsonObject = userdetails.getJSONObject(j);

                            ReviewsListModel reviewsListModel = new ReviewsListModel();

                            reviewsListModel.setReviews_id(jsonObject.getString("review_id"));
                            reviewsListModel.setReviews(jsonObject.getString("review"));
                            reviewsListModel.setRating(jsonObject.getString("rating"));
                            reviewsListModel.setCreated_date(jsonObject.getString("created_date"));
                            reviewsListModel.setUser_name(jsonObject.getString("u_name"));
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

package com.hnweb.ubercuts.user.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.user.adaptor.BeauticianDetailsAdapter;
import com.hnweb.ubercuts.user.bo.Details;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.vendor.adaptor.MyWorkListImagesAdapter;
import com.hnweb.ubercuts.vendor.adaptor.MyWorkListVideosAdapter;
import com.hnweb.ubercuts.vendor.bo.MyWorkModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class VendorMyWorkFragment extends Fragment implements AdapterCallback {

    RecyclerView recyclerViewImages, recyclerViewVideos;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    TextView textViewImagesCount, textViewVideosCount;
    TextView textViewEmptyReviews, textViewImagesEmpty, textViewVideosEmpty;
    private ArrayList<MyWorkModel> myWorkModelsImages = null;
    private ArrayList<MyWorkModel> myWorkModelsVideos = null;

    MyWorkListImagesAdapter reviewsListAdapterImages;
    MyWorkListVideosAdapter reviewsListAdapterVideos;
    String user_id;
    Button btnImages, btnVideos;
    AdapterCallback mAdapterCallback;
    SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_work, container, false);


        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);


        user_id = prefs.getString(AppConstant.KEY_ID, null);
        initViewById(view);

        mAdapterCallback = VendorMyWorkFragment.this;

        return view;
    }

    private void initViewById(View view) {

        textViewImagesEmpty = view.findViewById(R.id.textView_Images_empty);
        textViewVideosEmpty = view.findViewById(R.id.textView_Videos_empty);

        recyclerViewImages = view.findViewById(R.id.recylerview_my_work_images);
        recyclerViewVideos = view.findViewById(R.id.recylerview_my_work_videos);

        LinearLayoutManager horizontalLMImages = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewImages.setLayoutManager(horizontalLMImages);

        LinearLayoutManager horizontalLMVideos = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewVideos.setLayoutManager(horizontalLMVideos);

        textViewImagesCount = view.findViewById(R.id.textView_images_count);
        textViewVideosCount = view.findViewById(R.id.textView_videos_count);

        btnImages = view.findViewById(R.id.button_more_images);
        btnVideos = view.findViewById(R.id.button_more_videos);

        String image_text = btnImages.getText().toString().trim();
        if (image_text.equals("Add")) {
            btnImages.setText("View More");
        }
        String video_text = btnVideos.getText().toString().trim();
        if (video_text.equals("Add")) {
            btnVideos.setText("View More");
        }

      /*  btnImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewMoreImagesActvity.class);
                intent.putExtra("BeauticianIds", beautician_id);
                intent.putExtra("ImagesVideos","Images");
                startActivity(intent);
            }
        });

        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewMoreImagesActvity.class);
                intent.putExtra("BeauticianIds", beautician_id);
                intent.putExtra("ImagesVideos","Videos");
                startActivity(intent);
            }
        });
*/
        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            getMyWorkList();
        } else {
           /* Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

    }


    private void getMyWorkList() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_VENDORWORK,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("ImagesVideo", "ImagesVideoResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {


                                JSONArray jsonArrayImages = jobj.getJSONArray("work_images");
                                JSONArray jsonArrayVideos = jobj.getJSONArray("work_videos");
                                myWorkModelsImages = new ArrayList<MyWorkModel>();
                                myWorkModelsVideos = new ArrayList<MyWorkModel>();
                                myWorkModelsImages.clear();
                                myWorkModelsVideos.clear();

                                if (jsonArrayImages.length() < 0) {
                                    textViewImagesEmpty.setVisibility(View.VISIBLE);
                                    recyclerViewImages.setVisibility(View.GONE);
                                } else {
                                    Log.d("ArrayLengthImages", String.valueOf(jsonArrayImages.length()));

                                    int total_images_size = jsonArrayImages.length();
                                    String list_of_images_count = String.format("%02d", total_images_size);
                                    textViewImagesCount.setText(String.valueOf(list_of_images_count));
                                    btnImages.setVisibility(View.VISIBLE);

                                    for (int i = 0; i < jsonArrayImages.length(); i++) {
                                        JSONObject jsonObjectImages = jsonArrayImages.getJSONObject(i);

                                        MyWorkModel myWorkModel = new MyWorkModel();

                                        myWorkModel.setMy_work_images_id(jsonObjectImages.getString("my_work_images_id"));
                                        myWorkModel.setMy_work_images_name(jsonObjectImages.getString("my_work_images_name"));
                                        myWorkModel.setCategory_images_name(jsonObjectImages.getString("category_name"));

                                        myWorkModelsImages.add(myWorkModel);

                                        Log.d("ArraySizeImages", String.valueOf(myWorkModelsImages.size()));

                                    }
                                    String profile_work_images = "MyWork";
                                    reviewsListAdapterImages = new MyWorkListImagesAdapter(getActivity(), myWorkModelsImages, profile_work_images, loadingDialog, mAdapterCallback);
                                    recyclerViewImages.setAdapter(reviewsListAdapterImages);

                                }

                                if (jsonArrayVideos.length() == 0) {
                                    textViewVideosEmpty.setVisibility(View.VISIBLE);
                                    //recyclerViewVideos.setVisibility(View.GONE);
                                    btnVideos.setVisibility(View.GONE);
                                } else {
                                    btnVideos.setVisibility(View.VISIBLE);
                                    Log.d("ArrayLengthVideos", String.valueOf(jsonArrayVideos.length()));
                                    int total_videos_size = jsonArrayVideos.length();
                                    String list_of_videos_count = String.format("%02d", total_videos_size);
                                    textViewVideosCount.setText(String.valueOf(list_of_videos_count));

                                    for (int j = 0; j < jsonArrayVideos.length(); j++) {
                                        JSONObject jsonObjectVideos = jsonArrayVideos.getJSONObject(j);

                                        MyWorkModel myWorkModel = new MyWorkModel();

                                        myWorkModel.setMy_work_videos_id(jsonObjectVideos.getString("my_work_videos_id"));
                                        myWorkModel.setMy_work_videos_name(jsonObjectVideos.getString("my_work_videos_name"));
                                        myWorkModel.setCategory_videos_name(jsonObjectVideos.getString("category_name"));
                                        myWorkModel.setMy_work_videos_thumb(jsonObjectVideos.getString("thumb_name"));
                                        myWorkModelsVideos.add(myWorkModel);

                                        Log.d("ArraySizeVideos", String.valueOf(myWorkModelsVideos.size()));

                                    }


                                    String profile_work_videos = "MyWorkVideos";
                                    reviewsListAdapterVideos = new MyWorkListVideosAdapter(getActivity(), myWorkModelsVideos, profile_work_videos, loadingDialog, mAdapterCallback);
                                    recyclerViewVideos.setAdapter(reviewsListAdapterVideos);
                                }


                            } else {
                                //Utils.AlertDialog(getActivity(), msg);
                                //textViewEmptyNails.setVisibility(View.VISIBLE);
                                textViewImagesCount.setText("00");
                                recyclerViewImages.setVisibility(View.GONE);
                                recyclerViewVideos.setVisibility(View.GONE);
                                textViewVideosEmpty.setVisibility(View.VISIBLE);
                                textViewImagesEmpty.setVisibility(View.VISIBLE);
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
                    params.put("vendor_id", user_id);

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

    @Override
    public void onMethodCallPosted() {
        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            getMyWorkList();
        } else {
           /* Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
    }
}

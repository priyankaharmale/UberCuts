package com.hnweb.ubercuts.user.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.hnweb.ubercuts.user.adaptor.ReviewsListAdapter;
import com.hnweb.ubercuts.user.adaptor.TodaysOfferAadapter;
import com.hnweb.ubercuts.user.bo.ReviewsListModel;
import com.hnweb.ubercuts.user.bo.TodaysOfferModel;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.vendor.bo.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TodaysOfferFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerViewPostedList;
    SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private ArrayList<TodaysOfferModel> todaysOfferModels = new ArrayList<TodaysOfferModel>();
    TodaysOfferAadapter todaysOfferAadapter;
    TextView textViewList;
    TextView textViewListCount;
    ImageView imageViewFilter, imageViewSearch;
    String category_id = "";
    String sub_category_id = "";
    ArrayList<String> selectedArrayList = new ArrayList<>();
    String replaceArrayListCategory;
    Toolbar toolbar;
    LinearLayout linearLayout;
    SearchView searchView;
    ImageView mCloseButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.common_rv_layout, container, false);


        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);

        Log.e("PostedUserIds", user_id);


        //toolbar = ((MainActivityUser) getActivity()).toolbar;
        //toolbar.setTitle("TODAY'S OFFER");

        initViewById(view);
        return view;
    }

    private void initViewById(View view) {

        recyclerViewPostedList = view.findViewById(R.id.recylerview_common_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewPostedList.setLayoutManager(layoutManager);

        textViewList = view.findViewById(R.id.textView_empty_list_common);
        textViewListCount = view.findViewById(R.id.textView_list_count_common);

        imageViewFilter = view.findViewById(R.id.imageView_filter_common);
        imageViewFilter.setOnClickListener(this);
        imageViewSearch = view.findViewById(R.id.imageView_search_common);
        imageViewSearch.setOnClickListener(this);
        imageViewFilter.setVisibility(View.GONE);
        linearLayout = view.findViewById(R.id.linearLayout_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = view.findViewById(R.id.searchView_todays_offer);
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
                    // mCloseButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                    mCloseButton.setVisibility(View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayout.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.GONE);
                        }
                    });
                }
                todaysOfferAadapter.getFilter().filter(query.toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(), "New Text "+newText, Toast.LENGTH_SHORT).show();
                if (newText.toString().trim().length() == 0) {
                    //linearLayout.setVisibility(View.VISIBLE);
                    //searchView.setVisibility(View.GONE);
                    mCloseButton.setVisibility(View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayout.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.GONE);
                        }
                    });
                }
                todaysOfferAadapter.getFilter().filter(newText.toString());

                return true;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                linearLayout.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
                return false;
            }
        });


        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());

        if (connectionDetector.isConnectingToInternet()) {
            gettodaysoffer();
        } else {

            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageView_filter_common:
                //   showAlertDialog();
                break;

            case R.id.imageView_search_common:
                linearLayout.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                break;


            default:
                break;
        }
    }

    /*
        private void showAlertDialog() {


            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.item_filter_dialog, null);
            alertDialog.setView(dialogView);

            //IndicatorSeekBar rangeSeekbarAge = (IndicatorSeekBar) dialogView.findViewById(R.id.range_seekbar_indicator);
            RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup_filter);
            RadioButton radioButtonNail = dialogView.findViewById(R.id.radioButton_hair);
            RadioButton radioButtonHair = dialogView.findViewById(R.id.radioButton_nail);
            LinearLayout linearLayout = dialogView.findViewById(R.id.linearLayoutRange);
            LinearLayout linearLayout1 = dialogView.findViewById(R.id.linearLayoutSeekbar);
            View viewRange = dialogView.findViewById(R.id.view_range);
            linearLayout.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.GONE);
            viewRange.setVisibility(View.GONE);

            final RecyclerView recyclerViewCate = dialogView.findViewById(R.id.recylerview_list_filter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
            recyclerViewCate.setLayoutManager(layoutManager);
            Button btnReset = dialogView.findViewById(R.id.btn_reset_filter);
            Button btnApply = dialogView.findViewById(R.id.btn_apply_filter);
            final TextView textView = dialogView.findViewById(R.id.textView_sub_service);
            final View view = dialogView.findViewById(R.id.view_filter);


            final AlertDialog ad = alertDialog.create();
            ad.show();
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
                    }

                    if (connectionDetector.isConnectingToInternet()) {
                        getTodaysOfferList(category_id, replaceArrayListCategory);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

                        snackbar.show();
                        //Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad.cancel();
                    if (connectionDetector.isConnectingToInternet()) {
                        getTodaysOfferList("", "");
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

                        snackbar.show();
                        //Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                    }
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

        }
    */
    private void gettodaysoffer() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_TODAYSOFFER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "Today's Offer :" + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                todaysOfferModels.clear();
                                JSONArray jsonArray = jobj.getJSONArray("details");

                                int total_size = jsonArray.length();
                                String list_of_count = String.format("%02d", total_size);
                                textViewListCount.setText(String.valueOf(list_of_count));


                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(j);

                                    TodaysOfferModel todaysOfferModel = new TodaysOfferModel();
                                    todaysOfferModel.setToday_service_id(jsonObject.getString("service_id"));
                                    todaysOfferModel.setToday_default_price(jsonObject.getString("default_price"));
                                    todaysOfferModel.setToday_today_offer(jsonObject.getString("todays_offer"));
                                    todaysOfferModel.setToday_category_id(jsonObject.getString("category_id"));
                                    todaysOfferModel.setToday_category_name(jsonObject.getString("category_name"));
                                    todaysOfferModel.setToday_sub_category_id(jsonObject.getString("sub_category_id"));
                                    todaysOfferModel.setToday_sub_category_name(jsonObject.getString("sub_category_name"));
                                    todaysOfferModel.setToday_u_name(jsonObject.getString("u_name"));
                                    todaysOfferModels.add(todaysOfferModel);

                                }
                                //getListOfdata(subCategoriesList);
                                //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                                recyclerViewPostedList.setVisibility(View.VISIBLE);
                                todaysOfferAadapter = new TodaysOfferAadapter(getActivity(), todaysOfferModels);
                                recyclerViewPostedList.setAdapter(todaysOfferAadapter);
                                textViewList.setVisibility(View.GONE);
                            } else {
                                //Utils.AlertDialog(getActivity(), msg);
                                textViewList.setVisibility(View.VISIBLE);
                                recyclerViewPostedList.setVisibility(View.GONE);
                                recyclerViewPostedList.setAdapter(null);
                                textViewList.setText(msg);
                                textViewListCount.setText(String.valueOf("00"));
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
                    // params.put("beautician_id", beautician_id);

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

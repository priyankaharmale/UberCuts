package com.hnweb.ubercuts.vendor.fragment;

import android.annotation.SuppressLint;
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
import com.hnweb.ubercuts.user.adaptor.PaymentHistoryAdapter;
import com.hnweb.ubercuts.user.bo.PaymentHistoryModel;
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

public class PaymentVendorFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerViewList;
    SharedPreferences prefs;
    String vendor_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private ArrayList<PaymentHistoryModel> paymentHistoryModels = new ArrayList<PaymentHistoryModel>();
    PaymentHistoryAdapter paymentHistoryAdapter;
    TextView textViewList;
    TextView textViewListCount;
    ImageView imageViewFilter, imageViewSearch;
    ArrayList<String> selectedArrayList = new ArrayList<>();
    String replaceArrayListCategory;
    String category_id = "";
    String sub_category_id = "";
    LinearLayout linearLayout;
    SearchView searchView;
    ImageView mCloseButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.common_rv_layout, container, false);

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        vendor_id = prefs.getString(AppConstant.KEY_ID, null);
        Log.e("VendorIds", vendor_id);

        initViewById(view);

        return view;
    }

    private void initViewById(View view) {

        recyclerViewList = view.findViewById(R.id.recylerview_common_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewList.setLayoutManager(layoutManager);

        textViewList = view.findViewById(R.id.textView_empty_list_common);
        textViewListCount = view.findViewById(R.id.textView_list_count_common);

        imageViewFilter = view.findViewById(R.id.imageView_filter_common);
        imageViewFilter.setOnClickListener(this);

        imageViewSearch = view.findViewById(R.id.imageView_search_common);
        imageViewSearch.setOnClickListener(this);

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
                paymentHistoryAdapter.getFilter().filter(query.toString());
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
                paymentHistoryAdapter.getFilter().filter(newText.toString());

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
            getPaymentHistoryList();
        } else {

            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void getPaymentHistoryList() {

        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_PAYMENTHOSTORY_VENDOR,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "PaymentHistoryModelUSER" + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONArray jsonArray = jobj.getJSONArray("details");
                                recyclerViewList.setVisibility(View.VISIBLE);
                                int total_size = jsonArray.length();
                                String list_of_count = String.format("%02d", total_size);
                                textViewListCount.setText(String.valueOf(list_of_count));
                                paymentHistoryModels.clear();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(j);

                                    PaymentHistoryModel paymentHistoryModel = new PaymentHistoryModel();
                                    paymentHistoryModel.setPay_booking_amount(jsonObject.getString("booking_amount"));
                                    paymentHistoryModel.setPay_payment_date(jsonObject.getString("payment_date"));
                                    paymentHistoryModel.setPay_beautician_name(jsonObject.getString("user_name"));
                                    paymentHistoryModel.setPay_category_name(jsonObject.getString("category_name"));
                                    paymentHistoryModel.setPay_category_id(jsonObject.getString("ref_id_category"));
                                    paymentHistoryModel.setPay_date(jsonObject.getString("date"));
                                    paymentHistoryModel.setPay_time(jsonObject.getString("time"));
                                    paymentHistoryModel.setU_img(jsonObject.getString("u_img"));
                                    paymentHistoryModel.setSub_service_id(jsonObject.getString("ref_id_sub_category"));
                                    paymentHistoryModel.setSub_service_name(jsonObject.getString("sub_category_name"));
                                    paymentHistoryModels.add(paymentHistoryModel);
/*
                                    "booking_amount": "70",
                                            "payment_date": "2018-08-31 05:05:30",
                                            "user_name": "Mike Taylor",
                                            "category_name": "Nails",
                                            "ref_id_category": "1",
                                            "sub_category_name": "Pedicure",
                                            "ref_id_sub_category": "22",
                                            "u_img": "http:\/\/tech599.com\/tech599.com\/johnaks\/Ubercuts\/upload_u\/img_1535546938222219594.png",
                                            "date": "2018-8-31",
                                            "time": "05:05:30"*/

                                }
                                //getListOfdata(subCategoriesList);
                                //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();


                                paymentHistoryAdapter = new PaymentHistoryAdapter(getActivity(), paymentHistoryModels);
                                recyclerViewList.setAdapter(paymentHistoryAdapter);
                                textViewList.setVisibility(View.GONE);
                            } else {
                                //Utils.AlertDialog(getActivity(), msg);
                                textViewList.setVisibility(View.VISIBLE);
                                recyclerViewList.setVisibility(View.GONE);
                                recyclerViewList.setAdapter(null);
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
                    params.put("vendor_id", vendor_id);

                    Log.e("Params", params.toString());


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

  /*  private void showAlertDialog() {


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

                    if (connectionDetector.isConnectingToInternet()) {
                        getPaymentHistoryList(category_id, replaceArrayListCategory);
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
                if (connectionDetector.isConnectingToInternet()) {
                    category_id = "";
                    sub_category_id = "";
                    getPaymentHistoryList("", "");
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
                    category_id  = "1";
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
}

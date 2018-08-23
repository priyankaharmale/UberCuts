package com.hnweb.ubercuts.user.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hnweb.ubercuts.user.adaptor.FavouritesAdapter;
import com.hnweb.ubercuts.user.adaptor.ReviewsListAdapter;
import com.hnweb.ubercuts.user.bo.FavouritesModel;
import com.hnweb.ubercuts.user.bo.ReviewsListModel;
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

import static android.content.Context.MODE_PRIVATE;

public class FavouritesFragment extends Fragment implements AdapterCallback, View.OnClickListener {

    RecyclerView recyclerViewList;
    SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private ArrayList<FavouritesModel> favouritesModels = new ArrayList<FavouritesModel>();
    FavouritesAdapter favouritesAdapter;
    TextView textViewList;
    TextView textViewListCount;
    private AdapterCallback mAdapterCallback;
    ImageView imageViewFilter, imageViewSearch;
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

        mAdapterCallback = FavouritesFragment.this;

        //toolbar = ((MainActivityUser) getActivity()).toolbar;
       // toolbar.setTitle("FAVOURITES");

        initViewById(view);

        return view;
    }

    private void initViewById(View view) {
        recyclerViewList = view.findViewById(R.id.recylerview_common_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewList.setLayoutManager(layoutManager);

        textViewList = view.findViewById(R.id.textView_empty_list_common);
        textViewListCount = view.findViewById(R.id.textView_list_count_common);

        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());

        imageViewFilter = view.findViewById(R.id.imageView_filter_common);
        imageViewFilter.setOnClickListener(this);
        imageViewSearch = view.findViewById(R.id.imageView_search_common);
        imageViewSearch.setOnClickListener(this);
        imageViewFilter.setVisibility(View.INVISIBLE);

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
                     mCloseButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                   // mCloseButton.setVisibility(View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayout.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.GONE);
                        }
                    });
                }
                favouritesAdapter.getFilter().filter(query.toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(), "New Text "+newText, Toast.LENGTH_SHORT).show();
                if (newText.toString().trim().length() == 0) {
                    //linearLayout.setVisibility(View.VISIBLE);
                    //searchView.setVisibility(View.GONE);
                    mCloseButton.setVisibility(newText.isEmpty() ? View.GONE : View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayout.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.GONE);
                        }
                    });
                }
                favouritesAdapter.getFilter().filter(newText.toString());

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

        if (connectionDetector.isConnectingToInternet()) {
           getFavouritesList();
        } else {
          /*  Snackbar snackbar = Snackbar
                    .make(((HomeActivity) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();
          */
          Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
    }


/*
    private void getFavouritesList() {

        loadingDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("u_id", user_id);
        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("favourites");
        request_info.setUrl(WebsServiceURLUser.USER_FAVOURITES);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, getActivity(), request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "Favourites" + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {
                        favouritesModels.clear();
                        JSONArray jsonArray = jobj.getJSONArray("data");

                        int total_size = jsonArray.length();
                        String list_of_count = String.format("%02d", total_size);
                        textViewListCount.setText(String.valueOf(list_of_count));

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            FavouritesModel favouritesModel = new FavouritesModel();
                            favouritesModel.setFavourite_id(jsonObject.getString("favorite_id"));
                            favouritesModel.setU_id(jsonObject.getString("u_id"));
                            favouritesModel.setU_name(jsonObject.getString("u_name"));
                            favouritesModel.setU_img(jsonObject.getString("u_img"));
                            favouritesModel.setExperience(jsonObject.getString("experience"));
                            favouritesModel.setRating(jsonObject.getString("rating"));

                            JSONArray jsonArray1 = jsonObject.getJSONArray("task");
                            if (jsonArray1.length() > 0) {
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                    favouritesModel.setCategory_name(jsonObject1.getString("category_name"));
                                    String cate_name = jsonObject1.getString("category_name");
                                    Log.d("CategoryName", cate_name);

                                }
                            } else {
                                favouritesModel.setCategory_name(" - ");
                            }

                            favouritesModels.add(favouritesModel);

                        }
                        //getListOfdata(subCategoriesList);
                        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();


                        favouritesAdapter = new FavouritesAdapter(getActivity(), favouritesModels, loadingDialog, mAdapterCallback);
                        recyclerViewList.setAdapter(favouritesAdapter);
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

    private void getFavouritesList() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETALL_VENDORFAV,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "Favourites" + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                favouritesModels.clear();
                                JSONArray jsonArray = jobj.getJSONArray("data");

                                int total_size = jsonArray.length();
                                String list_of_count = String.format("%02d", total_size);
                                textViewListCount.setText(String.valueOf(list_of_count));

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    FavouritesModel favouritesModel = new FavouritesModel();
                                    favouritesModel.setFavourite_id(jsonObject.getString("favorite_id"));
                                    favouritesModel.setU_id(jsonObject.getString("u_id"));
                                    favouritesModel.setU_name(jsonObject.getString("u_name"));
                                    favouritesModel.setU_img(jsonObject.getString("u_img"));
                                    favouritesModel.setExperience(jsonObject.getString("experience"));
                                    favouritesModel.setRating(jsonObject.getString("rating"));


                                    favouritesModels.add(favouritesModel);

                                }
                                //getListOfdata(subCategoriesList);
                                //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();


                                favouritesAdapter = new FavouritesAdapter(getActivity(), favouritesModels, loadingDialog, mAdapterCallback);
                                recyclerViewList.setAdapter(favouritesAdapter);
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
                    params.put("u_id", user_id);

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
        getFavouritesList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imageView_search_common:
                linearLayout.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }
}

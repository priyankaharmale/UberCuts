package com.hnweb.ubercuts.user.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.user.adaptor.MyTaskAadapter;
import com.hnweb.ubercuts.user.bo.MyTaskModel;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

@SuppressLint("ValidFragment")
public class PostedFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerViewPostedList;
    SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private ArrayList<MyTaskModel> myTaskModels = new ArrayList<MyTaskModel>();
    MyTaskAadapter myTaskAadapter;
    TextView textViewList;
    TextView textViewListCount;
    AdapterCallback adapterCallback;
    int position;
    ImageView imageViewFilter, imageViewSearch;
    private int mYear, mMonth, mDay;
    String value_date_filter = "";
    ArrayList<String> selectedArrayList = new ArrayList<>();
    String replaceArrayListCategory = "";
    String category_id = "";
    String sub_category_id = "";
    LinearLayout linearLayout;
    SearchView searchView;
    ImageView mCloseButton;
    View view;

    @Override
    public void setUserVisibleHint(boolean isUserVisible) {
        super.setUserVisibleHint(isUserVisible);
        // when fragment visible to user and view is not null then enter here.
        if (isUserVisible) {
            // do your stuff here.
           // getPostedTaskList();
            //getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_posted, container, false);


        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        Log.e("PostedUserIds", user_id);
        initViewById(view);

        //setUserVisibleHint(true);

        return view;
    }

    private void initViewById(View view) {
        recyclerViewPostedList = view.findViewById(R.id.recylerview_posted_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewPostedList.setLayoutManager(layoutManager);

        textViewList = view.findViewById(R.id.textView_empty_list);
        textViewListCount = view.findViewById(R.id.textView_list_count);

        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());

        imageViewFilter = view.findViewById(R.id.imageView_filter_booked);
        imageViewFilter.setOnClickListener(this);
        imageViewSearch = view.findViewById(R.id.imageView_search);
        imageViewSearch.setOnClickListener(this);

        linearLayout = view.findViewById(R.id.linearLayout_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = view.findViewById(R.id.searchView_my_task);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Add Text Change Listener to EditText
        mCloseButton = searchView.findViewById(R.id.search_close_btn);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                myTaskAadapter.getFilter().filter(query.toString());
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
                myTaskAadapter.getFilter().filter(newText.toString());

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

            getPostedTaskList();
        } else {
            /*Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();
*/
             Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
    }


    private void getPostedTaskList() {

        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_MYTASKLISTING,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "PostList :" + response);


                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                JSONArray userdetails = jobj.getJSONArray("details");
                                recyclerViewPostedList.setVisibility(View.VISIBLE);
                                int total_size = userdetails.length();
                                String list_of_count = String.format("%02d", total_size);
                                textViewListCount.setText(String.valueOf(list_of_count));
                                myTaskModels.clear();
                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);

                                    MyTaskModel myTaskModel = new MyTaskModel();
                                    myTaskModel.setMy_task_id(jsonObject.getString("my_task_id"));
                                    myTaskModel.setCategory_name(jsonObject.getString("category_name"));
                                    myTaskModel.setCategory_id(jsonObject.getString("category_id"));
                                    myTaskModel.setBeautician(jsonObject.getString("beautician"));
                                    myTaskModel.setDate(jsonObject.getString("date"));
                                    myTaskModel.setTime(jsonObject.getString("time"));

                                    myTaskModel.setStatus(jsonObject.getString("status"));
                                    myTaskModel.setSub_category_name(jsonObject.getString("sub_category_name"));
                                    myTaskModel.setSub_category_id(jsonObject.getString("sub_category_id"));
                                    myTaskModels.add(myTaskModel);

                                }
                                //getListOfdata(subCategoriesList);
                                //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();


                                myTaskAadapter = new MyTaskAadapter(getActivity(), myTaskModels);
                                recyclerViewPostedList.setAdapter(myTaskAadapter);
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
                    params.put("client_id", user_id);
                    params.put("status", "0");
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
            case R.id.imageView_filter_booked:
        //        showAlertDialog();
                break;

            case R.id.imageView_search:
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
        View dialogView = inflater.inflate(R.layout.item_filter_my_task_dialog, null);
        alertDialog.setView(dialogView);

        //IndicatorSeekBar rangeSeekbarAge = (IndicatorSeekBar) dialogView.findViewById(R.id.range_seekbar_indicator);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup_filter);
        RadioButton radioButtonNail = dialogView.findViewById(R.id.radioButton_hair);
        RadioButton radioButtonHair = dialogView.findViewById(R.id.radioButton_nail);
        LinearLayout linearLayout = dialogView.findViewById(R.id.linearLayoutRange);
        LinearLayout linearLayout1 = dialogView.findViewById(R.id.linearLayoutSeekbar);

        //linearLayout.setVisibility(View.v);
        //linearLayout1.setVisibility(View.GONE);
        //viewRange.setVisibility(View.GONE);

        final RecyclerView recyclerViewCate = dialogView.findViewById(R.id.recylerview_list_filter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewCate.setLayoutManager(layoutManager);
        Button btnReset = dialogView.findViewById(R.id.btn_reset_filter);
        Button btnApply = dialogView.findViewById(R.id.btn_apply_filter);
        final TextView textView = dialogView.findViewById(R.id.textView_sub_service);
        final View view = dialogView.findViewById(R.id.view_filter);

        final CheckBox checkBox = dialogView.findViewById(R.id.checkBox_date);

        Date today = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yyyy");
        String dateToStr = formatDate.format(today);
        checkBox.setText(dateToStr);

        final ImageView imageView = dialogView.findViewById(R.id.imageView_date);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String date_checked = checkBox.getText().toString().trim();
                    String date_format;
                    String input_date_format = "dd MMM yyyy";
                    String output_date_format = "yyyy-MM-dd";
                    Utils utils = new Utils();
                    String outputDateFormat = utils.dateFormats(date_checked, input_date_format, output_date_format);
                    Log.e("dateFormatsConverts", outputDateFormat);
                    value_date_filter = outputDateFormat;
                } else {
                    value_date_filter = "";
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPickerDate(checkBox);
            }
        });


        final AlertDialog ad = alertDialog.create();
        ad.show();
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
                if (!category_id.matches("")) {
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
                }

                if (connectionDetector.isConnectingToInternet()) {
                    getPostedTaskList(category_id, replaceArrayListCategory, value_date_filter);
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
                    category_id = "";
                    replaceArrayListCategory = "";
                    value_date_filter = "";
                    getPostedTaskList(category_id, replaceArrayListCategory, value_date_filter);
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

    private void getPickerDate(final CheckBox checkBox) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String list_of_count = String.format("%02d", (monthOfYear + 1));

                        String date = dayOfMonth + "-" + list_of_count + "-" + year;
                        Log.e("DateFormatChange", date);

                        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                        Date date_format = null;
                        try {
                            date_format = inputFormat.parse(date);
                            date_format = inputFormat.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateFormat = outputFormat.format(date_format);
                        Log.d("DateFormatClass", outputDateFormat);


                        checkBox.setText(outputDateFormat);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
*/


}

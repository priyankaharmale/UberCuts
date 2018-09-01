package com.hnweb.ubercuts.vendor.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.user.adaptor.ServiceAdaptor;
import com.hnweb.ubercuts.user.bo.MyTaskModel;
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.vendor.adaptor.MyJobsVendorAdapter;
import com.hnweb.ubercuts.vendor.bo.Category;

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
public class MyJobsCompletedFragment extends Fragment implements AdapterCallback, View.OnClickListener, OnCallBack {

    RecyclerView recyclerViewPostedList;
    SharedPreferences prefs;
    String vendor_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private ArrayList<MyTaskModel> myTaskModels = new ArrayList<MyTaskModel>();
    MyJobsVendorAdapter myTaskAadapter;
    TextView textViewList;
    OnCallBack onCallBack;
    TextView textViewListCount;
    Fragment fragmentSet = new Fragment();
    int position;
    AdapterCallback adapterCallback;
    ImageView imageViewFilter, imageViewSearch;
    private int mYear, mMonth, mDay;
    String value_date_filter = "";
    ArrayList<String> selectedArrayList = new ArrayList<>();
    String replaceArrayListCategory = "";
    String category_id = "";
    LinearLayout linearLayout;
    SearchView searchView;
    ListView recyclerViewCate;
    ImageView mCloseButton;
    ProgressBar progressBarTask;
    ServiceAdaptor serviceAdaptor;
    String serviceId;
    ArrayList<Services> servicesArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_posted, container, false);

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        vendor_id = prefs.getString(AppConstant.KEY_ID, null);
        onCallBack = this;
        initViewById(view);

        try {
            adapterCallback = MyJobsCompletedFragment.this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initViewById(View view) {
        recyclerViewPostedList = view.findViewById(R.id.recylerview_posted_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewPostedList.setLayoutManager(layoutManager);
        progressBarTask = view.findViewById(R.id.progressBar_task);

        textViewList = view.findViewById(R.id.textView_empty_list);
        textViewListCount = view.findViewById(R.id.textView_list_count);

        imageViewFilter = view.findViewById(R.id.imageView_filter_booked);
        imageViewFilter.setOnClickListener(this);

        imageViewSearch = view.findViewById(R.id.imageView_search);
        imageViewSearch.setOnClickListener(this);
        progressBarTask.setVisibility(View.VISIBLE);
        linearLayout = view.findViewById(R.id.linearLayout_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = view.findViewById(R.id.searchView_my_task);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Add Text Change Listener to EditText
        mCloseButton = searchView.findViewById(R.id.search_close_btn);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
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
                    myTaskAadapter.getFilter().filter(query.toString());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(), "New Text "+newText, Toast.LENGTH_SHORT).show();
                try {
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
                    myTaskAadapter.getFilter().filter(newText.toString());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

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
            category_id = "";
            replaceArrayListCategory = "";
            value_date_filter = "";
            getPostedTaskList(category_id, replaceArrayListCategory, value_date_filter);
        } else {
           /* Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }


    }

    private void getPostedTaskList(final String category_id, final String replaceArrayListCategory, final String value_date_filter) {

        //loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_VENDOR_JOB_LISTING,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "Completed :" + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                progressBarTask.setVisibility(View.GONE);
                                recyclerViewPostedList.setVisibility(View.VISIBLE);
                                JSONArray userdetails = jobj.getJSONArray("details");
                                int total_size = userdetails.length();
                                @SuppressLint("DefaultLocale") String list_of_count = String.format("%02d", total_size);
                                textViewListCount.setText(String.valueOf(list_of_count));
                                myTaskModels.clear();
                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);

                                    MyTaskModel myTaskModel = new MyTaskModel();
                                    myTaskModel.setMy_task_id(jsonObject.getString("my_task_id"));
                                    myTaskModel.setCategory_name(jsonObject.getString("category_name"));
                                    myTaskModel.setCategory_id(jsonObject.getString("category_id"));
                                    myTaskModel.setVendor_name(jsonObject.getString("user"));
                                    myTaskModel.setDate(jsonObject.getString("date"));
                                    myTaskModel.setStatus(jsonObject.getString("status"));
                                    myTaskModel.setU_image(jsonObject.getString("user_image"));

                                    myTaskModel.setSub_category_id(jsonObject.getString("sub_category_id"));
                                    myTaskModel.setSub_category_name(jsonObject.getString("sub_category_name"));
                                    myTaskModel.setJob_location_name(jsonObject.getString("job_location"));
                                    myTaskModels.add(myTaskModel);

                                }
                                //getListOfdata(subCategoriesList);
                                //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                                FragmentManager fragmentManager = getChildFragmentManager();
                                Fragment fragment = new Fragment();

                                myTaskAadapter = new MyJobsVendorAdapter(getActivity(), myTaskModels, adapterCallback);
                                recyclerViewPostedList.setAdapter(myTaskAadapter);
                                textViewList.setVisibility(View.GONE);
                            } else {
                                //Utils.AlertDialog(getActivity(), msg);
                                progressBarTask.setVisibility(View.GONE);
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
                    params.put("vendor_id", vendor_id);
                    params.put("status", "2");
                    params.put("category_id", category_id);
                    params.put("sub_category_id", replaceArrayListCategory);
                    params.put("fdt", value_date_filter);

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
        category_id = "";
        replaceArrayListCategory = "";
        value_date_filter = "";
        getPostedTaskList(category_id, replaceArrayListCategory, value_date_filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_filter_booked:
                showAlertDialog();
                break;

            case R.id.imageView_search:
                linearLayout.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void showAlertDialog() {
        getServices();
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_filter_my_task_dialog, null);
        alertDialog.setView(dialogView);
        recyclerViewCate = dialogView.findViewById(R.id.recylerview_list_filter);
        Button btnReset = dialogView.findViewById(R.id.btn_reset_filter);
        Button btnApply = dialogView.findViewById(R.id.btn_apply_filter);

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
                if (serviceId == null) {
                    Toast.makeText(getActivity(), "Please selected at least one service", Toast.LENGTH_SHORT).show();
                } else {
                    ad.cancel();
                    if (connectionDetector.isConnectingToInternet()) {
                        getPostedTaskList("1", serviceId, value_date_filter);
                    } else {
                        Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
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
                    replaceArrayListCategory = "";
                    value_date_filter = "";
                    getPostedTaskList(category_id, replaceArrayListCategory, value_date_filter);
                } else {

                    Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
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

    private void getServices() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_SERVICELIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            servicesArrayList = new ArrayList<Services>();
                            if (message_code == 1) {

                               /* AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
*/
                                try {
                                    JSONArray jsonArray = j.getJSONArray("details");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        Services services = new Services();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String serviceId = jsonObject.getString("sub_category_id");
                                        String serviceName = jsonObject.getString("sub_category_name");
                                        services.setId(serviceId);
                                        services.setServicesName(serviceName);

                                        servicesArrayList.add(services);
                                        serviceAdaptor = new ServiceAdaptor(getActivity(), servicesArrayList, onCallBack);
                                        recyclerViewCate.setAdapter(serviceAdaptor);
                                    }


                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                                              /*  dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();*/
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
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
                    params.put(AppConstant.KEY_CATEGORY_ID, "1");

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
    public void callback(String count) {

        serviceId = count;
    }

    @Override
    public void callbackYear(String count) {

    }

    @Override
    public void callcountryList(String id, String name) {

    }

    @Override
    public void callstateList(String id, String name) {

    }

    @Override
    public void callcityList(String id, String name) {

    }

    @Override
    public void callrefresh() {

    }
}

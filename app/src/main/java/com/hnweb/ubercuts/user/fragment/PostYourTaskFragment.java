package com.hnweb.ubercuts.user.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.hnweb.ubercuts.user.activity.HomeActivity;
import com.hnweb.ubercuts.user.adaptor.NewServicesAdaptor;
import com.hnweb.ubercuts.user.adaptor.ServiceAdaptor;
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

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PC-21 on 04-Apr-18.
 */

public class PostYourTaskFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private SharedPreferences prefs;
    String user_id;
    Button btnPostTask, btnSelectJobs, btnSubCate;
    EditText etTaskyourMessage, etYourPrice;
    Spinner spJob, spCategory, spSubCategory;
    private String[] job_location = {"Customer Place", "Beauty Parlor"};
    private ListView listView;
    private ArrayAdapter<String> jobsArrayAdapter;
    ArrayList<Services> serivcesList;
    private ArrayAdapter<Services> serviceAdaptor;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    private String selectedJobs = "";
    private String selectedCategoryName = "";
    private String category_id="";
    Toolbar toolbar;
    private String beautician_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_your_task, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        toolbar = ((HomeActivity) getActivity()).toolbar;
        toolbar.setTitle("POST YOUR TASK");


        user_id = prefs.getString(AppConstant.KEY_ID, null);

        initViewById(view);


        return view;
    }

    private void initViewById(View view) {

        btnPostTask = view.findViewById(R.id.btn_post_task);
        btnSelectJobs = view.findViewById(R.id.button_select_jobs);
        btnSubCate = view.findViewById(R.id.button_sub_category);

        etTaskyourMessage = view.findViewById(R.id.et_task_your_message);
        etYourPrice = view.findViewById(R.id.et_your_price);

        btnPostTask.setOnClickListener(this);
        btnSelectJobs.setOnClickListener(this);
        btnSubCate.setOnClickListener(this);
        connectionDetector = new ConnectionDetector(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            getServices();
        } else {
           /* Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_post_task:
                inputFieldValidation();
                break;

            case R.id.button_select_jobs:
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                onClickedSelectedJobs();
                break;


            case R.id.button_sub_category:
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                onClickedSelectedCategory();
                break;

            default:
                break;
        }
    }

    private void inputFieldValidation() {

        String your_messsage = etTaskyourMessage.getText().toString().trim();
        String your_price = etYourPrice.getText().toString().trim();
        String sub_cate = btnSubCate.getText().toString().trim();

        if (!your_messsage.matches("")) {
            if (!your_price.matches("")) {
                if (!selectedJobs.equals("")) {
                    if (!sub_cate.matches("")) {
                        if (connectionDetector.isConnectingToInternet()) {


                                Fragment fragment = new BookingConfirmationFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("my_task_message", your_messsage);
                                bundle.putString("my_task_price", your_price);
                                bundle.putString("my_task_job_location", selectedJobs);
                                bundle.putString("ref_id_category", "1");
                                bundle.putString("ref_id_sub_category", category_id);
                                bundle.putString("ref_category_name", "Nails");
                                bundle.putString("ref_sub_category_name", selectedCategoryName);
                                bundle.putString("status", "0");
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.addToBackStack(null);
                                fragment.setArguments(bundle);
                                transaction.replace(R.id.frame_layout, fragment);
                                transaction.commit();
                            } else {
                                Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                            }

                    } else {
                        Toast.makeText(getActivity(), "Please Select the service!!", Toast.LENGTH_SHORT).show();
                        }
                        } else {
                    Toast.makeText(getActivity(), "Please selected Jobs!!", Toast.LENGTH_SHORT).show();
                }
            } else {
                etYourPrice.setError("Please Enter Price!!");
            }
        } else {
            etTaskyourMessage.setError("Please Enter Your Message!!");
        }
    }

/*
    private void postYourTask(String your_messsage, String your_price, String job_location, String cate, String sub_cate) {

        loadingDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("my_task_message", your_messsage);
        params.put("my_task_price", your_price);
        params.put("my_task_job_location", "1");
        params.put("ref_id_category", selectedCategoryId);
        params.put("ref_id_sub_category", selectedSubCategoryId);
        params.put("status", "0");

        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("login");
        request_info.setUrl(WebsServiceURLUser.USER_POST_YOUR_TASK);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, getActivity(), request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "Category= " + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        etTaskyourMessage.setText("");
                        etYourPrice.setText("");
                        selectedJobs = "";
                        selectedCategoryId = "";
                        selectedSubCategoryId = "";
                        getActivity().getSupportFragmentManager().popBackStack();
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

    }
*/

    private void onClickedSelectedCategory() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.common_list, null);
        alertDialog.setView(dialogView);

        TextView textView_header = (TextView) dialogView.findViewById(R.id.textView_custom_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select Category");
        }
        Button btnCancel = (Button) dialogView.findViewById(R.id.button_cancel);
        listView = (ListView) dialogView.findViewById(R.id.listView_cate);
        final AlertDialog ad = alertDialog.create();
        ad.show();

        ImageView imageView = dialogView.findViewById(R.id.imageViewBackArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });
        serviceAdaptor = new ArrayAdapter<Services>(getActivity(), R.layout.spinner_text, serivcesList);
        listView.setAdapter(serviceAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Toast.makeText(RegisterActivity.this, genderArrayList.get(position), Toast.LENGTH_SHORT).show();
                Services stateList = serviceAdaptor.getItem(position);
                 category_id = stateList.getId();
                String category_name = stateList.getServicesName();
                selectedCategoryName = category_name;
                btnSubCate.setText(selectedCategoryName);
                ad.dismiss();
              /*  if (category_id != null) {
                    selectedCategory = true;

                    if (connectionDetector.isConnectingToInternet()) {
                        getSubCategoryList(category_id);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG)
                                .setAction("Retry!!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar snackbar1 = Snackbar.make(((MainActivityUser) getActivity()).coordinatorLayout, "Message is restored!", Snackbar.LENGTH_SHORT);
                                        snackbar1.show();
                                    }
                                });

                        snackbar.show();
                        //Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select State !!", Toast.LENGTH_SHORT).show();
                }*/

            }
        });
    }


    private void onClickedSelectedJobs() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.common_list, null);
        alertDialog.setView(dialogView);

        TextView textView_header = (TextView) dialogView.findViewById(R.id.textView_custom_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select Job Location");
        }
        Button btnCancel = (Button) dialogView.findViewById(R.id.button_cancel);
        listView = (ListView) dialogView.findViewById(R.id.listView_cate);
        final AlertDialog ad = alertDialog.create();
        ad.show();

        ImageView imageView = dialogView.findViewById(R.id.imageViewBackArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });
        jobsArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, job_location);
        listView.setAdapter(jobsArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stringText;
                //in normal case
                stringText = ((TextView) view).getText().toString();
                btnSelectJobs.setText(stringText);
                if (stringText.equals("Customer Place")) {
                    selectedJobs = "0";
                } else {
                    selectedJobs = "1";
                }
                ad.dismiss();
                if (stringText != null) {
                    //selectedChildCate = true;
                } else {
                    Toast.makeText(getActivity(), "Please Select Parent Category...!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                            serivcesList = new ArrayList<Services>();
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

                                        serivcesList.add(services);
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

}

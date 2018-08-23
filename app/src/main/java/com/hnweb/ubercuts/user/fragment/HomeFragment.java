package com.hnweb.ubercuts.user.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.SliderLayout;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.user.activity.UserRegistrationActivityStepThree;
import com.hnweb.ubercuts.user.adaptor.NewServicesAdaptor;
import com.hnweb.ubercuts.user.adaptor.ServiceAdaptor;
import com.hnweb.ubercuts.user.adaptor.SlidingPagerAdapter;
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.LoadingDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment implements View.OnClickListener, OnCallBack {
    private SliderLayout mDemoSlider;
    SlidingPagerAdapter sliderPagerAdapter;
    private ViewPager vp_slider;
    private LinearLayout ll_dots, linearLayoutNails, linearLayoutHairs;
    ArrayList<Integer> slider_image_list;
    private TextView[] dots;
    int page_position = 0;
    ArrayList<Services> stockList;
    LoadingDialog loadingDialog;
    ListView listView;
    Button btn_proceed;
    ServiceAdaptor adapter;
    Services services;
    String serviceId;
    OnCallBack onCallBack;
    SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);

        onCallBack = this;
        initViewById(view);


        addBottomDots(0);

       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Services services = (Services) adapter.getItem(position);
                serviceId = services.getId();
            }
        });*/
// Attach the adapter to a ListView

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == slider_image_list.size()) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                vp_slider.setCurrentItem(page_position, true);
            }
        };

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 5000);

        return view;
    }

    private void initViewById(View view) {

        vp_slider = view.findViewById(R.id.vp_slider);
        ll_dots = view.findViewById(R.id.ll_dots);
        listView = (ListView) view.findViewById(R.id.listview_services);
        loadingDialog = new LoadingDialog(getActivity());

        btn_proceed = view.findViewById(R.id.btn_proceed);
        slider_image_list = new ArrayList<>();

        //Add few items to slider_image_list ,this should contain url of images which should be displayed in slider
        // here i am adding few sample image links, you can add your own

        slider_image_list.add(R.drawable.slider);
        slider_image_list.add(R.drawable.slider_two);
        slider_image_list.add(R.drawable.slider_three);
        slider_image_list.add(R.drawable.slider_four);

        sliderPagerAdapter = new SlidingPagerAdapter(getActivity(), slider_image_list);
        vp_slider.setAdapter(sliderPagerAdapter);

        vp_slider.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getServices();

        btn_proceed.setOnClickListener(this);

    }

    private void addBottomDots(int currentPage) {

        try {
            dots = new TextView[slider_image_list.size()];
            ll_dots.removeAllViews();
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(getActivity());
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(Color.parseColor("#20C0F4"));
                ll_dots.addView(dots[i]);
            }

            if (dots.length > 0)
                dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.btn_proceed:

                //fragment = new NailsFragment();

                if (serviceId == null) {
                    Toast.makeText(getActivity(), "Please Select the Service " + serviceId, Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("SubcategoryId", serviceId);
                    editor.commit();
                    fragment = new NailsFragment();
                    changeFragment(fragment);
                }

                break;

            default:
                break;

        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
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
                            stockList = new ArrayList<Services>();
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

                                        stockList.add(services);
                                        adapter = new ServiceAdaptor(getActivity(), stockList, onCallBack);
                                        listView.setAdapter(adapter);
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

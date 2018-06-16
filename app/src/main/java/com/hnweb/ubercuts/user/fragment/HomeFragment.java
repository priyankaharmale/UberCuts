package com.hnweb.ubercuts.user.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.user.activity.HomeActivity;
import com.hnweb.ubercuts.user.adaptor.ServicesAdaptor;
import com.hnweb.ubercuts.user.adaptor.SlidingPagerAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment implements  View.OnClickListener{
    private SliderLayout mDemoSlider;
    SlidingPagerAdapter sliderPagerAdapter;
    private ViewPager vp_slider;
    private LinearLayout ll_dots, linearLayoutNails, linearLayoutHairs;
    ArrayList<Integer> slider_image_list;
    private TextView[] dots;
    int page_position = 0;
    ArrayList<String> stockList = new ArrayList<String>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViewById(view);
        addBottomDots(0);
        stockList.add("Manicure");
        stockList.add("Pedicure");
        stockList.add("Gel Nails");
        stockList.add("Acrylic Nails");
        stockList.add("Acrylic Filling");
        stockList.add("Nail Art");
        stockList.add("Nail Extension");


        ServicesAdaptor adapter = new ServicesAdaptor(getActivity(), stockList);
// Attach the adapter to a ListView
        ListView listView = (ListView) view.findViewById(R.id.listview_services);
        listView.setAdapter(adapter);
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


            default:
                break;

        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
    }
}

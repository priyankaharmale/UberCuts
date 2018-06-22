package com.hnweb.ubercuts.user.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.user.adaptor.SlidingPagerAdapter;
import com.hnweb.ubercuts.user.bo.Services;
import com.hnweb.ubercuts.utils.LoadingDialog;

import java.util.ArrayList;

public class NailsFragment extends Fragment{
    private SliderLayout mDemoSlider;
    SlidingPagerAdapter sliderPagerAdapter;
    private ViewPager vp_slider;
    private LinearLayout ll_dots, linearLayoutNails, linearLayoutHairs;
    ArrayList<Integer> slider_image_list;
    private TextView[] dots;
    int page_position = 0;
    ArrayList<Services> stockList = new ArrayList<Services>();
    LoadingDialog loadingDialog;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nails_map, container, false);
        return view;
    }

}

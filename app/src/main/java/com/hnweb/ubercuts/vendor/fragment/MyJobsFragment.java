package com.hnweb.ubercuts.vendor.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.hnweb.ubercuts.R;

import java.util.ArrayList;
import java.util.List;

public class MyJobsFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public TextView textViewListCount;
    ViewPagerAdapter adapter;
    int position;
    Toolbar toolbar;

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);

        //toolbar = ((MainActivityVendor) getActivity()).toolbar;
        //toolbar.setTitle("My Jobs");

        initViewById(view);

        return view;
    }

    private void initViewById(View view) {

        viewPager = view.findViewById(R.id.viewpager_my_jobs);
        setupViewPager(viewPager);

        tabLayout = view.findViewById(R.id.tabs_my_jobs);
        tabLayout.setupWithViewPager(viewPager);

        textViewListCount = view.findViewById(R.id.textView_list_count);
        tabLayout.setOnTabSelectedListener(this);

    }

    private void setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        //adapter.addFragment(new NotBookedMyJobsFragment(), "NOT BOOKED");
        adapter.addFragment(new MyJobsBookedFragment(), "BOOKED");
        adapter.addFragment(new MyJobsCompletedFragment(), "COMPLETED");
        adapter.addFragment(new MyJobsCancelledFragment(), "CANCELLED");
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(limit);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        try {
            viewPager.setCurrentItem(tab.getPosition());
            position = tab.getPosition();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}

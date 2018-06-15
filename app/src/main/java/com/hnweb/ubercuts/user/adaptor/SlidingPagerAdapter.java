package com.hnweb.ubercuts.user.adaptor;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.R;

import java.util.ArrayList;

/**
 * Created by PC-21 on 03-Apr-18.
 */

public class SlidingPagerAdapter extends PagerAdapter {
    private LayoutInflater layoutInflater;
    Activity activity;
    ArrayList<Integer> image_arraylist;

    public SlidingPagerAdapter(Activity activity, ArrayList<Integer> image_arraylist) {
        this.activity = activity;
        this.image_arraylist = image_arraylist;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.layout_slider, container, false);
        ImageView im_slider = (ImageView) view.findViewById(R.id.im_slider);

        /*Picasso.with(activity.getApplicationContext())
                .load(image_arraylist.get(position))
                .placeholder(R.mipmap.ic_launcher) // optional
                .error(R.mipmap.ic_launcher)         // optional
                .into(im_slider);*/

        Glide.with(activity).load(image_arraylist.get(position)).into(im_slider);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return image_arraylist.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}

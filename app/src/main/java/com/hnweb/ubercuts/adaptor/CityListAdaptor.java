package com.hnweb.ubercuts.adaptor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.bo.City;
import com.hnweb.ubercuts.bo.Country;

import java.util.ArrayList;

/**
 * Created by Priyanka H on 13/06/2018.
 */

public class CityListAdaptor extends RecyclerView.Adapter<CityListAdaptor.MyViewHolder> {
    private ArrayList<City> cities;
    Context context;
    Drawable drawable;

    public CityListAdaptor(ArrayList<City> cities, Context context) {

        this.cities = cities;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_city;


        public MyViewHolder(View view) {
            super(view);
            tv_city = (TextView) view.findViewById(R.id.tv_year);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_yearlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final City city = cities.get(position);
        holder.tv_city.setText(city.getCityName());
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }


    public void filterList(ArrayList<City> filterdNames) {
        this.cities = filterdNames;
        notifyDataSetChanged();
    }

}

package com.hnweb.ubercuts.adaptor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.bo.Country;
import com.hnweb.ubercuts.interfaces.OnCallBack;

import java.util.ArrayList;

/**
 * Created by Priyanka H on 13/06/2018.
 */

public class CountryListAdaptor extends RecyclerView.Adapter<CountryListAdaptor.MyViewHolder> {
    private ArrayList<Country> countries;
    Context context;
    OnCallBack onCallBack;
    Dialog dialog;


    public CountryListAdaptor(ArrayList<Country> countries, Context context, OnCallBack onCallBack, Dialog dialog) {

        this.countries = countries;
        this.context = context;
        this.onCallBack = onCallBack;
        this.dialog = dialog;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_country;
        public RelativeLayout rl_list;

        public MyViewHolder(View view) {
            super(view);
            tv_country = (TextView) view.findViewById(R.id.tv_year);
            rl_list = (RelativeLayout) view.findViewById(R.id.rl_list);

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

        final Country country = countries.get(position);
        holder.tv_country.setText(country.getCountyName());

        holder.rl_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchcountry(country.getId(), country.getCountyName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }


    public void filterList(ArrayList<Country> filterdNames) {
        this.countries = filterdNames;
        notifyDataSetChanged();
    }

    private void fetchcountry(String id, String name) {
        onCallBack.callbackList(id, name);
        dialog.dismiss();

    }

}

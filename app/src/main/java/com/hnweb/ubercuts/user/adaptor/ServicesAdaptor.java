package com.hnweb.ubercuts.user.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.user.bo.City;
import com.hnweb.ubercuts.user.bo.Services;

import java.util.ArrayList;

public class ServicesAdaptor extends BaseAdapter {

    private Context context;
    private ArrayList<Services> version;
    private LayoutInflater inflater;
    private RadioButton selected = null;
    private int selectedPosition = -1;

    public ServicesAdaptor(Context context, ArrayList<Services> version) {
        this.context = context;
        this.version = version;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return version.size();
    }

    @Override
    public Object getItem(int position) {
        return version;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        final Holder holder;
        if (view == null) {
            holder = new Holder();
            view = inflater.inflate(R.layout.adaptor_services, null);

            holder.radioButton = (RadioButton) view.findViewById(R.id.radio1);
            holder.textViewVersion = (TextView) view.findViewById(R.id.textView1);
            holder.ll_main=(LinearLayout) view.findViewById(R.id.ll_main);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        final Services services = version.get(position);

        holder.textViewVersion.setText(services.getServicesName());

        //by default last radio button selected
       /* if (position == getCount() - 1) {
            if (selected == null) {
                holder.radioButton.setChecked(true);
                selected = holder.radioButton;
            }
        }*/

      /*  holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/




        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
                if(position == 0) {

                    holder.radioButton.setChecked(position==getCount()-1);
                    holder.radioButton.setButtonDrawable(R.drawable.radio_button_selected);
                    holder.radioButton.setChecked(true);
                }
                else{
                    holder.radioButton.setButtonDrawable(R.drawable.radio_button_unselected);
                    holder.radioButton.setChecked(position == position);
                    holder.radioButton.setChecked(false);
                }

               /* holder.radioButton.setChecked(true);
                selected = holder.radioButton;*/
            }
        });
        return view;
    }

    private class Holder {
        private RadioButton radioButton;
        private TextView textViewVersion;
        LinearLayout ll_main;
    }
}
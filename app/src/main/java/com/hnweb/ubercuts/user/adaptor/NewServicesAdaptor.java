package com.hnweb.ubercuts.user.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.user.bo.Services;

import java.util.ArrayList;

public class NewServicesAdaptor extends BaseAdapter {
    private Context context;
    private ArrayList<Services> arrayList;
    private LayoutInflater inflater;
    private boolean isListView;
    Services services;
    OnCallBack onCallBack;
    TextView tv_price;
    private int selectedPosition = -1;

    public NewServicesAdaptor(Context context, ArrayList<Services> arrayList, OnCallBack onCallBack) {
        this.context = context;
        this.arrayList = arrayList;
        this.onCallBack = onCallBack;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();

            //inflate the layout on basis of boolean
            view = inflater.inflate(R.layout.adaptor_serviceswithprice, viewGroup, false);

            viewHolder.radioButton = (RadioButton) view.findViewById(R.id.radio1);
            viewHolder.tv_price=(TextView) view.findViewById(R.id.tv_price);
            //viewHolder.ll_main= (LinearLayout) view.findViewById(R.id.ll_main);
            viewHolder.label = (TextView) view.findViewById(R.id.textView1);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        services = arrayList.get(i);

        viewHolder.label.setText(services.getServicesName());
        viewHolder.tv_price.setText(services.getDefault_price());

        //check the radio button if both position and selectedPosition matches
        viewHolder.radioButton.setChecked(i == selectedPosition);

        //Set the position tag to both radio button and label
        viewHolder.radioButton.setTag(i);
        viewHolder.label.setTag(i);
        viewHolder.tv_price.setTag(i);

        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  viewHolder.radioButton.setButtonDrawable(R.drawable.radio_button_selected);
                itemCheckChanged(v, viewHolder, arrayList.get(i).getId(),arrayList.get(i).getDefault_price());
            }
        });

        viewHolder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCheckChanged(v, viewHolder, arrayList.get(i).getId(),arrayList.get(i).getDefault_price());
            }


        });
        return view;
    }

    //On selecting any view set the current position to selectedPositon and notify adapter
    private void itemCheckChanged(View v, ViewHolder viewHolder, String id,String default_price) {
        selectedPosition = (Integer) v.getTag();
        //  viewHolder.radioButton.setButtonDrawable(R.drawable.radio_button_unselected);
        fetchcount(id,default_price);

        notifyDataSetChanged();
    }

    private class ViewHolder {
        private TextView label,tv_price;
        private RadioButton radioButton;

    }

    //Delete the selected position from the arrayList
    public void deleteSelectedPosition() {
        if (selectedPosition != -1) {
            arrayList.remove(selectedPosition);
            selectedPosition = -1;//after removing selectedPosition set it back to -1
            notifyDataSetChanged();
        }
    }

    private void fetchcount(String seviceId,String price) {
        onCallBack.callcityList(seviceId,price);
    }
}
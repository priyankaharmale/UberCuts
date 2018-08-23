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

public class ServiceAdaptor extends BaseAdapter {




        private Context context;
        private ArrayList<Services> arrayList;
        private LayoutInflater inflater;
        private boolean isListView;
        Services services;
        OnCallBack onCallBack;
        private int selectedPosition = -1;

        public ServiceAdaptor(Context context, ArrayList<Services> arrayList, OnCallBack onCallBack) {
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
            final ServiceAdaptor.ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ServiceAdaptor.ViewHolder();

                //inflate the layout on basis of boolean
                view = inflater.inflate(R.layout.adaptor_services, viewGroup, false);

                viewHolder.radioButton = (RadioButton) view.findViewById(R.id.radio1);
                //viewHolder.ll_main= (LinearLayout) view.findViewById(R.id.ll_main);
                viewHolder.label = (TextView) view.findViewById(R.id.textView1);

                view.setTag(viewHolder);
            } else
                viewHolder = (ServiceAdaptor.ViewHolder) view.getTag();
            services = arrayList.get(i);

            viewHolder.label.setText(services.getServicesName());

            //check the radio button if both position and selectedPosition matches
            viewHolder.radioButton.setChecked(i == selectedPosition);

            //Set the position tag to both radio button and label
            viewHolder.radioButton.setTag(i);
            viewHolder.label.setTag(i);

            viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  viewHolder.radioButton.setButtonDrawable(R.drawable.radio_button_selected);
                    itemCheckChanged(v, viewHolder, arrayList.get(i).getId());
                }
            });

            viewHolder.label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCheckChanged(v, viewHolder, arrayList.get(i).getId());
                }


            });
            return view;
        }

        //On selecting any view set the current position to selectedPositon and notify adapter
        private void itemCheckChanged(View v, ServiceAdaptor.ViewHolder viewHolder, String id) {
            selectedPosition = (Integer) v.getTag();
            //  viewHolder.radioButton.setButtonDrawable(R.drawable.radio_button_unselected);
            fetchcount(id);

            notifyDataSetChanged();
        }

        private class ViewHolder {
            private TextView label;
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

        private void fetchcount(String count) {
            onCallBack.callback(count);
        }
    }

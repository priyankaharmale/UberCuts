package com.hnweb.ubercuts.user.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hnweb.ubercuts.R;

import java.util.ArrayList;

public class ServicesAdaptor extends BaseAdapter {

    private Context context;
    private ArrayList<String> version;
    private LayoutInflater inflater;
    private RadioButton selected = null;

    public ServicesAdaptor(Context context, ArrayList<String> version) {
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

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.textViewVersion.setText(version.get(position));

        //by default last radio button selected
        if (position == getCount() - 1) {
            if (selected == null) {
                holder.radioButton.setChecked(true);
                selected = holder.radioButton;
            }
        }

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selected != null) {
                    selected.setChecked(false);
                    }

                if(holder.radioButton.isChecked())
                {
                    holder.radioButton.setButtonDrawable(R.drawable.radio_button_selected);

                }
                else
                {

                    holder.radioButton.setButtonDrawable(R.drawable.radio_button_unselected);

                    }

                holder.radioButton.setChecked(true);
                selected = holder.radioButton;
            }
        });
        return view;
    }

    private class Holder {
        private RadioButton radioButton;
        private TextView textViewVersion;
    }
}
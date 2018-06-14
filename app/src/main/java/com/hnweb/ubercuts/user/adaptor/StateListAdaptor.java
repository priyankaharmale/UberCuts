package com.hnweb.ubercuts.user.adaptor;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.interfaces.OnCallBack;
import com.hnweb.ubercuts.user.bo.State;

import java.util.ArrayList;

/**
 * Created by Priyanka H on 13/06/2018.
 */

public class StateListAdaptor extends RecyclerView.Adapter<StateListAdaptor.MyViewHolder> {
    private ArrayList<State> states;
    Context context;
    OnCallBack onCallBack;
    Dialog dialog;

    public StateListAdaptor(ArrayList<State> states, Context contex, OnCallBack onCallBack, Dialog dialog) {

        this.states = states;
        this.context = context;
        this.onCallBack = onCallBack;
        this.dialog = dialog;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_state;

        public MyViewHolder(View view) {
            super(view);
            tv_state = (TextView) view.findViewById(R.id.tv_year);
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

        final State state = states.get(position);
        holder.tv_state.setText(state.getStateName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchcountry(state.getId(), state.getStateName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    public void filterList(ArrayList<State> filterdNames) {
        this.states = filterdNames;
        notifyDataSetChanged();
    }

    private void fetchcountry(String id, String name) {
        onCallBack.callstateList(id, name);
        dialog.dismiss();

    }
}

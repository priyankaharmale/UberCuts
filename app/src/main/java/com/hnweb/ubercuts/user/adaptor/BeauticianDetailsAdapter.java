package com.hnweb.ubercuts.user.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.user.activity.BeauticianDetailsActivity;
import com.hnweb.ubercuts.user.bo.Details;

import java.util.List;

/**
 * Created by Priyanka on 26-june-18.
 */

public class BeauticianDetailsAdapter extends RecyclerView.Adapter<BeauticianDetailsAdapter.ViewHolder> {

    private Context context;
    private List<Details> detailsList;
    private LayoutInflater inflater;


    public BeauticianDetailsAdapter(Context context, List<Details> data) {
        this.context = context;
        this.detailsList = data;
        this.inflater = LayoutInflater.from(context);
        //this.imageLoader = MainApplication.getInstance().getImageLoader();
    }

    @Override
    public BeauticianDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_vendor_list, parent, false);
        BeauticianDetailsAdapter.ViewHolder vh = new BeauticianDetailsAdapter.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final BeauticianDetailsAdapter.ViewHolder holder, final int i) {
        final Details details = detailsList.get(i);

        Log.e("Data", details.toString());
        String user_name = detailsList.get(i).getUName();
        String todays_offer = detailsList.get(i).getTodaysOffer();
        String sub_cat_name = detailsList.get(i).getSubCategoryName();
        String experince = detailsList.get(i).getExperience();
        String avail_time_from = detailsList.get(i).getAvailableFrom();
        String avail_time_to = detailsList.get(i).getAvailableTo();
        String user_image = detailsList.get(i).getUImg();


        if (user_name.equals("")) {
            holder.textViewName.setText("No Name");
        } else {
            holder.textViewName.setText(detailsList.get(i).getUName());
        }

        if (todays_offer.equals("")) {
            holder.textViewOffer.setText(" - ");
        } else {
            holder.textViewOffer.setText(detailsList.get(i).getTodaysOffer() + "%" + " OFF");
        }

        if (experince.equals("")) {
            holder.textViewExperience.setText(" - ");
        } else {
            holder.textViewExperience.setText(detailsList.get(i).getExperience() + " " + "Years");
        }

        if (user_image.equals("")) {
            Glide.with(context).load(R.drawable.user_register).into(holder.iv_profile);
        } else {
            Glide.with(context).load(user_image).into(holder.iv_profile);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get position
                        // check if item still exists
                        String user_details_ids = details.getUId();
                        String user_category_id = details.getCategoryId();
                        String user_sub_category_id = details.getSubCategoryId();

                        Intent intent = new Intent(context, BeauticianDetailsActivity.class);
                        intent.putExtra("user_details_ids", user_details_ids);
                        context.startActivity(intent);

                        //Toast.makeText(v.getContext(), "You clicked " + details.getUId(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewOffer, textViewExperience;
        ImageView iv_profile;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.tv_vendroName);
            textViewOffer = itemView.findViewById(R.id.tv_offer);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            textViewExperience = itemView.findViewById(R.id.tv_experience);
            // textViewTime = itemView.findViewById(R.id.textView_ava_time);

        }
    }


}


package com.hnweb.ubercuts.vendor.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.vendor.activity.LeadsDetailsActivity;
import com.hnweb.ubercuts.vendor.bo.LeadsModel;

import java.util.ArrayList;
import java.util.List;

public class LeadsListAdapter extends RecyclerView.Adapter<LeadsListAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<LeadsModel> leadsModelList;
    private LayoutInflater inflater;
    private List<LeadsModel> mFilteredList;
    public MyFilter mFilter;

    AdapterCallback adapterCallback;

    public LeadsListAdapter(Context context, List<LeadsModel> data, AdapterCallback adapterCallback) {
        this.context = context;
        this.leadsModelList = data;
        this.inflater = LayoutInflater.from(context);
        this.adapterCallback = adapterCallback;
        //this.imageLoader = MainApplication.getInstance().getImageLoader();
        this.mFilteredList = new ArrayList<LeadsModel>();
    }

    @Override
    public LeadsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_leads_list, parent, false);
        LeadsListAdapter.ViewHolder vh = new LeadsListAdapter.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final LeadsListAdapter.ViewHolder holder, final int i) {
        final LeadsModel details = leadsModelList.get(i);

        Log.e("Data", details.toString());
        String leads_name = leadsModelList.get(i).getSub_category_name();
        String leads_price = leadsModelList.get(i).getMy_task_price();
        String job_location = leadsModelList.get(i).getMy_task_job_location();
        String user_name = leadsModelList.get(i).getU_name();
        String user_image = leadsModelList.get(i).getU_img();

        if (leads_name.equals("")) {
            holder.textViewNameLeads.setText("No Name");
        } else {
            holder.textViewNameLeads.setText(leadsModelList.get(i).getSub_category_name());
        }


        if (leads_price.equals("")) {
            holder.textViewOfferLeads.setText(" - ");
        } else {
            holder.textViewOfferLeads.setText("$ " + leadsModelList.get(i).getMy_task_price());
        }

        if (job_location.equals("")) {
            holder.textViewJobLocation.setText(" - ");
        } else {
            if (job_location.equals("1")) {
                holder.textViewJobLocation.setText("Customer Place");
            } else {
                holder.textViewJobLocation.setText("Vendor Place");
            }
            }

        if (user_name.equals("")) {
            holder.textViewCustomerName.setText(" - ");
        } else {
            holder.textViewCustomerName.setText(leadsModelList.get(i).getU_name());
        }
        if (user_image.equals("")) {
            Glide.with(context).load(R.drawable.user_register).into(holder.iv_vendorimage);
        } else {
            Glide.with(context).load(user_image).into(holder.iv_vendorimage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get position
                        // check if item still exists
                        String vendor_lead_id = details.getMy_task_id();
                        Intent intent = new Intent(context, LeadsDetailsActivity.class);
                        intent.putExtra("VendorLeadIds", vendor_lead_id);
                        context.startActivity(intent);

                        //Toast.makeText(v.getContext(), "You clicked " + details.getUId(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    @Override
    public int getItemCount() {
        return leadsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNameLeads, textViewOfferLeads, textViewCustomerName, textViewJobLocation;
        ImageView iv_vendorimage;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewNameLeads = itemView.findViewById(R.id.tv_serviceName);
            textViewOfferLeads = itemView.findViewById(R.id.tv_default);

            textViewCustomerName = itemView.findViewById(R.id.tv_customerName);
            textViewJobLocation = itemView.findViewById(R.id.tv_location);
            iv_vendorimage=itemView.findViewById(R.id.iv_vendorimage);

        }
    }

    @Override
    public Filter getFilter() {

        if (mFilter == null) {
            mFilteredList.clear();
            mFilteredList.addAll(this.leadsModelList);
            mFilter = new MyFilter(this, mFilteredList);
        }
        return mFilter;

    }

    private static class MyFilter extends Filter {

        private final LeadsListAdapter myAdapter;
        private final List<LeadsModel> originalList;
        private final List<LeadsModel> filteredList;

        private MyFilter(LeadsListAdapter myAdapter, List<LeadsModel> originalList) {
            this.myAdapter = myAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<LeadsModel>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (LeadsModel leadsModel : originalList) {
                    if (leadsModel.getCategory_name().toLowerCase().contains(filterPattern) || leadsModel.getSub_category_name().toLowerCase().contains(filterPattern) || leadsModel.getMy_task_price().toLowerCase().contains(filterPattern)
                            || leadsModel.getU_name().contains(filterPattern)) {
                        filteredList.add(leadsModel);
                        Log.d("FilterData", filteredList.toString());
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            myAdapter.leadsModelList.clear();
            myAdapter.leadsModelList.addAll((ArrayList<LeadsModel>) filterResults.values);
            myAdapter.notifyDataSetChanged();

        }
    }


}



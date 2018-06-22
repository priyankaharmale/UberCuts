package com.hnweb.ubercuts.vendor.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.vendor.bo.LeadsModel;

import java.util.List;

public class LeadsListAdapter extends RecyclerView.Adapter<LeadsListAdapter.ViewHolder> {

    private Context context;
    private List<LeadsModel> leadsModelList;
    private LayoutInflater inflater;


    public LeadsListAdapter(Context context, List<LeadsModel> data) {
        this.context = context;
        this.leadsModelList = data;
        this.inflater = LayoutInflater.from(context);
        //this.imageLoader = MainApplication.getInstance().getImageLoader();
    }

    @Override
    public LeadsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adapotr_ledads_list, parent, false);
        LeadsListAdapter.ViewHolder vh = new LeadsListAdapter.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final LeadsListAdapter.ViewHolder holder, final int i) {
        final LeadsModel details = leadsModelList.get(i);

        Log.e("Data", details.toString());
        String leads_name = leadsModelList.get(i).getCategory_name();
        String leads_price = leadsModelList.get(i).getMy_task_price();
        String job_location = leadsModelList.get(i).getMy_task_job_location();
        String user_name = leadsModelList.get(i).getU_name();

        if (leads_name.equals("")) {
            holder.textViewNameLeads.setText("No Name");
        } else {
            holder.textViewNameLeads.setText(leadsModelList.get(i).getCategory_name());
        }

        if (leads_price.equals("")) {
            holder.textViewOfferLeads.setText(" - ");
        } else {
            holder.textViewOfferLeads.setText("$ "+leadsModelList.get(i).getMy_task_price());
        }

        if (job_location.equals("")) {
            holder.textViewJobLocation.setText(" - ");
        } else {
            if (job_location.equals("1")){
                holder.textViewJobLocation.setText("Customer Place");
            }else {
                holder.textViewJobLocation.setText("Vendor Place");
            }

        }

        if (user_name.equals("")) {
            holder.textViewCustomerName.setText(" - ");
        } else {
            holder.textViewCustomerName.setText(leadsModelList.get(i).getU_name());
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

                        /*Intent intent = new Intent(context, LeadsDetailsActivity.class);
                        intent.putExtra("VendorLeadIds", vendor_lead_id);
                        context.startActivity(intent);*/

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

        public ViewHolder(View itemView) {
            super(itemView);

            textViewNameLeads = itemView.findViewById(R.id.textView_service_name_leads);
            textViewOfferLeads = itemView.findViewById(R.id.textView_offer_price_leads);

            textViewCustomerName = itemView.findViewById(R.id.textView_customer_name_leads);
            textViewJobLocation = itemView.findViewById(R.id.textView_job_location_leads);

        }
    }
}



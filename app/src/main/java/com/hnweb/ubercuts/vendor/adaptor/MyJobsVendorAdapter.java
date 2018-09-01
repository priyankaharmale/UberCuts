package com.hnweb.ubercuts.vendor.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
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
import com.hnweb.ubercuts.user.bo.MyTaskModel;
import com.hnweb.ubercuts.vendor.activity.MyJobsBookedDetailsActivity;
import com.hnweb.ubercuts.vendor.activity.MyJobsCancelledDetailsActivity;
import com.hnweb.ubercuts.vendor.activity.MyJobsCompletedDetailsActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyJobsVendorAdapter extends RecyclerView.Adapter<MyJobsVendorAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<MyTaskModel> myTaskModels;
    private LayoutInflater inflater;
    AdapterCallback adapterCallback;
    public MyFilter mFilter;
    private List<MyTaskModel> mFilteredList;

    public MyJobsVendorAdapter(FragmentActivity activity, ArrayList<MyTaskModel> myTaskModels, AdapterCallback adapterCallback) {

        this.context = activity;
        this.myTaskModels = myTaskModels;
        this.inflater = LayoutInflater.from(context);
        this.adapterCallback = adapterCallback;
        this.mFilteredList = new ArrayList<MyTaskModel>();

    }

    @Override
    public MyJobsVendorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_my_jobs_vendor, parent, false);
        MyJobsVendorAdapter.ViewHolder vh = new MyJobsVendorAdapter.ViewHolder(rowView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyJobsVendorAdapter.ViewHolder holder, final int i) {
        final MyTaskModel myTaskModel = myTaskModels.get(i);
        String user_image = myTaskModel.getU_image();
        holder.tvCategoryName.setText(myTaskModel.getSub_category_name());


        holder.textViewCustomerPlace.setText(myTaskModel.getJob_location_name());
        holder.tvCustomerName.setText(myTaskModel.getVendor_name());

        String date = myTaskModel.getDate();
        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date_format = null;
        try {
            date_format = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String date_formats = outputFormat.format(date_format);
        Log.d("DateFormatPay", date_formats);

        holder.tvTaskDate.setText(date_formats);
        if (user_image.equals("")) {
            Glide.with(context).load(R.drawable.user_register).into(holder.iv_customerimage);
        } else {
            Glide.with(context).load(user_image).into(holder.iv_customerimage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get position
                // check if item still exists
                String mytask_id = myTaskModel.getMy_task_id();
                String my_status = myTaskModel.getStatus();

                if (my_status.equals("1")) {
                    Intent intent = new Intent(context, MyJobsBookedDetailsActivity.class);
                    intent.putExtra("my_task_id", mytask_id);
                    //intent.putExtra("AdapterCallBack", (Serializable) adapterCallback);
                    context.startActivity(intent);
                } else if (my_status.equals("2")) {
                    Intent intent = new Intent(context, MyJobsCompletedDetailsActivity.class);
                    intent.putExtra("my_task_id", mytask_id);
                    context.startActivity(intent);
                    // Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show();
                } else if (my_status.equals("3")) {
                    Intent intent = new Intent(context, MyJobsCancelledDetailsActivity.class);
                    intent.putExtra("my_task_id", mytask_id);
                    context.startActivity(intent);
                    //Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(v.getContext(), "You clicked " + myTaskModel.getMy_task_id(), Toast.LENGTH_SHORT).show();
            }
        });

        Log.e("Data", myTaskModel.toString());
    }


    @Override
    public int getItemCount() {
        return myTaskModels.size();
    }

    @Override
    public Filter getFilter() {

        if (mFilter == null) {
            mFilteredList.clear();
            mFilteredList.addAll(this.myTaskModels);
            mFilter = new MyFilter(this, mFilteredList);
        }
        return mFilter;

    }

    private static class MyFilter extends Filter {

        private final MyJobsVendorAdapter myAdapter;
        private final List<MyTaskModel> originalList;
        private final List<MyTaskModel> filteredList;

        private MyFilter(MyJobsVendorAdapter myAdapter, List<MyTaskModel> originalList) {
            this.myAdapter = myAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<MyTaskModel>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (MyTaskModel myTaskModel : originalList) {
                   /* if (user.getUName().toLowerCase().contains(filterPattern) || user.getSubCategoryName().contains(filterPattern)|| user.getExperience().contains(filterPattern)){
                        filteredList.add(user);
                    }*/
                    if (myTaskModel.getCategory_name().toLowerCase().contains(filterPattern) || myTaskModel.getSub_category_name().toLowerCase().contains(filterPattern) || myTaskModel.getDate().toString().contains(filterPattern) ||
                            myTaskModel.getJob_location_name().toString().contains(filterPattern)) {
                        filteredList.add(myTaskModel);
                        Log.d("FilterData", filterPattern + " :: " + filteredList + " :: " + myTaskModel.getSub_category_name() + ":: " + myTaskModel.getDate());
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            myAdapter.myTaskModels.clear();
            myAdapter.myTaskModels.addAll((ArrayList<MyTaskModel>) filterResults.values);
            myAdapter.notifyDataSetChanged();

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCustomerName, tvCategoryName, tvTaskDate, textViewCustomerPlace;
        private final ImageView iv_customerimage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCustomerName = itemView.findViewById(R.id.tv_customerName);
            tvCategoryName = itemView.findViewById(R.id.tv_serviceName);

            tvTaskDate = itemView.findViewById(R.id.tv_date);
            textViewCustomerPlace = itemView.findViewById(R.id.tv_location);
            iv_customerimage = itemView.findViewById(R.id.iv_vendorimage);
        }
    }
}


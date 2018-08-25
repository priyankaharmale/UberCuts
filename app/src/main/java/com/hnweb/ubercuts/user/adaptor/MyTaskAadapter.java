package com.hnweb.ubercuts.user.adaptor;

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
import android.widget.TextView;


import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.user.bo.MyTaskModel;

import java.util.ArrayList;
import java.util.List;

public class MyTaskAadapter extends RecyclerView.Adapter<MyTaskAadapter.ViewHolder> implements Filterable {

    private Context context;
    private List<MyTaskModel> myTaskModels;
    private LayoutInflater inflater;
    public MyFilter mFilter;
    private List<MyTaskModel> mFilteredList;

    public MyTaskAadapter(FragmentActivity activity, ArrayList<MyTaskModel> myTaskModels) {

        this.context = activity;
        this.myTaskModels = myTaskModels;
        this.inflater = LayoutInflater.from(context);
        this.mFilteredList = new ArrayList<MyTaskModel>();

    }

    @Override
    public MyTaskAadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_payment_history, parent, false);
        MyTaskAadapter.ViewHolder vh = new MyTaskAadapter.ViewHolder(rowView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyTaskAadapter.ViewHolder holder, final int i) {
        final MyTaskModel myTaskModel = myTaskModels.get(i);

        //      holder.tvCategoryName.setText(myTaskModel.getCategory_name());

        holder.tvBeauticianName.setText(myTaskModel.getBeautician());

        holder.tvTaskDate.setText(myTaskModel.getDate());

        holder.tvCategoryName.setText(myTaskModel.getSub_category_name());

        holder.tv_time.setText(myTaskModel.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get position
                // check if item still exists
                String mytask_id = myTaskModel.getMy_task_id();
                String my_status = myTaskModel.getStatus();

               /* if (my_status.equals("0")) {
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    intent.putExtra("my_task_id", mytask_id);
                    context.startActivity(intent);
                } else if (my_status.equals("1")) {
                    String my_beuatician_id = myTaskModel.getBeautician_id();
                    Intent intent = new Intent(context, BookedDetailsActivity.class);
                    intent.putExtra("my_task_id", mytask_id);
                    intent.putExtra("beautician_id", my_beuatician_id);
                    context.startActivity(intent);
                } else if (my_status.equals("2")) {
                    Intent intent = new Intent(context, CompletedDetailsActivity.class);
                    intent.putExtra("my_task_id", mytask_id);
                    context.startActivity(intent);
                    // Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show();
                } else if (my_status.equals("3")) {
                    Intent intent = new Intent(context, CancelledDetailsActivity.class);
                    intent.putExtra("my_task_id", mytask_id);
                    context.startActivity(intent);
                    //Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show();
                }*/
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
            mFilter = new MyFilter(MyTaskAadapter.this, mFilteredList);
        }
        return mFilter;

    }

    private static class MyFilter extends Filter {

        private final MyTaskAadapter myAdapter;
        private final List<MyTaskModel> originalList;
        private final List<MyTaskModel> filteredList;

        private MyFilter(MyTaskAadapter myAdapter, List<MyTaskModel> originalList) {
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
                    if (myTaskModel.getCategory_name().toLowerCase().contains(filterPattern) || myTaskModel.getSub_category_name().toLowerCase().contains(filterPattern) || myTaskModel.getDate().contains(filterPattern)) {
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

        private final TextView tvCategoryName, tvBeauticianName, tvTaskDate, tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_serviceName);
            tvBeauticianName = itemView.findViewById(R.id.tv_vendroName);
            tvTaskDate = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);

        }
    }
}

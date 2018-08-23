package com.hnweb.ubercuts.user.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.user.bo.TodaysOfferModel;

import java.util.ArrayList;
import java.util.List;

public class TodaysOfferAadapter extends RecyclerView.Adapter<TodaysOfferAadapter.ViewHolder> implements Filterable{

    private Context context;
    private List<TodaysOfferModel> todaysOfferModels;
    private LayoutInflater inflater;
    AdapterCallback mAdapterCallback;
    public MyFilter mFilter;
    private List<TodaysOfferModel> mFilteredList;

    public TodaysOfferAadapter(FragmentActivity activity, ArrayList<TodaysOfferModel> todaysOfferModels) {

        this.context = activity;
        this.todaysOfferModels = todaysOfferModels;
        this.inflater = LayoutInflater.from(context);
        this.mFilteredList = new ArrayList<TodaysOfferModel>();

    }

    @Override
    public TodaysOfferAadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_todays_offers, parent, false);
        TodaysOfferAadapter.ViewHolder vh = new TodaysOfferAadapter.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final TodaysOfferAadapter.ViewHolder holder, final int i) {
        final TodaysOfferModel todaysOfferModel = todaysOfferModels.get(i);

        String u_name = todaysOfferModel.getToday_u_name();
        String category = todaysOfferModel.getToday_category_name();
        String sub_cate = todaysOfferModel.getToday_sub_category_name();
        String default_price = todaysOfferModel.getToday_default_price();
        String discount_price = todaysOfferModel.getToday_today_offer();

        if (u_name.equals("")) {
            holder.tvUserName.setText(" - ");
        } else {
            holder.tvUserName.setText(todaysOfferModel.getToday_u_name());
        }

        if (sub_cate.equals("")){
            holder.tvSubCateTodays.setText(" - ");
        }else {
            holder.tvSubCateTodays.setText(todaysOfferModel.getToday_sub_category_name());
        }



        if (default_price.equals("")) {
            holder.tvOfferPrice.setText("$" + " " + "00");
        } else {
            holder.tvOfferPrice.setText("$" + " " + todaysOfferModel.getToday_default_price());
        }

        if (discount_price.equals("")) {
            holder.tvOfferPriceDiscount.setText("0" + "% OFF");
        } else {
            holder.tvOfferPriceDiscount.setText(todaysOfferModel.getToday_today_offer() + "% OFF");
        }
    }

    @Override
    public int getItemCount() {
        return todaysOfferModels.size();
    }

    @Override
    public Filter getFilter() {

        if (mFilter == null){
            mFilteredList.clear();
            mFilteredList.addAll(this.todaysOfferModels);
            mFilter = new MyFilter(this,mFilteredList);
        }
        return mFilter;

    }

    private static class MyFilter extends Filter {

        private final TodaysOfferAadapter myAdapter;
        private final List<TodaysOfferModel> originalList;
        private final List<TodaysOfferModel> filteredList;

        private MyFilter(TodaysOfferAadapter todaysOfferAadapter, List<TodaysOfferModel> originalList) {
            this.myAdapter = todaysOfferAadapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<TodaysOfferModel>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0){
                filteredList.addAll(originalList);
            }else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for ( TodaysOfferModel myTaskModel : originalList){
                    if (myTaskModel.getToday_u_name().toLowerCase().contains(filterPattern) || myTaskModel.getToday_category_name().toLowerCase().contains(filterPattern) || myTaskModel.getToday_sub_category_name().toLowerCase().contains(filterPattern)
                            || myTaskModel.getToday_default_price().contains(filterPattern) || myTaskModel.getToday_today_offer().contains(filterPattern)){
                        filteredList.add(myTaskModel);
                        Log.d("FilterData",filteredList.toString());
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            myAdapter.todaysOfferModels.clear();
            myAdapter.todaysOfferModels.addAll((ArrayList<TodaysOfferModel>)filterResults.values);
            myAdapter.notifyDataSetChanged();

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvUserName, tvOfferPrice, tvOfferPriceDiscount,tvSubCateTodays;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tv_vendroName);
          // tvCategoryName = itemView.findViewById(R.id.textView_cate_name_today);
            tvSubCateTodays = itemView.findViewById(R.id.tv_serviceName);
            tvOfferPrice = itemView.findViewById(R.id.tv_default);
            tvOfferPriceDiscount = itemView.findViewById(R.id.tv_offer);
        }
    }
}

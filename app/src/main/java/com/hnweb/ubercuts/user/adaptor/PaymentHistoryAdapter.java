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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.user.bo.PaymentHistoryModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<PaymentHistoryModel> paymentHistoryModels;
    private LayoutInflater inflater;
    AdapterCallback mAdapterCallback;
    public MyFilter mFilter;
    private List<PaymentHistoryModel> mFilteredList;

    public PaymentHistoryAdapter(FragmentActivity activity, ArrayList<PaymentHistoryModel> paymentHistoryModels) {

        this.context = activity;
        this.paymentHistoryModels = paymentHistoryModels;
        this.inflater = LayoutInflater.from(context);
        this.mFilteredList = new ArrayList<PaymentHistoryModel>();

    }

    @Override
    public PaymentHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_payment_history, parent, false);
        PaymentHistoryAdapter.ViewHolder vh = new PaymentHistoryAdapter.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final PaymentHistoryAdapter.ViewHolder holder, final int i) {
        final PaymentHistoryModel paymentHistoryModel = paymentHistoryModels.get(i);

        String booking_amount = paymentHistoryModel.getPay_booking_amount();
        String payment_date = paymentHistoryModel.getPay_payment_date();
        String beautician_name = paymentHistoryModel.getPay_beautician_name();
        String category_name = paymentHistoryModel.getPay_category_name();
        String date = paymentHistoryModel.getPay_date();
        String time = paymentHistoryModel.getPay_time();
        String user_image=paymentHistoryModel.getU_img();
        String sub_service = paymentHistoryModel.getSub_service_name();

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

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outFormat = new SimpleDateFormat("hh:mm aa");
        String time_format = null;
        try {
            time_format = outFormat.format(inFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("TimeFormatPay", time_format);

        if (booking_amount.equals("")) {
            holder.tvBookingAmount.setText(" - ");
        } else {
            holder.tvBookingAmount.setText("$ "+booking_amount);
        }

        if (beautician_name.equals("")) {
            holder.tvCustomerName.setText(" - ");
        } else {
            holder.tvCustomerName.setText(beautician_name);
        }

        if (sub_service.equals("")) {
            holder.tvCategoryeName.setText(" - ");
        } else {
            holder.tvCategoryeName.setText(sub_service);
        }

        if (date.equals("")){
            holder.tvDate.setText(" - ");
        }else {
            holder.tvDate.setText(date_formats);
        }

        if (time.equals("")){
            holder.tvTime.setText(" - ");
        }else {
            holder.tvTime.setText(time_format);
        }
        if (user_image.equals("")) {
            Glide.with(context).load(R.drawable.user_register).into(holder.iv_vendorimage);
        } else {
            Glide.with(context).load(user_image).into(holder.iv_vendorimage);
        }

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get position
                        // check if item still exists
                        String user_details_ids = paymentHistoryModel.ge();
                        String user_category_id = paymentHistoryModel.getCategoryId();
                        String user_sub_category_id = paymentHistoryModel.getSubCategoryId();

                        Intent intent = new Intent(context, BeauticianDetailsActivity.class);
                        intent.putExtra("user_details_ids", user_details_ids);
                        context.startActivity(intent);

                        //Toast.makeText(v.getContext(), "You clicked " + details.getUId(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return paymentHistoryModels.size();
    }

    @Override
    public Filter getFilter() {

        if (mFilter == null){
            mFilteredList.clear();
            mFilteredList.addAll(this.paymentHistoryModels);
            mFilter = new MyFilter(this,mFilteredList);
        }
        return mFilter;

    }

    private static class MyFilter extends Filter {

        private final PaymentHistoryAdapter myAdapter;
        private final List<PaymentHistoryModel> originalList;
        private final List<PaymentHistoryModel> filteredList;

        private MyFilter(PaymentHistoryAdapter myAdapter, List<PaymentHistoryModel> originalList) {
            this.myAdapter = myAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<PaymentHistoryModel>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0){
                filteredList.addAll(originalList);
            }else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for ( PaymentHistoryModel paymentHistoryModel : originalList){
                    if (paymentHistoryModel.getPay_category_name().toLowerCase().toString().contains(filterPattern) || paymentHistoryModel.getSub_service_name().toLowerCase().toString().contains(filterPattern) || paymentHistoryModel.getPay_beautician_name().toLowerCase().contains(filterPattern)
                            || paymentHistoryModel.getPay_date().toLowerCase().toString().contains(filterPattern) || paymentHistoryModel.getPay_time().contains(filterPattern) ||  paymentHistoryModel.getPay_booking_amount().contains(filterPattern)){
                        filteredList.add(paymentHistoryModel);
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

            myAdapter.paymentHistoryModels.clear();
            myAdapter.paymentHistoryModels.addAll((ArrayList<PaymentHistoryModel>)filterResults.values);
            myAdapter.notifyDataSetChanged();

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCategoryeName, tvBookingAmount, tvCustomerName, tvDate, tvTime;
        ImageView iv_vendorimage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCategoryeName = itemView.findViewById(R.id.tv_serviceName);
            tvBookingAmount = itemView.findViewById(R.id.tv_default);
            tvCustomerName = itemView.findViewById(R.id.tv_vendroName);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            iv_vendorimage=itemView.findViewById(R.id.iv_vendorimage);
        }
    }
}


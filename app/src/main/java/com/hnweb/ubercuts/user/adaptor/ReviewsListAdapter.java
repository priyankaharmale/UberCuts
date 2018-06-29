package com.hnweb.ubercuts.user.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.user.bo.ReviewsListModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ViewHolder> {

    private Context context;
    private List<ReviewsListModel> reviewsListModelList;
    private LayoutInflater inflater;


    public ReviewsListAdapter(Context context, List<ReviewsListModel> data) {
        this.context = context;
        this.reviewsListModelList = data;
        this.inflater = LayoutInflater.from(context);
        //this.imageLoader = MainApplication.getInstance().getImageLoader();
    }

    @Override
    public ReviewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_review_list, parent, false);
        ReviewsListAdapter.ViewHolder vh = new ReviewsListAdapter.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ReviewsListAdapter.ViewHolder holder, final int i) {
        final ReviewsListModel details = reviewsListModelList.get(i);

        Log.e("Data", details.toString());
        String reviews_id = reviewsListModelList.get(i).getReviews_id();
        String user_name = reviewsListModelList.get(i).getUser_name();
        String reviews = reviewsListModelList.get(i).getReviews();
        String rating_value = reviewsListModelList.get(i).getRating();
        String created_date = reviewsListModelList.get(i).getCreated_date();
        String user_image = reviewsListModelList.get(i).getUser_image();

        if (user_name.equals("")) {
            holder.textViewReviwerName.setText("No Name");
        } else {
            holder.textViewReviwerName.setText(reviewsListModelList.get(i).getUser_name());
        }

        if (reviews.equals("")) {
            holder.textViewReviews.setText("No Reviews");
        } else {
            holder.textViewReviews.setText(reviewsListModelList.get(i).getReviews());
        }

        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat("dd MMM");
        Date date_format = null;
        try {
            date_format = inputFormat.parse(created_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateFormat = outputFormat.format(date_format);
        Log.d("DateFormat", outputDateFormat);

        if (created_date.equals("")) {
            holder.textViewReviewDate.setText(" - ");
        } else {
            holder.textViewReviewDate.setText(outputDateFormat);
        }

        holder.ratingBarReviwes.setRating(Float.parseFloat(rating_value));
        holder.ratingBarReviwes.setFocusable(false);

      /*  if (user_image.equals("")) {
            Glide.with(context).load(R.drawable.user_register).into(holder.imageViewReviews);
        } else {
            Glide.with(context).load(user_image).into(holder.imageViewReviews);
        }*/

    }


    @Override
    public int getItemCount() {
        return reviewsListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewReviwerName, textViewReviews, textViewReviewDate;
        ImageView imageViewReviews;
        RatingBar ratingBarReviwes;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewReviwerName = itemView.findViewById(R.id.textView_user_name_reviews);
            textViewReviews = itemView.findViewById(R.id.textView_user_reviews);

            textViewReviewDate = itemView.findViewById(R.id.textView_reviews_date);

            imageViewReviews = itemView.findViewById(R.id.profile_image_reviews);
            ratingBarReviwes = itemView.findViewById(R.id.rtb_reviews_rating);
        }
    }
}

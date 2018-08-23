package com.hnweb.ubercuts.user.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.user.bo.FavouritesModel;
import com.hnweb.ubercuts.utils.LoadingDialog;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<FavouritesModel> favouritesModels;
    private LayoutInflater inflater;
    AdapterCallback mAdapterCallback;
    LoadingDialog loadingDialog;
    public MyFilter mFilter;
    private List<FavouritesModel> mFilteredList;

    public FavouritesAdapter(FragmentActivity activity, ArrayList<FavouritesModel> favouritesModels, LoadingDialog loadingDialog, AdapterCallback mAdapterCallback) {

        this.context = activity;
        this.favouritesModels = favouritesModels;
        this.inflater = LayoutInflater.from(context);
        this.loadingDialog = loadingDialog;
        this.mAdapterCallback = mAdapterCallback;
        this.mFilteredList = new ArrayList<FavouritesModel>();

    }

    @Override
    public FavouritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_favoritelist, parent, false);
        FavouritesAdapter.ViewHolder vh = new FavouritesAdapter.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final FavouritesAdapter.ViewHolder holder, final int i) {
        final FavouritesModel favouritesModel = favouritesModels.get(i);

        String u_id = favouritesModel.getU_id();
        String u_name = favouritesModel.getU_name();
        String u_image = favouritesModel.getU_img();
        String experience = favouritesModel.getExperience();
        String rating_bar = favouritesModel.getRating();

        if (u_name.equals("")) {
            holder.tvUserName.setText(" - ");
        } else {
            holder.tvUserName.setText(favouritesModel.getU_name());
        }



        if (experience.equals("")) {
            holder.tvExperience.setText(" - ");
        } else {
            if (experience.equals("0")) {
                holder.tvExperience.setText("Starter");
            } else if (experience.equals("1")) {
                holder.tvExperience.setText("0 - 1 Years");
            } else if (experience.equals("2")) {
                holder.tvExperience.setText("1 - 5 Years");
            } else if (experience.equals("3")) {
                holder.tvExperience.setText("5 - 10 Years");
            }
        }

        if (u_image.equals("")) {
            Glide.with(context).load(R.drawable.user_register).into(holder.imageViewUserPhoto);
        } else {
            Glide.with(context).load(u_image).into(holder.imageViewUserPhoto);
        }

        holder.imageViewFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vendor_id = favouritesModel.getU_id();
                //addFavouritesList(vendor_id);
            }
        });

        if (rating_bar.equals("")) {
            holder.ratingBar.setRating(0.0f);
        } else {
            holder.ratingBar.setRating(Float.parseFloat(rating_bar));
        }


    }

/*
    private void addFavouritesList(String vendor_id) {

        SharedPreferences prefs = context.getSharedPreferences("MyPrefUser", MODE_PRIVATE);
        String user_id = prefs.getString("user_id_user", null);
        Log.e("PostedUserIds", user_id);

        //loadingDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("u_id", user_id);
        params.put("vendor_id", vendor_id);

        Log.e("ParamsFav", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("favourites");
        request_info.setUrl(WebsServiceURLUser.USER_REMOVE_FAVOURITES);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, context, request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.d("AddFavourites", data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);

                    if (message_code == 1) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        callAddFavourites();
                    } else {
                        Utils.AlertDialog((FragmentActivity) context, msg);
                    }
                } catch (JSONException e) {
                    System.out.println("jsonexeption" + e.toString());
                }

            }

            @Override
            public void OnError(String message) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                AlertUtility.showAlert(context, false, "Network Error,Please Check Internet Connection");
            }
        });
    }
*/

    private void callAddFavourites() {
        mAdapterCallback.onMethodCallPosted();
    }

    @Override
    public int getItemCount() {
        return favouritesModels.size();
    }

    @Override
    public Filter getFilter() {

        if (mFilter == null){
            mFilteredList.clear();
            mFilteredList.addAll(this.favouritesModels);
            mFilter = new MyFilter(this,mFilteredList);
        }
        return mFilter;

    }

    private static class MyFilter extends Filter {

        private final FavouritesAdapter myAdapter;
        private final List<FavouritesModel> originalList;
        private final List<FavouritesModel> filteredList;

        private MyFilter(FavouritesAdapter favouritesAdapter, List<FavouritesModel> originalList) {
            this.myAdapter = favouritesAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<FavouritesModel>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0){
                filteredList.addAll(originalList);
            }else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for ( FavouritesModel favouritesModel : originalList){
                    if (favouritesModel.getU_name().toLowerCase().contains(filterPattern)
                            || favouritesModel.getExperience().contains(filterPattern) ){
                        filteredList.add(favouritesModel);
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

            myAdapter.favouritesModels.clear();
            myAdapter.favouritesModels.addAll((ArrayList<FavouritesModel>)filterResults.values);
            myAdapter.notifyDataSetChanged();

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvUserName, tvExperience;
        private final ImageView imageViewUserPhoto, imageViewFav;
        private final RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tv_vendroName);
            tvExperience = itemView.findViewById(R.id.tv_experience);
            imageViewUserPhoto = itemView.findViewById(R.id.iv_profile);
            imageViewFav = itemView.findViewById(R.id.imageView_fav);
            ratingBar = itemView.findViewById(R.id.rtb_reviews_rating);
        }
    }
}

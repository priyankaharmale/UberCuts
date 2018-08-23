package com.hnweb.ubercuts.vendor.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.vendor.activity.VideoViewActivity;
import com.hnweb.ubercuts.vendor.bo.MyWorkModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWorkListVideosAdapter extends RecyclerView.Adapter<MyWorkListVideosAdapter.ViewHolder> {

    private Context context;
    private List<MyWorkModel> myWorkModelList;
    private LayoutInflater inflater;
    private MediaController mediacontroller;
    String work_flag;
    LoadingDialog loadingDialog;
    private AdapterCallback mAdapterCallback;

    public MyWorkListVideosAdapter(Context context, List<MyWorkModel> data, String profile_work_videos, LoadingDialog loadingDialog, AdapterCallback mAdapterCallback) {
        this.context = context;
        this.myWorkModelList = data;
        this.inflater = LayoutInflater.from(context);
        this.work_flag = profile_work_videos;
        this.loadingDialog = loadingDialog;
        this.mAdapterCallback = mAdapterCallback;
        //this.imageLoader = MainApplication.getInstance().getImageLoader();
    }

    @Override
    public MyWorkListVideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.item_my_work_videos, parent, false);
        MyWorkListVideosAdapter.ViewHolder vh = new MyWorkListVideosAdapter.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyWorkListVideosAdapter.ViewHolder holder, final int i) {
        final MyWorkModel details = myWorkModelList.get(i);

        Log.e("Data", details.toString());
        String user_videos_id = myWorkModelList.get(i).getMy_work_videos_id();
        final String user_videos = myWorkModelList.get(i).getMy_work_videos_name();
        String user_videos_category_name = myWorkModelList.get(i).getCategory_videos_name();
        String user_videos_thumb = myWorkModelList.get(i).getMy_work_videos_thumb();
        Log.e("VideosUrl", user_videos);

        try {


            if (user_videos_thumb.equals("")) {
                Glide.with(context).load(R.drawable.user_register).into(holder.videoImages);
            } else {
                Glide.with(context).load(user_videos_thumb).into(holder.videoImages);
            }


           /* Uri uri = Uri.parse(user_videos);
            holder.videoView.setVideoURI(uri);
            holder.videoView.requestFocus();
            holder.videoView.start();*/

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        //String newString = String.replace(".", "xyz");
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_url = myWorkModelList.get(i).getMy_work_videos_name();
                //Toast.makeText(context, "Video", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,VideoViewActivity.class);
                intent.putExtra("VideoUrlS",video_url);
                context.startActivity(intent);
            }
        });

        if (work_flag.equals("MyWorkVideos")) {
            holder.ivDeleteVideos.setVisibility(View.GONE);
        } else {
            holder.ivDeleteVideos.setVisibility(View.VISIBLE);

        }

        holder.ivDeleteVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String delete_video_id = myWorkModelList.get(i).getMy_work_videos_id();
                //Toast.makeText(context, "Image Id "+delete_image_id, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // set title
                alertDialogBuilder.setTitle("Delete Videos!!");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete videos?")
                        .setCancelable(false)
                        .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                removeImage(delete_video_id,dialog);
                                //Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
    }



    @Override
    public int getItemCount() {
        return myWorkModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        ImageView imageView, videoImages,ivDeleteVideos;


        public ViewHolder(View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            imageView = itemView.findViewById(R.id.play_button);
            videoImages = itemView.findViewById(R.id.video_images);
            ivDeleteVideos = itemView.findViewById(R.id.imageView_delete_images);

        }
    }

    private void postCallBack() {
        mAdapterCallback.onMethodCallPosted();
    }
    private void removeImage(final String delete_video_id, final DialogInterface dialog) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_REMOVE_VENDOR_VIDEO,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "ImagesDelted= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                postCallBack();
                                dialog.cancel();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(context, error);
                        AlertUtility.showAlert(context, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("my_work_videos_id", delete_video_id);
                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

}

package com.hnweb.ubercuts.vendor.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.hnweb.ubercuts.vendor.bo.MyWorkModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWorkListImagesAdapter extends RecyclerView.Adapter<MyWorkListImagesAdapter.ViewHolder> {

    private Context context;
    private List<MyWorkModel> myWorkModelList;
    private LayoutInflater inflater;
    String work_flag;
    LoadingDialog loadingDialog;
    private AdapterCallback mAdapterCallback;

    public MyWorkListImagesAdapter(Context context, List<MyWorkModel> data, String profile_work, LoadingDialog loadingDialog, AdapterCallback mAdapterCallback) {
        this.context = context;
        this.myWorkModelList = data;
        this.inflater = LayoutInflater.from(context);
        this.work_flag = profile_work;
        this.loadingDialog = loadingDialog;
        this.mAdapterCallback = mAdapterCallback;
        //this.imageLoader = MainApplication.getInstance().getImageLoader();
    }

    @Override
    public MyWorkListImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.item_my_work_images, parent, false);
        MyWorkListImagesAdapter.ViewHolder vh = new MyWorkListImagesAdapter.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyWorkListImagesAdapter.ViewHolder holder, final int i) {
        final MyWorkModel details = myWorkModelList.get(i);

        Log.e("Data", details.toString());
        String user_image_id = myWorkModelList.get(i).getMy_work_images_id();
        String user_images = myWorkModelList.get(i).getMy_work_images_name();
        String user_category_name = myWorkModelList.get(i).getCategory_images_name();
        Log.d("Images", user_images);

        try {

            if (user_images.equals("")) {
                Glide.with(context).load(R.drawable.user_register).into(holder.ivImages);
            } else {
                Glide.with(context).load(user_images).into(holder.ivImages);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (work_flag.equals("MyWork")) {
            holder.ivDelete.setVisibility(View.GONE);
        } else {
            holder.ivDelete.setVisibility(View.VISIBLE);

        }

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String delete_image_id = myWorkModelList.get(i).getMy_work_images_id();
                //Toast.makeText(context, "Image Id "+delete_image_id, Toast.LENGTH_SHORT).show();


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Delete Image!!");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete image?")
                        .setCancelable(false)
                        .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                removeImage(delete_image_id,dialog);
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

/*
    private void postImagesDeleteRequest(String delete_image_id, final DialogInterface dialog) {

        //loadingDialog.show();
        Map<String, String> params = new HashMap<>();

        params.put("my_work_images_id", delete_image_id);
        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("delete");
        request_info.setUrl(WebsServiceURLVendor.VENDOR_MY_WORK_IMAGES_DELETES);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, context, request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "ImagesDelted= " + data);
                try {
                    JSONObject jobj = new JSONObject(data);
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

    private void postCallBack() {
        mAdapterCallback.onMethodCallPosted();
    }


    @Override
    public int getItemCount() {
        return myWorkModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImages, ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            ivImages = itemView.findViewById(R.id.imageView_images);
            ivDelete = itemView.findViewById(R.id.imageView_delete_images);
        }
    }

    private void removeImage(final String delete_image_id, final DialogInterface dialog) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_REMOVE_VENDOR_IMAGE,
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
                    params.put("my_work_images_id", delete_image_id);
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

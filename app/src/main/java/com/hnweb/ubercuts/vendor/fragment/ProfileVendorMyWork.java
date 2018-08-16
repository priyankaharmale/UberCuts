package com.hnweb.ubercuts.vendor.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.ubercuts.R;
import com.hnweb.ubercuts.contants.AppConstant;
import com.hnweb.ubercuts.interfaces.AdapterCallback;
import com.hnweb.ubercuts.multipartrequest.MultiPart_Key_Value_Model;
import com.hnweb.ubercuts.multipartrequest.MultipartFileUploaderAsync;
import com.hnweb.ubercuts.multipartrequest.OnEventListener;
import com.hnweb.ubercuts.utils.AlertUtility;
import com.hnweb.ubercuts.utils.AppUtils;
import com.hnweb.ubercuts.utils.ConnectionDetector;
import com.hnweb.ubercuts.utils.DataUtility;
import com.hnweb.ubercuts.utils.LoadingDialog;
import com.hnweb.ubercuts.utils.RequestInfo;
import com.hnweb.ubercuts.utils.Utils;
import com.hnweb.ubercuts.vendor.adaptor.MyWorkListImagesAdapter;
import com.hnweb.ubercuts.vendor.adaptor.MyWorkListVideosAdapter;
import com.hnweb.ubercuts.vendor.bo.Category;
import com.hnweb.ubercuts.vendor.bo.MyWorkModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileVendorMyWork extends Fragment implements AdapterCallback {
    RecyclerView recyclerViewImages, recyclerViewVideos;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    TextView textViewImagesCount, textViewVideosCount, textViewImageEmpty, textViewVideosEmpty;
    //TextView textViewEmptyReviews;
    private ArrayList<MyWorkModel> myWorkModelsImages = null;
    private ArrayList<MyWorkModel> myWorkModelsVideos = null;

    MyWorkListImagesAdapter reviewsListAdapterImages;
    MyWorkListVideosAdapter reviewsListAdapterVideos;
    String beautician_id;
    SharedPreferences prefs;
    String vendor_id;
    Button btnImages, btnVideos;
    AdapterCallback mAdapterCallback;
    private Uri imageUri;
    private String image_path_selected = "";
    private ListView listView;
    private String selectedCategoryId = "";
    private ArrayList<String> myList = new ArrayList<String>();
    private ArrayList<String> myListVideo = new ArrayList<String>();
    private ArrayList<String> myListVideoThumb = new ArrayList<String>();
    private static final int PICK_VIDEO_REQUEST = 3;
    private String selectedFilePath = "";
    ArrayList<Uri> selectedUriListVideo = new ArrayList<>();
    ArrayList<Uri> selectedUriListVideoThumb = new ArrayList<>();
    ArrayList<Uri> selectedUriListImage = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_work, container, false);

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        vendor_id = prefs.getString(AppConstant.KEY_ID, null);

        mAdapterCallback = ProfileVendorMyWork.this;

        initViewById(view);

        return view;
    }

    private void initViewById(View view) {

        //textViewEmptyReviews = view.findViewById(R.id.textView_empty_service_nails);

        recyclerViewImages = view.findViewById(R.id.recylerview_my_work_images);
        recyclerViewVideos = view.findViewById(R.id.recylerview_my_work_videos);

        LinearLayoutManager horizontalLMImages = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewImages.setLayoutManager(horizontalLMImages);

        LinearLayoutManager horizontalLMVideos = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewVideos.setLayoutManager(horizontalLMVideos);

        textViewImagesCount = view.findViewById(R.id.textView_images_count);
        textViewVideosCount = view.findViewById(R.id.textView_videos_count);

        textViewImageEmpty = view.findViewById(R.id.textView_Images_empty);
        textViewVideosEmpty = view.findViewById(R.id.textView_Videos_empty);

        btnImages = view.findViewById(R.id.button_more_images);
        btnVideos = view.findViewById(R.id.button_more_videos);
        btnImages.setVisibility(View.VISIBLE);
        btnVideos.setVisibility(View.VISIBLE);

        btnImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected_images_videos = "Images";
              /*  if (categoriesList.size() == 0 || categoriesList == null) {
                    Toast.makeText(getActivity(), "Please Wait....", Toast.LENGTH_SHORT).show();
                } else {*/
                    onClickedSelectedCategory(selected_images_videos);
                //}
            }
        });
        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected_images_videos = "Videos";
                /*if (categoriesList.size() == 0 || categoriesList == null) {
                    Toast.makeText(getActivity(), "Please Wait....", Toast.LENGTH_SHORT).show();
                }*//* else {*/
                    onClickedSelectedCategory(selected_images_videos);
                //}
            }
        });

        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            getVendorWork();
        } else {

            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

    }


    private void onClickedSelectedCategory(final String selected_images_videos) {
        /*final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.common_list, null);
        alertDialog.setView(dialogView);

        TextView textView_header = (TextView) dialogView.findViewById(R.id.textView_custom_view);
        String text = textView_header.getText().toString();
        if (text.equals("TextView")) {
            textView_header.setText("Select Category");
        }
        Button btnCancel = (Button) dialogView.findViewById(R.id.button_cancel);
        listView = (ListView) dialogView.findViewById(R.id.listView_cate);
        final AlertDialog ad = alertDialog.create();
        ad.show();

        ImageView imageView = dialogView.findViewById(R.id.imageViewBackArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });
        categoryArrayAdapter = new ArrayAdapter<Category>(getActivity(), R.layout.spinner_text, categoriesList);
        listView.setAdapter(categoryArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Toast.makeText(RegisterActivity.this, genderArrayList.get(position), Toast.LENGTH_SHORT).show();
                Category stateList = categoryArrayAdapter.getItem(position);
                String category_id = stateList.getCategory_id();
                String category_name = stateList.getCategory_name();
                selectedCategoryId = category_id;
                //btnCate.setText(category_name);
                ad.dismiss();



            }
        });*/

        if (selected_images_videos.equals("Images")) {
            selectedImage();
        } else {
            Intent intent = new Intent();
            //sets the select file to all types of files
            intent.setType("video/*");
            //allows to select data and return it
            intent.setAction(Intent.ACTION_GET_CONTENT);
            //starts new activity to select file and return data
            startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_VIDEO_REQUEST);
        }
    }

    private void selectedImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                try {
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    //imageViewPost.setImageBitmap(thumbnail);
                    String imageurl = getRealPathFromURI(imageUri);
                    image_path_selected = imageurl;
                    //Imageview imageView = (ImageView) findViewById(R.id.image_view);
                    //Glide.with(this).load(imageurl).into(imageViewProfile);
                    if (image_path_selected != null && !image_path_selected.equals("")) {
                        myList.add(image_path_selected);
                        showImageUpload(image_path_selected, selectedCategoryId);
                        selectedUriListImage.add(imageUri);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                try {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                    image_path_selected = picturePath;

                    Log.w("pathofimagefromgallery", picturePath + "" + thumbnail);
                    if (image_path_selected != null && !image_path_selected.equals("")) {
                        myList.add(image_path_selected);
                        showImageUpload(image_path_selected, selectedCategoryId);
                    }
                    ///Glide.with(this).load(picturePath).into(imageViewProfile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (requestCode == PICK_VIDEO_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }
                Log.d("ABC", "Video Selected");
                Uri selectedFileUri = data.getData();
                selectedFilePath = getPath(getActivity(), selectedFileUri);
                Log.i("TAG", "SelectedFilePath" + selectedFilePath);

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    //tvFileName.setText(selectedFilePath);
                    Log.e("tvFileName", selectedFilePath);

                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(selectedFilePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    Log.d("setFileName", selectedFilePath);
                    Log.d("setFileName", thumb.toString());

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    Uri tempUri = getImageUri(getActivity(), thumb);
                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    File finalFile = new File(getRealPathFromURI(tempUri));
                    // print the path of Image
                    System.out.println(finalFile);
                    Log.e("TempFileName", finalFile.toString());


                    //imageView.setImageURI(selectedFileUri);
                    //selectedFilePath = "";

                    myListVideo.add(selectedFilePath);
                    myListVideoThumb.add(finalFile.toString());
                    selectedUriListVideo.add(selectedFileUri);
                    selectedUriListVideoThumb.add(tempUri);
                    showVideoUpload(selectedFilePath, finalFile.toString(), selectedCategoryId);
                    //VideoImageUpload(selectedFilePath, finalFile.toString(), selectedCategoryId);
                    Log.d("ListVideos", String.valueOf(myListVideo.size()) + " :: " + myListVideoThumb.size());

                } else {
                    Toast.makeText(getActivity(), "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showVideoUpload(final String selectedFilePath, final String finalFile, final String selectedCategoryId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("Upload Video");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want Upload Video?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                 VideoImageUpload(selectedFilePath, finalFile, selectedCategoryId);

                // Write your code here to invoke YES event
                //Toast.makeText(getActivity(), "You clicked on Upload", Toast.LENGTH_SHORT).show();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                //Toast.makeText(getActivity(), "You clicked on Cancel", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    private String getPath(FragmentActivity activity, Uri selectedFileUri) {
        Cursor cursor = getActivity().getContentResolver().query(selectedFileUri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    protected byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void showImageUpload(final String image_path_selected, final String selectedCategoryId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("Upload Image");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want Upload image?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                uploadImageToServer(image_path_selected, selectedCategoryId);

                // Write your code here to invoke YES event
                //Toast.makeText(getActivity(), "You clicked on Upload", Toast.LENGTH_SHORT).show();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                //Toast.makeText(getActivity(), "You clicked on Cancel", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    private void onCallBack() {
        mAdapterCallback.onMethodCallPosted();
    }

    private String getRealPathFromURI(Uri imageUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(imageUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    private void getVendorWork() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_MYWORK_VENDOR,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "NailsResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {
                                btnImages.setVisibility(View.VISIBLE);
                                btnVideos.setVisibility(View.VISIBLE);
                                JSONArray jsonArrayImages = jobj.getJSONArray("images");
                                JSONArray jsonArrayVideos = jobj.getJSONArray("videos");
                                myWorkModelsImages = new ArrayList<MyWorkModel>();
                                myWorkModelsVideos = new ArrayList<MyWorkModel>();
                                myWorkModelsImages.clear();
                                myWorkModelsVideos.clear();
                                Log.e("ArrayLengthVideos11", String.valueOf(jsonArrayVideos.length()));
                                if (jsonArrayImages.length() < 0) {
                                    textViewImageEmpty.setVisibility(View.VISIBLE);
                                    recyclerViewImages.setVisibility(View.GONE);
                                } else {
                                    textViewImageEmpty.setVisibility(View.GONE);
                                    recyclerViewImages.setVisibility(View.VISIBLE);
                                    Log.d("ArrayLengthImages", String.valueOf(jsonArrayImages.length()));
                                    // Log.d("ArrayLengthVideos", String.valueOf(jsonArrayVideos.length()));
                                    int total_images_size = jsonArrayImages.length();
                                    @SuppressLint("DefaultLocale") String list_of_images_count = String.format("%02d", total_images_size);
                                    textViewImagesCount.setText(String.valueOf(list_of_images_count));

                                    for (int i = 0; i < jsonArrayImages.length(); i++) {
                                        JSONObject jsonObjectImages = jsonArrayImages.getJSONObject(i);

                                        MyWorkModel myWorkModel = new MyWorkModel();

                                        myWorkModel.setMy_work_images_id(jsonObjectImages.getString("my_work_images_id"));
                                        myWorkModel.setMy_work_images_name(jsonObjectImages.getString("my_work_images_name"));

                                        myWorkModelsImages.add(myWorkModel);

                                        Log.e("ArraySizeImages", String.valueOf(myWorkModelsImages.size()));

                                    }

                                    String profile_work_images = "ProfileVendor";
                                    reviewsListAdapterImages = new MyWorkListImagesAdapter(getActivity(), myWorkModelsImages, profile_work_images, loadingDialog, mAdapterCallback);
                                    recyclerViewImages.setAdapter(reviewsListAdapterImages);
                                }

                                if (jsonArrayVideos.length() < 0) {
                                    textViewVideosEmpty.setVisibility(View.VISIBLE);
                                    //recyclerViewVideos.setVisibility(View.GONE);
                                    btnVideos.setVisibility(View.VISIBLE);
                                    //Toast.makeText(getActivity(), "If", Toast.LENGTH_SHORT).show();
                                } else if (jsonArrayVideos.length() == 0) {
                                    textViewVideosEmpty.setVisibility(View.VISIBLE);
                                    //recyclerViewVideos.setVisibility(View.GONE);
                                    btnVideos.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerViewVideos.setVisibility(View.VISIBLE);
                                    textViewVideosEmpty.setVisibility(View.GONE);
                                    Log.e("ArrayLengthVideos", String.valueOf(jsonArrayVideos.length()));
                                    int total_videos_size = jsonArrayVideos.length();
                                    @SuppressLint("DefaultLocale") String list_of_videos_count = String.format("%02d", total_videos_size);
                                    textViewVideosCount.setText(String.valueOf(list_of_videos_count));


                                    for (int j = 0; j < jsonArrayVideos.length(); j++) {
                                        JSONObject jsonObjectVideos = jsonArrayVideos.getJSONObject(j);

                                        MyWorkModel myWorkModel = new MyWorkModel();

                                        myWorkModel.setMy_work_videos_id(jsonObjectVideos.getString("my_work_videos_id"));
                                        myWorkModel.setMy_work_videos_name(jsonObjectVideos.getString("my_work_videos_name"));
                                        myWorkModel.setMy_work_videos_thumb(jsonObjectVideos.getString("thumb_name"));

                                        myWorkModelsVideos.add(myWorkModel);

                                        Log.d("ArraySizeVideos", String.valueOf(myWorkModelsVideos.size()));

                                    }

                                    String profile_work_videos = "ProfileVendorVideos";
                                    reviewsListAdapterVideos = new MyWorkListVideosAdapter(getActivity(), myWorkModelsVideos, profile_work_videos, loadingDialog, mAdapterCallback);
                                    recyclerViewVideos.setAdapter(reviewsListAdapterVideos);
                                }

                            } else {
                                //Utils.AlertDialog(getActivity(), msg);
                                //textViewEmptyNails.setVisibility(View.VISIBLE);
                                textViewImageEmpty.setVisibility(View.VISIBLE);
                                textViewVideosEmpty.setVisibility(View.VISIBLE);
                                //btnImages.setVisibility(View.GONE);
                                //btnVideos.setVisibility(View.VISIBLE);
                                recyclerViewImages.setVisibility(View.GONE);
                                recyclerViewVideos.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("vendor_id", vendor_id);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }


    @Override
    public void onMethodCallPosted() {
        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            getVendorWork();
        } else {
           /* Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getVendorWork();
    }


    private void uploadImageToServer(String image_path_selected, final String selectedCategoryId) {

        loadingDialog.show();
        MultiPart_Key_Value_Model OneObject = new MultiPart_Key_Value_Model();

        Map<String, String> fileParams = new HashMap<>();

        if (image_path_selected.equals("")) {

        } else {
            for (int i = 0; i < myList.size(); i++) {
                fileParams.put("my_work_images_name[" + i + "]", myList.get(i).toString());

            }
            //fileParams.put("my_work_images_name", image_path_selected);
        }

        Map<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("vendor_id", vendor_id);


        Log.d("UploafParams", stringHashMap + " :: " + fileParams);

        String requestURL = AppConstant.API_SAVE_VENDOR_IMAGES;
        OneObject.setUrl(requestURL);
        OneObject.setFileparams(fileParams);
        OneObject.setStringparams(stringHashMap);

        Log.d("StringParams", stringHashMap.toString() + " :: " + fileParams.toString());

        MultipartFileUploaderAsync someTask = new MultipartFileUploaderAsync(getActivity(), OneObject, new OnEventListener<String>() {

            @Override
            public void onSuccess(String object) {
                //Toast.makeText(AddProductsActivity.this, "Response " + object, Toast.LENGTH_SHORT).show();
                Log.d("Response ", object);
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                try {
                    JSONObject jobj = new JSONObject(object);
                    int message_code = jobj.getInt("message_code");
                    String message = jobj.getString("message");
                    if (message_code == 1) {
                        getVendorWork();
                        myList.clear();
                        selectedFilePath = "";
                        //onCallBack();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
        someTask.execute();
        return;

    }

    private void VideoImageUpload(final String selectedFilePath, final String image_thumb, final String selectedCategoryId) {

        loadingDialog.show();
        MultiPart_Key_Value_Model OneObject = new MultiPart_Key_Value_Model();

        Map<String, String> fileParams = new HashMap<>();
        //fileParams.put("thumbnail", thumbnail);
        Log.d("ServerVideo", String.valueOf(myListVideo.size()));
        if (selectedFilePath.equals("")) {

        } else {
            for (int i = 0; i < myListVideo.size(); i++) {
                fileParams.put("my_work_videos_name[" + i + "]", myListVideo.get(i).toString());

            }
            //fileParams.put("my_work_images_name", image_path_selected);
        }

        if (image_thumb.equals("")) {

        } else {
            for (int j = 0; j < myListVideoThumb.size(); j++) {
                fileParams.put("my_work_videos_thumb[" + j + "]", myListVideoThumb.get(j).toString());

            }
            //fileParams.put("my_work_images_name", image_path_selected);
        }

        Map<String, String> Stringparams = new HashMap<>();
        Stringparams.put("vendor_id\n", vendor_id);
        Log.e("VideoParams", Stringparams.toString() + " :: " + fileParams);

        String requestURL = AppConstant.API_SAVE_VENDOR_VIDEO;
        OneObject.setUrl(requestURL);
        OneObject.setFileparams(fileParams);
        OneObject.setStringparams(Stringparams);


        MultipartFileUploaderAsync someTask = new MultipartFileUploaderAsync(getActivity(), OneObject,
                new OnEventListener<String>() {

                    @Override
                    public void onSuccess(String object) {
                        //Toast.makeText(getActivity(), "" + object, Toast.LENGTH_SHORT).show();
                        Log.e("UploadVideo", object.toString());

                        if (loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        try {

                            JSONObject jsonObject = new JSONObject(object);
                            int code = jsonObject.getInt("message_code");
                            Log.d("message_code:- ", String.valueOf(code));

                            if (code == 1) {
                                String msg = jsonObject.getString("message");
                                Log.d("message:- ", msg);
                                myListVideo.clear();
                                myListVideoThumb.clear();
                                getVendorWork();
                                if (loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            } else {

                                String msg = jsonObject.getString("message");
                                Log.d("message:- ", msg);
                                if (loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });
        someTask.execute();
        return;

        /*loadingDialog.show();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebsServiceURLVendor.VENDOR_MY_WORK_ADD_VIDEOS,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            int code = obj.getInt("message_code");
                            Log.d("message_code:- ", String.valueOf(code));
                            String message = obj.getString("message");
                            if (code == 1) {
                                String msg = obj.getString("message");
                                Log.d("message:- ", msg);
                                myListVideo.clear();
                                myListVideoThumb.clear();
                                getMyWorkList();
                                if (loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            } else {

                                String msg = obj.getString("message");
                                Log.d("message:- ", msg);
                                if (loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String status = response.getString("status");
                                String message = response.getString("message");

                                Log.e("Error Status", status);
                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + " Please login again";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + " Check your inputs";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            *//*
         * If you want to add more parameters with the image
         * you can do it here
         * here we have only one parameter with the image
         * which is tags
         * *//*
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("u_id", vendor_id);
                params.put(" category_id", selectedCategoryId);
                Log.e("ParamsImage", params.toString());
                return params;
            }

            *//*
         * Here we are passing image by renaming it with a unique name
         * *//*
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();

                if (selectedFilePath.equals("")) {

                } else {
                    int i = 0;
                    for (Uri uri : selectedUriListVideo) {
                        i++;
                        try {
                            InputStream iStream = getActivity().getContentResolver().openInputStream(uri);
                            byte[] inputData = getBytes(iStream);
                            params.put("my_work_videos_name" + i, new DataPart("image" + i + ".jpg", inputData));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    //fileParams.put("my_work_images_name", image_path_selected);
                }

                if (image_thumb.equals("")) {

                } else {

                    int i = 0;
                    for (Uri uri : selectedUriListVideoThumb) {
                        i++;
                        try {
                            InputStream iStream = getActivity().getContentResolver().openInputStream(uri);
                            byte[] inputData = getBytes(iStream);
                            params.put("my_work_videos_thumb" + i, new DataPart("image" + i + ".jpg", inputData));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    //fileParams.put("my_work_images_name", image_path_selected);
                }

                //params.put("img", new DataPart(imagename + ".png", getFileDataFromDrawable(thumbnail)));
                Log.d("ParamsImage", params.toString());
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);*/
    }

}

package com.hnweb.ubercuts.vendor.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.hnweb.ubercuts.R;


public class VideoViewActivity extends AppCompatActivity {

    // Declare variables
    ProgressDialog pDialog;
    VideoView videoview;
    String VideoURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        videoview = (VideoView) findViewById(R.id.VideoView);
        // Execute StreamVideo AsyncTask

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            VideoURL = bundle.getString("VideoUrlS");
            Log.e("VideoURL", VideoURL);
        }

        // Create a progressbar
        pDialog = new ProgressDialog(VideoViewActivity.this);
        // Set progressbar title
        pDialog.setTitle("Video Streaming....");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    VideoViewActivity.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(VideoURL);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}

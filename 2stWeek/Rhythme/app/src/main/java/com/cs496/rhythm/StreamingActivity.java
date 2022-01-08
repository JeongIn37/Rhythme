package com.cs496.rhythm;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class StreamingActivity extends AppCompatActivity {
    Button clk;
    VideoView videov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_video);
        clk = (Button) findViewById(R.id.button);
        videov = (VideoView) findViewById(R.id.videoView);
    }
    public void videoplay(View v){
        String videopath = "android.resource://com.cs496.rhythm/" + R.raw.test;
        Uri uri = Uri.parse(videopath);
        videov.setVideoURI(uri);
        videov.start();
    }

}

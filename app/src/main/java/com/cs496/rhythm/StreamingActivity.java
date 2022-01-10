package com.cs496.rhythm;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StreamingActivity extends AppCompatActivity {

    Timer timer = new Timer();
    VideoView videov;
    ArrayList<double[]> posePerTime;
    int time=0;
    private MediaMetadataRetriever retriever;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_video);
        videov = (VideoView) findViewById(R.id.videoView);

        loadGame();
        startGame();
    }

    void loadGame()
    {
        //동영상 view 셋팅
        String videopath = "android.resource://com.cs496.rhythm/" + R.raw.test;
        Uri uri = Uri.parse(videopath);

        videov.setVideoURI(uri);

        retriever = new MediaMetadataRetriever();
        posePerTime = new ArrayList<double[]>();
        retriever.setDataSource(getApplication(), uri);
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
        int millisecond = mediaPlayer.getDuration();

        for(int i=0; i<millisecond/3000; i++)
        {
            Bitmap temp = retriever.getFrameAtTime(i*1000,MediaMetadataRetriever.OPTION_CLOSEST);
//            posePerTime.add();
        }
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            // 현재 유저 자세와 posePerTime(time)와 비교후 화면 출력
            time++;
        }
    };

    void startGame()
    {
        videoplay();
        timer.schedule(timerTask, 0, 3000);
    }

    public void videoplay(){
        videov.start();
    }

}

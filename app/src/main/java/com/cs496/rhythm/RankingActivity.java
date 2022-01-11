package com.cs496.rhythm;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RankingActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    TextView textView2;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_detail);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.aTitle);
        textView2 = findViewById(R.id.rank);
        button  = findViewById(R.id.playBtn);

        Intent intent = getIntent();
        int selectedVideo = intent.getIntExtra("video", 0);

        if(intent.getExtras() != null){
            String selectedTitle = intent.getStringExtra("title");
            int selectedImage = intent.getIntExtra("image", 0);

            textView.setText(selectedTitle);
            imageView.setImageResource(selectedImage);
            textView2.setText(selectedVideo);
        }

        Log.d("확인----------------------", String.valueOf(selectedVideo));

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RankingActivity.this, MainActivity.class).putExtra("video", selectedVideo));
            }
        });

//    var selectedVideo = intent.getStringExtra("video")

    }

}

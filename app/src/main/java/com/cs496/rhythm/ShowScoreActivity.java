package com.cs496.rhythm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ShowScoreActivity extends AppCompatActivity {

    Button button;
    TextView textView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_end);
        button  = findViewById(R.id.againBtn);
        textView = findViewById(R.id.score);
        Intent intent = getIntent();
        String finalScore = intent.getStringExtra("finalScore");
        Log.d("-------------점수 나와라", finalScore);
        textView.setText(finalScore);


        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowScoreActivity.this, SelectVideoActivity.class));
            }
        });


    }




}

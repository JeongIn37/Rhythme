package com.cs496.rhythm;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RankingActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    TextView textView2;
    Button button;
    TextView rankView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_detail);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.aTitle);
        textView2 = findViewById(R.id.rank);
        button  = findViewById(R.id.playBtn);
        rankView = findViewById(R.id.rankingList);

        Intent intent = getIntent();
        int selectedVideo = intent.getIntExtra("video", 0);

        if(intent.getExtras() != null){
            String selectedTitle = intent.getStringExtra("title");
            int selectedImage = intent.getIntExtra("image", 0);

            textView.setText(selectedTitle);
            imageView.setImageResource(selectedImage);
            //textView2.setText(selectedVideo);
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://imagelab.ho9.me:9412/info";

        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("video_id","1");
            jsonBodyObj.put("user_id","최준영");
        }catch (JSONException e){
            e.printStackTrace();
        }
        final String requestBody = String.valueOf(jsonBodyObj.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                //TODO 데이터 전송 요청 성공
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("---","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() 메소드 : Volley POST_BODY_JSON 요청 응답]");
                        Log.d("---","\n"+"["+"응답 전체 - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");
                        //rankView.setText(response.get(my));
                        //getJsonArray

                    }
                },
                //TODO 데이터 전송 요청 에러 발생
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
//                        Log.e("//===========//","================================================");
                        Log.d("---","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() 메소드 : Volley POST_BODY_JSON 요청 실패]");
                        Log.d("---","\n"+"["+"에러 코드 - "+error.toString()+"]");
//                        Log.e("//===========//","================================================");
                        Log.d("---","---");
                    }
                }) {

            @Override
            public byte[] getBody() {
                try {
                    if (requestBody != null && requestBody.length()>0 && !requestBody.equals("")){
                        return requestBody.getBytes("utf-8");
                    }
                    else {
                        return null;
                    }
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
        queue.add(request);

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

package com.cs496.rhythm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


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

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://imagelab.ho9.me:9412/update";

        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("user_id","최준영");
            jsonBodyObj.put("score","100");
            jsonBodyObj.put("video_id","1");
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



        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowScoreActivity.this, SelectVideoActivity.class));
            }
        });


    }




}

package com.cs496.rhythm;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SelectVideoActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //game list
    GridView gridView;
    String[] names = {"Rasputin", "BBoom BBoom", "Coming Soon"};
    String[] scores = {"77", "82", "60"};
    int[] images = {R.drawable.rasputin_list, R.drawable.bboom, R.drawable.question};
    int[] videos = {2131689472, 2131689472, 2131689472};


    //google login
    private ImageView profile_image;
    private TextView name;
    private Button signoutBtn;

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list);

        //profile_image = findViewById(R.id.profileImage);
        name = findViewById(R.id.name);
        signoutBtn = findViewById(R.id.logoutBtn);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()){
                            gotoLoginActivity();
                        } else {
                            Toast.makeText(SelectVideoActivity.this, "Logout Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://imagelab.ho9.me:9412/home";

        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("user_id","정");
            jsonBodyObj.put("page","1");
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
                        try {
                            JSONObject first = response.getJSONObject("firstVideo");
                            names[0]=first.getString("name");
                            //scores[0] = first.getString("score");

                            Log.d("test", names[0]);
                        } catch (JSONException e) {
                            Log.d("test", "tlfvo");
                            e.printStackTrace();
                        }
                        try {
                            JSONObject first = response.getJSONObject("secondVideo");
                            names[1]=first.getString("name");
                            //scores[1] = first.getString("score");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject first = response.getJSONObject("thirdVideo");
                            names[2]=first.getString("name");
                            //scores[2] = first.getString("score");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //game list
                        gridView = findViewById(R.id.gridView);
                        CustomAdapter customAdapter = new CustomAdapter(names, images, videos, scores, getApplicationContext());
                        gridView.setAdapter(customAdapter);

                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String selectedTitle = names[i];
                                int selectedImage = images[i];
                                int selectedVideo = videos[i];
                                //String selectedScore = scores[i];

                                startActivity(new Intent(SelectVideoActivity.this, RankingActivity.class).putExtra("title", selectedTitle).putExtra("image", selectedImage).putExtra("video", selectedVideo));
                            }
                        });
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




    }

    //google login
    private void gotoLoginActivity() {
        startActivity(new Intent(SelectVideoActivity.this, LogInActivity.class));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            name.setText(account.getDisplayName());
            //Picasso.get().load(account.getPhotoUrl()).placeholder(R.mipmap.ic_launcher_round).into(profile_image);
        } else {
//            gotoLoginActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }

    //game list
    public class CustomAdapter extends BaseAdapter{
        private String[] imageNames;
        private int[] imagePhoto;
        private int[] videos;
        private Context context;
        private String[] scores;
        private LayoutInflater layoutInflater;

        public CustomAdapter(String[] imageNames, int[] imagePhoto, int[] videos, String[] scores, Context context){
            this.imageNames = imageNames;
            this.imagePhoto = imagePhoto;
            this.scores = scores;
            this.videos = videos;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagePhoto.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null){
                view = layoutInflater.inflate(R.layout.row_data, viewGroup, false);

            }
            TextView vTitle = view.findViewById(R.id.vTitle);
            ImageView imageView = view.findViewById(R.id.imageView);

            vTitle.setText(imageNames[i]);
            imageView.setImageResource(imagePhoto[i]);

            return view;
        }
    }

}
package com.example.bookapp.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {
    private TextView userName, uploadImage;
    private ImageView avatar, imagePost, ivBack;
    private Button btnPost;
    private  EditText tcontent;
    private String access_token;
    private  final  int GALLERY_REQ_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpost);

        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            userName = findViewById(R.id.tvPostAuthor);
            avatar= findViewById(R.id.userPostImage);
            imagePost= findViewById(R.id.imgPost);
            uploadImage = findViewById(R.id.tvUpload);
            tcontent= findViewById(R.id.tvContent);
            btnPost= findViewById(R.id.btnPost);
            ivBack = findViewById(R.id.ivBack);
            User user = SharedPrefManager.getInstance(this).getUser();
            access_token = user.getAccess_token();
            userName.setText(user.getFull_name());
            Glide.with(this).load(user.getAvatar()).into(avatar);
            uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent iGalley = new Intent(Intent.ACTION_PICK);
                  iGalley.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  startActivityForResult(iGalley,GALLERY_REQ_CODE);
                }
            });
            btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreatePost(access_token);
                }
            });

            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(CreatePostActivity.this, MainActivity.class));
                }
            });
        }

    }
    @Override
    protected  void onActivityResult(int requestCode, int resultCode,@NonNull Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode==GALLERY_REQ_CODE){
                //for galley
                imagePost.setImageURI(data.getData());
            }
        }
    }
    void  CreatePost(String access_token){
        String url = "http://10.0.2.2:5000/api/post/";
        String contentPost = tcontent.getText().toString();
        if (TextUtils.isEmpty(contentPost)) {
            Toast.makeText(this, "Hôm nay bạn nghĩ gì?", Toast.LENGTH_SHORT).show();
            tcontent.requestFocus();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getInt("err") == 0) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();


                                startActivity(new Intent(CreatePostActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization",  access_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("tcontent", contentPost);
                return params;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);

    }
}

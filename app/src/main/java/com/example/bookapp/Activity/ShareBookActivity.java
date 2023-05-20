package com.example.bookapp.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.RealPathUtil;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.Interface.ApiService;
import com.example.bookapp.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShareBookActivity extends AppCompatActivity {
    private TextView userName, uploadImage;
    private ImageView avatar, imagePost, ivBack;
    private Button btnPost;
    private EditText tcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharebook);
        Integer bookId = getIntent().getIntExtra("book_id", 0);
        String bookImage = getIntent().getStringExtra("book_image");

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            userName = findViewById(R.id.tvPostAuthor);
            avatar = findViewById(R.id.userPostImage);
            imagePost = findViewById(R.id.imgPost);
            tcontent = findViewById(R.id.tvContent);
            btnPost = findViewById(R.id.btnPost);
            ivBack = findViewById(R.id.ivBack);
            User user = SharedPrefManager.getInstance(this).getUser();
            userName.setText(user.getFull_name());
            Glide.with(this).load(bookImage).into(imagePost);
            Glide.with(this).load(user.getAvatar()).into(avatar);
            btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnPost.setEnabled(false);
                    shareBook(bookId, bookImage, user.getAccess_token());
                }
            });
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    public void shareBook(Integer bookId, String image, String access_token) {
        String tcontent = this.tcontent.getText().toString().trim();
        if (TextUtils.isEmpty(tcontent)) {
            this.tcontent.setError("Vui lòng nhập nội dung");
            this.tcontent.requestFocus();
            btnPost.setEnabled(true);
            return;
        }
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, "http://10.0.2.2:5000/api/post/share-book",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ShareBookActivity.this, "Chia sẻ sách thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnPost.setEnabled(true);
                        Toast.makeText(ShareBookActivity.this, "Chia sẻ sách thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", access_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("book_id", bookId.toString());
                params.put("tcontent", tcontent);
                params.put("image", image);
                return params;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }

}

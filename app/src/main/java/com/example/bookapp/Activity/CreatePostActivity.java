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

import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.RealPathUtil;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Interface.ApiService;
import com.example.bookapp.R;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatePostActivity extends AppCompatActivity {
    private TextView userName, uploadImage;
    private ImageView avatar, imagePost, ivBack;
    private Button btnPost;
    private EditText tcontent;
    private String filePath;
    private ApiService apiService;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpost);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            userName = findViewById(R.id.tvPostAuthor);
            avatar = findViewById(R.id.userPostImage);
            imagePost = findViewById(R.id.imgPost);
            uploadImage = findViewById(R.id.tvUpload);
            tcontent = findViewById(R.id.tvContent);
            btnPost = findViewById(R.id.btnPost);
            ivBack = findViewById(R.id.ivBack);
            User user = SharedPrefManager.getInstance(this).getUser();
            userName.setText(user.getFull_name());
            Glide.with(this).load(user.getAvatar()).into(avatar);
            uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 10);
                    } else {
                        ActivityCompat.requestPermissions(CreatePostActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
            });
            btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnPost.setEnabled(false);
                    createPost(user.getAccess_token());
                }
            });
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            // Create Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:5000/") // Update with your API base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create API service
            apiService = retrofit.create(ApiService.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            Uri picUri = data.getData();
            filePath = RealPathUtil.getRealPath(this, picUri);
            bitmap = BitmapFactory.decodeFile(filePath);
            imagePost.setImageBitmap(bitmap);
        }
    }

    private MultipartBody.Part prepareImageFilePart(String partName, String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public void createPost(String access_token) {
        // Check if filePath is null or empty
        if (TextUtils.isEmpty(filePath)) {
            // Handle error when no image is selected
            Toast.makeText(this, "Chưa chọn hình ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create MultipartBody.Part from filePath
        MultipartBody.Part imagePart = prepareImageFilePart("image", filePath);

        // Prepare content to upload
        RequestBody contentPost = RequestBody.create(MediaType.parse("text/plain"), tcontent.getText().toString());

        // Make API call to upload image
        Call<ResponseBody> call = apiService.createPost(access_token, imagePart, contentPost);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle successful upload
                    Toast.makeText(getApplicationContext(), "Tải lên thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Handle unsuccessful upload
                    Toast.makeText(getApplicationContext(), "Lỗi tải lên ảnh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure during upload
                Toast.makeText(getApplicationContext(), "Lỗi tải lên ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

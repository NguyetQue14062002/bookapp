package com.example.bookapp.Activity;

import static android.content.ContentValues.TAG;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.RealPathUtil;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleyMultipartRequest;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.Interface.ApiService;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

public class UpdateProfileActivity extends AppCompatActivity {
    private ImageView quit, avatarUpdate;
    private EditText dtFullname, dtPhone;
    private TextView etEmail;
    private Button btnUpdate, btnChooseImg;
    private String filePath;
    private Bitmap bitmap;

    private ApiService apiService;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);
        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            User user= SharedPrefManager.getInstance(this).getUser();
            getData(user.getAccess_token());
            quit= findViewById(R.id.exitProfile);
            dtFullname = findViewById(R.id.etFullnameProUpdate);
            dtPhone= findViewById(R.id.etPhoneProUpdate);
            etEmail = findViewById(R.id.tvEmailProUpdate);
            avatarUpdate= findViewById(R.id.imageUpdatePro);
            btnUpdate= findViewById(R.id.btnUpdateIn4);
            btnChooseImg= findViewById(R.id.btnChooseAvt);

            if (user.getAvatar() != null)
                Glide.with(this).load(user.getAvatar()).into(avatarUpdate);
            quit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            btnChooseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 10);
                    } else {
                        ActivityCompat.requestPermissions(UpdateProfileActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
            });
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateAvatar(user.getAccess_token());
                    updateProfile(user.getAccess_token());
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
            avatarUpdate.setImageBitmap(bitmap);
        }
    }

    private MultipartBody.Part prepareImageFilePart(String partName, String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public void updateAvatar(String access_token) {
        if (filePath == null) {
            return;
        }
        MultipartBody.Part imagePart = prepareImageFilePart("avatar", filePath);

        // Make API call to upload image
        Call<ResponseBody> call = apiService.uploadAvatar(access_token, imagePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        if (obj.getInt("err") == 0) {
                            SharedPreferences.Editor user = getSharedPreferences("volleyregisterlogin", MODE_PRIVATE).edit();
                            user.putString("key_avatar", obj.getString("avatar"));
                            user.commit();
                            finish();
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
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

    void updateProfile(String access_token){
       String  url = "http://10.0.2.2:5000/api/user/profile";
       String fullnameEdit = dtFullname.getText().toString();
       String phoneEdit= dtPhone.getText().toString();
       User user= SharedPrefManager.getInstance(this).getUser();
       if (fullnameEdit == user.getFull_name() && phoneEdit == user.getPhone_number()){
           return;
       }
       StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
               new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getInt("err") == 0) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor user = getSharedPreferences("volleyregisterlogin", MODE_PRIVATE).edit();
                                user.putString("key_full_name", fullnameEdit);
                                user.putString("key_phone_number", phoneEdit);
                                user.commit();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization",  access_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("phone_number", phoneEdit);
                params.put("full_name", fullnameEdit);

                return params;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(putRequest);
    }
    private void getData(String access_token) {
        // String Request initialized
        StringRequest stringRequest = new StringRequest(Request.Method.GET, " http://10.0.2.2:5000/api/user", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);
                    //if no error in response
                    if (obj.getInt("err") == 0) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("userData");
                        String email= userJson.getString("email");
                        String phone= userJson.getString("phone_number");
                        String fullname= userJson.getString("full_name");
                        etEmail.setText(email);
                        dtFullname.setText(fullname);
                        dtPhone.setText(phone);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", access_token);
                return headers;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }
}
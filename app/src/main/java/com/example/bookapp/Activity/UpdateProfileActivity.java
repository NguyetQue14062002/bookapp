package com.example.bookapp.Activity;

import static android.content.ContentValues.TAG;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleyMultipartRequest;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    private ImageView quit, avatarUpdate;
    private EditText dtFullname, dtPhone;
    private TextView etEmail;
    private Button btnUpdate, btnChooseImg;
    private String access_token;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private String filePath;
    private Bitmap bitmap;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);
        getData();
        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            quit= findViewById(R.id.exitProfile);
            dtFullname = findViewById(R.id.etFullnameProUpdate);
            dtPhone= findViewById(R.id.etPhoneProUpdate);
            etEmail = findViewById(R.id.tvEmailProUpdate);
            avatarUpdate= findViewById(R.id.imageUpdatePro);
            btnUpdate= findViewById(R.id.btnUpdateIn4);
            btnChooseImg= findViewById(R.id.btnChooseAvt);
            User user= SharedPrefManager.getInstance(this).getUser();
            access_token = user.getAccess_token();
            if (user.getAvatar() != null)
                Glide.with(this).load(user.getAvatar()).into(avatarUpdate);
            quit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UpdateProfileActivity.this, ProfileActivity.class));
                }
            });
            btnChooseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
                }
            });
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateAvatar(bitmap, access_token);
                    updateProfile(access_token);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Uri picUri = data.getData();
        filePath = getPath(picUri);
        if (filePath != null) {
            try {
                Log.d("filePath", String.valueOf(filePath));
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                avatarUpdate.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(UpdateProfileActivity.this, "no image selected",
                    Toast.LENGTH_LONG).show();
        }
    }

    public String getPath(Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(UpdateProfileActivity.this, uri)) {
            // Uri là một Uri tài liệu
            if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider
                final String documentId = DocumentsContract.getDocumentId(uri);
                final String[] split = documentId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO: Xử lý các thẻ SD card khác
            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                filePath = getDataColumn(contentUri, null, null);

            } else if (isMediaDocument(uri)) {
                // MediaProvider
                final String documentId = DocumentsContract.getDocumentId(uri);
                final String[] split = documentId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                filePath = getDataColumn(contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Nếu không phải Uri tài liệu, sử dụng phương pháp thông thường để lấy đường dẫn
            filePath = getDataColumn(uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // Nếu là Uri file, trực tiếp lấy đường dẫn
            filePath = uri.getPath();
        }

        return filePath;
    }

    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        String filePath = null;
        final String column = "_data";
        final String[] projection = {column};

        try (Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                filePath = cursor.getString(column_index);
            }
        }
        return filePath;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void updateAvatar(final Bitmap bitmap, String access_token) {

        if (bitmap == null) {
            return;
        }

        String  url = "http://10.0.2.2:5000/api/user/avatar";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if (obj.getInt("err") == 0) {
                                SharedPreferences.Editor user = getSharedPreferences("volleyregisterlogin", MODE_PRIVATE).edit();
                                user.putString("key_avatar", obj.getString("avatar"));
                                user.commit();
                                finish();
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("avatar", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", access_token);
                return params;
            }

        };
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    void updateProfile(String access_token){
        String  url = "http://10.0.2.2:5000/api/user/profile";
        String fullnameEdit = dtFullname.getText().toString();
        String phoneEdit= dtPhone.getText().toString();
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
    private void getData() {


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
                headers.put("Authorization",  access_token);
                return headers;
            }
        };

        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }
}
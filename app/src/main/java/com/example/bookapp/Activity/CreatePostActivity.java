package com.example.bookapp.Activity;

import android.content.ContentUris;
import android.content.Intent;
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
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {
    private TextView userName, uploadImage;
    private ImageView avatar, imagePost, ivBack;
    private Button btnPost;
    private EditText tcontent;
    private String access_token, filePath;
    private Bitmap bitmap;
    private final int GALLERY_REQ_CODE = 1000;

    private static final int REQUEST_IMAGE_GALLERY = 1;


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
            access_token = user.getAccess_token();
            userName.setText(user.getFull_name());
            Glide.with(this).load(user.getAvatar()).into(avatar);
            uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
                }
            });
            btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bitmap == null && TextUtils.isEmpty(tcontent.getText().toString())) {
                        Toast.makeText(CreatePostActivity.this, "Vui lòng điền nội dung để đăng bài!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    createBitmapPost(bitmap, access_token);
                    createContentPost(access_token);
                    createBitmap(bitmap, access_token);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Uri picUri = data.getData();
        filePath = getPath(picUri);
        if (filePath != null) {
            try {
                Log.d("filePath", String.valueOf(filePath));
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                imagePost.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(
                    CreatePostActivity.this, "no image selected",
                    Toast.LENGTH_LONG).show();
        }
    }

    public String getPath(Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(CreatePostActivity.this, uri)) {
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

    public void createBitmapPost(final Bitmap bitmap, String access_token) {

        if (bitmap == null) {
            return;
        }

        String contentPost = tcontent.getText().toString();
        if (TextUtils.isEmpty(contentPost)) {
            Toast.makeText(this, "Hôm nay bạn nghĩ gì?", Toast.LENGTH_SHORT).show();
            tcontent.requestFocus();
            return;
        }
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "http://10.0.2.2:5000/api/post/",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
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
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", access_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("tcontent", contentPost);
                return params;
            }

        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public void createContentPost(String access_token) {
        String contentPost = tcontent.getText().toString();
        if (TextUtils.isEmpty(contentPost)) {
            Toast.makeText(this, "Hôm nay bạn nghĩ gì?", Toast.LENGTH_SHORT).show();
            tcontent.requestFocus();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/post/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Lỗi tạo bài viết", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreatePostActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("GotError", "" + error.getMessage());
                        finish();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String,String>();
                headers.put("Authorization", access_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("tcontent", contentPost);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void createBitmap(final Bitmap bitmap, String access_token) {

        if (bitmap == null) {
            return;
        }
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "http://10.0.2.2:5000/api/post/",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
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
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", access_token);
                return headers;
            }

        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}
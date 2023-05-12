package com.example.bookapp.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    private ImageView quit, avatarUpdate;
    private EditText dtFullname, dtPhone;
    private TextView etEmail;
    private Button btnUpdate;
    private String access_token;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);

        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            quit= findViewById(R.id.exitProfile);
            dtFullname = findViewById(R.id.etFullnameProUpdate);
            dtPhone= findViewById(R.id.etPhoneProUpdate);
            etEmail = findViewById(R.id.tvEmailProUpdate);
            avatarUpdate= findViewById(R.id.imageUpdatePro);
            btnUpdate= findViewById(R.id.btnUpdateIn4);

            User user= SharedPrefManager.getInstance(this).getUser();
            access_token = user.getAccess_token();
            dtFullname.setText(user.getFull_name());
            etEmail.setText(user.getEmail());
            dtPhone.setText(user.getPhone_number());
            Glide.with(this).load(user.getAvatar()).into(avatarUpdate);

            quit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UpdateProfileActivity.this, ProfileActivity.class));
                }
            });
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UpdateProfile(access_token);
                    finish();
                }
            });
        }
    }
    void UpdateProfile(String access_token){
       String  url = "http://10.0.2.2:5000/api/user/profile";
       String fullnameEdit = dtFullname.getText().toString();
       String phoneEdit= dtPhone.getText().toString();
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
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
}
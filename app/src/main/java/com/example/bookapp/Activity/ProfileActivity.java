package com.example.bookapp.Activity;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Fragment.HomeFragment;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvfull_name, tvemail, tvsdt;
    private ImageView avatarPro, exit;
    private Button btnResetpass, btnUpdate;
    private String access_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getData();
        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            tvfull_name = findViewById(R.id.tvFullnameProfile);
            tvemail = findViewById(R.id.tvEmailProfile);
            tvsdt = findViewById(R.id.tvPhoneProfile);
            exit = findViewById(R.id.exitProfile);
            avatarPro = findViewById(R.id.imageProfile);
            btnResetpass = findViewById(R.id.btnResetPass);
            btnUpdate = findViewById(R.id.btnUpdateProfile);
            User user= SharedPrefManager.getInstance(this).getUser();
            if (user.getRole_id() == 1) {
                btnResetpass.setVisibility(View.GONE);
            }
            access_token = user.getAccess_token();
            if (user.getAvatar() == null)
                avatarPro.setImageResource(R.drawable.defaultavt);
            else
                Glide.with(this).load(user.getAvatar()).into( avatarPro);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            btnResetpass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProfileActivity.this, OTPActivity.class ));
                }
            });
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class));
                }
            });
        }

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
                            tvemail.setText(email);
                            tvfull_name.setText(fullname);
                            tvsdt.setText(phone);
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

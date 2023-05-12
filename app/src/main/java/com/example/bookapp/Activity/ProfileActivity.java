package com.example.bookapp.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            tvfull_name= findViewById(R.id.tvFullnameProfile);
            tvemail= findViewById(R.id.tvEmailProfile);
            tvsdt= findViewById(R.id.tvPhoneProfile);
            exit= findViewById(R.id.exitProfile);
            avatarPro = findViewById(R.id.imageProfile);
            btnResetpass= findViewById(R.id.btnResetPass);
            btnUpdate = findViewById(R.id.btnUpdateProfile);
            User user= SharedPrefManager.getInstance(this).getUser();
            tvfull_name.setText(user.getFull_name());
            tvemail.setText(user.getEmail());
            tvsdt.setText(user.getPhone_number());
            Glide.with(this).load(user.getAvatar()).into(avatarPro);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                }
            });
            btnResetpass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProfileActivity.this,ResetPassActivity.class ));
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

}

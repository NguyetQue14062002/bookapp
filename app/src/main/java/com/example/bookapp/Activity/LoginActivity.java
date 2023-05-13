package com.example.bookapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.Constants;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Constants constants;
    private EditText etmail, etpass;
    private TextView tvRegister,  tvFogetPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


//        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
//            finish();
//            startActivity(new Intent(this, MainActivity.class));
//        }

        etmail = findViewById(R.id.tvEmailLogin);
        etpass = findViewById(R.id.tvPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvFogetPassword= findViewById(R.id.tvFogortPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        tvFogetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, OTPActivity.class));

            }
        });
    }

    private void login() {
        String email, password;
        email = etmail.getText().toString();
        password = etpass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email !", Toast.LENGTH_SHORT).show();
            etmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập password !", Toast.LENGTH_SHORT).show();
            etpass.requestFocus();
            return;
        }


        //If everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/account/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getInt("err") == 0) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user_data");
                                String avatar = userJson.getString("avatar");
                                if (avatar == "null")
                                    avatar = null;

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getInt("role_id"),
                                        userJson.getString("email"),
                                        userJson.getString("full_name"),
                                        avatar,
                                        userJson.getString("token"),
                                        userJson.getString("phone_number"),
                                        obj.getString("access_token"),
                                        obj.getString("refresh_token")
                                );
                                //storing the user in shared preferences

                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
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
                        if (error.getMessage() != null) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);

    }

}

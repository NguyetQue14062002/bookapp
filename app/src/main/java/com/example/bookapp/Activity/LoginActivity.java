package com.example.bookapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Constants constants;
    private EditText etmail, etpass;
    private TextView tvRegister,  tvFogetPassword;
    private Button btnLogin;
    private CheckBox MemoryPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etmail = findViewById(R.id.tvEmailLogin);
        etpass = findViewById(R.id.tvPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvFogetPassword= findViewById(R.id.tvFogortPass);
        MemoryPass= findViewById(R.id.checkBox);

        MemoryPass= findViewById(R.id.checkBox);
        MemoryPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){

                    etpass.setTransformationMethod(PasswordTransformationMethod.getInstance());

                } else {
                    etpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        TextView txt = findViewById(R.id.editTextNewAcc2);
        txt.setPaintFlags(txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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
                                if (avatar == "null") {
                                    avatar = null;
                                }

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

                                //get history
                                ArrayList<Integer> history = new ArrayList<>();
                                JSONArray historyArray = obj.getJSONArray("history");
                                for (int i = 0; i < historyArray.length(); i++) {
                                    history.add(historyArray.getJSONObject(i).getInt("book_id"));
                                }
                                //storing the user in shared preferences

                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                SharedPrefManager.getInstance(getApplicationContext()).setHistory(history);
                                Log.d("History", String.valueOf(history.size()));
                                //if it is user
                                if (user.getRole_id() == 3) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else if (user.getRole_id() == 1) {
                                    //if it is admin
                                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                }
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

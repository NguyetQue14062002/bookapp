package com.example.bookapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private TextView tvUserName, tvEmail, tvPhoneNumber, tvPassword, tvConfirmPass;

    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmailRegister);
        tvPhoneNumber = findViewById(R.id.tvSdt);
        tvPassword = findViewById(R.id.tvPassRegister);
        tvConfirmPass = findViewById(R.id.tvPassConfirm);
        registerBtn = findViewById(R.id.btnRegister);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register() {
        String username = tvUserName.getText().toString();
        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();
        String password_confirm = tvConfirmPass.getText().toString();
        String phone_number = tvPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Vui lòng nhập username !", Toast.LENGTH_SHORT).show();
            tvUserName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập password !", Toast.LENGTH_SHORT).show();
            tvPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password_confirm)) {
            Toast.makeText(this, "Vui lòng nhập password confirm !", Toast.LENGTH_SHORT).show();
            tvConfirmPass.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone_number)) {
            Toast.makeText(this, "Vui lòng nhập phone number !", Toast.LENGTH_SHORT).show();
            tvPhoneNumber.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email !", Toast.LENGTH_SHORT).show();
            tvEmail.requestFocus();
            return;
        }

        if (!password.equals(password_confirm)) {
            Toast.makeText(this, "Password va Confirm Password khong giong nhau !", Toast.LENGTH_SHORT).show();
            tvConfirmPass.clearComposingText();
            tvConfirmPass.requestFocus();
            return;
        }

        //If everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/auth/register",
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
//                                JSONObject userJson = obj.getJSONObject("user");

                                // TODO: Get user data from response
                                //creating a new user object
//                                User user = new User(
//                                        userJson.getInt("id"),
//                                        userJson.getString("username"),
//                                        userJson.getString("email"),
//                                        userJson.getString("gender"),
//                                        userJson.getString("images")
//                                );
//                                //storing the user in shared preferences
//                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("full_name", username);
                params.put("phone_number", phone_number);
                return params;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }
}
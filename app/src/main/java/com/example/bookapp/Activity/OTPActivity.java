package com.example.bookapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OTPActivity extends AppCompatActivity {
    private EditText dtEmail, otp;
    private TextView  sendEmailAgain;
    private ImageView btnCheck;
    private Button btnSend;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendotp);

        dtEmail= findViewById(R.id.tvEmailOtp);
        otp= findViewById(R.id.edOTP);
        btnSend=findViewById(R.id.btnSendMail);
        btnCheck= findViewById(R.id.btnCheck);
        sendEmailAgain= findViewById(R.id.tvAgain);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendEmail();
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOpt();
            }
        });
        sendEmailAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendEmail();
            }
        });
    }
    private void SendEmail(){
        String email;
        email = dtEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email !", Toast.LENGTH_SHORT).show();
            dtEmail.requestFocus();
            return;
        }

        //If everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/auth/send-otp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getInt("err") == 0) {
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
                return params;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void  checkOpt(){
        String OTP, email;
        OTP = otp.getText().toString();
        email = dtEmail.getText().toString();
        //If everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/auth/check-otp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getInt("err") == 0) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OTPActivity.this, ResetPassActivity.class);
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
                params.put("otp", OTP);
                return params;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }
}

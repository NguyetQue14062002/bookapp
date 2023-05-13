package com.example.bookapp.Activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassActivity extends AppCompatActivity {
    private EditText pass, confirmpass;
    private Button btnReset;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);
        pass= findViewById(R.id.edPasswordNew);
        confirmpass= findViewById(R.id.edConfirm);
        btnReset= findViewById(R.id.btnResetPass);

        btnReset.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ResetPass();
                    }
                }
        );
    }
    private void ResetPass(){
        String  url = "http://10.0.2.2:5000/api/auth/reset-password";
        String password = pass.getText().toString();
        String password_confirm= confirmpass.getText().toString();
        if (!password.equals(password_confirm)) {
            Toast.makeText(this, "Mat khau khong giong nhau !", Toast.LENGTH_SHORT).show();
           confirmpass.clearComposingText();
           confirmpass.requestFocus();
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
                                startActivity( new Intent(ResetPassActivity.this, ProfileActivity.class));
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
                String access_token= SharedPrefManager.getInstance(getApplicationContext()).getUser().getAccess_token();
                headers.put("Authorization",  access_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                String email= SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        VolleySingle.getInstance(this).addToRequestQueue(putRequest);
    }
}
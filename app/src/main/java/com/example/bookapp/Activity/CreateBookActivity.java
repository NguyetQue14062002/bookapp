package com.example.bookapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookapp.Domain.Publisher;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateBookActivity extends AppCompatActivity {
    private EditText etTitle, etAuth, etDescripton, etPage, etLink, etUrlImage;
    private Spinner spPulisher, spCategory, spStatus;
    private Button btnAdd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createbook);

        etTitle = findViewById(R.id.tvTitle);
        etAuth = findViewById(R.id.tvAuthor);
        etDescripton = findViewById(R.id.tvDescripton);
        etPage = findViewById(R.id.tvTotalPage);
        etLink = findViewById(R.id.tvLink);
        etUrlImage = findViewById(R.id.tvUrlImage);
        spCategory = findViewById(R.id.spinnerCategory);
        spPulisher = findViewById(R.id.spinnerPublisher);
        spStatus = findViewById(R.id.spinnerStatus);
        btnAdd = findViewById(R.id.btnCreate);
        ItemPublisher();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateBook();
            }
        });

    }
    private void CreateBook(){
        String title= etTitle.getText().toString();
        String author = etAuth.getText().toString();
        String description= etDescripton.getText().toString();
        String page= etPage.getText().toString();
        String link= etLink.getText().toString();
        String image= etUrlImage.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/book/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getInt("err") == 0) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(CreateBookActivity.this, MainActivity.class));
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
                params.put("title", title);
                params.put("author", author);
                params.put("description", description);
                params.put("page_number", page);
                params.put("image_url", image);
                params.put("link", link);
                return params;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void ItemPublisher()  {
        //ToDo
       //Api Publisher  trong spinner
        }
    public void ItemStatus()  {
        //ToDo
        //Api Publisher  trong spinner
    }
    public void ItemCategory()  {
        //ToDo
        //Api Publisher  trong spinner
    }

}

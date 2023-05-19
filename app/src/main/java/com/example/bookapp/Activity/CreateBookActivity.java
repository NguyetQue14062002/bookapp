package com.example.bookapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Domain.Category;
import com.example.bookapp.Domain.Publisher;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateBookActivity extends AppCompatActivity {
    private EditText etTitle, etAuth, etDescripton, etPage, etLink, etUrlImage;
    private Spinner spPublisher, spCategory;
    private Button btnAdd;
    private ImageView imgExit;

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
        spPublisher = findViewById(R.id.spinnerPublisher);
        btnAdd = findViewById(R.id.btnCreate);
        imgExit = findViewById(R.id.imgExitBookCreate);

        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(CreateBookActivity.this, AdminActivity.class));
            }
        });

        ItemPublisher();
        ItemCategory();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateBook();
            }
        });


    }
    /*
    title": "AQQ",
    "author": "AB",
    "publisher_id": 1,
    "description": "AB",
    "page_number": 200,
    "category_id": 7,
    "image_url": "sdfg.img",
    "link": "dsfghg.com
     */
    private void CreateBook(){
        JSONObject jsonObject = new JSONObject();
        Publisher publisher = (Publisher) spPublisher.getSelectedItem();
        Category category = (Category) spCategory.getSelectedItem();
        try {
            jsonObject.put("title", etTitle.getText().toString());
            jsonObject.put("author", etAuth.getText().toString());
            jsonObject.put("publisher_id", publisher.getId());
            jsonObject.put("description", etDescripton.getText().toString());
            jsonObject.put("page_number", etPage.getText().toString());
            jsonObject.put("category_id", category.getId());
            jsonObject.put("image_url", etUrlImage.getText().toString());
            jsonObject.put("link", etLink.getText().toString());
        } catch(Exception e) {

        }

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST, "http://10.0.2.2:5000/api/book/", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting response to json object
                            //if no error in response
                            if (response.getInt("err") == 0) {
                                Toast.makeText(CreateBookActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response


                            } else {
                                Toast.makeText(CreateBookActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(CreateBookActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", SharedPrefManager.getInstance(CreateBookActivity.this).getUser().getAccess_token());
                return params;
            }

        };
        VolleySingle.getInstance(this).addToRequestQueue(request);
    }

    public void ItemPublisher()  {
        //ToDo
       //Get all publishers
        ArrayList<Publisher> publishers = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/publisher/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                JSONObject data = obj.getJSONObject("data");
                                JSONArray allPublishers = data.getJSONArray("rows");
                                for (int i = 0; i < allPublishers.length(); i++) {
                                    JSONObject object = allPublishers.getJSONObject(i);
                                    Publisher publisher = new Publisher(
                                            object.getInt("id"),
                                            object.getString("name")
                                    );

                                    publishers.add(publisher);
                                }
                                //Set  adapter for publisher spinner

                                ArrayAdapter<Publisher> adapter = new ArrayAdapter<Publisher>(CreateBookActivity.this, android.R.layout.simple_spinner_dropdown_item, publishers);
                                spPublisher.setAdapter(adapter);
                                Log.d("publishers", String.valueOf(publishers.size()));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);



    }

    public void ItemCategory()  {
        //ToDo
        //Get all categories
        ArrayList<Category> categories = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/category/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                JSONObject data = obj.getJSONObject("data");
                                JSONArray allCategories = data.getJSONArray("rows");
                                for (int i = 0; i < allCategories.length(); i++) {
                                    JSONObject object = allCategories.getJSONObject(i);
                                    Category category = new Category(
                                            object.getInt("id"),
                                            object.getString("name")
                                    );

                                    categories.add(category);
                                }
                                //Set  adapter for publisher spinner

                                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(CreateBookActivity.this, android.R.layout.simple_spinner_dropdown_item, categories);
                                spCategory.setAdapter(adapter);
                                Log.d("categories", String.valueOf(categories.size()));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }

}

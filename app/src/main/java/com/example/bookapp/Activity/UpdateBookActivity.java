package com.example.bookapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Domain.Book;
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

public class UpdateBookActivity extends AppCompatActivity {

    private EditText etTitle, etAuth, etDescripton, etPage, etLink, etUrlImage;
    private Spinner spPublisher, spCategory, spStatus;
    private Button btnUpdate;
    private ImageView imgExit;

    Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);

        etTitle = findViewById(R.id.tvTitleUpdate);
        etAuth = findViewById(R.id.tvAuthorUpdate);
        etDescripton = findViewById(R.id.tvDescriptonUpdate);
        etPage = findViewById(R.id.tvTotalPageUpdate);
        etLink = findViewById(R.id.tvLinkUpdate);
        etUrlImage = findViewById(R.id.tvUrlImageUpdate);
        spCategory = findViewById(R.id.spinnerCategoryUpdate);
        spPublisher = findViewById(R.id.spinnerPublisherUpdate);
        spStatus = findViewById(R.id.spinnerStatusUpdate);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgExit = findViewById(R.id.imgExitUpdateBook);

        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(UpdateBookActivity.this, AdminActivity.class));
            }
        });

        book = (Book) getIntent().getSerializableExtra("myBook");

        Log.d("BookUpdate", book.toString());

        etTitle.setText(book.getTitle());
        etAuth.setText(book.getAuthor());
        etDescripton.setText(book.getDescription());
        etPage.setText(String.valueOf(book.getPage_number()));
        etLink.setText(book.getLink());
        etUrlImage.setText(book.getImage_url());


        ItemPublisher();
        ItemCategory();
        ItemStatus();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBook();
            }
        });
    }
    /*
    "id": 1,
    "title": "Huyen",
    "author": "Que",
    "publisher_id": 1,
    "description": "bfx",
    "page_number": 123,
    "category_id": 1,
    "image_url": "sdfg.img",
    "link": "dsfghg.com",
    "status_id": 2
     */

    private void updateBook(){
        JSONObject jsonObject = new JSONObject();
        Publisher publisher = (Publisher) spPublisher.getSelectedItem();
        Category category = (Category) spCategory.getSelectedItem();
        int status_id = (Integer) spStatus.getSelectedItem();

        try {
            jsonObject.put("id", book.getId());
            jsonObject.put("title", etTitle.getText().toString());
            jsonObject.put("author", etAuth.getText().toString());
            jsonObject.put("publisher_id", publisher.getId());
            jsonObject.put("description", etDescripton.getText().toString());
            jsonObject.put("page_number", etPage.getText().toString());
            jsonObject.put("category_id", category.getId());
            jsonObject.put("image_url", etUrlImage.getText().toString());
            jsonObject.put("link", etLink.getText().toString());
            jsonObject.put("status_id", status_id);
        } catch(Exception e) {

        }

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.PUT, "http://10.0.2.2:5000/api/book/", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting response to json object
                            //if no error in response
                            if (response.getInt("err") == 0) {
                                Toast.makeText(UpdateBookActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response


                            } else {
                                Toast.makeText(UpdateBookActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(UpdateBookActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", SharedPrefManager.getInstance(UpdateBookActivity.this).getUser().getAccess_token());
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

                                ArrayAdapter<Publisher> adapter = new ArrayAdapter<Publisher>(UpdateBookActivity.this, android.R.layout.simple_spinner_dropdown_item, publishers);
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

                                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(UpdateBookActivity.this, android.R.layout.simple_spinner_dropdown_item, categories);
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

    public void ItemStatus() {
        ArrayList<Integer> statuses = new ArrayList<>();
        statuses.add(1);
        statuses.add(2);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(UpdateBookActivity.this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spStatus.setAdapter(adapter);
    }
}
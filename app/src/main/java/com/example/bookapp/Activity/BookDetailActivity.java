package com.example.bookapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView addToListBtn, backBtn, imgBook;

    private TextView bookName, author, description;

    private Book book;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        currentUser = SharedPrefManager.getInstance(this).getUser();


        initData();





    }

    private void initData() {
        book = (Book) getIntent().getSerializableExtra("myBook");
        addToListBtn = findViewById(R.id.addToListBtn);
        backBtn = findViewById(R.id.backBtn);

        imgBook = findViewById(R.id.imgBook);
        bookName = findViewById(R.id.tvBookName);
        author = findViewById(R.id.tvAuthor);
        description = findViewById(R.id.tvIntro);


        // Add eventListener for buttons
        addToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(BookDetailActivity.this, addToListBtn);
                popup.getMenuInflater()
                        .inflate(R.menu.book_list, popup.getMenu());

                //TH1: Neu sach da co trong history roi

                if (book.getStatus_id() != 1 && book.getStatus_id() != 2) {


                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.reading:
                                    book.setStatus_id(4);
                                    break;

                                case R.id.done:
                                    book.setStatus_id(5);
                                    break;

                                case R.id.unread:
                                    book.setStatus_id(3);
                                    break;

                            }
                            updateHistory();
                            return true;
                        }
                    });
                } else {
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(
                                    BookDetailActivity.this,
                                    "You Clicked : " + item.getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            switch (item.getItemId()) {
                                case R.id.reading:
                                    book.setStatus_id(4);
                                    break;

                                case R.id.done:
                                    book.setStatus_id(5);
                                    break;

                                case R.id.unread:
                                    book.setStatus_id(3);
                                    break;

                            }

                            createHistory();

                            return true;
                        }
                    });
                }


                popup.show(); //showing popup menu

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookDetailActivity.this, MainActivity.class));
            }
        });

        // Init Data for Book
        Glide.with(this).load(book.getImage_url()).into(imgBook);
        bookName.setText(book.getTitle());
        author.setText(book.getAuthor());
        description.setText(book.getDescription());

    }

    private void updateHistory() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("book_id", book.getId());
            jsonObject.put("status_id", book.getStatus_id());
        } catch(Exception e) {

        }

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.PUT, "http://10.0.2.2:5000/api/history/", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting response to json object
                            //if no error in response
                            if (response.getInt("err") == 0) {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response


                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", currentUser.getAccess_token());
                return params;
            }

        };
        VolleySingle.getInstance(this).addToRequestQueue(request);
    }

    private void createHistory() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("book_id", book.getId());
            jsonObject.put("status_id", book.getStatus_id());
        } catch(Exception e) {

        }

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST, "http://10.0.2.2:5000/api/history/", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting response to json object
                            //if no error in response
                            if (response.getInt("err") == 0) {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response


                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", currentUser.getAccess_token());
                return params;
            }

        };
        VolleySingle.getInstance(this).addToRequestQueue(request);
    }
}
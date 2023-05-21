package com.example.bookapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Adapter.ReviewAdapter;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.Review;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView addToListBtn, backBtn, imgBook, ivShare;

    private TextView bookName, author, description, pageNumber, numPeopleRead, tvNumRv;
    private Button btnRead, btnReview;
    private RatingBar ratingBarBook;
    private Book book;
    private User currentUser;
    private RecyclerView rvReview;
    private List<Review> reviews = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        currentUser = SharedPrefManager.getInstance(this).getUser();
        initData();
        ReadBookNum();
    }
    private void initData() {
        book = (Book) getIntent().getSerializableExtra("myBook");
        addToListBtn = findViewById(R.id.addToListBtn);
        backBtn = findViewById(R.id.backBtn);
        imgBook = findViewById(R.id.imgBook);
        bookName = findViewById(R.id.tvBookName);
        author = findViewById(R.id.tvAuthor);
        description = findViewById(R.id.tvIntro);
        pageNumber = findViewById(R.id.tvpageNumber);
        numPeopleRead = findViewById(R.id.tvNumRead);
        ivShare = findViewById(R.id.ivShare);
        btnRead= findViewById(R.id.btnRead);
        btnReview = findViewById(R.id.btnReview);
        ratingBarBook = findViewById(R.id.ratingBarBook);
        tvNumRv = findViewById(R.id.tvNumRv);
        rvReview = findViewById(R.id.rvReview);

        getReviews(book.getId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvReview.setLayoutManager(layoutManager);
        reviewAdapter = new ReviewAdapter(this, reviews);
        rvReview.setAdapter(reviewAdapter);

        if (currentUser.getRole_id() == 1) {
            btnReview.setVisibility(View.GONE);
        }

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, ReadBookActivity.class);
                intent.putExtra("link", book.getLink());
                intent.putExtra("title", book.getTitle());
                createHistory(4);
                startActivity(intent);
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, ReviewActivity.class);
                intent.putExtra("book_id", book.getId());
                startActivity(intent);
            }
        });

        // Add eventListener for buttons
        addToListBtn.setOnClickListener(new View.OnClickListener() {
            int status_id;
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(BookDetailActivity.this, addToListBtn);
                popup.getMenuInflater()
                        .inflate(R.menu.book_list, popup.getMenu());
                popup.show(); //showing popup menu

                //TH1: Neu sach da co trong history roi
                Log.d("book_status_id", String.valueOf(book.getStatus_id()));
                if (SharedPrefManager.getInstance(getApplicationContext()).getHistory().contains(book.getId())) {
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.reading:
                                    status_id = 4;
                                    break;
                                case R.id.done:
                                    status_id = 5;
                                    break;
                                case R.id.unread:
                                    status_id = 3;
                                    break;
                            }
                            updateHistory(status_id);
                            return true;
                        }
                    });
                } else {
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.reading:
                                    status_id = 4;
                                    break;
                                case R.id.done:
                                    status_id = 5;
                                    break;
                                case R.id.unread:
                                    status_id = 3;
                                    break;
                            }
                            createHistory(status_id);
                            return true;
                        }
                    });
                }
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, ShareBookActivity.class);
                intent.putExtra("book_id", book.getId());
                intent.putExtra("book_title", book.getTitle());
                intent.putExtra("book_image", book.getImage_url());
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Init Data for Book
        Glide.with(this).load(book.getImage_url()).into(imgBook);
        bookName.setText(book.getTitle());
        author.setText(book.getAuthor());
        description.setText(book.getDescription());
        pageNumber.setText(String.valueOf(book.getPage_number()));
    }
    private void ReadBookNum(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, " http://10.0.2.2:5000/api/history/?book_id="+book.getId()+"&status_id=4&status_id=5&status_id=6", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getInt("err") == 0) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject userJson = obj.getJSONObject("data");
                        String count= userJson.getString("count");
                        Log.d("ReadBookNum: ",count);
                        numPeopleRead.setText(count);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void updateHistory(int status_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("book_id", book.getId());
            jsonObject.put("status_id", status_id);
        } catch(Exception e) {

        }
        JsonObjectRequest request = new JsonObjectRequest (Request.Method.PUT, "http://10.0.2.2:5000/api/history/", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("err") == 0) {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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

    private void createHistory(int status_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("book_id", book.getId());
            jsonObject.put("status_id", status_id);
        } catch(Exception e) {

        }

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST, "http://10.0.2.2:5000/api/history/", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("err") == 0) {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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

    private void getReviews(Integer bookId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/review?book_id=" + bookId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject data = obj.getJSONObject("data");
                            JSONArray reviewsJson = data.getJSONArray("rows");
                            if (obj.getInt("err") == 0) {
                                float rating = 0;
                                for (int i = 0; i < reviewsJson.length(); i++) {
                                    JSONObject reviewJson = reviewsJson.getJSONObject(i);
                                    JSONObject userJson = reviewJson.getJSONObject("user");
                                    Review review = new Review();
                                    review.setId(reviewJson.getInt("id"));
                                    review.setBook_id(reviewJson.getInt("book_id"));
                                    review.setUser(userJson.getString("full_name"));
                                    review.setTcontent(reviewJson.getString("tcontent"));
                                    review.setRate(reviewJson.getInt("rate"));
                                    rating += reviewJson.getInt("rate");
                                    reviews.add(review);
                                    reviewAdapter.setReviews(reviews);
                                    tvNumRv.setText(reviews.size() + " reviews");
                                }
                                ratingBarBook.setRating(rating / reviewsJson.length());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        });
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }
}
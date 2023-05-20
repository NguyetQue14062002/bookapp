package com.example.bookapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Adapter.ReviewAdapter;
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

public class ReviewActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private TextView content, tvNumRv;
    private Button btnSubmit;
    private RecyclerView rcReview;
    private ReviewAdapter reviewAdapter;

    private List<Review> reviews = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            int bookId = getIntent().getIntExtra("book_id",0);
            ratingBar = findViewById(R.id.ratingBarRv);
            content = findViewById(R.id.tvReview);
            btnSubmit = findViewById(R.id.btnSubmit);
            tvNumRv = findViewById(R.id.tvNumRv);
            rcReview = findViewById(R.id.rcReview);
            getReviews(bookId);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rcReview.setLayoutManager(layoutManager);
            reviewAdapter = new ReviewAdapter(this, reviews);
            rcReview.setAdapter(reviewAdapter);
            tvNumRv.setText(reviews.size() + " reviews");

            User user = SharedPrefManager.getInstance(this).getUser();
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reviewBook(bookId, user.getAccess_token());
                    finish();
                }
            });
        }
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
                                for (int i = 0; i < reviewsJson.length(); i++) {
                                    JSONObject reviewJson = reviewsJson.getJSONObject(i);
                                    JSONObject userJson = reviewJson.getJSONObject("user");
                                    Review review = new Review();
                                    review.setId(reviewJson.getInt("id"));
                                    review.setBook_id(reviewJson.getInt("book_id"));
                                    review.setUser(userJson.getString("full_name"));
                                    review.setTcontent(reviewJson.getString("tcontent"));
                                    review.setRate(reviewJson.getInt("rate"));
                                    reviews.add(review);
                                    reviewAdapter.setReviews(reviews);
                                    tvNumRv.setText(reviews.size() + " reviews");
                                }
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
    private void reviewBook(int idBook, String access_token){
        String tcontent= content.getText().toString();
        String rate= String.valueOf(ratingBar.getRating());
        String book_id = String.valueOf(idBook);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/review/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getInt("err") == 0) {
                                reviewAdapter.notifyDataSetChanged();
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization",  access_token);
                return headers;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("book_id", book_id);
                params.put("tcontent", tcontent);
                params.put("rate", rate);
                return params;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }

}

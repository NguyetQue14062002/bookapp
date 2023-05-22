package com.example.bookapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Activity.BookDetailActivity;
import com.example.bookapp.Domain.Review;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    Context context;
    List<Review> reviews = new ArrayList<>();
    User user;
    private ReviewDeleteListener listener;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (reviews != null) {
            return reviews.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        user = SharedPrefManager.getInstance(context).getUser();
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Review review = reviews.get(position);
        holder.tvReviewer.setText(review.getUser());
        holder.ratingBar.setRating(review.getRate());
        holder.tvContentReview.setText(review.getTcontent());
        holder.ratingBar.setEnabled(false);
        if (user.getRole_id() == 1) {
            holder.ivDeleteRv.setVisibility(View.VISIBLE);
        }
        else {
            holder.ivDeleteRv.setVisibility(View.GONE);
        }
        holder.ivDeleteRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReview(review.getId());
                if (listener != null) {
                    listener.onReviewDeleted(review.getId());
                }
                reviews.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, reviews.size());

            }
        });
    }
    public void setReviewDeleteListener(ReviewDeleteListener listener) {
        this.listener = listener;
    }

    public interface ReviewDeleteListener {
        void onReviewDeleted(int commentId);
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewer, tvContentReview;
        RatingBar ratingBar;
        ImageView ivDeleteRv;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewer = itemView.findViewById(R.id.tvReviewer);
            ratingBar = itemView.findViewById(R.id.ratingBarReview);
            tvContentReview = itemView.findViewById(R.id.tvContentReview);
            ivDeleteRv = itemView.findViewById(R.id.ivDeleteRv);
        }
    }

    public void deleteReview(int id) {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.DELETE, "http://10.0.2.2:5000/api/review?id=" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization", user.getAccess_token());
                return header;
            }
        };
        VolleySingle.getInstance(context).addToRequestQueue(stringRequest);
    }
}

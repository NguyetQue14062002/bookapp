package com.example.bookapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.Domain.Review;
import com.example.bookapp.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    Context context;
    List<Review> reviews = new ArrayList<>();

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
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.tvReviewer.setText(review.getUser());
        holder.ratingBar.setRating(review.getRate());
        holder.tvContentReview.setText(review.getTcontent());
        holder.ratingBar.setEnabled(false);
    }
    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewer, tvContentReview;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewer = itemView.findViewById(R.id.tvReviewer);
            ratingBar = itemView.findViewById(R.id.ratingBarReview);
            tvContentReview = itemView.findViewById(R.id.tvContentReview);
        }
    }
}

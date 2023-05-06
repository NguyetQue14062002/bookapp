package com.example.bookapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.Domain.Post;
import com.example.bookapp.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    Context context;
    ArrayList<Post> posts;

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_post_community,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        // TODO: Binding data for a post
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage, postImage;
        TextView name, createAt, content, numLikes, numShares, numComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            postImage = itemView.findViewById(R.id.postImage);
            name = itemView.findViewById(R.id.tvName);
            createAt = itemView.findViewById(R.id.tvDate);
            content = itemView.findViewById(R.id.tvContent);
            numLikes = itemView.findViewById(R.id.tvNumLike);
            numShares = itemView.findViewById(R.id.tvNumShare);
            numComments = itemView.findViewById(R.id.tvNumCmt);
        }
    }
}

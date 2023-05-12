package com.example.bookapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Domain.Post;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    List<Post> posts = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public PostAdapter() {
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_post_community,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        // TODO: Binding data for a post
        holder.name.setText(post.getUser());
        holder.tcontent.setText(post.getTcontent());
        Integer numLikes = post.getNumLikes();
        if (numLikes != null) {
            holder.numLikes.setText(numLikes.toString());
        } else {
            holder.numLikes.setText("0");
        }
        Integer numShares = post.getNumShares();
        if (numShares != null) {
            holder.numShares.setText(numShares.toString());
        } else {
            holder.numShares.setText("0");
        }
        Integer numComments = post.getNumComments();
        if (numComments != null) {
            holder.numComments.setText(numComments.toString());
        } else {
            holder.numComments.setText("0");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(post);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage, postImage;
        TextView name, createAt, tcontent, numLikes, numShares, numComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            postImage = itemView.findViewById(R.id.postImage);
            name = itemView.findViewById(R.id.tvName);
            tcontent = itemView.findViewById(R.id.tvContent);
            numLikes = itemView.findViewById(R.id.tvNumLike);
            numShares = itemView.findViewById(R.id.tvNumShare);
            numComments = itemView.findViewById(R.id.tvNumCmt);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

}

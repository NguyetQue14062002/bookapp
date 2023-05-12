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
    Context context;
    List<Post> posts = new ArrayList<>();

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

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
        holder.name.setText(post.getUser());
        holder.tcontent.setText(post.getTcontent());
        holder.numLikes.setText(post.getNumLikes());
        holder.numShares.setText(post.getNumShares());
        holder.numComments.setText(post.getNumComments());
    }

    @Override
    public int getItemCount() {
        return posts.size();
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

}

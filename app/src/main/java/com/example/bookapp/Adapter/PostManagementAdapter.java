package com.example.bookapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.Post;
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

public class PostManagementAdapter extends RecyclerView.Adapter<PostManagementAdapter.PostManagementViewHolder>{
    Context context;
    List<Post> posts = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public PostManagementAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_post_manage,parent,false);
        return new PostManagementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostManagementViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = posts.get(position);

        holder.userName.setText(post.getUser());
        holder.content.setText(post.getTcontent());
        Glide.with(context).load(post.getImage()).into(holder.postImage);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost(post.getId());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(post, position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Post post, int position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
    public Post getItem(int position) {
        return posts.get(position);
    }

    public class PostManagementViewHolder extends RecyclerView.ViewHolder{

        ImageView postImage;
        TextView userName, content, updateBtn, deleteBtn;
        public PostManagementViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.postImage);
            userName = itemView.findViewById(R.id.userName);
            content = itemView.findViewById(R.id.contentPost);
            deleteBtn = itemView.findViewById(R.id.tvDeletePost);
        }
    }

    private void deletePost(int id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("status_id", 8);
        } catch(Exception e) {

        }

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.PUT, "http://10.0.2.2:5000/api/post/status", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("err") == 0) {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", SharedPrefManager.getInstance(context).getUser().getAccess_token());
                return params;
            }

        };
        VolleySingle.getInstance(context).addToRequestQueue(request);
    }

}

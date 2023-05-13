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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.Post;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private String access_token;
    List<Post> posts = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    Context context;
    public PostAdapter() {
    }

    public PostAdapter(Context context, String access_token) {
        this.context = context;
        this.access_token = access_token;
    }

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_post_community,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = posts.get(position);
        // TODO: Binding data for a post
        if (post.getImage() != null) {
            Glide.with(context).load(post.getImage()).into(holder.postImage);
        }
        if (post.getAvatar() != null) {
            Glide.with(context).load(post.getAvatar()).into(holder.userImage);
        }
        holder.name.setText(post.getUser());
        holder.tcontent.setText(post.getTcontent());
        Integer numLikes = post.getNumLikes();
        if (numLikes != null) {
            holder.numLikes.setText(numLikes.toString());
        } else {
            holder.numLikes.setText("0");
        }
        Integer numComments = post.getNumComments();
        if (numComments != null) {
            holder.numComments.setText(numComments.toString());
        } else {
            holder.numComments.setText("0");
        }
        Boolean isLiked = post.getLiked();
        if (isLiked == null) {
            isLiked = false;
        }
        if (isLiked == true) {
            holder.imgLike.setImageResource(R.drawable.ic_like);
        } else if (isLiked == false) {
            holder.imgLike.setImageResource(R.drawable.ic_unlike);
        }
//        Integer numShares = post.getNumShares();
//        if (numShares != null) {
//            holder.numShares.setText(numShares.toString());
//        } else {
//            holder.numShares.setText("0");
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(post, position);
                }
            }
        });
        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer numLikes = post.getNumLikes();
                if (numLikes == null) {
                    numLikes = 0;
                }
                Boolean isLiked = post.getLiked();
                if (isLiked == null) {
                    isLiked = false;
                }
                if (isLiked == true) {
                    post.setLiked(false);
                    post.setNumLikes(numLikes - 1);
                    holder.imgLike.setImageResource(R.drawable.ic_unlike);
                    holder.numLikes.setText(post.getNumLikes().toString());
                    updateLike(post.getId(), access_token, 8);
                } else {
                    post.setLiked(true);
                    post.setNumLikes(numLikes + 1);
                    holder.imgLike.setImageResource(R.drawable.ic_like);
                    holder.numLikes.setText(post.getNumLikes().toString());
                    addLike(post.getId(), access_token, 6);
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

    public Post getItem(int position) {
        return posts.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage, postImage, imgLike;
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
            imgLike = itemView.findViewById(R.id.imgLike);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Post post, int position);
    }

    public void addLike(Integer postId, String token, Integer statusId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/like/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                Toast.makeText(context, "Like success", Toast.LENGTH_SHORT).show();
                            }
                            else updateLike(postId, token, statusId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ERROR like: ", "onResponse: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR like: ", "onResponse: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (postId != null) {
                    params.put("post_id", String.valueOf(postId));
                }
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", token);
                }
                return headers;
            }
        };
        VolleySingle.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void updateLike(Integer postId, String token, Integer statusId) {
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, "http://10.0.2.2:5000/api/like/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                Toast.makeText(context, "Update like success", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ERROR update like: ", "onResponse: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR update like: ", "onResponse: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (postId != null) {
                    params.put("post_id", String.valueOf(postId));
                }
                params.put("status_id", String.valueOf(statusId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", token);
                }
                return headers;
            }
        };
        VolleySingle.getInstance(context).addToRequestQueue(stringRequest);
    }

}

package com.example.bookapp.Adapter;

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
import java.util.Map;

public class PostManagementAdapter extends RecyclerView.Adapter<PostManagementAdapter.PostManagementViewHolder>{
    Context context;
    ArrayList<Post> posts;

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
    public void onBindViewHolder(@NonNull PostManagementViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.userName.setText(post.getUser());
        holder.content.setText(post.getTcontent());
        Glide.with(context).load(post.getImage()).into(holder.postImage);

        //TODO: Add Event Listener for Update and Delete
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost(post.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostManagementViewHolder extends RecyclerView.ViewHolder{

        ImageView postImage;
        TextView userName, content, updateBtn, deleteBtn;
        public PostManagementViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.postImage);
            userName = itemView.findViewById(R.id.userName);
            content = itemView.findViewById(R.id.contentPost);
            updateBtn = itemView.findViewById(R.id.tvUpdatePost);
            deleteBtn = itemView.findViewById(R.id.tvDeletePost);
        }
    }

    private void deletePost(int post_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, "http://10.0.2.2:5000/api/post/?ids[0]="+post_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getInt("err") == 0) {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
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
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", SharedPrefManager.getInstance(context).getUser().getAccess_token());
                return params;
            }

        };
        VolleySingle.getInstance(context).addToRequestQueue(stringRequest);
    }

}

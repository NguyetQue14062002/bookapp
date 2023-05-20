package com.example.bookapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Activity.CreatePostActivity;
import com.example.bookapp.Adapter.PostManagementAdapter;
import com.example.bookapp.Domain.Post;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostManagementFragment extends Fragment {

    private ArrayList<Post> posts;
    private PostManagementAdapter postAdapter;
    private RecyclerView postsRecyclerView;

    private ImageView addPostBtn;


    public PostManagementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: Get all posts
        posts = new ArrayList<>();

        postsRecyclerView = view.findViewById(R.id.rcPostsAdmin);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        postAdapter = new PostManagementAdapter(getContext(), posts);

        postsRecyclerView.setAdapter(postAdapter);
        getAllPosts(view);

        //TODO: Create new Post

        addPostBtn = view.findViewById(R.id.ivAddPost);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreatePostActivity.class));
            }
        });
    }

    private void getAllPosts(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/post?order[]=id&order[]=DESC&status_id=6&status_id=7",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                JSONObject data = obj.getJSONObject("data");
                                JSONArray allPosts = data.getJSONArray("rows");
                                for (int i = 0; i < allPosts.length(); i++) {
                                    JSONObject object = allPosts.getJSONObject(i);
                                    User user = new User(
                                            object.getJSONObject("user").getString("full_name")

                                    );
                                    Post post = new Post(
                                            object.getInt("id"),
                                            object.getInt("status_id"),
                                            object.getString("tcontent"),
                                            object.getString("image"),
                                            object.getJSONObject("user").getString("full_name")
                                    );
                                    posts.add(post);
                                }
                                postAdapter.setPosts(posts);
                                postAdapter.notifyDataSetChanged();
                                Log.d("posts", String.valueOf(posts.size()));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}
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
import com.example.bookapp.Activity.CreateBookActivity;
import com.example.bookapp.Activity.CreatePostActivity;
import com.example.bookapp.Adapter.BookManagementAdapter;
import com.example.bookapp.Adapter.PostManagementAdapter;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.Post;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostManagementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Post> posts;
    private PostManagementAdapter postAdapter;
    private RecyclerView postsRecyclerView;

    private ImageView addPostBtn;


    public PostManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostManagementFragment newInstance(String param1, String param2) {
        PostManagementFragment fragment = new PostManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/post/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            //converting response to json object
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
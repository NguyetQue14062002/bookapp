package com.example.bookapp.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Activity.CreatePostActivity;
import com.example.bookapp.Activity.MainActivity;
import com.example.bookapp.Adapter.PostAdapter;
import com.example.bookapp.Domain.Post;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private List<Post> posts = new ArrayList<>();
    private PostAdapter postAdapter;

    private ImageView ivNewPost;
    private RecyclerView rvPosts;

    public CommunityFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityFragment newInstance(String param1, String param2) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false);
    }


    /**
     *
     * Override Ham onViewCreated va bat dau code nhu onCreate trong Activity
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivNewPost = view.findViewById(R.id.ivNewPost);
        rvPosts = view.findViewById(R.id.rvPosts);
        posts = getPosts();
        rvPosts.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvPosts.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(getContext(), posts);
        rvPosts.setAdapter(postAdapter);

        ivNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                startActivity(intent);
            }
        });

        // TODO: Initial Data and Create Layout Manager for postsRecyclerView
    }

    public List<Post> getPosts () {
        List<Post> listPosts = new ArrayList<>();
        StringRequest stringRequestPost = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/post/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject data = obj.getJSONObject("data");
                            JSONArray postsJson = data.getJSONArray("rows");

                            if (obj.getInt("err") == 0) {
                                for (int i = 0; i < postsJson.length(); i++) {
                                    JSONObject postJson = postsJson.getJSONObject(i);
                                    Post post = new Post();
                                    post.setId(postJson.getInt("id"));
                                    JSONObject userJson = postJson.getJSONObject("user");
                                    post.setUser(userJson.getString("full_name"));
                                    post.setTcontent(postJson.getString("tcontent"));
                                    post.setImage(postJson.getString("image"));
                                    post.setStatus_id(postJson.getInt("status_id"));
                                    StringRequest stringRequestLike = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/like/?id=" + post.getId(),
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject obj = new JSONObject(response);
                                                        JSONObject data = obj.getJSONObject("data");
                                                        Integer num_likes = data.getInt("count");
                                                        if (obj.getInt("err") == 0) {
                                                            post.setNumLikes(num_likes);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            , new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if (error.getMessage() != null) {
                                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    });
                                    VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequestLike);

                                    StringRequest stringRequestComment = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/comment/?id=" + post.getId(),
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject obj = new JSONObject(response);
                                                        JSONObject data = obj.getJSONObject("data");
                                                        Integer num_comments = data.getInt("count");
                                                        if (obj.getInt("err") == 0) {
                                                            post.setNumComments(num_comments);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            , new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if (error.getMessage() != null) {
                                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    });
                                    VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequestComment);

                                    StringRequest stringRequestShare = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/share/?id=" + post.getId(),
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject obj = new JSONObject(response);
                                                        JSONObject data = obj.getJSONObject("data");
                                                        Integer num_shares = data.getInt("count");
                                                        if (obj.getInt("err") == 0) {
                                                            post.setNumShares(num_shares);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            , new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if (error.getMessage() != null) {
                                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    });
                                    VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequestShare);

                                    listPosts.add(post);

                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
        VolleySingle.getInstance(getActivity()).addToRequestQueue(stringRequestPost);
        return listPosts;
    }



}
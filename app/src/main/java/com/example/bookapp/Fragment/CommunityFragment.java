package com.example.bookapp.Fragment;

import android.app.Activity;
import android.content.Intent;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
import com.example.bookapp.Activity.PostActivity;
import com.example.bookapp.Adapter.PostAdapter;
import com.example.bookapp.Domain.Post;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.ctory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {
    private List<Post> posts = new ArrayList<>();
    private PostAdapter postAdapter;

    private ImageView ivNewPost;
    private RecyclerView rvPosts;

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community, container, false);
        rvPosts = v.findViewById(R.id.rvPosts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvPosts.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter();
        rvPosts.setAdapter(postAdapter);
        getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Post>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Khởi tạo
                    }

                    @Override
                    public void onNext(List<Post> posts) {
                        // Xử lý dữ liệu và cập nhật Adapter
                        postAdapter.setPosts(posts);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Xử lý lỗi
                    }

                    @Override
                    public void onComplete() {
                        // Hoàn thành
                    }
                });
        return v;
    }

    /*
     * Override Ham onViewCreated va bat dau code nhu onCreate trong Activity
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivNewPost = view.findViewById(R.id.ivNewPost);
        rvPosts = view.findViewById(R.id.rvPosts);
        ivNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                startActivity(intent);
            }
        });

        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                // Xử lý sự kiện click vào post
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("post_id", post.getId());
                startActivity(intent);
            }
        });

        // TODO: Initial Data and Create Layout Manager for postsRecyclerView
    }

    public Observable<List<Post>> getPosts() {
        List<Post> listPosts = new ArrayList<>();
        return Observable.create(emitter -> {
            StringRequest stringRequestPost = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/post/",
                    response -> {
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
                                    // Gọi API lấy danh sách like
                                    StringRequest stringRequestLike = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/like?post_id=" + post.getId(),
                                            responseLike -> {
                                                try {
                                                    JSONObject objLike = new JSONObject(responseLike);
                                                    JSONObject dataLike = objLike.getJSONObject("data");
                                                    int numLikes = dataLike.getInt("count");
                                                    if (objLike.getInt("err") == 0) {
                                                        post.setNumLikes(numLikes);
                                                        post.setNumLikes(numLikes);
                                                        postAdapter.notifyDataSetChanged();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            },
                                            error -> {
                                                if (error.getMessage() != null) {
                                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequestLike);

                                    // Gọi API lấy danh sách comment
                                    StringRequest stringRequestComment = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/comment?post_id=" + post.getId(),
                                            responseComment -> {
                                                try {
                                                    JSONObject objComment = new JSONObject(responseComment);
                                                    JSONObject dataComment = objComment.getJSONObject("data");
                                                    int numComments = dataComment.getInt("count");
                                                    if (objComment.getInt("err") == 0) {
                                                        post.setNumComments(numComments);
                                                        postAdapter.notifyDataSetChanged();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            },
                                            error -> {
                                                if (error.getMessage() != null) {
                                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequestComment);

                                    // Gọi API lấy danh sách share
                                    StringRequest stringRequestShare = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/share?post_id=" + post.getId(),
                                            responseShare -> {
                                                try {
                                                    JSONObject objShare = new JSONObject(responseShare);
                                                    JSONObject dataShare = objShare.getJSONObject("data");
                                                    int numShares = dataShare.getInt("count");
                                                    if (objShare.getInt("err") == 0) {
                                                        post.setNumShares(numShares);
                                                        postAdapter.notifyDataSetChanged();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            },
                                            error -> {
                                                if (error.getMessage() != null) {
                                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequestShare);
                                    listPosts.add(post);

                                }
                            }
                            emitter.onNext(listPosts);
                            emitter.onComplete();
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    },
                    error -> {
                        if (error.getMessage() != null) {
                            emitter.onError(error);
                        }
                    });

            VolleySingle.getInstance(getActivity()).addToRequestQueue(stringRequestPost);
        });
    }


//    public List<Post> getPosts () {
//        List<Post> listPosts = new ArrayList<>();
//        StringRequest stringRequestPost = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/post/",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            JSONObject data = obj.getJSONObject("data");
//                            JSONArray postsJson = data.getJSONArray("rows");
//
//                            if (obj.getInt("err") == 0) {
//                                for (int i = 0; i < postsJson.length(); i++) {
//                                    JSONObject postJson = postsJson.getJSONObject(i);
//                                    Post post = new Post();
//                                    post.setId(postJson.getInt("id"));
//                                    JSONObject userJson = postJson.getJSONObject("user");
//                                    post.setUser(userJson.getString("full_name"));
//                                    post.setTcontent(postJson.getString("tcontent"));
//                                    post.setImage(postJson.getString("image"));
//                                    post.setStatus_id(postJson.getInt("status_id"));
//                                    StringRequest stringRequestLike = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/like?id=" + post.getId(),
//                                            new Response.Listener<String>() {
//                                                @Override
//                                                public void onResponse(String response) {
//                                                    try {
//                                                        JSONObject obj = new JSONObject(response);
//                                                        JSONObject data = obj.getJSONObject("data");
//                                                        Integer num_likes = data.getInt("count");
//                                                        if (obj.getInt("err") == 0) {
//                                                            post.setNumLikes(num_likes);
//                                                        }
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            }
//                                            , new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            if (error.getMessage() != null) {
//                                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//
//                                    });
//                                    VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequestLike);
//
//                                    StringRequest stringRequestComment = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/comment?id=" + post.getId(),
//                                            new Response.Listener<String>() {
//                                                @Override
//                                                public void onResponse(String response) {
//                                                    try {
//                                                        JSONObject obj = new JSONObject(response);
//                                                        JSONObject data = obj.getJSONObject("data");
//                                                        Integer num_comments = data.getInt("count");
//                                                        if (obj.getInt("err") == 0) {
//                                                            post.setNumComments(num_comments);
//                                                        }
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            }
//                                            , new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            if (error.getMessage() != null) {
//                                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//
//                                    });
//                                    VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequestComment);
//
//                                    StringRequest stringRequestShare = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/share?id=" + post.getId(),
//                                            new Response.Listener<String>() {
//                                                @Override
//                                                public void onResponse(String response) {
//                                                    try {
//                                                        JSONObject obj = new JSONObject(response);
//                                                        JSONObject data = obj.getJSONObject("data");
//                                                        Integer num_shares = data.getInt("count");
//                                                        if (obj.getInt("err") == 0) {
//                                                            post.setNumShares(num_shares);
//                                                        }
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            }
//                                            , new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            if (error.getMessage() != null) {
//                                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//
//                                    });
//                                    VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequestShare);
//
//                                    listPosts.add(post);
//
//                                }
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                , new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error.getMessage() != null) {
//                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        });
//        VolleySingle.getInstance(getActivity()).addToRequestQueue(stringRequestPost);
//        return listPosts;
//    }



}
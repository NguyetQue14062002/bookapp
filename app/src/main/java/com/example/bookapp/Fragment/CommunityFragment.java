package com.example.bookapp.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Activity.CreatePostActivity;
import com.example.bookapp.Activity.PostDetailActivity;
import com.example.bookapp.Adapter.PostAdapter;
import com.example.bookapp.Domain.Post;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CommunityFragment extends Fragment {
    private PostAdapter postAdapter;
    private ImageView ivNewPost;
    private RecyclerView rvPosts;
    ActivityResultLauncher<Intent> activityResultLauncher;

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community, container, false);
        if (SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
            User user = SharedPrefManager.getInstance(getActivity()).getUser();
            rvPosts = v.findViewById(R.id.rvPosts);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rvPosts.setLayoutManager(layoutManager);
            postAdapter = new PostAdapter(getActivity(), user.getAccess_token());
            rvPosts.setAdapter(postAdapter);
            getPosts(user)
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
        }
        return v;
    }

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
            public void onItemClick(Post post, int position) {
                // Xử lý sự kiện click vào post
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("post_id", post.getId());
                intent.putExtra("post_user", post.getUser());
                intent.putExtra("post_tcontent", post.getTcontent());
                intent.putExtra("post_image", post.getImage());
                intent.putExtra("book_id", post.getBookId());
                intent.putExtra("post_status_id", post.getStatus_id());
                intent.putExtra("post_num_likes", post.getNumLikes());
                intent.putExtra("post_num_comments", post.getNumComments());
                intent.putExtra("is_liked", post.getLiked());
                intent.putExtra("position", position);
                intent.putExtra("avatar", post.getAvatar());
                activityResultLauncher.launch(intent);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        int postId = data.getIntExtra("post_id", 0);
                        int numLikes = data.getIntExtra("post_num_likes", 0);
                        int numComments = data.getIntExtra("post_num_comments", 0);
                        int position = data.getIntExtra("position", -1);
                        Boolean isLiked = data.getBooleanExtra("is_liked", false);

                        if (position != -1 && postAdapter.getItemCount() > position) {
                            // Update the like and comment count for the corresponding item in the adapter
                            Post post = postAdapter.getItem(position);
                            post.setNumLikes(numLikes);
                            post.setNumComments(numComments);
                            post.setLiked(isLiked);
                            postAdapter.notifyItemChanged(position);
                        }
                    }
                }
        );
    }

    public Observable<List<Post>> getPosts(User user) {
        return Observable.create(emitter -> {
            List<Post> listPosts = new ArrayList<>();

            StringRequest stringRequestPost = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/post?order[]=id&order[]=DESC",
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject data = obj.getJSONObject("data");
                            JSONArray postsJson = data.getJSONArray("rows");

                            if (obj.getInt("err") == 0) {
                                List<Observable<Post>> observables = new ArrayList<>();

                                for (int i = 0; i < postsJson.length(); i++) {
                                    JSONObject postJson = postsJson.getJSONObject(i);
                                    Post post = new Post();
                                    post.setId(postJson.getInt("id"));
                                    JSONObject userJson = postJson.getJSONObject("user");
                                    post.setUser(userJson.getString("full_name"));
                                    post.setAvatar(userJson.getString("avatar"));
                                    post.setTcontent(postJson.getString("tcontent"));
                                    post.setImage(postJson.getString("image"));
                                    post.setStatus_id(postJson.getInt("status_id"));
                                    post.setBookId(postJson.getInt("book_id"));

                                    Observable<Post> likeObservable = Observable.create(likeEmitter -> {
                                        StringRequest stringRequestLike = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/like?status_id=6&post_id=" + String.valueOf(post.getId()),
                                                responseLike -> {
                                                    try {
                                                        JSONObject objLike = new JSONObject(responseLike);
                                                        JSONObject dataLike = objLike.getJSONObject("data");
                                                        int numLikes = dataLike.getInt("count");
                                                        if (objLike.getInt("err") == 0) {
                                                            post.setNumLikes(numLikes);
                                                        }
                                                        likeEmitter.onNext(post);
                                                        likeEmitter.onComplete();
                                                    } catch (Exception e) {
                                                        likeEmitter.onError(e);
                                                    }
                                                },
                                                error -> {
                                                    likeEmitter.onError(error);
                                                });

                                        VolleySingle.getInstance(getActivity()).addToRequestQueue(stringRequestLike);
                                    });

                                    Observable<Post> statusLikeObservable = Observable.create(statusLikeEmitter -> {
                                        StringRequest stringRequestLikeUser = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/like?post_id=" + String.valueOf(post.getId()) + "&user_id=" + String.valueOf(user.getId()),
                                                responseLikeUser -> {
                                                    try {
                                                        JSONObject objLikeUser = new JSONObject(responseLikeUser);
                                                        JSONObject dataLikeUser = objLikeUser.getJSONObject("data");
                                                        JSONArray rowsLikeUser = dataLikeUser.getJSONArray("rows");
                                                        if (objLikeUser.getInt("err") == 0) {
                                                            JSONObject rowLikeUser = rowsLikeUser.optJSONObject(0);
                                                            if (rowLikeUser != null) {
                                                                Integer status_id = rowLikeUser.getInt("status_id");
                                                                if (status_id == 6) {
                                                                    post.setLiked(true);
                                                                } else if (status_id == 8) {
                                                                    post.setLiked(false);
                                                                }
                                                            }
                                                        }
                                                        statusLikeEmitter.onNext(post);
                                                        statusLikeEmitter.onComplete();
                                                    } catch (Exception e) {
                                                        statusLikeEmitter.onError(e);
                                                    }
                                                },
                                                error -> {
                                                    statusLikeEmitter.onError(error);
                                                });
                                        VolleySingle.getInstance(getActivity()).addToRequestQueue(stringRequestLikeUser);
                                    });

                                    Observable<Post> commentObservable = Observable.create(commentEmitter -> {
                                        StringRequest stringRequestComment = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/comment?status_id=6&post_id=" + String.valueOf(post.getId()),
                                                responseComment -> {
                                                    try {
                                                        JSONObject objComment = new JSONObject(responseComment);
                                                        JSONObject dataComment = objComment.getJSONObject("data");
                                                        int numComments = dataComment.getInt("count");
                                                        if (objComment.getInt("err") == 0) {
                                                            post.setNumComments(numComments);
                                                        }
                                                        commentEmitter.onNext(post);
                                                        commentEmitter.onComplete();
                                                    } catch (Exception e) {
                                                        commentEmitter.onError(e);
                                                    }
                                                },
                                                error -> {
                                                    commentEmitter.onError(error);
                                                });
                                        VolleySingle.getInstance(getActivity()).addToRequestQueue(stringRequestComment);
                                    });

                                    observables.add(likeObservable);
                                    observables.add(statusLikeObservable);
                                    observables.add(commentObservable);

                                    listPosts.add(post);
                                }

                                Observable.zip(observables, objects -> listPosts)
                                        .subscribe(posts -> {
                                            emitter.onNext(posts);
                                            emitter.onComplete();
                                        }, error -> emitter.onError(error));
                            } else {
                                emitter.onNext(listPosts);
                                emitter.onComplete();
                            }
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    },
                    error -> {
                        emitter.onError(error);
                    });

            VolleySingle.getInstance(getActivity()).addToRequestQueue(stringRequestPost);
        });
    }
}
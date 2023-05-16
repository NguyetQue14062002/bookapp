package com.example.bookapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Adapter.PersonalPostAdapter;
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

public class PersonalPostActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    private TextView tvAllPost, tvActivePost, tvHiddenPost;
    private PersonalPostAdapter postAdapter;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalpost);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            User user = SharedPrefManager.getInstance(this).getUser();
            rvPosts = findViewById(R.id.rvPosts);
            tvAllPost = findViewById(R.id.tvAllPost);
            tvActivePost = findViewById(R.id.tvActivePost);
            tvHiddenPost = findViewById(R.id.tvHiddenPost);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvPosts.setLayoutManager(layoutManager);
            postAdapter = new PersonalPostAdapter(this, user.getAccess_token());
            rvPosts.setAdapter(postAdapter);
            tvAllPost.setTextColor(Color.BLUE);
            String urlAllPost = "http://10.0.2.2:5000/api/post?order[]=id&order[]=DESC&status_id=6&status_id=7&user_id=";
            String urlActivePost = "http://10.0.2.2:5000/api/post?order[]=id&order[]=DESC&status_id=6&user_id=";
            String urlHiddenPost = "http://10.0.2.2:5000/api/post?order[]=id&order[]=DESC&status_id=7&user_id=";
            getPersonalPosts(user, urlAllPost)
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

            tvAllPost.setOnClickListener(v -> {
                tvAllPost.setTextColor(Color.BLUE);
                tvActivePost.setTextColor(Color.BLACK);
                tvHiddenPost.setTextColor(Color.BLACK);
                getPersonalPosts(user, urlAllPost)
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
            });

            tvActivePost.setOnClickListener(v -> {
                tvActivePost.setTextColor(Color.BLUE);
                tvAllPost.setTextColor(Color.BLACK);
                tvHiddenPost.setTextColor(Color.BLACK);
                getPersonalPosts(user, urlActivePost)
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
            });

            tvHiddenPost.setOnClickListener(v -> {
                tvHiddenPost.setTextColor(Color.BLUE);
                tvActivePost.setTextColor(Color.BLACK);
                tvAllPost.setTextColor(Color.BLACK);
                getPersonalPosts(user, urlHiddenPost)
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
            });

            postAdapter.setOnItemClickListener(new PersonalPostAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Post post, int position) {
                    // Xử lý sự kiện click vào post
                    Intent intent = new Intent(PersonalPostActivity.this, PostDetailActivity.class);
                    intent.putExtra("post_id", post.getId());
                    intent.putExtra("post_user", post.getUser());
                    intent.putExtra("post_tcontent", post.getTcontent());
                    intent.putExtra("post_image", post.getImage());
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

    public Observable<List<Post>> getPersonalPosts(User user, String url) {
        return Observable.create(emitter -> {
            List<Post> listPosts = new ArrayList<>();

            StringRequest stringRequestPost = new StringRequest(Request.Method.GET, url + String.valueOf(user.getId()),
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

                                        VolleySingle.getInstance(this).addToRequestQueue(stringRequestLike);
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
                                        VolleySingle.getInstance(this).addToRequestQueue(stringRequestLikeUser);
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
                                        VolleySingle.getInstance(this).addToRequestQueue(stringRequestComment);
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

            VolleySingle.getInstance(this).addToRequestQueue(stringRequestPost);
        });
    }
}

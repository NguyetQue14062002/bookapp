package com.example.bookapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Adapter.CommentAdapter;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.Comment;
import com.example.bookapp.Domain.Post;
import com.example.bookapp.Domain.User;
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

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvName, tvDate, tvContent, tvNumLike, tvNumCmt;
    private ImageView imgAvatar, imgLike, imgComment, imgExit, imgSend, imgIconCmt, imgPost, ivMore;
    private EditText etComment;
    private RecyclerView rcCmt;
    private CommentAdapter commentAdapter;
    private Post post = new Post();
    private Integer numCmt = 0;
    private Integer numLike = 0;
    private Integer position = -1;
    private Integer bookId = 0;
    private Book myBook;
    private List<Comment> comments = new ArrayList<>();
    private Boolean isLiked = false;
    private int postId;
    private LinearLayout linearLayoutCmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            User user = SharedPrefManager.getInstance(this).getUser();
            postId = getIntent().getIntExtra("post_id", 0);
            numLike = getIntent().getIntExtra("post_num_likes", 0);
            numCmt = getIntent().getIntExtra("post_num_comments", 0);
            position = getIntent().getIntExtra("position", -1);
            isLiked = getIntent().getBooleanExtra("is_liked", false);
            bookId = getIntent().getIntExtra("book_id", 0);

            getComments(postId);

            tvName = findViewById(R.id.tvName);
            tvContent = findViewById(R.id.tvContent);
            tvNumLike = findViewById(R.id.tvNumLike);
            tvNumCmt = findViewById(R.id.tvNumCmt);
            imgAvatar = findViewById(R.id.imgAvatar);
            imgPost = findViewById(R.id.postImage);
            imgLike = findViewById(R.id.imgLike);
            imgComment = findViewById(R.id.imgComment);
            imgExit = findViewById(R.id.imgExit);
            ivMore = findViewById(R.id.ivMore);
            imgSend = findViewById(R.id.imgSend);
            imgIconCmt = findViewById(R.id.imgIconCmt);
            etComment = findViewById(R.id.etComment);
            rcCmt = findViewById(R.id.rcCmt);
            linearLayoutCmt = findViewById(R.id.linearLayoutCmt);

            tvName.setText(getIntent().getStringExtra("post_user"));
            tvContent.setText(getIntent().getStringExtra("post_tcontent"));
            tvNumLike.setText(String.valueOf(numLike));
            tvNumCmt.setText(String.valueOf(numCmt));
            if (isLiked) {
                imgLike.setImageResource(R.drawable.ic_like);
            } else {
                imgLike.setImageResource(R.drawable.ic_unlike);
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rcCmt.setLayoutManager(linearLayoutManager);
            commentAdapter = new CommentAdapter(this, comments);
            rcCmt.setAdapter(commentAdapter);

            getBook();

            String accessToken = user.getAccess_token();

            String avt = getIntent().getStringExtra("avatar");
            String postImage = getIntent().getStringExtra("post_image");
            if (postImage != "null") {
                Glide.with(this).load(postImage).into(imgPost);
            }
            Glide.with(this).load(R.drawable.defaultavt).into(imgAvatar);
            if (avt != "null") {
                Glide.with(this).load(avt).into(imgAvatar);
            }
            imgExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("post_id", postId);
                    intent.putExtra("post_num_likes", numLike);
                    intent.putExtra("post_num_comments", numCmt);
                    intent.putExtra("position", position);
                    intent.putExtra("is_liked", isLiked);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            if (bookId == 0) {
                ivMore.setEnabled(false);
            }
            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(PostDetailActivity.this, ivMore);
                    popupMenu.getMenuInflater().inflate(R.menu.share_book, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.detailBook) {
                            Intent intent = new Intent(PostDetailActivity.this, BookDetailActivity.class);
                            intent.putExtra("myBook", myBook);
                            startActivity(intent);
                        }
                        return true;
                    });
                    popupMenu.show();
                }
            });

            imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if liked -> delete like
                    addLike(postId, accessToken);
                    //if not liked -> add like
                }
            });

            imgSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comment = etComment.getText().toString();
                    if (comment.isEmpty()) {
                        Toast.makeText(PostDetailActivity.this, "Comment is empty", Toast.LENGTH_SHORT).show();
                    } else {
                        addComment(postId, accessToken, comment);
                        etComment.setText("");
                        getComments(postId);
                        commentAdapter.notifyDataSetChanged();
                    }
                }
            });
            commentAdapter.setCommentDeleteListener(new CommentAdapter.CommentDeleteListener() {
                @Override
                public void onCommentDeleted(int commentId) {
                    getComments(postId);
                    commentAdapter.notifyDataSetChanged();
                    tvNumCmt.setText(String.valueOf(comments.size()));
                }
            });
        } else {
            Intent intent = new Intent(PostDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void getComments(Integer postId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/comment?post_id=" + String.valueOf(postId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            comments.clear();
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                JSONObject data = obj.getJSONObject("data");
                                numCmt = data.getInt("count");
                                JSONArray commentJson = data.getJSONArray("rows");
                                for (int i = 0; i < commentJson.length(); i++) {
                                    JSONObject cmt = commentJson.getJSONObject(i);
                                    JSONObject user = cmt.getJSONObject("user");
                                    Comment comment = new Comment();
                                    comment.setId(cmt.getInt("id"));
                                    comment.setPost_id(cmt.getInt("post_id"));
                                    comment.setUser_id(cmt.getInt("user_id"));
                                    comment.setUser_name(user.getString("full_name"));
                                    comment.setTcontent(cmt.getString("tcontent"));
                                    comments.add(comment);
                                }
                                commentAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("ERROR getListComment: ", "onResponse: " + e.getMessage());
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR getListComment: ", "onResponse: " + error.getMessage());
            }
        });
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void addLike(Integer postId, String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/like/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                Toast.makeText(PostDetailActivity.this, "Like success", Toast.LENGTH_SHORT).show();
                                imgLike.setImageResource(R.drawable.ic_like);
                                numLike++;
                                tvNumLike.setText(String.valueOf(numLike));
                                isLiked = true;
                            } else {
                                Integer statusId = obj.getInt("status_id");
                                if (statusId == 6) {
                                    updateLike(postId, token, 8);
                                    isLiked = false;
                                } else if (statusId == 8) {
                                    updateLike(postId, token, 6);
                                    isLiked = true;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ERROR like: ", "onResponse: " + e.getMessage());
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR like: ", "onResponse: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", token);
                return headers;
            }
        };
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void updateLike(Integer postId, String token, Integer statusId) {
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, "http://10.0.2.2:5000/api/like/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0 && statusId == 6) {
                                imgLike.setImageResource(R.drawable.ic_like);
                                numLike++;
                                tvNumLike.setText(String.valueOf(numLike));
                                Toast.makeText(PostDetailActivity.this, "Like success", Toast.LENGTH_SHORT).show();
                            } else if (obj.getInt("err") == 0 && statusId == 8) {
                                imgLike.setImageResource(R.drawable.ic_unlike);
                                numLike--;
                                tvNumLike.setText(String.valueOf(numLike));
                                Toast.makeText(PostDetailActivity.this, "Delete Like success", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ERROR update like: ", "onResponse: " + e.getMessage());
                        }
                    }
                },
                new ErrorListener() {
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
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void addComment(Integer postId, String token, String tcontent) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/comment/"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getInt("err") == 0) {
                        numCmt++;
                        tvNumCmt.setText(String.valueOf(numCmt));
                        getComments(postId);
                        commentAdapter.setComments(comments);
                        commentAdapter.notifyDataSetChanged();
                        Toast.makeText(PostDetailActivity.this, "Comment success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PostDetailActivity.this, "Comment fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ERROR comment: ", "onResponse: " + e.getMessage());
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR comment: ", "onResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (postId != null) {
                    params.put("post_id", String.valueOf(postId));
                }
                if (tcontent != null) {
                    params.put("tcontent", tcontent);
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
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void getBook() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/book?id=" + bookId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                JSONObject data = obj.getJSONObject("data");
                                JSONArray allBooks = data.getJSONArray("rows");
                                JSONObject object = allBooks.getJSONObject(0);
                                try {
                                    object.getInt("category_id");
                                } catch (JSONException e) {
                                    object.put("category_id", -1);
                                }
                                try {
                                    object.getInt("publisher_id");
                                } catch (JSONException e) {
                                    object.put("publisher_id", -1);
                                }
                                Book book = new Book(
                                        object.getInt("id"),
                                        object.getInt("category_id"),
                                        object.getInt("status_id"),
                                        object.getInt("publisher_id"),
                                        object.getInt("page_number"),
                                        object.getString("author"),
                                        object.getString("description"),
                                        object.getString("image_url"),
                                        object.getString("link"),
                                        object.getString("title")
                                );
                                myBook = book;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR get book: ", "onResponse: " + error.getMessage());
                    }
                }
        );
        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }
}


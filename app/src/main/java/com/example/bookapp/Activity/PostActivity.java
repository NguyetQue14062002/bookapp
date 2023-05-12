package com.example.bookapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Adapter.CommentAdapter;
import com.example.bookapp.Domain.Comment;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class PostActivity extends AppCompatActivity {

    private TextView tvName, tvDate, tvContent, tvNumLike, tvNumCmt, tvNumShare;
    private ImageView imgAvatar, imgLike, imgComment, imgShare, imgExit, imgSend, imgIconCmt;
    private EditText etComment;
    private RecyclerView rcCmt;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private int postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", 0);

        tvName = findViewById(R.id.tvName);
        tvDate = findViewById(R.id.tvDate);
        tvContent = findViewById(R.id.tvContent);
        tvNumLike = findViewById(R.id.tvNumLike);
        tvNumCmt = findViewById(R.id.tvNumCmt);
        tvNumShare = findViewById(R.id.tvNumShare);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgLike = findViewById(R.id.imgLike);
        imgComment = findViewById(R.id.imgComment);
        imgShare = findViewById(R.id.imgShare);
        imgExit = findViewById(R.id.imgExit);
        imgSend = findViewById(R.id.imgSend);
        imgIconCmt = findViewById(R.id.imgIconCmt);
        etComment = findViewById(R.id.etComment);
        rcCmt = findViewById(R.id.rcCmt);


    }

    //get post detail
    public void getPostDetail(int postId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.0.2/api/post?id=" + postId, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                JSONObject data = obj.getJSONObject("data");
                JSONArray postsJson = data.getJSONArray("rows");

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            //error
        });
    }
    //get like
    //get comment
    //get share
}

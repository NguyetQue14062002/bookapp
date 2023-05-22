package com.example.bookapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Domain.Comment;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    List<Comment> comments;
    User user;

    Context context;
    private CommentDeleteListener listener;

    public CommentAdapter(Context context, List<Comment> commentList) {

        this.comments = commentList;
    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        user = SharedPrefManager.getInstance(parent.getContext()).getUser();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Comment comment = comments.get(position);
        holder.user.setText(comment.getUser_name());
        holder.content.setText(comment.getTcontent());
        if (user.getRole_id() == 1) {
            holder.ivDeleteCmt.setVisibility(View.VISIBLE);
        } else {
            holder.ivDeleteCmt.setVisibility(View.GONE);
        }
        holder.ivDeleteCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteComment(comment.getId());
                if (listener != null) {
                    listener.onCommentDeleted(comment.getId());
                }
                comments.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, comments.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setCommentDeleteListener(CommentDeleteListener listener) {
        this.listener = listener;
    }

    public interface CommentDeleteListener {
        void onCommentDeleted(int commentId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user, content;
        ImageView ivDeleteCmt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.tvUserCmt);
            content = itemView.findViewById(R.id.tvContentCmt);
            ivDeleteCmt = itemView.findViewById(R.id.ivDeleteCmt);
        }
    }

    public void deleteComment(int id) {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.DELETE, "http://10.0.2.2:5000/api/comment?id=" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization", user.getAccess_token());
                return header;
            }
        };
        VolleySingle.getInstance(context).addToRequestQueue(stringRequest);
    }
}

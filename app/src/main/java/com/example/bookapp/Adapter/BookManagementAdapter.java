package com.example.bookapp.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Activity.AdminActivity;
import com.example.bookapp.Activity.BookDetailActivity;
import com.example.bookapp.Activity.CreateBookActivity;
import com.example.bookapp.Activity.LoginActivity;
import com.example.bookapp.Activity.MainActivity;
import com.example.bookapp.Activity.UpdateBookActivity;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.Category;
import com.example.bookapp.Domain.Publisher;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookManagementAdapter extends RecyclerView.Adapter<BookManagementAdapter.BookManagementViewHolder>{
    Context context;
    ArrayList<Book> books;

    public BookManagementAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_book_manage,parent,false);
        return new BookManagementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookManagementViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        Glide.with(context).load(book.getImage_url()).into(holder.imgBook);

        //TODO: Add Event Listener for Delete

        holder.tvDeleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBook(book.getId());
            }
        });

        //TODO: Add Event Listener for Update
        holder.tvUpdateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateBookActivity.class);
                intent.putExtra("myBook", book);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookManagementViewHolder extends RecyclerView.ViewHolder {

        ImageView imgBook;
        TextView tvTitle, tvAuthor, tvUpdateBook, tvDeleteBook;
        public BookManagementViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBook = itemView.findViewById(R.id.imgBookAdmin);
            tvTitle = itemView.findViewById(R.id.tvTitleAdmin);
            tvAuthor = itemView.findViewById(R.id.tvAuthorAdmin);
            tvUpdateBook = itemView.findViewById(R.id.tvUpdateBook);
            tvDeleteBook = itemView.findViewById(R.id.tvDeleteBook);
        }
    }

    private void deleteBook(int id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("status_id", 2);
        } catch(Exception e) {

        }

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.PUT, "http://10.0.2.2:5000/api/book/status", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("err") == 0) {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
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
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", SharedPrefManager.getInstance(context).getUser().getAccess_token());
                return params;
            }

        };
        VolleySingle.getInstance(context).addToRequestQueue(request);
    }


}

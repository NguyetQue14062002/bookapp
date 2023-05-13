package com.example.bookapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.example.bookapp.Activity.BookDetailActivity;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookLibraryAdapter extends RecyclerView.Adapter<BookLibraryAdapter.BookLibraryViewHolder>{

    Context context;
    ArrayList<Book> books;


    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }

    public BookLibraryAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public BookLibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_book,parent,false);
        return new BookLibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookLibraryViewHolder holder, int position) {
        Book book = books.get(position);

        holder.tvBookName.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        Glide.with(context).load(book.getImage_url()).into(holder.imgBook);

        //TODO: deleteHistory
        holder.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteHistory(book);
            }
        });

        //TODO: updateHistory
        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            int status_id;
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, holder.updateBtn);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.book_list, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                context,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();

                        switch (item.getItemId()) {
                            case R.id.reading:
                                status_id = 4;
                                break;

                            case R.id.done:
                                status_id=5;
                                break;

                            case R.id.unread:
                                status_id=3;
                                break;

                        }

                        updateHistory(book, status_id);

                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookLibraryViewHolder extends RecyclerView.ViewHolder{

        ImageView imgBook, updateBtn;
        TextView tvBookName, tvAuthor, tvRemove;

        public BookLibraryViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBook = itemView.findViewById(R.id.libraryBookImage);
            tvBookName = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvRemove = itemView.findViewById(R.id.tvRemove);
            updateBtn = itemView.findViewById(R.id.updateLibraryBtn);
        }
    }

    private void updateHistory(Book book, int status_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("book_id", book.getId());
            jsonObject.put("status_id", status_id);
        } catch(Exception e) {

        }

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.PUT, "http://10.0.2.2:5000/api/history/", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting response to json object
                            //if no error in response
                            if (response.getInt("err") == 0) {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response


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

    private void deleteHistory(Book book) {

//TODO: Delete history by bookID

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("book_id", book.getId());
        } catch(Exception e) {

        }

        JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST, "http://10.0.2.2:5000/api/history/delete", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting response to json object
                            //if no error in response
                            if (response.getInt("err") == 0) {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response


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

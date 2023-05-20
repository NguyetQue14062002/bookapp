package com.example.bookapp.Fragment;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Activity.BookDetailActivity;
import com.example.bookapp.Activity.CreateBookActivity;
import com.example.bookapp.Adapter.BookAdapter;
import com.example.bookapp.Adapter.BookManagementAdapter;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookManagementFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Book> books;
    private BookManagementAdapter bookAdapter;
    private RecyclerView booksRecyclerView;

    private ImageView addBookBtn;
    private TextView tvAllBook, tvActiveBook, tvDeactivateBook;

    public BookManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: Get all books
        books = new ArrayList<>();

        tvAllBook = view.findViewById(R.id.tvAllBook);
        tvActiveBook = view.findViewById(R.id.tvActiveBook);
        tvDeactivateBook = view.findViewById(R.id.tvDeactivateBook);
        booksRecyclerView = view.findViewById(R.id.rcBooksAdmin);

        booksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        bookAdapter = new BookManagementAdapter(getContext(), books);
        booksRecyclerView.setAdapter(bookAdapter);

        String urlAllBooks = "http://10.0.2.2:5000/api/book?order[]=id&order[]=DESC";
        String urlActiveBooks = "http://10.0.2.2:5000/api/book?order[]=id&order[]=DESC&status_id=1";
        String urlDeactivateBooks = "http://10.0.2.2:5000/api/book?order[]=id&order[]=DESC&status_id=2";

        tvAllBook.setTextColor(Color.BLUE);
        books.clear();
        getAllBooks(urlActiveBooks);

        addBookBtn = view.findViewById(R.id.ivAddBook);
        addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreateBookActivity.class));
            }
        });

        bookAdapter.setOnItemClickListener(new BookManagementAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book, int position) {
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });

        tvAllBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvAllBook.setTextColor(Color.BLUE);
                tvActiveBook.setTextColor(Color.BLACK);
                tvDeactivateBook.setTextColor(Color.BLACK);
                getAllBooks(urlAllBooks);
                bookAdapter.notifyDataSetChanged();
            }
        });

        tvActiveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvAllBook.setTextColor(Color.BLACK);
                tvActiveBook.setTextColor(Color.BLUE);
                tvDeactivateBook.setTextColor(Color.BLACK);
                getAllBooks(urlActiveBooks);
                bookAdapter.notifyDataSetChanged();
            }
        });

        tvDeactivateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvAllBook.setTextColor(Color.BLACK);
                tvActiveBook.setTextColor(Color.BLACK);
                tvDeactivateBook.setTextColor(Color.BLUE);
                getAllBooks(urlDeactivateBooks);
                bookAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllBooks(String url) {
        books.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                JSONObject data = obj.getJSONObject("data");
                                JSONArray allBooks = data.getJSONArray("rows");
                                for (int i = 0; i < allBooks.length(); i++) {
                                    JSONObject object = allBooks.getJSONObject(i);
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
                                    books.add(book);
                                }
                                bookAdapter.setBooks(books);
                                bookAdapter.notifyDataSetChanged();
                                Log.d("books", String.valueOf(books.size()));
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
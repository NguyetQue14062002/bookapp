package com.example.bookapp.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Adapter.BookAdapter;
import com.example.bookapp.Adapter.BookLibraryAdapter;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DoneFragment extends Fragment {

    private ArrayList<Book> books;
    private BookLibraryAdapter bookLibraryAdapter;
    private RecyclerView booksRecyclerView;

    public DoneFragment() {} //Constructor

    public static DoneFragment newInstance() {       //Create new instance
        DoneFragment fragment = new DoneFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.done_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        books = new ArrayList<>();
        booksRecyclerView = view.findViewById(R.id.rcAlreadyReadingBook);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        bookLibraryAdapter = new BookLibraryAdapter(getContext(), books);
        booksRecyclerView.setAdapter(bookLibraryAdapter);
        getAlreadyReadHistory(view);
    }

    private void getAlreadyReadHistory(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/history/?status_id=5&user_id="+ SharedPrefManager.getInstance(getContext()).getUser().getId(),
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
                                    JSONObject object = allBooks.getJSONObject(i).getJSONObject("book");
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
                                            object.getString("author"),
                                            object.getString("description"),
                                            object.getString("image_url"),
                                            object.getString("link"),
                                            object.getString("title")
                                    );
                                    books.add(book);
                                }
                                bookLibraryAdapter.setBooks(books);
                                bookLibraryAdapter.notifyDataSetChanged();

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
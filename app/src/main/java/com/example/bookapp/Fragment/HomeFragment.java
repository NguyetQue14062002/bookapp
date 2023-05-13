package com.example.bookapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookapp.Adapter.BookAdapter;
import com.example.bookapp.Adapter.CategoryAdapter;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.Category;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tvHi;

    private ArrayList<Book> books;
    private ArrayList<Category> categories;

    private BookAdapter bookAdapter;

    private CategoryAdapter categoryAdapter;
    private RecyclerView booksRecyclerView;

    private RecyclerView categoryRecyclerView;
    private EditText etSearch;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    /**
     *
     * Override Ham onViewCreated va bat dau code nhu onCreate trong Activity
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        books = new ArrayList<>();
        booksRecyclerView = view.findViewById(R.id.rcAllBooks);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        bookAdapter = new BookAdapter(getContext(), books);
        booksRecyclerView.setAdapter(bookAdapter);
        getAllBooks(view);


        categories = new ArrayList<>();
        categoryRecyclerView = view.findViewById(R.id.rcCategory);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(getContext(), categories);
        categoryRecyclerView.setAdapter(categoryAdapter);
        getCategories(view);



        tvHi = view.findViewById(R.id.tvHello);
        etSearch = view.findViewById(R.id.tvSearch);


        tvHi.setText("Chào "+ SharedPrefManager.getInstance(getActivity()).getUser().getFull_name());


    }


    private void getCategories(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/category/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("err") == 0) {
                                JSONObject data = obj.getJSONObject("data");
                                JSONArray allCategories = data.getJSONArray("rows");
                                for (int i = 0; i < allCategories.length(); i++) {
                                    JSONObject object = allCategories.getJSONObject(i);
                                    Category category = new Category(
                                            object.getInt("id"),
                                            object.getString("name")
                                    );
                                    categories.add(category);
                                }
                                categoryAdapter.setCategories(categories);
                                categoryAdapter.notifyDataSetChanged();
                                Log.d("categories", String.valueOf(categories.size()));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("categories", String.valueOf(categories.size()));
                    }
                }
        );
        VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void getAllBooks(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/book/",
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
                                    Book book = new Book(
                                            object.getInt("id"),
                                            object.getInt("category_id"),
                                            object.getInt("status_id"),
                                            -1,
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
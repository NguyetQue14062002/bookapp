package com.example.bookapp.Fragment;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.bookapp.Activity.LoginActivity;
import com.example.bookapp.Activity.MainActivity;
import com.example.bookapp.Activity.ProfileActivity;
import com.example.bookapp.Adapter.BookAdapter;
import com.example.bookapp.Adapter.CategoryAdapter;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.Category;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.Interface.OnCategoryClickListener;
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
public class HomeFragment extends Fragment implements OnCategoryClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tvHi;

    private ArrayList<Book> books;
    private ArrayList<Book> bookSearch;
    private ArrayList<Book> bookSearchCate;
    private ArrayList<Category> categories;

    private BookAdapter bookAdapter;

    private CategoryAdapter categoryAdapter;
    private RecyclerView booksRecyclerView;

    private RecyclerView categoryRecyclerView;
    private EditText etSearch;
    private ImageView image, imgSearch;
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
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_home, container, false);
        ImageView profile = (ImageView) rootView.findViewById(R.id.imageUserHome);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAttractProfile();
            }
        });

        return rootView;

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
        categoryAdapter = new CategoryAdapter(getContext(), categories,this::onCategoryClick );
        categoryRecyclerView.setAdapter(categoryAdapter);

        getCategories(view);

        tvHi = view.findViewById(R.id.tvHello);
        etSearch = view.findViewById(R.id.tvSearch);
        image = view.findViewById(R.id.imageUserHome);

        getData();
        if (SharedPrefManager.getInstance(getActivity()).getUser().getAvatar() != null)
            Glide.with(this).load(SharedPrefManager.getInstance(getActivity()).getUser().getAvatar()).into(image);
        imgSearch= view.findViewById(R.id.imgSearchBook);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookSearch = new ArrayList<>();
                bookAdapter = new BookAdapter(getContext(), bookSearch);
                bookAdapter = new BookAdapter(getContext(), books);
                booksRecyclerView.setAdapter(bookAdapter);
                GetSearch();
            }
        });
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
                            /*
this.id = id;
        this.category_id = category_id;
        this.status_id = status_id;
        this.publisher_id = publisher_id;
        this.page_number = page_number;
        this.author = author;
        this.description = description;
        this.image_url = image_url;
        this.link = link;
        this.title = title;
 */

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
    public void goToAttractProfile(){
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }
    private void getData() {
        // String Request initialized
        StringRequest stringRequest = new StringRequest(Request.Method.GET, " http://10.0.2.2:5000/api/user", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);
                    //if no error in response
                    if (obj.getInt("err") == 0) {
                        JSONObject userJson = obj.getJSONObject("userData");
                        String fullname= userJson.getString("full_name");
                        tvHi.setText(fullname);

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String access_token = SharedPrefManager.getInstance(getActivity()).getUser().getAccess_token();
                headers.put("Authorization",  access_token);
                return headers;
            }
        };

        VolleySingle.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
    public void GetSearch(){
        String key= etSearch.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/book/?search_key="+key,
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
                                            object.getInt("page_number"),
                                            object.getString("author"),
                                            object.getString("description"),
                                            object.getString("image_url"),
                                            object.getString("link"),
                                            object.getString("title")
                                    );
                                    bookSearch.add(book);
                                }
                                bookAdapter.setBooks(bookSearch);
                                bookAdapter.notifyDataSetChanged();
                                Log.d("booksearch", String.valueOf(bookSearch.size()));
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

    @Override
    public void onCategoryClick( Category hymnModel) {
        bookSearchCate = new ArrayList<>();
        bookAdapter = new BookAdapter(getContext(), bookSearchCate);
        bookAdapter = new BookAdapter(getContext(), books);
        booksRecyclerView.setAdapter(bookAdapter);
        String key= etSearch.getText().toString();
        int idCate= hymnModel.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/api/book/?category_id="+idCate+"&search_key="+key,
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
                                            object.getInt("page_number"),
                                            object.getString("author"),
                                            object.getString("description"),
                                            object.getString("image_url"),
                                            object.getString("link"),
                                            object.getString("title")
                                    );
                                    bookSearchCate.add(book);
                                }
                                bookAdapter.setBooks( bookSearchCate);
                                bookAdapter.notifyDataSetChanged();
                                Log.d(" bookSearchCate", String.valueOf( bookSearchCate.size()));
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
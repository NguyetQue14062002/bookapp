package com.example.bookapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bookapp.Adapter.BookAdapter;
import com.example.bookapp.Adapter.CategoryAdapter;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.Domain.Category;
import com.example.bookapp.Domain.User;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        dataInitial();

        booksRecyclerView = view.findViewById(R.id.rcAllBooks);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        booksRecyclerView.setAdapter(new BookAdapter(getContext(), books));

        categoryRecyclerView = view.findViewById(R.id.rcCategory);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(new CategoryAdapter(getContext(), categories));
        tvHi = view.findViewById(R.id.tvHello);
        etSearch = view.findViewById(R.id.tvSearch);


            tvHi.setText("Chào "+ SharedPrefManager.getInstance(getActivity()).getUser().getFull_name());

        }


    private void dataInitial() {
        books = new ArrayList<>();
//        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));
//        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));
//        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));
//        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));
//        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));

        categories = new ArrayList<>();

        categories.add(new Category("Tiểu Thuyết"));
        categories.add(new Category("Truyện Tranh"));
        categories.add(new Category("Học thuật"));
        categories.add(new Category("Thiếu nhi"));
        categories.add(new Category("Cổ tích"));

    }


}
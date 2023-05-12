package com.example.bookapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.Adapter.BookAdapter;
import com.example.bookapp.Adapter.BookLibraryAdapter;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.R;

import java.util.ArrayList;


public class UnreadFragment extends Fragment {


    private ArrayList<Book> books;
    private RecyclerView booksRecyclerView;

    public UnreadFragment() {} //Constructor
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }       //onCreate

    public static UnreadFragment newInstance() {       //Create new instance
        UnreadFragment fragment = new UnreadFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.unread_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        books = new ArrayList<>();

        //TODO: Call api to get all books in database
        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));
        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));
        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));
        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));
        books.add(new Book(1, 1, 1, 1, "J. K. Rowling", "Description", "https://i.pinimg.com/564x/8a/86/6d/8a866d42d846e3a3f8bd7285feca53fc.jpg", "https://thuviensach.vn/pdf/viewer.php?id=dbc25e" ,"Harry Potter"));

        booksRecyclerView = view.findViewById(R.id.rcWantToReadBook);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        booksRecyclerView.setAdapter(new BookLibraryAdapter(getContext(), books));
    }
}
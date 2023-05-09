package com.example.bookapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.R;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView addToListBtn, backBtn, imgBook;

    private TextView bookName, author, description;

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        initData();





    }

    private void initData() {
        book = (Book) getIntent().getSerializableExtra("myBook");
        addToListBtn = findViewById(R.id.addToListBtn);
        backBtn = findViewById(R.id.backBtn);

        imgBook = findViewById(R.id.imgBook);
        bookName = findViewById(R.id.tvBookName);
        author = findViewById(R.id.tvAuthor);
        description = findViewById(R.id.tvIntro);

        // Add eventListener for buttons
        addToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(BookDetailActivity.this, addToListBtn);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.book_list, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                BookDetailActivity.this,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookDetailActivity.this, MainActivity.class));
            }
        });

        // Init Data for Book
        Glide.with(this).load(book.getImage_url()).into(imgBook);
        bookName.setText(book.getTitle());
        author.setText(book.getAuthor());
        description.setText(book.getDescription());
    }
}
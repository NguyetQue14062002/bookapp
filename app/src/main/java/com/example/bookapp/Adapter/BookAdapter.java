package com.example.bookapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookapp.Activity.BookDetailActivity;
import com.example.bookapp.Domain.Book;
import com.example.bookapp.R;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder>{

    Context context;
    ArrayList<Book> books;

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }

    public BookAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_book,parent,false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Book book = books.get(position);
        holder.bookTitle.setText(book.getTitle());
        holder.bookAuthor.setText(book.getAuthor());
        Glide.with(context).load(book.getImage_url()).into(holder.bookImage);

        //TODO: Add Event Listener for Book

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookDetailActivity.class);
                // TODO: Put a Book object to BookDetailActivity
                intent.putExtra("myBook", books.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder{

        ImageView bookImage;
        TextView bookTitle, bookAuthor;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookAuthor = itemView.findViewById(R.id.tvAuthorBook);
            bookImage = itemView.findViewById(R.id.imageView12);
            bookTitle = itemView.findViewById(R.id.tvNameBook);
        }
    }
}

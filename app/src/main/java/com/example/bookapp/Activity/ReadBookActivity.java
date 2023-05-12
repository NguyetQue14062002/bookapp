package com.example.bookapp.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
public class ReadBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readbook);

        WebView webView = findViewById(R.id.web);

        webView.loadUrl("https://thuviensach.vn/pdf/viewer.php?id=23d3de");
        //Todo gọi Api url Sách

        webView.getSettings().setJavaScriptEnabled(true);


        webView.setWebViewClient(new WebViewClient());
    }

}



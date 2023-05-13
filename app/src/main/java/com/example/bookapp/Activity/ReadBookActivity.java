package com.example.bookapp.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;
import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
public class ReadBookActivity extends AppCompatActivity {

    private ImageView imgexit;
    private TextView tvNameBook;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readbook);

        WebView webView = findViewById(R.id.web);
        imgexit= findViewById(R.id.exitReadBook);
        tvNameBook = findViewById(R.id.tvNameBookDeading);
        imgexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReadBookActivity.this,BookDetailActivity.class));
            }
        });
        Intent intent = getIntent();
        String idBook = intent.getStringExtra("id");
        String url ="http://10.0.2.2:5000/api/book/?id="+idBook;
//        webView.loadUrl("https://thuviensach.vn/pdf/viewer.php?id=23d3de");
//        //Todo gọi Api url Sách
//
//        webView.getSettings().setJavaScriptEnabled(true);
//
//
//        webView.setWebViewClient(new WebViewClient());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);
                    //if no error in response
                    if (obj.getInt("err") == 0) {

                        JSONObject bookJson = obj.getJSONObject("data");
                        JSONArray rowJson=bookJson.getJSONArray("rows");
                        String url = "";
                        String name = "";
                        for (int i=0; i< rowJson.length(); i++) {
                            JSONObject jsonInner = rowJson.getJSONObject(i);
                            url =  jsonInner.getString("link");
                            name = jsonInner.getString("title");
                        }
                        webView.loadUrl(url);
                        tvNameBook.setText(name);
                        //Todo gọi Api url Sách

                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new WebViewClient());
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        );

        VolleySingle.getInstance(this).addToRequestQueue(stringRequest);
    }

}



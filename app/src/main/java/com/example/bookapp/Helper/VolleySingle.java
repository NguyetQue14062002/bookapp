package com.example.bookapp.Helper;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingle {
    private static VolleySingle mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    //Constructor
    private VolleySingle(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }
    //Lay context
    public static synchronized  VolleySingle getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingle(context);
        }
        return mInstance;
    }

    //Ham RequestQueue
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    //Ham addToRequest
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}


package com.example.bookapp.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.bookapp.Activity.LoginActivity;
import com.example.bookapp.Domain.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefManager {
    /*
    Khoi tao cac hang key
        this.id = id;
        this.role_id = role_id;
        this.email = email;
        this.full_name = full_name;
        this.avatar = avatar;
        this.token = token;
        this.phone_number = phone_number;
        this.password = password;
     */
    private static final String SHARED_PREF_NAME = "volleyregisterlogin";
    private static final String KEY_FULL_NAME = "key_full_name";    private static final String KEY_ID = "key_id";
    private static final String KEY_ROLE_ID = "key_role_id";    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_AVATAR = "key_avatar";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_PHONE_NUMBER = "key_phone_number";
    private static final String KEY_PASSWORD = "key_password";

    private static final String KEY_ACCESS_TOKEN = "key_access_token";
    private static final String KEY_REFRESH_TOKEN = "key_refresh_token";

    private static final String KEY_HISTORY = "key_history";
    //History se chua cac id cua sach da co history


    private  static SharedPrefManager mInstance;
    private static Context ctx;

    //Constructor
    public SharedPrefManager(Context context) {
        ctx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //This method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putInt(KEY_ROLE_ID, user.getRole_id());
        editor.putString(KEY_FULL_NAME, user.getFull_name());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_AVATAR, user.getAvatar());
        editor.putString(KEY_PHONE_NUMBER, user.getPhone_number());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ACCESS_TOKEN, user.getAccess_token());
        editor.putString(KEY_REFRESH_TOKEN, user.getRefresh_token());
        editor.apply();
    }

    //This method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null) != null;
    }

    //This method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getInt(KEY_ROLE_ID, -1),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_FULL_NAME, null),
                sharedPreferences.getString(KEY_AVATAR, null),
                sharedPreferences.getString(KEY_TOKEN, null),
                sharedPreferences.getString(KEY_PHONE_NUMBER, null),
                sharedPreferences.getString(KEY_ACCESS_TOKEN, null),
                sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        );
    }

    //This method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        ctx.startActivity(new Intent(ctx, LoginActivity.class));
    }

    public void updateImage(String profileImage) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AVATAR, profileImage);
        editor.apply();
    }

    public ArrayList<Integer> getHistory() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
        ArrayList<Integer> history = gson.fromJson(sharedPreferences.getString(KEY_HISTORY, null), type);
        return history;
    }


    public void setHistory(ArrayList<Integer> history) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(KEY_HISTORY, gson.toJson(history));
        editor.apply();
    }
}

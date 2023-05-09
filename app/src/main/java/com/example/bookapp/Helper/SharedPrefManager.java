package com.example.bookapp.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.bookapp.Activity.LoginActivity;
import com.example.bookapp.Domain.User;

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
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.apply();
    }

    //This method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FULL_NAME, null) != null;
    }

    //This method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getInt(KEY_ROLE_ID, -1),
                sharedPreferences.getString(KEY_FULL_NAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_AVATAR, null),
                sharedPreferences.getString(KEY_PHONE_NUMBER, null),
                sharedPreferences.getString(KEY_PASSWORD, null),
                sharedPreferences.getString(KEY_TOKEN, null)
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
}

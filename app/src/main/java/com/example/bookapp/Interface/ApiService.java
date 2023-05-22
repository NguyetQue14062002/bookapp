package com.example.bookapp.Interface;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @Multipart
    @POST("api/post/")
    Call<ResponseBody> createPost(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part image,
            @Part("tcontent") RequestBody content
    );

    @Multipart
    @PUT("api/user/avatar/")
    Call<ResponseBody> uploadAvatar(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part image
    );
}

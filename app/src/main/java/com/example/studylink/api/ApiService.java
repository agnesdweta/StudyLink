package com.example.studylink.api;

import com.example.studylink.Assignment;
import com.example.studylink.model.LoginRequest;
import com.example.studylink.model.LoginResponse;
import com.example.studylink.model.RegisterRequest;
import com.example.studylink.model.RegisterResponse;
import com.example.studylink.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // ===== AUTH =====
    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ===== FORUM =====
    @GET("posts")
    Call<List<Post>> getPosts();

    @POST("posts")
    Call<Post> createPost(@Body Post post);

    @PUT("posts/{id}")
    Call<Post> updatePost(@Path("id") int id, @Body Post post);

    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);

    @GET("assignments")
    Call<List<Assignment>> getAssignments(
            @Header("Authorization") String token
    );
}

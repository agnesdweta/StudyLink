package com.example.studylink.api;

import com.example.studylink.Assignment;
import com.example.studylink.Schedule;
import com.example.studylink.model.LoginRequest;
import com.example.studylink.model.LoginResponse;
import com.example.studylink.model.RegisterRequest;
import com.example.studylink.model.RegisterResponse;
import com.example.studylink.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
    @POST("assignments")
    Call<Assignment> addAssignment(
            @Header("Authorization") String token,
            @Body Assignment assignment
    );
    @PUT("assignments/{id}")
    Call<Assignment> updateAssignment(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body Assignment assignment
    );
    @DELETE("assignments/{id}")
    Call<Void> deleteAssignment(
            @Header("Authorization") String token,
            @Path("id") int id
    );
    @GET("schedules")
    Call<List<Schedule>> getSchedules();

    @POST("schedules")
    Call<Schedule> addSchedule(@Body Schedule schedule);
    @PUT("schedules/{id}")
    Call<Schedule> updateSchedule(@Path("id") int id, @Body Schedule schedule);

    @DELETE("schedules/{id}")
    Call<Void> deleteSchedule(@Path("id") int id);
}


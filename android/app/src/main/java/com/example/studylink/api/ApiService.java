package com.example.studylink.api;

import com.example.studylink.Assignment;
import com.example.studylink.Course;
import com.example.studylink.Exam;
import com.example.studylink.Question;
import com.example.studylink.Schedule;
import com.example.studylink.model.*;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ================= AUTH =================
    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ================= FORUM =================
    @GET("posts")
    Call<List<Post>> getPosts();

    @POST("posts")
    Call<Post> createPost(@Body Post post);

    @PUT("posts/{id}")
    Call<Post> updatePost(@Path("id") int id, @Body Post post);

    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);

    // ================= ASSIGNMENT =================
    @GET("assignments")
    Call<List<Assignment>> getAssignments(@Header("Authorization") String token);

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

    // ================= SCHEDULE (ROOM SYNC) =================
    @GET("schedules")
    Call<List<Schedule>> getSchedules();

    @POST("schedules")
    Call<Schedule> addSchedule(@Body Schedule schedule);

    @PUT("schedules/{id}")
    Call<Schedule> updateSchedule(
            @Path("id") int id,
            @Body Schedule schedule
    );

    @DELETE("schedules/{id}")
    Call<Void> deleteSchedule(@Path("id") int id);

    // ================= EXAM =================
    @GET("exams")
    Call<List<Exam>> getExams();

    @GET("exams/{id}/questions")
    Call<List<Question>> getExamQuestions(@Path("id") int examId);

    @POST("exams")
    Call<Exam> addExam(@Body Exam exam);

    @PUT("exams/{id}")
    Call<Exam> updateExam(@Path("id") int id, @Body Exam exam);

    @DELETE("exams/{id}")
    Call<Void> deleteExam(@Path("id") int id);

    // ================= COURSE =================
    @GET("courses")
    Call<List<Course>> getCourses(@Header("Authorization") String token);

    @POST("courses")
    Call<Course> addCourse(
            @Header("Authorization") String token,
            @Body Course course
    );

    @PUT("courses/{id}")
    Call<Course> updateCourse(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body Course course
    );

    @DELETE("courses/{id}")
    Call<Void> deleteCourse(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @Multipart
    @POST("assignments/upload")
    Call<ResponseBody> uploadAssignment(
            @Part("assignmentId") RequestBody assignmentId,
            @Part MultipartBody.Part file
    );
}

package com.example.studylink.api;

import com.example.studylink.Assignment;
import com.example.studylink.CalendarEntity;
import com.example.studylink.Course;
import com.example.studylink.ExamEntity;
import com.example.studylink.ForumEntity;
import com.example.studylink.ImageResponse;
import com.example.studylink.Schedule;
import com.example.studylink.UserEntity;
import com.example.studylink.model.*;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
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
    // ================= USER PROFILE =================
    @GET("users/{id}")
    Call<UserEntity> getUser(@Path("id") long id);

    @PUT("users/{id}")
    Call<UserEntity> updateUser(@Path("id") long id, @Body UserEntity user);
    @Multipart
    @PUT("users/{id}/photo")
    Call<UserEntity> uploadUserPhoto(
            @Path("id") long id,
            @Part MultipartBody.Part photo
    );

    // ================= SCHEDULE (ROOM SYNC) =================
    @GET("schedules")
    Call<List<Schedule>> getSchedules();

    @POST("schedules")
    Call<Schedule> addSchedule(@Body Schedule schedule);

    @PUT("schedules/{id}")
    Call<Schedule> updateSchedule(
            @Path("id") long id,
            @Body Schedule schedule
    );

    @DELETE("schedules/{id}")
    Call<Void> deleteSchedule(@Path("id") long id);

    // ================= EXAM =================

    // GET all exams
    @GET("/exams")
    Call<List<ExamEntity>> getExams();

    // CREATE exam
    @POST("/exams")
    Call<ExamEntity> createExam(@Body ExamEntity exam);

    // UPDATE exam
    @PUT("/exams/{id}")
    Call<ExamEntity> updateExam(@Path("id") long id, @Body ExamEntity exam);

    // DELETE exam
    @DELETE("/exams/{id}")
    Call<Void> deleteExam(@Path("id") long id);

    // ================= COURSE =================
    @GET("courses")
    Call<List<Course>> getCourses();

    @POST("courses")
    Call<Course> addCourse(
            @Body Course course
    );

    @PUT("courses/{id}")
    Call<Course> updateCourse(
            @Path("id") long id,
            @Body Course course
    );

    @DELETE("courses/{id}")
    Call<Void> deleteCourse(
            @Path("id") long id
    );

    // ================= FORUM =================
    @GET("forum")
    Call<List<ForumEntity>> getForums();

    @POST("forum")
    Call<ForumEntity> createForumPost(@Body ForumEntity forum);

    @PUT("forum/{id}")
    Call<ForumEntity> updateForumPost(
            @Path("id") long id,
            @Body ForumEntity forum
    );
    @DELETE("forum/{id}")
    Call<Void> deleteForumPost(@Path("id") long id);

    // ================= CALENDAR =================

    // GET events by date (format yyyy-MM-dd)
    @GET("calendar/{date}")
    Call<List<CalendarEntity>> getCalendarEventsByDate(@Path("date") String date);

    // CREATE new calendar event
    @POST("calendar")
    Call<CalendarEntity> createCalendarEvent(@Body CalendarEntity event);
    // UPDATE calendar event by id
    @PUT("calendar/{id}")
    Call<CalendarEntity> updateCalendarEvent(
            @Path("id") long id,
            @Body CalendarEntity event
    );
    // DELETE calendar event by id
    @DELETE("calendar/{id}")
    Call<Void> deleteCalendarEvent(@Path("id") long id);

    @GET("calendar")
    Call<List<CalendarEntity>> getCalendarEvents();

    @Multipart
    @POST("assignments/{id}/image")
    Call<ImageResponse> uploadAssignmentImage(
            @Path("id") long id,
            @Part MultipartBody.Part image);
    @GET("assignments")
    Call<List<Assignment>> getAssignments();
    @PUT("assignments/{id}")
    Call<Assignment> updateAssignment(
            @Path("id") long id,
            @Body Assignment assignment);
    @POST("assignments")
    Call<Assignment> createAssignment(@Body Assignment assignment);

    @DELETE("assignments/{id}")
    Call<Void> deleteAssignment(@Path("id") long id);
}


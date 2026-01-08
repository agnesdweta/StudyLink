package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;

@Dao
public interface SearchAllDao {

    // Exam
    @Query("SELECT id AS itemId, title AS itemTitle, course AS itemDesc, 'Exam' AS type " +
            "FROM exams " +
            "WHERE title LIKE '%' || :keyword || '%' OR course LIKE '%' || :keyword || '%'")
    List<SearchResult> searchExam(String keyword);

    // Course
    @Query("SELECT id AS itemId, name AS itemTitle, description AS itemDesc, 'Course' AS type " +
            "FROM courses " +
            "WHERE name LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%'")
    List<SearchResult> searchCourse(String keyword);

    // Forum
    @Query("SELECT id AS itemId, content AS itemTitle, user AS itemDesc, 'Forum' AS type " +
            "FROM forum " +
            "WHERE content LIKE '%' || :keyword || '%' OR user LIKE '%' || :keyword || '%'")
    List<SearchResult> searchForum(String keyword);

    @Query("SELECT id AS itemId, title AS itemTitle, course AS itemDesc, 'Assignment' AS type " +
            "FROM assignments " +
            "WHERE title LIKE '%' || :keyword || '%' OR course LIKE '%' || :keyword || '%'")
    List<SearchResult> searchAssignment(String keyword);

    @Query("SELECT id AS itemId, title AS itemTitle, title AS itemDesc, 'Schedule' AS type " +
            "FROM schedules " +
            "WHERE title LIKE '%' || :keyword || '%'")
    List<SearchResult> searchSchedule(String keyword);

}
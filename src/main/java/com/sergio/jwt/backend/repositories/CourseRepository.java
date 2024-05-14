package com.sergio.jwt.backend.repositories;

import com.sergio.jwt.backend.entites.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c JOIN c.authors a WHERE a.id = :userId")
    List<Course> findCoursesByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Course c WHERE c.category = :category")
    List<Course> findCoursesByCategory(@Param("category") String category);

    List<Course> findAllByCategory(String category);
}


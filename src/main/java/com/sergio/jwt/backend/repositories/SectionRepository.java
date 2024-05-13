package com.sergio.jwt.backend.repositories;

import com.sergio.jwt.backend.entites.Course;
import com.sergio.jwt.backend.entites.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query("SELECT s FROM Section s WHERE s.course.id = :courseId")
    List<Section> findSectionsByCourseId(Long courseId) ;
}

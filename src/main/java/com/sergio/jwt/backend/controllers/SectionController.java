package com.sergio.jwt.backend.controllers;

import com.sergio.jwt.backend.dtos.NewCourseDto;
import com.sergio.jwt.backend.dtos.NewSectionDto;
import com.sergio.jwt.backend.entites.Course;
import com.sergio.jwt.backend.entites.Section;
import com.sergio.jwt.backend.entites.User;
import com.sergio.jwt.backend.repositories.CourseRepository;
import com.sergio.jwt.backend.repositories.SectionRepository;
import com.sergio.jwt.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/sections")
@CrossOrigin("*")
public class SectionController {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public SectionController(SectionRepository sectionRepository, CourseRepository courseRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<Section>> getSectionsByCourseId(@PathVariable("courseId") Long courseId) {
        List<Section> sections = sectionRepository.findSectionsByCourseId(courseId);
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/{sectionId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Section> getSectionById(@PathVariable("sectionId") Long sectionId) {
        Optional<Section> section = sectionRepository.findById(sectionId);
        return section.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/course/{courseId}") // Updated URL pattern
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Section> createSection(@PathVariable("courseId") Long courseId, @RequestBody NewSectionDto newSectionDto) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Course course = courseOptional.get();

        Section section = Section.builder()
                .name(newSectionDto.getName())
                .sectionOrder(newSectionDto.getSectionOrder())
                .course(course)
                .build();

        Section createdSection = sectionRepository.save(section);
        URI location = URI.create(String.format("/api/sections/%d", createdSection.getId()));
        return ResponseEntity.created(location).body(createdSection);
    }

    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteSection(@PathVariable("sectionId") Long sectionId) {
        Optional<Section> section = sectionRepository.findById(sectionId);
        if (section.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        sectionRepository.deleteById(sectionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{sectionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Section> updateSection(@PathVariable("sectionId") Long sectionId, @RequestBody NewSectionDto newSectionDto) {
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);
        if (sectionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Section section = sectionOptional.get();
        section.setName(newSectionDto.getName());
        section.setSectionOrder(newSectionDto.getSectionOrder());

        Section updatedSection = sectionRepository.save(section);
        return ResponseEntity.ok(updatedSection);
    }
}









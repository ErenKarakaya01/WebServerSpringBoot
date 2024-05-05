package com.sergio.jwt.backend.controllers;

import com.sergio.jwt.backend.dtos.NewCourseDto;
import com.sergio.jwt.backend.entites.Course;
import com.sergio.jwt.backend.entites.User;
import com.sergio.jwt.backend.repositories.CourseRepository;
import com.sergio.jwt.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/courses")
@CrossOrigin("*")
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Course>> getCoursesByUserId(@PathVariable("userId") Long userId) {
        List<Course> courses = courseRepository.findCoursesByUserId(userId);
        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Course> getCourseById(@PathVariable("courseId") Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);

        if (course.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }

        return ResponseEntity.ok(course.get()); // Return 200 OK with course data
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseRepository.findAll(); // Fetch all courses from the repository
        return ResponseEntity.ok(courses); // Return 200 OK with the list of courses
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable("courseId") Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        courseRepository.deleteById(courseId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Course> updateCourse(
            @PathVariable("courseId") Long courseId,
            @RequestBody NewCourseDto newCourseDto) {

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Course existingCourse = courseOpt.get();

        // Update title and description
        existingCourse.setTitle(newCourseDto.getTitle());
        existingCourse.setDescription(newCourseDto.getDescription());

        // Update authors without replacing existing ones
        Set<User> authors = new HashSet<>(existingCourse.getAuthors());
        for (Long authorId : newCourseDto.getAuthors()) {
            if (authors.stream().noneMatch(author -> author.getId().equals(authorId))) {
                Optional<User> userOpt = userRepository.findById(authorId);
                if (userOpt.isPresent()) {
                    authors.add(userOpt.get());
                } else {
                    return ResponseEntity.badRequest().body(null); // Author not found
                }
            }
        }

        existingCourse.setAuthors(new ArrayList<>(authors));

        // Save the updated course
        courseRepository.save(existingCourse);

        return ResponseEntity.ok(existingCourse);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Course> createCourse(@RequestBody NewCourseDto newCourseDto) {
        // Create a new Course object
        Course course = Course.builder()
                .title(newCourseDto.getTitle())
                .description(newCourseDto.getDescription())
                .build();

        // Find authors by their IDs
        List<User> authors = new ArrayList<>();
        for (Long authorId : newCourseDto.getAuthors()) {
            System.out.println(authorId);
            Optional<User> user = userRepository.findById(authorId);
            if (user.isPresent()) {
                authors.add(user.get());
            } else {
                return ResponseEntity.badRequest().body(null); // Or handle this appropriately
            }
        }

        // Set authors to the course
        course.setAuthors(authors);

        // Save the new course to the database
        Course createdCourse = courseRepository.save(course);

        URI location = URI.create(String.format("/api/courses/%d", createdCourse.getId()));

        return ResponseEntity.created(location).body(createdCourse);
    }
}



package com.intellilearn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.intellilearn.dto.request.CourseRequest;
import com.intellilearn.dto.response.CourseResponse;
import com.intellilearn.service.interfaces.CourseService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/courses")
@Validated
public class CourseController {


    @Autowired
    private CourseService courseService;


    // Create Course
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseRequest request) {

        CourseResponse response = courseService.createCourse(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    // Get All Courses
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {

        List<CourseResponse> courses = courseService.getAllCourses();

        return ResponseEntity.ok(courses);
    }


    // Get Course By ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long id) {

        CourseResponse response = courseService.getCourseById(id);

        return ResponseEntity.ok(response);
    }


    // Update Course
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {

        CourseResponse response = courseService.updateCourse(id, request);

        return ResponseEntity.ok(response);
    }


    // Delete Course
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Long id) {

        courseService.deleteCourse(id);

        return ResponseEntity.ok("Course deleted successfully");
    }

}
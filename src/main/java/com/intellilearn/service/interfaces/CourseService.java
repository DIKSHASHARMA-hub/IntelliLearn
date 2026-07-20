package com.intellilearn.service.interfaces;

import java.util.List;

import com.intellilearn.dto.response.CourseResponse;
import com.intellilearn.dto.request.CourseRequest;

public interface CourseService {

    CourseResponse createCourse(CourseRequest request);

    List<CourseResponse> getAllCourses();

    CourseResponse getCourseById(Long id);

    CourseResponse updateCourse(Long id, CourseRequest request);

    void deleteCourse(Long id);

}
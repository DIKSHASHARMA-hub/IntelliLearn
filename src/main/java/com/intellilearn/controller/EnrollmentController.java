package com.intellilearn.controller;

import com.intellilearn.entity.Enrollment;
import com.intellilearn.service.interfaces.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin("*")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public Enrollment enrollStudent(@RequestBody Enrollment enrollment) {
        return enrollmentService.enrollStudent(enrollment);
    }

    @GetMapping("/student/{id}")
    public List<Enrollment> getByStudent(@PathVariable Long id) {
        return enrollmentService.getEnrollmentsByStudent(id);
    }

    @GetMapping("/course/{id}")
    public List<Enrollment> getByCourse(@PathVariable Long id) {
        return enrollmentService.getEnrollmentsByCourse(id);
    }

    @DeleteMapping("/{id}")
    public String deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return "Enrollment deleted successfully.";
    }
}
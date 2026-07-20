package com.intellilearn.service.interfaces;

import com.intellilearn.entity.Enrollment;
import java.util.List;

public interface EnrollmentService {

    Enrollment enrollStudent(Enrollment enrollment);

    List<Enrollment> getEnrollmentsByStudent(Long studentId);

    List<Enrollment> getEnrollmentsByCourse(Long courseId);

    void deleteEnrollment(Long id);

}
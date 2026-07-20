package com.intellilearn.service.impl;

import com.intellilearn.entity.Enrollment;
import com.intellilearn.repository.EnrollmentRepository;
import com.intellilearn.service.interfaces.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public Enrollment enrollStudent(Enrollment enrollment) {

        enrollment.setEnrollmentDate(LocalDate.now());

        if (enrollment.getStatus() == null) {
            enrollment.setStatus("ACTIVE");
        }

        if (enrollment.getProgress() == null) {
            enrollment.setProgress(0);
        }

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Override
    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }
}
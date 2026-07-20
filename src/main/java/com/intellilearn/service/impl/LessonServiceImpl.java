package com.intellilearn.service.impl;

import com.intellilearn.entity.Course;
import com.intellilearn.entity.Lesson;
import com.intellilearn.repository.CourseRepository;
import com.intellilearn.repository.LessonRepository;
import com.intellilearn.service.interfaces.LessonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    public LessonServiceImpl(LessonRepository lessonRepository,
                             CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Lesson createLesson(Lesson lesson) {

        Course course = courseRepository.findById(
                lesson.getCourse().getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        lesson.setCourse(course);

        return lessonRepository.save(lesson);
    }

    @Override
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    @Override
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    @Override
    public List<Lesson> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
    }

    @Override
    public Lesson updateLesson(Long id, Lesson lesson) {

        Lesson existing = getLessonById(id);

        existing.setTitle(lesson.getTitle());
        existing.setDescription(lesson.getDescription());
        existing.setVideoUrl(lesson.getVideoUrl());
        existing.setPdfUrl(lesson.getPdfUrl());
        existing.setLessonOrder(lesson.getLessonOrder());
        existing.setDuration(lesson.getDuration());

        if (lesson.getCourse() != null) {

            Course course = courseRepository.findById(
                    lesson.getCourse().getId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            existing.setCourse(course);
        }

        return lessonRepository.save(existing);
    }

    @Override
    public void deleteLesson(Long id) {

        Lesson lesson = getLessonById(id);

        lessonRepository.delete(lesson);
    }
}
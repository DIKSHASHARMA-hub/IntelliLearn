package com.intellilearn.service.interfaces;

import com.intellilearn.entity.Lesson;

import java.util.List;

public interface LessonService {

    Lesson createLesson(Lesson lesson);

    List<Lesson> getAllLessons();

    Lesson getLessonById(Long id);

    List<Lesson> getLessonsByCourse(Long courseId);

    Lesson updateLesson(Long id, Lesson lesson);

    void deleteLesson(Long id);
}
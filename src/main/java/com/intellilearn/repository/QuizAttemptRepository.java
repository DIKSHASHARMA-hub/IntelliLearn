package com.intellilearn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.intellilearn.entity.QuizAttempt;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByStudentId(Long userId);

    List<QuizAttempt> findByQuizId(Long quizId);
    List<QuizAttempt> findTop3ByStudentIdOrderBySubmittedAtDesc(Long userId);

}
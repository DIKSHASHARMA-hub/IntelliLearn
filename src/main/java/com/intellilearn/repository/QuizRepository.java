package com.intellilearn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.intellilearn.entity.Quiz;
import com.intellilearn.entity.Subject;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findBySubject(Subject subject);

}
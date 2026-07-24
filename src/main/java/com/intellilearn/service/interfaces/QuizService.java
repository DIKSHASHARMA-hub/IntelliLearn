package com.intellilearn.service.interfaces;

import com.intellilearn.dto.response.QuizResponseDTO;

public interface QuizService {

    QuizResponseDTO generateQuiz(Long subjectId);

    QuizResponseDTO getQuiz(Long quizId);

}
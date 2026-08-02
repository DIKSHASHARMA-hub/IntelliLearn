package com.intellilearn.service.interfaces;

import com.intellilearn.dto.response.QuizResponseDTO;

public interface QuizService {

   
    QuizResponseDTO generateQuizForNote(Long noteId);

    QuizResponseDTO getQuiz(Long quizId);

    QuizResponseDTO getQuizBySubject(Long subjectId);

    QuizResponseDTO getQuizByNote(Long noteId);

}

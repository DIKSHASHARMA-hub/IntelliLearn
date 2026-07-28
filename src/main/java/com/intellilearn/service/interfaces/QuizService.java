package com.intellilearn.service.interfaces;

import com.intellilearn.dto.response.QuizResponseDTO;

public interface QuizService {

    /** Generates a quiz from a single note (PDF) — one quiz per note, not per subject. */
    QuizResponseDTO generateQuizForNote(Long noteId);

    QuizResponseDTO getQuiz(Long quizId);

    QuizResponseDTO getQuizBySubject(Long subjectId);

    QuizResponseDTO getQuizByNote(Long noteId);

}

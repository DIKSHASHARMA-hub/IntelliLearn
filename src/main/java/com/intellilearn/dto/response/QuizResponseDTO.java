package com.intellilearn.dto.response;

import java.util.List;

public class QuizResponseDTO {

    private Long quizId;

    private String title;

    private List<QuestionDTO> questions;

    public QuizResponseDTO() {
    }

    public QuizResponseDTO(Long quizId, String title, List<QuestionDTO> questions) {
        this.quizId = quizId;
        this.title = title;
        this.questions = questions;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}
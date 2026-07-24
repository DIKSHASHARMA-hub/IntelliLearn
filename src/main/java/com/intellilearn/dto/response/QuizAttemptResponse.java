package com.intellilearn.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class QuizAttemptResponse {

    private Long attemptId;
    private Long studentId;
    private Long quizId;
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
    private String message;
    private LocalDateTime submittedAt;
    private List<AnswerResultResponse> answers;

    public QuizAttemptResponse() {
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<AnswerResultResponse> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerResultResponse> answers) {
        this.answers = answers;
    }
}
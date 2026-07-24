package com.intellilearn.dto.response;

import java.time.LocalDateTime;

public class RecentAttemptDTO {

    private String quizTitle;
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
    private LocalDateTime submittedAt;

    public RecentAttemptDTO() {
    }

    public RecentAttemptDTO(String quizTitle, Integer score, Integer totalQuestions,
                            Double percentage, LocalDateTime submittedAt) {
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.submittedAt = submittedAt;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
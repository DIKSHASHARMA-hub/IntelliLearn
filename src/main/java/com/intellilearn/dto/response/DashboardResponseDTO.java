package com.intellilearn.dto.response;

import java.util.List;

public class DashboardResponseDTO {

    private Long studentId;
    private String studentName;

    private Integer totalQuizzesAttempted;
    private Double averageScore;
    private Double highestScore;

    private List<RecentAttemptDTO> recentAttempts;

    public DashboardResponseDTO() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getTotalQuizzesAttempted() {
        return totalQuizzesAttempted;
    }

    public void setTotalQuizzesAttempted(Integer totalQuizzesAttempted) {
        this.totalQuizzesAttempted = totalQuizzesAttempted;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Double highestScore) {
        this.highestScore = highestScore;
    }

    public List<RecentAttemptDTO> getRecentAttempts() {
        return recentAttempts;
    }

    public void setRecentAttempts(List<RecentAttemptDTO> recentAttempts) {
        this.recentAttempts = recentAttempts;
    }
}
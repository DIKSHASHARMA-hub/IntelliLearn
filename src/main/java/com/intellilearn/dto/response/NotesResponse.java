package com.intellilearn.dto.response;

import java.time.LocalDate;

public class NotesResponse {

    private Long id;

    private String title;

    private String fileName;

    private LocalDate uploadDate;

    private Long subjectId;

    private String subjectName;

    private Long uploadedByUserId;

    public NotesResponse() {
    }

    public NotesResponse(Long id,
                         String title,
                         String fileName,
                         LocalDate uploadDate,
                         Long subjectId,
                         String subjectName,
                         Long uploadedByUserId) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.uploadDate = uploadDate;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.uploadedByUserId = uploadedByUserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Long getUploadedByUserId() {
        return uploadedByUserId;
    }

    public void setUploadedByUserId(Long uploadedByUserId) {
        this.uploadedByUserId = uploadedByUserId;
    }
}
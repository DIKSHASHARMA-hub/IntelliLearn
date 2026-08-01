package com.intellilearn.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "notes")
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDate uploadDate;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    public Notes() {
    }

    public Notes(Long id,
                 String title,
                 String fileName,
                 String filePath,
                 LocalDate uploadDate,
                 Subject subject) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
        this.subject = subject;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}
package com.example.mindjavafx.model;

import java.time.LocalDateTime;

public class Report {
    private int id;
    private int userId;
    private int auditId;
    private String name;
    private String filePath;
    private long fileSize;
    private LocalDateTime generatedAt;

    // Default constructor
    public Report() {
    }

    // Constructor with all fields
    public Report(int id, int userId, int auditId, String name, String filePath,
                  long fileSize, LocalDateTime generatedAt) {
        this.id = id;
        this.userId = userId;
        this.auditId = auditId;
        this.name = name;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.generatedAt = generatedAt;
    }

    // Constructor without ID (for creating new reports)
    public Report(int userId, int auditId, String name, String filePath, long fileSize) {
        this.userId = userId;
        this.auditId = auditId;
        this.name = name;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", userId=" + userId +
                ", auditId=" + auditId +
                ", name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", generatedAt=" + generatedAt +
                '}';
    }
}

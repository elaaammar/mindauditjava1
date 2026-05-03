package com.example.mindjavafx.model;

import java.time.LocalDateTime;

public class Audit {
    private int id;
    private int userId;
    private String name;
    private String category;
    private int globalScore;
    private int securityScore;
    private int complianceScore;
    private int performanceScore;
    private String findings;
    private String status;
    private LocalDateTime auditDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Audit() {
    }

    // Constructor with all fields
    public Audit(int id, int userId, String name, String category, int globalScore,
                 int securityScore, int complianceScore, int performanceScore,
                 String findings, String status, LocalDateTime auditDate,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.globalScore = globalScore;
        this.securityScore = securityScore;
        this.complianceScore = complianceScore;
        this.performanceScore = performanceScore;
        this.findings = findings;
        this.status = status;
        this.auditDate = auditDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor without ID (for creating new audits)
    public Audit(int userId, String name, String category, int globalScore,
                 int securityScore, int complianceScore, int performanceScore,
                 String findings, String status) {
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.globalScore = globalScore;
        this.securityScore = securityScore;
        this.complianceScore = complianceScore;
        this.performanceScore = performanceScore;
        this.findings = findings;
        this.status = status;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getGlobalScore() {
        return globalScore;
    }

    public void setGlobalScore(int globalScore) {
        this.globalScore = globalScore;
    }

    public int getSecurityScore() {
        return securityScore;
    }

    public void setSecurityScore(int securityScore) {
        this.securityScore = securityScore;
    }

    public int getComplianceScore() {
        return complianceScore;
    }

    public void setComplianceScore(int complianceScore) {
        this.complianceScore = complianceScore;
    }

    public int getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(int performanceScore) {
        this.performanceScore = performanceScore;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDateTime auditDate) {
        this.auditDate = auditDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Audit{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", globalScore=" + globalScore +
                ", status='" + status + '\'' +
                ", auditDate=" + auditDate +
                '}';
    }
}

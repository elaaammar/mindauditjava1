package com.example.mindjavafx.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Schedule {
    private int id;
    private int userId;
    private String auditName;
    private String category;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private boolean reminderSent;
    private String status;
    private LocalDateTime createdAt;

    // Default constructor
    public Schedule() {
    }

    // Constructor with all fields
    public Schedule(int id, int userId, String auditName, String category,
                   LocalDate scheduledDate, LocalTime scheduledTime,
                   boolean reminderSent, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.auditName = auditName;
        this.category = category;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.reminderSent = reminderSent;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor without ID (for creating new schedules)
    public Schedule(int userId, String auditName, String category,
                   LocalDate scheduledDate, LocalTime scheduledTime) {
        this.userId = userId;
        this.auditName = auditName;
        this.category = category;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.reminderSent = false;
        this.status = "pending";
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

    public String getAuditName() {
        return auditName;
    }

    public void setAuditName(String auditName) {
        this.auditName = auditName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", userId=" + userId +
                ", auditName='" + auditName + '\'' +
                ", category='" + category + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", scheduledTime=" + scheduledTime +
                ", status='" + status + '\'' +
                '}';
    }
}

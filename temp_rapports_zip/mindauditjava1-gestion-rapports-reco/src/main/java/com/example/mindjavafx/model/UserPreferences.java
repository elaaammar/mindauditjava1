package com.example.mindjavafx.model;

import java.time.LocalDateTime;

public class UserPreferences {
    private int userId;
    private boolean darkModeEnabled;
    private boolean notificationAuditComplete;
    private boolean notificationRecommendations;
    private boolean notificationScheduledReminders;
    private LocalDateTime updatedAt;

    // Default constructor
    public UserPreferences() {
    }

    // Constructor with all fields
    public UserPreferences(int userId, boolean darkModeEnabled,
                          boolean notificationAuditComplete,
                          boolean notificationRecommendations,
                          boolean notificationScheduledReminders,
                          LocalDateTime updatedAt) {
        this.userId = userId;
        this.darkModeEnabled = darkModeEnabled;
        this.notificationAuditComplete = notificationAuditComplete;
        this.notificationRecommendations = notificationRecommendations;
        this.notificationScheduledReminders = notificationScheduledReminders;
        this.updatedAt = updatedAt;
    }

    // Constructor with defaults
    public UserPreferences(int userId) {
        this.userId = userId;
        this.darkModeEnabled = false;
        this.notificationAuditComplete = true;
        this.notificationRecommendations = true;
        this.notificationScheduledReminders = true;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isDarkModeEnabled() {
        return darkModeEnabled;
    }

    public void setDarkModeEnabled(boolean darkModeEnabled) {
        this.darkModeEnabled = darkModeEnabled;
    }

    public boolean isNotificationAuditComplete() {
        return notificationAuditComplete;
    }

    public void setNotificationAuditComplete(boolean notificationAuditComplete) {
        this.notificationAuditComplete = notificationAuditComplete;
    }

    public boolean isNotificationRecommendations() {
        return notificationRecommendations;
    }

    public void setNotificationRecommendations(boolean notificationRecommendations) {
        this.notificationRecommendations = notificationRecommendations;
    }

    public boolean isNotificationScheduledReminders() {
        return notificationScheduledReminders;
    }

    public void setNotificationScheduledReminders(boolean notificationScheduledReminders) {
        this.notificationScheduledReminders = notificationScheduledReminders;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "userId=" + userId +
                ", darkModeEnabled=" + darkModeEnabled +
                ", notificationAuditComplete=" + notificationAuditComplete +
                ", notificationRecommendations=" + notificationRecommendations +
                ", notificationScheduledReminders=" + notificationScheduledReminders +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

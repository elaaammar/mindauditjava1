package com.example.mindjavafx.model;

import java.time.LocalDateTime;

public class Recommendation {
    private int id;
    private int userId;
    private String category;
    private String recommendationText;
    private String priority;
    private boolean isDismissed;
    private LocalDateTime createdAt;

    // Default constructor
    public Recommendation() {
    }

    // Constructor with all fields
    public Recommendation(int id, int userId, String category, String recommendationText,
                         String priority, boolean isDismissed, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.recommendationText = recommendationText;
        this.priority = priority;
        this.isDismissed = isDismissed;
        this.createdAt = createdAt;
    }

    // Constructor without ID (for creating new recommendations)
    public Recommendation(int userId, String category, String recommendationText, String priority) {
        this.userId = userId;
        this.category = category;
        this.recommendationText = recommendationText;
        this.priority = priority;
        this.isDismissed = false;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRecommendationText() {
        return recommendationText;
    }

    public void setRecommendationText(String recommendationText) {
        this.recommendationText = recommendationText;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isDismissed() {
        return isDismissed;
    }

    public void setDismissed(boolean dismissed) {
        isDismissed = dismissed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "id=" + id +
                ", userId=" + userId +
                ", category='" + category + '\'' +
                ", priority='" + priority + '\'' +
                ", isDismissed=" + isDismissed +
                ", createdAt=" + createdAt +
                '}';
    }
}

package models;

import java.util.ArrayList;
import java.util.List;

public class Rapport {
    private int id;
    private String title;
    private String description;
    private String date;
    private String type; // New field for categorization
    private int duration; // Exam duration in minutes
    private List<QuestionAudit> questions = new ArrayList<>(); // New list for associated questions

    public Rapport() {}

    public Rapport(int id, String title, String description, String date, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    public Rapport(String title, String description, String date, String type) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Rapport{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", question_count=" + (questions != null ? questions.size() : 0) +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<QuestionAudit> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionAudit> questions) {
        this.questions = questions;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

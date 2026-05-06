package models;

public class QuestionAudit {
    private int id;
    private String content;
    private String type;
    private String bonneReponse; // Correct answer set by Admin (hidden from User)
    private int timeLimit; // Time limit in seconds for this specific question

    public QuestionAudit() {}

    public QuestionAudit(int id, String content, String type) {
        this.id = id;
        this.content = content;
        this.type = type;
    }

    public QuestionAudit(String content, String type) {
        this.content = content;
        this.type = type;
    }

    @Override
    public String toString() {
        return "QuestionAudit{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBonneReponse() {
        return bonneReponse;
    }

    public void setBonneReponse(String bonneReponse) {
        this.bonneReponse = bonneReponse;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}

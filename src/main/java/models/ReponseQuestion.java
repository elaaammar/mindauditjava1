package models;

public class ReponseQuestion {
    private int id;
    private int questionId;
    private int rapportId;
    private String reponse;
    private String dateReponse;

    public ReponseQuestion() {}

    public ReponseQuestion(int questionId, int rapportId, String reponse, String dateReponse) {
        this.questionId = questionId;
        this.rapportId = rapportId;
        this.reponse = reponse;
        this.dateReponse = dateReponse;
    }

    public ReponseQuestion(int id, int questionId, int rapportId, String reponse, String dateReponse) {
        this.id = id;
        this.questionId = questionId;
        this.rapportId = rapportId;
        this.reponse = reponse;
        this.dateReponse = dateReponse;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public int getRapportId() { return rapportId; }
    public void setRapportId(int rapportId) { this.rapportId = rapportId; }

    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }

    public String getDateReponse() { return dateReponse; }
    public void setDateReponse(String dateReponse) { this.dateReponse = dateReponse; }
}

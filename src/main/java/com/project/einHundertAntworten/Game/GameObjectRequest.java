package com.project.einHundertAntworten.Game;

public class GameObjectRequest {
    private String answer;
    private String question;
    private String category;

    public GameObjectRequest(String answer, String question, String category) {
        this.answer = answer;
        this.question = question;
        this.category = category;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

package com.project.einHundertAntworten.Game;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gameobjects")
public class GameObject {
    private String id;
    private String answer;
    private String question;
    private String category;

    public GameObject(String id, String answer, String question, String category){
        this.id = id;
        this.answer = answer;
        this.question = question;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

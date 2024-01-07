package com.project.einHundertAntworten.Game;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "questions")
public class Question {
    @Id
    private String id;
    // Question Text
    private String text;
    // The id of the matching answer
    private String match;
    private String category;
    public Question(String text, String match) {
        this.text = text;
        this.match = match;
    }
    public Question(String text) {
        this.text = text;
    }

    public Question() {
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}

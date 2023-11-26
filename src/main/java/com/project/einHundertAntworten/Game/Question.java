package com.project.einHundertAntworten.Game;

import org.springframework.data.annotation.Id;

public class Question {
    @Id
    private String id;
    // Question Text
    private String text;
    // The id of the matching answer
    private String match;

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
}

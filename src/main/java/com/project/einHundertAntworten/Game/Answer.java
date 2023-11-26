package com.project.einHundertAntworten.Game;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Answer {
    @Id
    private String id;
    // Answer test
    private String text;
    // List of ids of the correct questions for this answer
    private List<String> matches = new ArrayList<>();
    // List of ids of possible answers
    private List<String> filler = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getMatches() {
        return matches;
    }

    public void setMatches(List<String> matches) {
        this.matches = matches;
    }
}

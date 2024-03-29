package com.project.einHundertAntworten.Game;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "answers")
public class Answer {
    @Id
    private String id;
    // Answer test
    private String text;
    // List of ids of the correct questions for this answer
    private List<String> matches = new ArrayList<>();
    // List of ids of possible answers
    private List<String> filler = new ArrayList<>();
    private String category;

    public Answer(String text, List<String> matches, List<String> filler, String category) {
        this.text = text;
        this.matches = matches;
        this.filler = filler;
        this.category = category;
    }
    public Answer(List<String> matches, List<String> filler) {
        this.matches = matches;
        this.filler = filler;
    }
    public Answer(String text) {
        this.text = text;
    }
    public Answer() {
    }
    public Answer(String text, String category) {
        this.text = text;
        this.category = category;
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

    public List<String> getMatches() {
        return matches;
    }

    public void setMatches(List<String> matches) {
        this.matches = matches;
    }

    public void addMatch(String match) {
        this.matches.add(match);
    }

    public void addFiller(String filler) {
        this.filler.add(filler);
    }

    public List<String> getFiller() {
        return filler;
    }

    public void setFiller(List<String> filler) {
        this.filler = filler;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}

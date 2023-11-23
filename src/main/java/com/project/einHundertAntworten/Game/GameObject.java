package com.project.einHundertAntworten.Game;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "gameobjects")
public class GameObject {
    @Id
    private String id;

    // e.g. "Paris"
    private String answer;

    // e.g. ["Wo liegt der Eiffelturm?", "Was ist die Hauptstadt von Frankreich?"]
    private List<String> questions = new ArrayList<>();
    private String category;

    public GameObject(String id, String answer, List<String> questions, String category){
        this.id = id;
        this.answer = answer;
        this.questions = questions;
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

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public void addQuestions(List<String> questions) {
        this.questions.addAll(questions);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

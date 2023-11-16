package com.project.einHundertAntworten;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String id;
    private String answerText;
    private List<String> questionSet = new ArrayList<>();

    public GameObject(String id, String answerText, List<String> questionSet){
        this.id = id;
        this.answerText = answerText;
        this.questionSet = questionSet;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public List<String> getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(List<String> questionSet) {
        this.questionSet = questionSet;
    }
}

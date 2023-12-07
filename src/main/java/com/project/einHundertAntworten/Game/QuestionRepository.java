package com.project.einHundertAntworten.Game;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {


    boolean existsByText(String text);
}

package com.project.einHundertAntworten.Game;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnswerRepository extends MongoRepository<Answer, String> {


    boolean existsByText(String text);
}

package com.project.einHundertAntworten.Game;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GameRepository extends MongoRepository<GameObject, String> {

    boolean existsByAnswer(String answer);

    GameObject findByAnswer(String answer);

    List<GameObject> findAllByCategory(String category);
}

package com.project.einHundertAntworten.Game;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<GameObject, String> {


}
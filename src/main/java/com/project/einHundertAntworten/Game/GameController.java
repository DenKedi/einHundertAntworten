package com.project.einHundertAntworten.Game;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    private GameRepository gameRepository;

    GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @GetMapping("/getAll")
    public List<GameObject> getAllGameObjects() {
        return gameRepository.findAll();
    }

//    @PostMapping("/create")
//    public ResponseEntity<String> addGameObject(@RequestBody GameObject gameObject) {
//        String answer = gameObject.getAnswer();
//        List<String> questions = gameObject.getQuestions();
//
//        if (checkIfGameObjectExists(answer, questions)) {
//            return new ResponseEntity<>("Game Object already exists", HttpStatus.BAD_REQUEST);
//        }
//
//        gameRepository.save(gameObject);
//        return new ResponseEntity<>("Game Object created.", HttpStatus.OK);
//    }
//
//    @PutMapping("/update")
//    public ResponseEntity<String> updateGameObject(@RequestBody GameObject gameObject) {
//        String answer = gameObject.getAnswer();
//        List<String> questions = gameObject.getQuestions();
//
//        if (!gameRepository.existsByAnswer(answer)) {
//            return new ResponseEntity<>("Game Object does not exist.", HttpStatus.BAD_REQUEST);
//        }
//
//        GameObject go = gameRepository.findByAnswer(answer);
//        gameRepository.save(go);
//        return new ResponseEntity<>("Game Object updated.", HttpStatus.OK);
//    }
//
//    public boolean checkIfGameObjectExists(String answer, List<String> questions) {
//        if (gameRepository.existsByAnswer(answer)) {
//            GameObject go = gameRepository.findByAnswer(answer);
//            return new HashSet<>(go.getQuestions()).equals(new HashSet<>(questions));
//        }
//        return false;
//    }
}



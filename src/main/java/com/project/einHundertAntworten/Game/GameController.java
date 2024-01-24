package com.project.einHundertAntworten.Game;

import com.project.einHundertAntworten.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.*;

@RestController
@RequestMapping("/game")
public class GameController {


    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/getAnswer/{id}")
    public ResponseEntity<Answer> getAnswer(@PathVariable String id) {

        Optional<Answer> answerOptional = answerRepository.findById(id);
        if (answerOptional.isPresent()){
            Answer answer = answerOptional.get();
            return new ResponseEntity<>(answer, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/getQuestion/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable String id) {

        Optional<Question> questionOptional = questionRepository.findById(id);
        if (questionOptional.isPresent()){
            Question question = questionOptional.get();
            return new ResponseEntity<>(question, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/getAllAnswers")
    public ResponseEntity<List<Answer>> getAllAnswers() {
        List<Answer> answers = answerRepository.findAll();
        return new ResponseEntity<>(answers, HttpStatus.OK);
    }
    @GetMapping("/getAllQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }
    @GetMapping("/getQuestions") //Request param category
    public ResponseEntity<List<Question>> getQuestionByCategory(@RequestParam String category) {
        List<Question> questions = questionRepository.findAll();
        List<Question> questionsByCategory = new ArrayList<>();
        for (Question question : questions) {
                if (question.getCategory().contains(category)){
                    questionsByCategory.add(question);
                }
        }
        if (questionsByCategory.isEmpty()){
            questionsByCategory.add(new Question("", category));
            return new ResponseEntity<>(questionsByCategory, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(questionsByCategory, HttpStatus.OK);
    }
    @GetMapping("/getAnswers") //Request param category
    public ResponseEntity<List<Answer>> getAnswerByCategory(@RequestParam String category) {
        List<Answer> answers = answerRepository.findAll();
        List<Answer> answersByCategory = new ArrayList<>();
        for (Answer answer : answers) {
            if (answer.getCategory().contains(category)){
                answersByCategory.add(answer);
            }
        }
        if (answersByCategory.isEmpty()){
            answersByCategory.add(new Answer("", category));
            return new ResponseEntity<>(answersByCategory, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(answersByCategory, HttpStatus.OK);
    }
    @PostMapping("/createAnswer")
    public ResponseEntity<Answer> createAnswer(@RequestBody Answer answer) {
        if (answerRepository.existsByText(answer.getText())){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        answerRepository.save(answer);
        return new ResponseEntity<>(answer, HttpStatus.CREATED);
    }
    @PostMapping("/createQuestion")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        if (questionRepository.existsByText(question.getText())){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        questionRepository.save(question);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    @PutMapping("/answer/{id}") //add filler and matches for answer
        public ResponseEntity<Answer> updateAnswer(@PathVariable String id, @RequestBody Answer answerRequest) {
       Optional<Answer> answerOptional = answerRepository.findById(id);
       if (answerOptional.isPresent()){
              Answer answer = answerOptional.get();
              for (String match : answerRequest.getMatches()) {
                  if (!answer.getMatches().contains(match)){
                      answer.addMatch(match);
                  }
              }
              for (String filler : answerRequest.getFiller()) {
                  if (!answer.getFiller().contains(filler)) {
                      answer.addFiller(filler);
                  }
              }
                 answerRepository.save(answer);
              return new ResponseEntity<>(answer, HttpStatus.OK);
       }

               return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/answer/removeFillerAndMatches/{id}") //remove filler and matches for answer
    public ResponseEntity<Answer> updateAnswerFiller(@PathVariable String id, @RequestBody Answer answerRequest) {
        Optional<Answer> answerOptional = answerRepository.findById(id);
        if (answerOptional.isPresent()){
            Answer answer = answerOptional.get();
            for (String match : answerRequest.getMatches()) {
                answer.getMatches().remove(match);
            }
            for (String filler : answerRequest.getFiller()) {
                answer.getFiller().remove(filler);
            }
            answerRepository.save(answer);
            return new ResponseEntity<>(answer, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/question/{id}") //Add match for question
    //Anmerkung: Änderung möglich machen, auch wenn match schon gesetzt ist?
    public ResponseEntity<Question> updateQuestion(@PathVariable String id, @RequestBody QuestionRequest request) {
        System.out.println(request.match);
        Optional<Question> questionOptional = questionRepository.findById(id);
        if (questionOptional.isPresent()){
         Question question = questionOptional.get();
         if (question.getMatch() == null || question.getMatch().isEmpty()){
                question.setMatch(request.match);
                questionRepository.save(question);
                return new ResponseEntity<>(question, HttpStatus.OK);
         }else{
                return new ResponseEntity<>(HttpStatus.CONFLICT);
         }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteAnswer/{id}")
    public ResponseEntity<Answer> deleteAnswer(@PathVariable String id) {
        Optional<Answer> answerOptional = answerRepository.findById(id);
        if (answerOptional.isPresent()){
            Answer answer = answerOptional.get();
            for (String match : answer.getMatches()) {
                Optional<Question> questionOptional = questionRepository.findById(match);
                if (questionOptional.isPresent()){
                    Question question = questionOptional.get();
                    for (Answer a : answerRepository.findAll()) {
                        if (a.getFiller().contains(question.getId())){
                            a.getFiller().remove(question.getId());
                            answerRepository.save(a);
                        }
                    }
                    questionRepository.delete(question);
                }

            }
            answerRepository.delete(answer);
            return new ResponseEntity<>(answer, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/deleteQuestion/{id}")
    public ResponseEntity<Question> deleteQuestion(@PathVariable String id) {
        Optional<Question> questionOptional = questionRepository.findById(id);
        if (questionOptional.isPresent()){

            Question question = questionOptional.get();
            Optional<Answer> answerOptional = answerRepository.findById(question.getMatch());
            if (answerOptional.isPresent()){
                Answer answer = answerOptional.get();
                answer.getMatches().remove(question.getId());
                answerRepository.save(answer);
            }
            for (Answer answer : answerRepository.findAll()) {
                if (answer.getFiller().contains(question.getId())){
                    answer.getFiller().remove(question.getId());
                    answerRepository.save(answer);
                }
            }
            questionRepository.delete(question);
            return new ResponseEntity<>(question, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getFullAnswer/{id}")//Test Request
    public ResponseEntity<List<String>[]> getFullAnswer(@PathVariable String id) {
        Optional<Answer> answerOptional = answerRepository.findById(id);
        List<String> answerText = new ArrayList<>();
        List<String> matches = new ArrayList<>();
        List<String> filler = new ArrayList<>();

        if (answerOptional.isPresent()){
            Answer answer = answerOptional.get();
            answerText.add(answer.getText());

            for (String match : answer.getMatches()) {
                if (questionRepository.existsById(match)){

                    Question question = questionRepository.findById(match).get();
                    if (question.getMatch().equals(answer.getId())) {
                        matches.add(question.getText());
                    }
                }else{
                    System.out.println("Question not found");
                }
            }
            for (String fill : answer.getFiller()) {
              if (questionRepository.existsById(fill)) {
                  Question question = questionRepository.findById(fill).get();
                  if (question.getMatch().equals(answer.getId())) {
                      filler.add(question.getText());
                  }

              }else{
                  System.out.println("Question not found");
              }
            }
            List<String>[] result = new List[3];
            result[0] = answerText;
            result[1] = matches;
            result[2] = filler;
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}



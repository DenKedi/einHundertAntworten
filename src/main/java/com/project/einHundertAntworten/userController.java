package com.project.einHundertAntworten;

import com.project.einHundertAntworten.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Check if the username or email already exists

        if (userRepository.existsByUsername(user.getUsername())) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }

        //encrypt password
        System.out.println(user.getPassword());
        user.setPassword(user.getPassword());
        System.out.println(user.getPassword());

        // Save the user to the database
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {

        return null;
    }

    @GetMapping("/getall")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(User user, String passwordNew){

        if (user.getPassword().equals(passwordNew)){
            return new ResponseEntity<String>("Passwords are equal.", HttpStatus.CONFLICT);
        }
        if (!Utility.pwMeetsRequirements(passwordNew).equals(Utility.statusOK)){
            return new ResponseEntity<String>(Utility.pwMeetsRequirements(passwordNew), HttpStatus.CONFLICT);
        }
        user.setPassword(passwordNew);
        return new ResponseEntity<String> ("Password changed.", HttpStatus.CREATED);
    }
}

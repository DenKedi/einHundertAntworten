package com.project.einHundertAntworten.User;

import com.project.einHundertAntworten.Misc.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Check if the username or email already exists

        if (userRepository.existsByUsername(user.getUsername())) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }

        if (Utility.pwMeetsRequirements(user.getPassword()) != Utility.statusOK) {
            return new ResponseEntity<>(Utility.pwMeetsRequirements(user.getPassword()), HttpStatus.BAD_REQUEST);
        }
        //encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));


        // set User role
        //user.setRole("USER");
        // Save the user to the database
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginRequest userLoginRequest) {

        System.out.println(userLoginRequest.getEmailOrUsername());
        System.out.println(userLoginRequest.getPassword());

        if (userRepository.existsByUsername(userLoginRequest.getEmailOrUsername())) {
            User userDB = userRepository.findByUsername(userLoginRequest.getEmailOrUsername());
            System.out.println(passwordEncoder.matches(userLoginRequest.getPassword(), userDB.getPassword()));

            if (passwordEncoder.matches(userLoginRequest.getPassword(), userDB.getPassword())) {
                return new ResponseEntity<>("User logged in successfully", HttpStatus.OK);
            }

        } else if (userRepository.existsByEmail(userLoginRequest.getEmailOrUsername())) {
            User userDB = userRepository.findByEmail(userLoginRequest.getEmailOrUsername());
            if (passwordEncoder.matches(userLoginRequest.getPassword(), userDB.getPassword())) {
                return new ResponseEntity<>("User logged in successfully", HttpStatus.OK);
            }

        } else {
            return new ResponseEntity<>("Username or Password wrong", HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>("Username or Password wrong", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getall")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(User user, String passwordNew) {

        if (user.getPassword().equals(passwordNew)) {
            return new ResponseEntity<String>("Passwords are equal.", HttpStatus.BAD_REQUEST);
        }
        if (!Utility.pwMeetsRequirements(passwordNew).equals(Utility.statusOK)) {
            return new ResponseEntity<String>(Utility.pwMeetsRequirements(passwordNew), HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordNew);
        return new ResponseEntity<String>("Password changed.", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){

        if (!userRepository.existsById(id)){
            return new ResponseEntity<String>("User not found.", HttpStatus.BAD_REQUEST);
        }
        /*
        if (userRepository.findById(id).get().getRole().equals("ADMIN")){
            return new ResponseEntity<String>("Admins cannot be deleted.", HttpStatus.BAD_REQUEST);
        }
        */

        userRepository.deleteById(id);

        return new ResponseEntity<String>("User deleted.", HttpStatus.OK);
    }
}

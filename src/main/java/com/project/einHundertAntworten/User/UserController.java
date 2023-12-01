package com.project.einHundertAntworten.User;

import com.project.einHundertAntworten.Misc.CustomUserDetailsService;
import com.project.einHundertAntworten.Misc.Utility;
import com.project.einHundertAntworten.service.TokenService;
import org.bson.codecs.DateCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    private final TokenService tokenService;
    private final CustomUserDetailsService customUserDetailsService;
    public UserController(TokenService tokenService, CustomUserDetailsService customUserDetailsService) {
        this.tokenService = tokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        // Check if the username or email already exists

        if (userRepository.existsByUsername(user.getUsername())) {
            String message = "Username already exists";
            System.out.println(message);
            return new ResponseEntity<>(Collections.singletonMap("message", message), HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            String message = "Email already exists";
            System.out.println(message);
            return new ResponseEntity<>(Collections.singletonMap("message", message), HttpStatus.BAD_REQUEST);
        }

        if (Utility.pwMeetsRequirements(user.getPassword()) != Utility.statusOK) {
            System.out.println(Utility.pwMeetsRequirements(user.getPassword()));
            return new ResponseEntity<>(Collections.singletonMap("message", Utility.pwMeetsRequirements(user.getPassword())), HttpStatus.BAD_REQUEST);
        }
        if (Utility.usernameMeetsRequirements(user.getUsername()) != Utility.statusOK) {
            System.out.println(Utility.usernameMeetsRequirements(user.getUsername()));
            return new ResponseEntity<>(Collections.singletonMap("message", Utility.usernameMeetsRequirements(user.getUsername())), HttpStatus.BAD_REQUEST);
        }

        //encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // set User role
        user.setDefaultRole();
        // Save the user to the database
        userRepository.save(user);
        System.out.println("User registered successfully");
        // Create User Profile
        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getUsername(), "", user.getEmail(), 0, LocalDateTime.now().toString(), 0);
        // Save the user profile to the database
        userProfileRepository.save(userProfile);
        userProfile = userProfileRepository.findByUserID(user.getId());
        user.setProfileID(userProfile.getId());
        userRepository.save(user);
        if (userProfileRepository.existsByUserID(user.getId())) {
            System.out.println("User Profile created successfully");
        } else{
            System.out.println("User Profile not created");
        }
        return new ResponseEntity<>(Collections.singletonMap("token", loadUserAndToken(user.getUsername(), false)), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        String token;
        System.out.println(userLoginRequest.getEmailOrUsername());
        System.out.println(userLoginRequest.getPassword());

        User userDB;
        if (userRepository.existsByUsername(userLoginRequest.getEmailOrUsername())) {
            userDB = userRepository.findByUsername(userLoginRequest.getEmailOrUsername());
            System.out.println(passwordEncoder.matches(userLoginRequest.getPassword(), userDB.getPassword()));
            if (passwordEncoder.matches(userLoginRequest.getPassword(), userDB.getPassword())) {
                token = loadUserAndToken(userLoginRequest.getEmailOrUsername(), false);
            }else {
                return new ResponseEntity<>(Collections.singletonMap("message","Username or Password wrong"), HttpStatus.BAD_REQUEST);
            }
        } else if (userRepository.existsByEmail(userLoginRequest.getEmailOrUsername())) {
            userDB = userRepository.findByEmail(userLoginRequest.getEmailOrUsername());
            if (passwordEncoder.matches(userLoginRequest.getPassword(), userDB.getPassword())) {
                token = loadUserAndToken(userLoginRequest.getEmailOrUsername(), true);
            } else {
                return new ResponseEntity<>(Collections.singletonMap("message", "Username or Password wrong"), HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>(Collections.singletonMap("message", "Username or Password wrong"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Collections.singletonMap("token", token), HttpStatus.OK);
    }
        @GetMapping("/userID")
        public ResponseEntity<Map<String, String>> getUserID (@RequestParam String username){
            System.out.println(username);
            User userDB = userRepository.findByUsername(username);
            if (userDB == null) {
                userDB = userRepository.findByEmail(username);
                if (userDB == null) {
                    return new ResponseEntity<>(Collections.singletonMap("message", "User not found"), HttpStatus.BAD_REQUEST);
                }
            }
            String userID = userDB.getId();
            return new ResponseEntity<>(Collections.singletonMap("userID", userID), HttpStatus.CREATED);

        }
        @GetMapping("/getUser/{userID}")
        public ResponseEntity<UserProfile> getUserProfile(@PathVariable String userID) {
            Optional<User> userOptional = userRepository.findById(userID);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserProfile userProfile = userProfileRepository.findByUserID(user.getId());
                // Return a single user in a list
                return new ResponseEntity<>(userProfile, HttpStatus.OK);
            } else {
                // User not found
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }

    @GetMapping("/getall")
        public List<User> getAllUsers (@RequestHeader("Authorization") String authorizationHeader){
            // Now, you can use the authorizationHeader variable to access the value of the "Authorization" header.
            // For example, you can print it:
            System.out.println("Authorization Header: " + authorizationHeader);
            // Your existing logic to fetch and return users
            return userRepository.findAll();
        }

        @PutMapping("/changePassword")
        public ResponseEntity<String> changePassword (@RequestBody PasswordChangeRequest loginRequest) {
            User userDB;
            System.out.println(loginRequest.getPasswordNew());
            System.out.println(loginRequest.getEmailOrUsername());
            if (userRepository.existsByUsername(loginRequest.getEmailOrUsername())) {
                 userDB = userRepository.findByUsername(loginRequest.getEmailOrUsername());

            } else if (userRepository.existsByEmail(loginRequest.getEmailOrUsername())) {
                 userDB = userRepository.findByEmail(loginRequest.getEmailOrUsername());

            }else {
                System.out.println("something");
                return new ResponseEntity<>("Username or Password wrong", HttpStatus.BAD_REQUEST);
            }
            if (passwordEncoder.matches(loginRequest.getPasswordOld(), userDB.getPassword())) {
                if (Utility.pwMeetsRequirements(loginRequest.getPasswordNew()) != Utility.statusOK) {
                    return new ResponseEntity<>(Utility.pwMeetsRequirements(loginRequest.getPasswordNew()), HttpStatus.OK);
                }
                userDB.setPassword(passwordEncoder.encode(loginRequest.getPasswordNew()));
                userRepository.save(userDB);
                return new ResponseEntity<>("Password changed", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Username or Password wrong", HttpStatus.BAD_REQUEST);
            }

        }

        @PutMapping("/setAdmin/{id}")
        public ResponseEntity<String> setAdmin (@PathVariable String id){
        User requestingUser = checkForAdmin();
        if (requestingUser == null){
            return new ResponseEntity<String>("User not authorized.", HttpStatus.BAD_REQUEST);
        }
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<String>("User not found.", HttpStatus.BAD_REQUEST);
        }
        User user = userOptional.get();
        if (user.getRole().equals("ADMIN")){
            return new ResponseEntity<String>("User is already admin.", HttpStatus.BAD_REQUEST);
        }
        user.setRole("ADMIN");
        userRepository.save(user);
        return new ResponseEntity<String>("User set to admin.", HttpStatus.OK);
        }

        @DeleteMapping("/delete/{id}")
        public ResponseEntity<String> deleteUser (@PathVariable String id){
            User requestingUser = checkForAdmin();
            if (requestingUser == null){
                return new ResponseEntity<String>("User not authorized.", HttpStatus.BAD_REQUEST);
            }
            if (!userRepository.existsById(id)) {
                return new ResponseEntity<String>("User not found.", HttpStatus.BAD_REQUEST);
            }
            if (userRepository.findById(id).get().getRole().equals("ADMIN")){
            return new ResponseEntity<String>("Admins cannot be deleted.", HttpStatus.BAD_REQUEST);
            }
            userProfileRepository.deleteById(userRepository.findById(id).get().getProfileID());
            userRepository.deleteById(id);
            return new ResponseEntity<String>("User deleted.", HttpStatus.OK);
        }

        @DeleteMapping("/deleteAll")
        public ResponseEntity<String> deleteAllUsers (){
            User requestingUser = checkForAdmin();
            if (requestingUser == null){
                return new ResponseEntity<String>("User not authorized.", HttpStatus.BAD_REQUEST);
            }
            //Delete all except admins
            for (User user : userRepository.findAll()) {
                if (!user.getRole().equals("ADMIN")){
                    userProfileRepository.deleteById(user.getProfileID());
                    userRepository.deleteById(user.getId());
                }
            }
            return new ResponseEntity<String>("All users deleted.", HttpStatus.OK);
        }

    private User checkForAdmin() {
        User requestingUser;
        try {requestingUser = userRepository.findByUsername(customUserDetailsService.getLastAuthentication().getName());
        }catch (Exception e){
            return null;
        }
        return requestingUser;
    }

    @PostMapping("/logout")
        public ResponseEntity<String> logoutUser (@RequestHeader("Authorization") String authorizationHeader){
            // Now, you can use the authorizationHeader variable to access the value of the "Authorization" header.
            // For example, you can print it:
            System.out.println("Authorization Header: " + authorizationHeader);
            // Your existing logic to fetch and return users
            return new ResponseEntity<String>("User logged out.", HttpStatus.OK);
        }



    private String loadUserAndToken(String principal, Boolean isEmail){
    if(isEmail) {
        principal = principal.concat("@");
    }
    customUserDetailsService.loadUserByUsername(principal);
    String token = tokenService.generateToken(customUserDetailsService);
    return token;
    }

}

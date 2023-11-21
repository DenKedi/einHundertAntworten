package com.project.einHundertAntworten.User;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;

    // Getters and setters

    public User(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        // Use BCryptPasswordEncoder to hash the password before storing it

        this.password = password;


    }


    //public Role getRole() {}
}

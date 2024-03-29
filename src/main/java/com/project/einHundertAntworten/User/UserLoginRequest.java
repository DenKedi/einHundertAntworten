package com.project.einHundertAntworten.User;

public class UserLoginRequest {
    private String emailOrUsername;
    private String password;

    public UserLoginRequest(String emailOrUsername, String password){
        this.emailOrUsername = emailOrUsername;
        this.password = password;
    }

    public String getEmailOrUsername() {
        return emailOrUsername;
    }

    public void setEmailOrUsername(String emailOrUsername) {
        this.emailOrUsername = emailOrUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

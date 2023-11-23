package com.project.einHundertAntworten.User;

public class PasswordChangeRequest {


    private String emailOrUsername;
    private String passwordOld;
    private final String passwordNew;

        public PasswordChangeRequest(String emailOrUsername, String passwordOld, String passwordNew){
            this.emailOrUsername = emailOrUsername;
            this.passwordOld = passwordOld;
            this.passwordNew = passwordNew;
        }

        public String getEmailOrUsername() {
            return emailOrUsername;
        }

        public void setEmailOrUsername(String emailOrUsername) {
            this.emailOrUsername = emailOrUsername;
        }

        public String getPasswordOld() {
        return passwordOld;
        }

        public void setPasswordOld(String passwordOld) {
        this.passwordOld = passwordOld;
        }

        public String getPasswordNew() {
        return passwordNew;
        }
}

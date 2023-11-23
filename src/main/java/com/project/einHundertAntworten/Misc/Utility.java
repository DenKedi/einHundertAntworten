package com.project.einHundertAntworten.Misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    static final String specialCharacters = "!\"#$%&'()*+,-./:;<=>?@[\\\\]^_`~ ";
    static final String specialCharactersUN = "!\"#$%&'()*+,./:;<=>?@[\\\\]^`~ ";
    static String regex = ".*[" + Pattern.quote(specialCharacters) + "].*";
    static Pattern pattern = Pattern.compile(regex);
    public static final String statusOK = "OK";
    public static String pwMeetsRequirements(String pw){
        if (pw.length()<=6){
            return("Password requires 6 or more characters.");
        }
        Matcher matcher = pattern.matcher(pw);
        if (!matcher.matches()){
            return("Special characters required in Password");
        }
        if (!pw.matches(".*\\d.*")){
            return("Numbers required in Password");
        }
        return(statusOK);
    }
    public static String usernameMeetsRequirements(String username){
        if (username.length()<=3){
            return("Username requires 3 or more characters.");
        }
        Matcher matcher = pattern.matcher(username);
        if (matcher.matches()){
            return("Special characters not allowed in Username (except for '_' and '-')");
        }
        if (username.equals("Username already exists") || username.equals("Email already exists")){
            return("Forbidden Username");
        }
        return(statusOK);
    }

    public static void main(String[] args) {
        System.out.println(pwMeetsRequirements("123456_"));
        System.out.println(pwMeetsRequirements("123456#"));
        System.out.println(pwMeetsRequirements("hallo-{1-"));
    }
}

package com.project.einHundertAntworten.Misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    static final String specialCharacters = "!\"#$%&'()*+,-./:;<=>?@[\\\\]^_`~";
    static String regex = ".*[" + Pattern.quote(specialCharacters) + "].*";
    static Pattern pattern = Pattern.compile(regex);
    public static final String statusOK = "OK";
    public static String pwMeetsRequirements(String pw){
        if (pw.length()<=6){
            return("6 or more characters required.");
        }
        Matcher matcher = pattern.matcher(pw);
        if (!matcher.matches()){
            return("Special characters required");
        }
        if (!pw.matches(".*\\d.*")){
            return("Numbers required");
        }
        return("OK");
    }

    public static void main(String[] args) {
        System.out.println(pwMeetsRequirements("123456_"));
        System.out.println(pwMeetsRequirements("123456#"));
        System.out.println(pwMeetsRequirements("hallo-{1-"));
    }
}

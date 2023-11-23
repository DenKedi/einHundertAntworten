package com.project.einHundertAntworten;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class LandingController {

    @GetMapping("/home")
    public String getLanding() {
        return "index";
    }

    @GetMapping("/overview")
    public String getQuizOverview() {
        return "quizOverview";
    }

    @GetMapping("/quizpanel")
    public String getQuizpanel() {
        return "quizPanel";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    @GetMapping("/profile")
    public String getProfile() {
        return "profile";
    }

    @GetMapping("/error")
    public String getError() {
        return "error";
    }

    @GetMapping("/quiz")
    public String getQuiz() {
        return "quiz";
    }

}

package com.project.einHundertAntworten;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class LandingController {

    @GetMapping("/home")
    public String getLanding() {
        return "index";
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
        return "errorPage";
    }

}

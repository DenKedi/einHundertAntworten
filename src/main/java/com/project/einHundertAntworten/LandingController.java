package com.project.einHundertAntworten;

import com.project.einHundertAntworten.User.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class LandingController {

    @GetMapping("/")
    public String landing(Model model) {
        return "index";
    }

    @GetMapping("/quizpanel")
    public String getQuizpanel() {
        return "quizPanel";
    }
}

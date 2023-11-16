package com.project.einHundertAntworten;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class LandingController {




    @GetMapping("/")
    public String landing(Model model) {
        Person person = new Person("Peter", "33");
        model.addAttribute("Peter", 42);
        return "index";
    }
}

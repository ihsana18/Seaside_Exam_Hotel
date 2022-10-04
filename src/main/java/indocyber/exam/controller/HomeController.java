package indocyber.exam.controller;

import org.dom4j.rule.Mode;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {

    @GetMapping("/")
    public String home(Model model, Authentication authentication){



        model.addAttribute("breadCrumbs","Landing Page");
        return "home/home-page";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model){
        model.addAttribute("breadCrumbs","Not Authorized");
        return "home/access-denied";
    }

}

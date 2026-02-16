package com.example.todo.security;

import com.example.todo.model.entity.User;
import com.example.todo.service.EmailService;
import com.example.todo.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {
    private final UserService userService;
    private final EmailService emailService;

    public SecurityController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }


//    @GetMapping("/")
//    public String home(Model model,
//                       @AuthenticationPrincipal UserDetails userDetails) {
//        if(userDetails != null){
//            User user = userService.findByEmail(userDetails.getUsername());
//            model.addAttribute("user",user);
//        }
//        return "home";
//    }

//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }

    @GetMapping("/registration")
    public String register(){
        return"registration";
    }
    @PostMapping("/registration")
    public String registration(@Valid @RequestParam(value = "username", required = true) String username,
                               @Valid @RequestParam(value = "password", required = true) String password,
                               @Valid @RequestParam(value = "email", required = true) String email) {
        userService.save(username, password, email);
        try {
            emailService.sendWelcomeEmail(username);
        } catch (Exception e) {
            System.err.println("Не удалось отправить письмо: " + e.getMessage());
            // Не прерываем регистрацию из-за ошибки почты
        }
        return "redirect:/login";

    }
}


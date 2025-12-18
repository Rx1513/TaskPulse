package web.auth;

import database.UserRepository;
import database.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import users.User;
import users.UserValidator;

@Controller
public class AuthController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/auth/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        try {
            UserValidator.validateName(username);
            UserValidator.validateEmail(email);

            User user = new User(null,username,email,password);

            userRepository.addUser(user);
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
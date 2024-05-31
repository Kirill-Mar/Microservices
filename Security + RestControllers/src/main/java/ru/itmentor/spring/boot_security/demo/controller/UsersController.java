package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/user")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/lk")
    public String getUserPage(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("user", currentUser);

        boolean isVIP = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("VIP"));

        if (isVIP) {
            model.addAttribute("flashMessage", "Вы успешно авторизовались как VIP пользователь!");
        } else {
            model.addAttribute("flashMessage", "Вы успешно авторизовались как обычный пользователь!");
        }
        return "userPage";
    }
}
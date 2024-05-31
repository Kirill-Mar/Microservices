package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {"", "/all"})
    public String allUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "allUsersPage";
    }

    @GetMapping("/users/new")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        return "addUser";
    }

    @PostMapping("/add")
    public String postAddUser(@Valid User user,
                              @RequestParam(required = false) String roleAdmin,
                              @RequestParam(required = false) String roleVIP,
                              RedirectAttributes attributes) {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("USER"));
        if (roleAdmin != null) {
            roles.add(new Role("ADMIN"));
        }
        if (roleVIP != null) {
            roles.add(new Role("VIP"));
        }
        user.setRoles(roles);
        userService.createOrUpdateUser(user);
        attributes.addFlashAttribute("flashMessage",
                "Пользователь " + user.getFirstName() + " успешно добавлен!");
        return "redirect:/admin/all";
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(Model model, @PathVariable("id") long id) {
        User user = userService.readUser(id);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable("id") long id,
                             @Valid User user,
                             RedirectAttributes attributes,
                             @RequestParam(value = "roleAdmin", required = false, defaultValue = "false") boolean roleAdmin,
                             @RequestParam(value = "roleVIP", required = false, defaultValue = "false") boolean roleVIP) {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("USER"));
        if (roleAdmin) {
            roles.add(new Role("ADMIN"));
        }
        if (roleVIP) {
            roles.add(new Role("VIP"));
        }
        user.setRoles(roles);

        user.setId(id);
        userService.createOrUpdateUser(user);
        attributes.addFlashAttribute("flashMessage",
                "Пользователь " + user.getFirstName() + " успешно обновлен!");
        return "redirect:/admin/all";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, RedirectAttributes attributes) {
        User user = userService.deleteUser(id);
        if (user != null) {
            attributes.addFlashAttribute("flashMessage", "Пользователь успешно удален!");
        } else {
            attributes.addFlashAttribute("flashMessage", "Пользователь не найден!");
        }
        return "redirect:/admin/all";
    }
    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable("id") long id, Model model) {
        User user = userService.readUser(id);
        model.addAttribute("user", user);
        return "userPage";
    }
    @GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "allUsersPage";
    }
}

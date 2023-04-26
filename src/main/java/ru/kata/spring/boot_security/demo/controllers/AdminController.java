package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserValidator userValidator;
    private final UserService userService;

    public AdminController(RoleService roleService, UserValidator userValidator, UserService userService) {
        this.roleService = roleService;
        this.userValidator = userValidator;
        this.userService = userService;
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/new";
    }

    @PostMapping()
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin/new";
        }
        userService.addUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping()
    public String showAdminPage() {
        return "admin/admin";
    }

    @GetMapping("/users")
    public String showAllUsers(ModelMap model) {
        model.addAttribute("users", userService.allUsers());
        return "admin/users";
    }

    @GetMapping("/{id}/edit")
    public String editUser(Model model, @PathVariable("id") long id) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/edit";
    }

    @PatchMapping("/edit")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "admin/edit";
        }
        userService.updateUser(user);
        return "redirect:/admin/users";
    }


    @DeleteMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}

package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping()
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "redirect:/admin?error=name";
        }
        userService.addUser(user);
        return "redirect:/admin";
    }

    @GetMapping()
    public String showAdminPage(@RequestParam(required = false) String error, ModelMap usersModel, Model adminModel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User admin = (User) authentication.getPrincipal();
        usersModel.addAttribute("users", userService.allUsers());
        adminModel.addAttribute("admin", admin);
        adminModel.addAttribute("newUser", new User());
        adminModel.addAttribute("roles", roleService.getAllRoles());
        if (error != null)
            adminModel.addAttribute("error", error);
        return "admin/admin";
    }

    @PatchMapping("/edit")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "redirect:/admin?error=name";
        }
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}

package ru.kata.spring.boot_security.demo.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.BaseDTO;
import ru.kata.spring.boot_security.demo.dto.ErrorDTO;
import ru.kata.spring.boot_security.demo.dto.RoleDTO;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.exceptions.ConstraintException;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.exceptions.UserNotCreatedException;
import ru.kata.spring.boot_security.demo.exceptions.UserNotEditedException;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class BaseRestController {

    private final UserValidator userValidator;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final RoleService roleService;

    public BaseRestController(UserValidator userValidator, ModelMapper modelMapper, UserService userService, RoleService roleService) {
        this.userValidator = userValidator;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.roleService = roleService;
    }
    @GetMapping("/users")
    public ResponseEntity<Map<String, List<BaseDTO>>> getUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO admin = convertToUserDTO((User) authentication.getPrincipal());
        List<BaseDTO> users = userService.allUsers().stream().map(this::convertToUserDTO).collect(Collectors.toList());
        List<BaseDTO> roles = roleService.getAllRoles().stream().map(this::convertToRoleDTO).collect(Collectors.toList());
        List<BaseDTO> adminDTO = new ArrayList<>();
        adminDTO.add(admin);
        Map<String, List<BaseDTO>> response = new HashMap<>();
        response.put("users", users);
        response.put("roles", roles);
        response.put("admin", adminDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<BaseDTO> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(convertToUserDTO(user));
    }

    @PostMapping
    public ResponseEntity<BaseDTO> addUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        User newUser = convertToUser(userDTO);
        userValidator.validate(newUser, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getDefaultMessage());
            }

            throw new UserNotCreatedException(errorMsg.toString());
        }

        User createdUser = userService.addUser(newUser);
        return ResponseEntity.ok(convertToUserDTO(createdUser));
    }

    @PutMapping
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        User userToEdit = convertToUser(userDTO);
        userValidator.validate(userToEdit, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getDefaultMessage());
            }

            throw new UserNotEditedException(errorMsg.toString());
        }

        userService.updateUser(userToEdit);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<BaseDTO> handleException (ConstraintException exception) {
        ErrorDTO errorDTO = new ErrorDTO(exception.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.I_AM_A_TEAPOT);
    }


    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserDTO convertToUserDTO(User user) {
        user.setPassword(null);
        return modelMapper.map(user, UserDTO.class);
    }

    private Role convertToRole(RoleDTO roleDTO) {
        return modelMapper.map(roleDTO, Role.class);
    }

    private RoleDTO convertToRoleDTO(Role role) {
        return modelMapper.map(role, RoleDTO.class);
    }
}

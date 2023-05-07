package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    User addUser(User user);
    void updateUser(User user);
    User getUserById(Long id);
    void deleteUser(Long id);
    List<User> allUsers();
}

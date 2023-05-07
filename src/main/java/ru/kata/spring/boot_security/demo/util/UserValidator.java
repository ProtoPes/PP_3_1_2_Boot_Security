package ru.kata.spring.boot_security.demo.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Optional;

@Component
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        long id = user.getId();
        Optional<User> userToFind = userRepository.findByUsername(user.getUsername());
        if (userToFind.isPresent() && (id == 0 || id != userToFind.get().getId())) {
            errors.rejectValue("username", "1", String.format("Username %s already exists!", user.getUsername()));
        }
    }
}

package ru.kata.spring.boot_security.demo.exceptions;

public class UserNotCreatedException extends ConstraintException{

    public UserNotCreatedException(String msg) {
        super(msg);
    }
}

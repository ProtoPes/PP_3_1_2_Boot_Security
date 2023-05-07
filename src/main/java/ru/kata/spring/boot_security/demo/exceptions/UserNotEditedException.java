package ru.kata.spring.boot_security.demo.exceptions;

public class UserNotEditedException extends ConstraintException {
    public UserNotEditedException(String msg) {
        super(msg);
    }
}

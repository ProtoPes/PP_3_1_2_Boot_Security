package ru.kata.spring.boot_security.demo.exceptions;

public class ConstraintException extends RuntimeException {
    public ConstraintException(String msg) {
        super(msg);
    }
}

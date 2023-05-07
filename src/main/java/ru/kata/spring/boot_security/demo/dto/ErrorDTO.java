package ru.kata.spring.boot_security.demo.dto;

public class ErrorDTO implements BaseDTO {
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorDTO(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

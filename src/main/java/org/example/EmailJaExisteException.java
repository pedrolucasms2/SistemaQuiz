package org.example;

public class EmailJaExisteException extends Exception {
    public EmailJaExisteException(String message) {
        super(message);
    }

    public EmailJaExisteException(String message, Throwable cause) {
        super(message, cause);
    }
}
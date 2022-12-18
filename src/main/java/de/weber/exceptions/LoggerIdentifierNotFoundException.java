package de.weber.exceptions;

public class LoggerIdentifierNotFoundException extends Exception {
    public LoggerIdentifierNotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
    public LoggerIdentifierNotFoundException() {
        super("Weren't able to find a logger with the specified identifier... Logger is getting created for you now.", null);
    }
}

package com.belema_fintech.exception;

public class IsoParsingException extends RuntimeException {
    public IsoParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IsoParsingException(String message) {
        super(message);
    }
}
package com.fullcycle.admin.catalogo.domain.exceptions;

public class NoStackTraceException extends RuntimeException {

    public NoStackTraceException(String message) {
        this(message, null);
    }

    public NoStackTraceException(final String message, final Throwable cause) {
        super(message, cause, true, false);
    }
}

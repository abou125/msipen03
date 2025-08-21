package com.cl.msipen03.exceptions;

public class InternalMSIPEN03Exception extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InternalMSIPEN03Exception(String message) {
        super(message);
    }

    public InternalMSIPEN03Exception(String message, Throwable cause) {
        super(message, cause);
    }

}

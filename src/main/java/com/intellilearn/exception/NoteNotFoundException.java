package com.intellilearn.exception;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(String message) {
        super(message);
    }
}
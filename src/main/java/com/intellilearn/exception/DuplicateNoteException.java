package com.intellilearn.exception;

public class DuplicateNoteException extends RuntimeException {

    public DuplicateNoteException(String message) {
        super(message);
    }
}
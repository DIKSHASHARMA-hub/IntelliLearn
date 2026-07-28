package com.intellilearn.exception;

/** Thrown when the AI quiz-generation step fails (bad/unparseable response, upstream error, etc). */
public class QuizGenerationException extends RuntimeException {

    public QuizGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

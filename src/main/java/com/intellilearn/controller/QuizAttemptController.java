package com.intellilearn.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.intellilearn.dto.request.QuizSubmissionRequest;
import com.intellilearn.dto.response.QuizAttemptResponse;
import com.intellilearn.service.interfaces.QuizAttemptService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/quiz-attempt")
@Validated
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizAttemptResponse> submitQuiz(
            @Valid @RequestBody QuizSubmissionRequest request) {

        QuizAttemptResponse response =
                quizAttemptService.submitQuiz(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
package com.intellilearn.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.intellilearn.dto.response.QuizResponseDTO;
import com.intellilearn.service.interfaces.QuizService;


@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Generate a new quiz from the notes of a subject.
     */
    @PostMapping("/generate/{subjectId}")
    public ResponseEntity<QuizResponseDTO> generateQuiz(
            @PathVariable Long subjectId) {

        QuizResponseDTO quiz = quizService.generateQuiz(subjectId);

        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }

    /**
     * Get an already generated quiz.
     */
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseDTO> getQuiz(
            @PathVariable Long quizId) {

        QuizResponseDTO quiz = quizService.getQuiz(quizId);

        return ResponseEntity.ok(quiz);
    }

}

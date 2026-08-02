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

    
    @PostMapping("/generate/note/{noteId}")
    public ResponseEntity<QuizResponseDTO> generateQuizForNote(
            @PathVariable Long noteId) {

        QuizResponseDTO quiz = quizService.generateQuizForNote(noteId);

        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }

    
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseDTO> getQuiz(
            @PathVariable Long quizId) {

        QuizResponseDTO quiz = quizService.getQuiz(quizId);

        return ResponseEntity.ok(quiz);
    }

   
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<QuizResponseDTO> getQuizBySubject(
            @PathVariable Long subjectId) {

        QuizResponseDTO quiz = quizService.getQuizBySubject(subjectId);

        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<QuizResponseDTO> getQuizByNote(
            @PathVariable Long noteId) {

        QuizResponseDTO quiz = quizService.getQuizByNote(noteId);

        return ResponseEntity.ok(quiz);
    }

}

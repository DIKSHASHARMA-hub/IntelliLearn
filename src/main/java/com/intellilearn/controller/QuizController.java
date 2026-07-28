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
     * Generate a new quiz from a single note (PDF) — each note gets its
     * own quiz, rather than combining every note in a subject into one.
     */
    @PostMapping("/generate/note/{noteId}")
    public ResponseEntity<QuizResponseDTO> generateQuizForNote(
            @PathVariable Long noteId) {

        QuizResponseDTO quiz = quizService.generateQuizForNote(noteId);

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

    /**
     * Find the (most recently generated) quiz for a subject — kept for
     * completeness; not currently used by the frontend since generation
     * is now per-note.
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<QuizResponseDTO> getQuizBySubject(
            @PathVariable Long subjectId) {

        QuizResponseDTO quiz = quizService.getQuizBySubject(subjectId);

        return ResponseEntity.ok(quiz);
    }

    /**
     * Find the (most recently generated) quiz for a specific note.
     */
    @GetMapping("/note/{noteId}")
    public ResponseEntity<QuizResponseDTO> getQuizByNote(
            @PathVariable Long noteId) {

        QuizResponseDTO quiz = quizService.getQuizByNote(noteId);

        return ResponseEntity.ok(quiz);
    }

}

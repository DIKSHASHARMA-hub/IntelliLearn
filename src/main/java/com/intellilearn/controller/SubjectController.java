package com.intellilearn.controller;

import com.intellilearn.dto.request.SubjectRequest;
import com.intellilearn.dto.response.SubjectResponse;
import com.intellilearn.service.interfaces.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(
            @Valid @RequestBody SubjectRequest request) {

        SubjectResponse response = subjectService.createSubject(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request) {

        SubjectResponse response = subjectService.updateSubject(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {

        subjectService.deleteSubject(id);

        return ResponseEntity.ok("Subject deleted successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable Long id) {

        SubjectResponse response = subjectService.getSubjectById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects() {

        List<SubjectResponse> subjects = subjectService.getAllSubjects();

        return ResponseEntity.ok(subjects);
    }
}
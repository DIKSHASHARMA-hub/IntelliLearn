package com.intellilearn.controller;

import com.intellilearn.dto.response.NotesResponse;
import com.intellilearn.service.interfaces.NotesService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/notes")
public class NotesController {

    private final NotesService notesService;

    public NotesController(NotesService notesService) {
        this.notesService = notesService;
    }

    @PostMapping(value = "/upload/{subjectId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NotesResponse> uploadNotes(
            @PathVariable Long subjectId,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file) {

        NotesResponse response =
                notesService.uploadNotes(subjectId, title, file);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<NotesResponse> getNotesBySubject(
            @PathVariable Long subjectId) {

        return ResponseEntity.ok(
                notesService.getNotesBySubject(subjectId));
    }

    @GetMapping("/download/{subjectId}")
    public ResponseEntity<Resource> downloadNotes(
            @PathVariable Long subjectId) {

        Resource resource =
                notesService.downloadNotes(subjectId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/subject/{subjectId}")
    public ResponseEntity<String> deleteNotes(
            @PathVariable Long subjectId) {

        notesService.deleteNotes(subjectId);

        return ResponseEntity.ok("Notes deleted successfully.");
    }
}
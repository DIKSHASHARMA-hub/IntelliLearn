package com.intellilearn.controller;

import java.util.List;

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

    /**
     * A subject can have multiple notes uploaded to it, so this returns all
     * of them rather than a single note.
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<NotesResponse>> getNotesBySubject(
            @PathVariable Long subjectId) {

        return ResponseEntity.ok(
                notesService.getNotesBySubject(subjectId));
    }

    @GetMapping("/{noteId}/download")
    public ResponseEntity<Resource> downloadNotes(
            @PathVariable Long noteId) {

        Resource resource =
                notesService.downloadNotes(noteId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<String> deleteNotes(
            @PathVariable Long noteId) {

        notesService.deleteNotes(noteId);

        return ResponseEntity.ok("Notes deleted successfully.");
    }
}

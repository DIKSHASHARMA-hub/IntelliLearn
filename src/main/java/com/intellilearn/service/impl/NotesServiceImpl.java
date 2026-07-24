package com.intellilearn.service.impl;

import com.intellilearn.dto.request.NotesRequest;
import com.intellilearn.dto.response.NotesResponse;
import com.intellilearn.entity.Notes;
import com.intellilearn.entity.Subject;
import com.intellilearn.exception.DuplicateNoteException;
import com.intellilearn.exception.NoteNotFoundException;
import com.intellilearn.exception.SubjectNotFoundException;
import com.intellilearn.repository.NotesRepository;
import com.intellilearn.repository.SubjectRepository;
import com.intellilearn.service.interfaces.NotesService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

@Service
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;
    private final SubjectRepository subjectRepository;

    private final Path uploadPath = Paths.get("uploads");

    public NotesServiceImpl(NotesRepository notesRepository,
                            SubjectRepository subjectRepository) {

        this.notesRepository = notesRepository;
        this.subjectRepository = subjectRepository;
    }
    @Override
    public NotesResponse uploadNotes(Long subjectId,
                                     String title,
                                     MultipartFile file)  {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Subject not found."));

        if (notesRepository.existsBySubjectId(subjectId)) {
            throw new DuplicateNoteException(
                    "Notes already exist for this subject.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("PDF file is required.");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are allowed.");
        }

        try {

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            Notes notes = new Notes();

            notes.setTitle(title);
            notes.setFileName(fileName);
            notes.setFilePath(filePath.toString());
            notes.setUploadDate(LocalDate.now());
            notes.setSubject(subject);

            Notes savedNotes = notesRepository.save(notes);

            return new NotesResponse(
                    savedNotes.getId(),
                    savedNotes.getTitle(),
                    savedNotes.getFileName(),
                    savedNotes.getUploadDate(),
                    subject.getId(),
                    subject.getName());

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload notes.");
        }
    }
    @Override
    public NotesResponse getNotesBySubject(Long subjectId) {

        Notes notes = notesRepository.findBySubjectId(subjectId)
                .orElseThrow(() ->
                        new NoteNotFoundException("Notes not found for this subject."));

        return new NotesResponse(
                notes.getId(),
                notes.getTitle(),
                notes.getFileName(),
                notes.getUploadDate(),
                notes.getSubject().getId(),
                notes.getSubject().getName()
        );
    }
    @Override
    public Resource downloadNotes(Long subjectId) {

        Notes notes = notesRepository.findBySubjectId(subjectId)
                .orElseThrow(() ->
                        new NoteNotFoundException("Notes not found for this subject."));

        try {

            Path path = Paths.get(notes.getFilePath());

            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new NoteNotFoundException("PDF file not found.");

        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to download notes.");
        }
    }
    @Override
    public void deleteNotes(Long subjectId) {

        Notes notes = notesRepository.findBySubjectId(subjectId)
                .orElseThrow(() ->
                        new NoteNotFoundException("Notes not found for this subject."));

        try {

            Path path = Paths.get(notes.getFilePath());

            Files.deleteIfExists(path);

            notesRepository.delete(notes);

        } catch (IOException e) {
            throw new RuntimeException("Unable to delete notes.");
        }
    }

}
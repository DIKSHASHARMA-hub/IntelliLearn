package com.intellilearn.service.impl;

import com.intellilearn.dto.response.NotesResponse;
import com.intellilearn.entity.Notes;
import com.intellilearn.entity.Quiz;
import com.intellilearn.entity.Subject;
import com.intellilearn.exception.FileStorageException;
import com.intellilearn.exception.NoteNotFoundException;
import com.intellilearn.exception.SubjectNotFoundException;
import com.intellilearn.repository.NotesRepository;
import com.intellilearn.repository.QuizRepository;
import com.intellilearn.repository.SubjectRepository;
import com.intellilearn.service.interfaces.NotesService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;
    private final SubjectRepository subjectRepository;
    private final QuizRepository quizRepository;

    private final Path uploadPath = Paths.get("uploads");

    // PDF files begin with this 5-byte magic number ("%PDF-"). Checking the
    // actual bytes instead of trusting the client-supplied Content-Type header
    // prevents someone from uploading arbitrary files by simply relabeling them.
    private static final byte[] PDF_MAGIC_BYTES =
            new byte[] { 0x25, 0x50, 0x44, 0x46, 0x2D };

    public NotesServiceImpl(NotesRepository notesRepository,
                            SubjectRepository subjectRepository,
                            QuizRepository quizRepository) {
        this.quizRepository = quizRepository;

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

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("PDF file is required.");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are allowed.");
        }

        if (!looksLikeAPdf(file)) {
            throw new IllegalArgumentException(
                    "This file doesn't look like a valid PDF.");
        }

        try {

            Path uploadDir = uploadPath.toAbsolutePath().normalize();

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Never trust the client-supplied filename for the actual disk path —
            // it could contain "../" segments. Keep the original name only for
            // display (sanitized to its base name); the file on disk gets a
            // random, collision-proof name of our own choosing.
            String originalName = sanitizeFileName(file.getOriginalFilename());
            String storedFileName = UUID.randomUUID() + "_" + originalName;

            Path filePath = uploadDir.resolve(storedFileName).normalize();

            if (!filePath.startsWith(uploadDir)) {
                throw new FileStorageException("Invalid file path.");
            }

            Files.copy(file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            Notes notes = new Notes();

            notes.setTitle(title);
            notes.setFileName(originalName);
            notes.setFilePath(filePath.toString());
            notes.setUploadDate(LocalDate.now());
            notes.setSubject(subject);

            Notes savedNotes = notesRepository.save(notes);

            return toResponse(savedNotes);

        } catch (IOException e) {
            throw new FileStorageException("Failed to upload notes.");
        }
    }

    @Override
    public List<NotesResponse> getNotesBySubject(Long subjectId) {

        if (!subjectRepository.existsById(subjectId)) {
            throw new SubjectNotFoundException("Subject not found.");
        }

        return notesRepository.findBySubjectId(subjectId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Resource downloadNotes(Long noteId) {

        Notes notes = notesRepository.findById(noteId)
                .orElseThrow(() ->
                        new NoteNotFoundException("Notes not found."));

        try {

            Path path = Paths.get(notes.getFilePath());

            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new NoteNotFoundException("PDF file not found.");

        } catch (MalformedURLException e) {
            throw new FileStorageException("Unable to download notes.");
        }
    }

    @Override
    public void deleteNotes(Long noteId) {

        Notes notes = notesRepository.findById(noteId)
                .orElseThrow(() ->
                        new NoteNotFoundException("Notes not found."));

        deleteFileAndRow(notes);
    }

    @Override
    public void deleteAllNotesForSubject(Long subjectId) {

        List<Notes> notes = notesRepository.findBySubjectId(subjectId);

        for (Notes n : notes) {
            deleteFileAndRow(n);
        }
    }

    private void deleteFileAndRow(Notes notes) {
        try {
            // Preserve quiz/attempt history: if a quiz was generated from this
            // note, keep the quiz (and its questions/attempts) — just detach
            // its link to the note being removed, rather than deleting it.
            List<Quiz> linkedQuizzes = quizRepository.findByNotesId(notes.getId());
            for (Quiz quiz : linkedQuizzes) {
                quiz.setNotes(null);
            }
            quizRepository.saveAll(linkedQuizzes);

            Path path = Paths.get(notes.getFilePath());
            Files.deleteIfExists(path);
            notesRepository.delete(notes);
        } catch (IOException e) {
            throw new FileStorageException("Unable to delete notes.");
        }
    }

    private NotesResponse toResponse(Notes notes) {
        return new NotesResponse(
                notes.getId(),
                notes.getTitle(),
                notes.getFileName(),
                notes.getUploadDate(),
                notes.getSubject().getId(),
                notes.getSubject().getName());
    }

    /** Strips any path segments from a client-supplied filename, keeping just the base name. */
    private String sanitizeFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "notes.pdf";
        }
        String base = Paths.get(originalFilename).getFileName().toString();
        // Belt-and-braces: also strip any remaining separator characters the
        // client might have sent literally (e.g. encoded slashes).
        return base.replaceAll("[\\\\/]", "_");
    }

    private boolean looksLikeAPdf(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[PDF_MAGIC_BYTES.length];
            int read = is.read(header);
            if (read < PDF_MAGIC_BYTES.length) {
                return false;
            }
            for (int i = 0; i < PDF_MAGIC_BYTES.length; i++) {
                if (header[i] != PDF_MAGIC_BYTES[i]) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

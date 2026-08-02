package com.intellilearn.service.interfaces;

import java.util.List;

import com.intellilearn.dto.response.NotesResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface NotesService {

	NotesResponse uploadNotes(Long subjectId,
            String title,
            MultipartFile file);

    List<NotesResponse> getNotesBySubject(Long subjectId);

    Resource downloadNotes(Long noteId);

    void deleteNotes(Long noteId);

   
    void deleteAllNotesForSubject(Long subjectId);

}

package com.intellilearn.service.interfaces;

import com.intellilearn.dto.request.NotesRequest;
import com.intellilearn.dto.response.NotesResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface NotesService {

	NotesResponse uploadNotes(Long subjectId,
            String title,
            MultipartFile file);

    NotesResponse getNotesBySubject(Long subjectId);

    Resource downloadNotes(Long subjectId);

    void deleteNotes(Long subjectId);

}
package com.intellilearn.service.interfaces;

import com.intellilearn.dto.request.SubjectRequest;
import com.intellilearn.dto.response.SubjectResponse;

import java.util.List;

public interface SubjectService {

    SubjectResponse createSubject(SubjectRequest request);

    SubjectResponse updateSubject(Long id, SubjectRequest request);

    void deleteSubject(Long id);

    SubjectResponse getSubjectById(Long id);

    List<SubjectResponse> getAllSubjects();

}
package com.intellilearn.service.impl;

import com.intellilearn.dto.request.SubjectRequest;
import com.intellilearn.dto.response.SubjectResponse;
import com.intellilearn.entity.Subject;
import com.intellilearn.exception.DuplicateSubjectException;
import com.intellilearn.exception.SubjectNotFoundException;
import com.intellilearn.repository.SubjectRepository;
import com.intellilearn.service.interfaces.SubjectService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public SubjectResponse createSubject(SubjectRequest request) {

        if (subjectRepository.existsByName(request.getName())) {
            throw new DuplicateSubjectException("Subject already exists.");
        }

        Subject subject = new Subject();
        subject.setName(request.getName());
        subject.setDescription(request.getDescription());

        Subject savedSubject = subjectRepository.save(subject);

        return mapToResponse(savedSubject);
    }

    @Override
    public SubjectResponse updateSubject(Long id, SubjectRequest request) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Subject not found with id: " + id));

        if (!subject.getName().equalsIgnoreCase(request.getName())
                && subjectRepository.existsByName(request.getName())) {

            throw new DuplicateSubjectException("Subject already exists.");
        }

        subject.setName(request.getName());
        subject.setDescription(request.getDescription());

        Subject updatedSubject = subjectRepository.save(subject);

        return mapToResponse(updatedSubject);
    }

    @Override
    public void deleteSubject(Long id) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Subject not found with id: " + id));

        subjectRepository.delete(subject);
    }

    @Override
    public SubjectResponse getSubjectById(Long id) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Subject not found with id: " + id));

        return mapToResponse(subject);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() {

        List<Subject> subjects = subjectRepository.findAll();

        return subjects.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SubjectResponse mapToResponse(Subject subject) {

        SubjectResponse response = new SubjectResponse();

        response.setId(subject.getId());
        response.setName(subject.getName());
        response.setDescription(subject.getDescription());
        response.setCreatedAt(subject.getCreatedAt());
        response.setUpdatedAt(subject.getUpdatedAt());

        return response;
    }
}
package com.intellilearn.service.impl;

import com.intellilearn.dto.request.SubjectRequest;
import com.intellilearn.dto.response.SubjectResponse;
import com.intellilearn.entity.AttemptAnswer;
import com.intellilearn.entity.Question;
import com.intellilearn.entity.Quiz;
import com.intellilearn.entity.QuizAttempt;
import com.intellilearn.entity.Subject;
import com.intellilearn.entity.User;
import com.intellilearn.exception.DuplicateSubjectException;
import com.intellilearn.exception.SubjectNotFoundException;
import com.intellilearn.repository.AttemptAnswerRepository;
import com.intellilearn.repository.QuestionRepository;
import com.intellilearn.repository.QuizAttemptRepository;
import com.intellilearn.repository.QuizRepository;
import com.intellilearn.repository.SubjectRepository;
import com.intellilearn.security.service.SecurityUtils;
import com.intellilearn.service.interfaces.NotesService;
import com.intellilearn.service.interfaces.SubjectService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final NotesService notesService;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final SecurityUtils securityUtils;

    public SubjectServiceImpl(SubjectRepository subjectRepository,
                               NotesService notesService,
                               QuizRepository quizRepository,
                               QuestionRepository questionRepository,
                               QuizAttemptRepository quizAttemptRepository,
                               AttemptAnswerRepository attemptAnswerRepository,
                               SecurityUtils securityUtils) {
        this.subjectRepository = subjectRepository;
        this.notesService = notesService;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    public SubjectResponse createSubject(SubjectRequest request) {

        if (subjectRepository.existsByName(request.getName())) {
            throw new DuplicateSubjectException("Subject already exists.");
        }

        Subject subject = new Subject();
        subject.setName(request.getName());
        subject.setDescription(request.getDescription());
        subject.setCreatedBy(securityUtils.getCurrentUser());

        Subject savedSubject = subjectRepository.save(subject);

        return mapToResponse(savedSubject);
    }

    @Override
    public SubjectResponse updateSubject(Long id, SubjectRequest request) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Subject not found with id: " + id));

        requireOwnership(subject);

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
    @Transactional
    public void deleteSubject(Long id) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Subject not found with id: " + id));

        requireOwnership(subject);

       
        List<Quiz> quizzes = quizRepository.findBySubject(subject);

        for (Quiz quiz : quizzes) {

            List<QuizAttempt> attempts = quizAttemptRepository.findByQuizId(quiz.getId());

            for (QuizAttempt attempt : attempts) {
                List<AttemptAnswer> answers =
                        attemptAnswerRepository.findByQuizAttemptAttemptId(attempt.getAttemptId());
                attemptAnswerRepository.deleteAll(answers);
            }
            quizAttemptRepository.deleteAll(attempts);

            List<Question> questions = questionRepository.findByQuiz(quiz);
            questionRepository.deleteAll(questions);
        }

        quizRepository.deleteAll(quizzes);

        notesService.deleteAllNotesForSubject(id);

        
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

   
    private void requireOwnership(Subject subject) {
        User creator = subject.getCreatedBy();
        User currentUser = securityUtils.getCurrentUser();

        if (creator != null && !creator.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only manage subjects you created yourself.");
        }
    }

    private SubjectResponse mapToResponse(Subject subject) {

        SubjectResponse response = new SubjectResponse();

        response.setId(subject.getId());
        response.setName(subject.getName());
        response.setDescription(subject.getDescription());
        response.setCreatedAt(subject.getCreatedAt());
        response.setUpdatedAt(subject.getUpdatedAt());
        response.setCreatedByUserId(
                subject.getCreatedBy() != null ? subject.getCreatedBy().getId() : null);

        return response;
    }
}

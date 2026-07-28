package com.intellilearn.service.impl;

import com.intellilearn.dto.request.SubjectRequest;
import com.intellilearn.dto.response.SubjectResponse;
import com.intellilearn.entity.AttemptAnswer;
import com.intellilearn.entity.Question;
import com.intellilearn.entity.Quiz;
import com.intellilearn.entity.QuizAttempt;
import com.intellilearn.entity.Subject;
import com.intellilearn.exception.DuplicateSubjectException;
import com.intellilearn.exception.SubjectNotFoundException;
import com.intellilearn.repository.AttemptAnswerRepository;
import com.intellilearn.repository.QuestionRepository;
import com.intellilearn.repository.QuizAttemptRepository;
import com.intellilearn.repository.QuizRepository;
import com.intellilearn.repository.SubjectRepository;
import com.intellilearn.service.interfaces.NotesService;
import com.intellilearn.service.interfaces.SubjectService;

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

    public SubjectServiceImpl(SubjectRepository subjectRepository,
                               NotesService notesService,
                               QuizRepository quizRepository,
                               QuestionRepository questionRepository,
                               QuizAttemptRepository quizAttemptRepository,
                               AttemptAnswerRepository attemptAnswerRepository) {
        this.subjectRepository = subjectRepository;
        this.notesService = notesService;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
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

    /**
     * Deleting a subject also removes everything hanging off it — notes
     * (rows + files on disk), quizzes, their questions, and any student
     * attempts/answers against those quizzes — so the delete doesn't fail
     * with a foreign-key constraint error and doesn't leave orphaned rows.
     */
    @Override
    @Transactional
    public void deleteSubject(Long id) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Subject not found with id: " + id));

        // 1. Quizzes for this subject, and everything hanging off each one —
        //    this must happen BEFORE deleting notes, since each Quiz now has
        //    a foreign key to the Notes it was generated from.
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

        // 2. Notes (files + rows) — safe to remove now that no quiz references them
        notesService.deleteAllNotesForSubject(id);

        // 3. Finally, the subject itself
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

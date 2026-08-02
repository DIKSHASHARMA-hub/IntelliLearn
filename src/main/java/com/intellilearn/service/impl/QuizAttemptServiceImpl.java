package com.intellilearn.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intellilearn.dto.request.AnswerRequest;
import com.intellilearn.dto.request.QuizSubmissionRequest;
import com.intellilearn.dto.response.AnswerResultResponse;
import com.intellilearn.dto.response.QuizAttemptResponse;
import com.intellilearn.entity.AttemptAnswer;
import com.intellilearn.entity.Question;
import com.intellilearn.entity.Quiz;
import com.intellilearn.entity.QuizAttempt;
import com.intellilearn.entity.User;
import com.intellilearn.repository.AttemptAnswerRepository;
import com.intellilearn.repository.QuestionRepository;
import com.intellilearn.repository.QuizAttemptRepository;
import com.intellilearn.repository.QuizRepository;
import com.intellilearn.repository.UserRepository;
import com.intellilearn.security.service.SecurityUtils;
import com.intellilearn.service.interfaces.QuizAttemptService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final SecurityUtils securityUtils;

    public QuizAttemptServiceImpl(UserRepository userRepository,
                                  QuizRepository quizRepository,
                                  QuestionRepository questionRepository,
                                  QuizAttemptRepository quizAttemptRepository,
                                  AttemptAnswerRepository attemptAnswerRepository,
                                  SecurityUtils securityUtils) {

        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    public QuizAttemptResponse submitQuiz(QuizSubmissionRequest request) {

        
        User student = securityUtils.getCurrentUser();

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        List<Question> questions = questionRepository.findByQuiz(quiz);
        if (request.getAnswers().size() != questions.size()) {
            throw new IllegalArgumentException(
                    "Please answer all " + questions.size() + " questions before submitting.");
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(student);
        attempt.setQuiz(quiz);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setTotalQuestions(questions.size());

        int score = 0;

        List<AnswerResultResponse> answerResponses = new ArrayList<>();
        List<AttemptAnswer> attemptAnswers = new ArrayList<>();

        java.util.Set<Long> seenQuestionIds = new java.util.HashSet<>();

        for (AnswerRequest answerRequest : request.getAnswers()) {

            if (!seenQuestionIds.add(answerRequest.getQuestionId())) {
                throw new IllegalArgumentException(
                        "Duplicate answer submitted for question " + answerRequest.getQuestionId());
            }

            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Question not found : " + answerRequest.getQuestionId()));

           
            if (!question.getQuiz().getId().equals(quiz.getId())) {
                throw new IllegalArgumentException(
                        "Question does not belong to the selected quiz.");
            }

            boolean correct = question.getCorrectAnswer()
                    .equalsIgnoreCase(answerRequest.getSelectedAnswer());

            if (correct) {
                score++;
            }

            
            AnswerResultResponse answerResponse = new AnswerResultResponse();
            answerResponse.setQuestionId(question.getId());
            answerResponse.setSelectedAnswer(answerRequest.getSelectedAnswer());
            answerResponse.setCorrectAnswer(question.getCorrectAnswer());
            answerResponse.setIsCorrect(correct);

            answerResponses.add(answerResponse);

           
            AttemptAnswer attemptAnswer = new AttemptAnswer();
            attemptAnswer.setQuestion(question);
            attemptAnswer.setSelectedAnswer(answerRequest.getSelectedAnswer());
            attemptAnswer.setIsCorrect(correct);

            attemptAnswers.add(attemptAnswer);
        }
        
        attempt.setScore(score);
        quizAttemptRepository.save(attempt);

        
        for (AttemptAnswer answer : attemptAnswers) {
            answer.setQuizAttempt(attempt);
            attemptAnswerRepository.save(answer);
        }

        
        QuizAttemptResponse response = new QuizAttemptResponse();

        response.setAttemptId(attempt.getAttemptId());
        response.setStudentId(student.getId());
        response.setQuizId(quiz.getId());
        response.setScore(score);
        response.setTotalQuestions(questions.size());

        double percentage = questions.isEmpty()
                ? 0.0
                : ((double) score / questions.size()) * 100;

        response.setPercentage(percentage);
        response.setMessage(getMessage(percentage));
        response.setSubmittedAt(attempt.getSubmittedAt());
        response.setAnswers(answerResponses);

        return response;
    }
        private String getMessage(double percentage) {
            if (percentage >= 90) {
                return "Excellent!";
            } else if (percentage >= 75) {
                return "Good Job!";
            } else if (percentage >= 50) {
                return "Keep Practicing!";
            } else {
                return "Needs Improvement!";
            }
        }
    }
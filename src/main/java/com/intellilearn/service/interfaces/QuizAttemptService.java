package com.intellilearn.service.interfaces;

import com.intellilearn.dto.request.QuizSubmissionRequest;
import com.intellilearn.dto.response.QuizAttemptResponse;

public interface QuizAttemptService {

    QuizAttemptResponse submitQuiz(QuizSubmissionRequest request);

}
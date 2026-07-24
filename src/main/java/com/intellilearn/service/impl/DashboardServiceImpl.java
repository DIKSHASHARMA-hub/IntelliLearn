package com.intellilearn.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.intellilearn.dto.response.DashboardResponseDTO;
import com.intellilearn.dto.response.RecentAttemptDTO;
import com.intellilearn.entity.QuizAttempt;
import com.intellilearn.repository.QuizAttemptRepository;
import com.intellilearn.service.interfaces.DashboardService;


@Service
public class DashboardServiceImpl implements DashboardService {

    private final QuizAttemptRepository quizAttemptRepository;


    public DashboardServiceImpl(QuizAttemptRepository quizAttemptRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
    }


    @Override
    public DashboardResponseDTO getStudentDashboard(Long studentId) {

        List<QuizAttempt> attempts =
                quizAttemptRepository.findByStudentId(studentId);


        DashboardResponseDTO response = new DashboardResponseDTO();


        if (attempts.isEmpty()) {

            response.setStudentId(studentId);
            response.setTotalQuizzesAttempted(0);
            response.setAverageScore(0.0);
            response.setHighestScore(0.0);
            response.setRecentAttempts(new ArrayList<>());

            return response;
        }


        double totalPercentage = 0;
        double highestPercentage = 0;


        for (QuizAttempt attempt : attempts) {

        	double percentage =
        	        ((double) attempt.getScore() /
        	        attempt.getTotalQuestions()) * 100;

            totalPercentage += percentage;

            if (percentage > highestPercentage) {
                highestPercentage = percentage;
            }
        }


        double averageScore =
                totalPercentage / attempts.size();


        List<QuizAttempt> recentAttempts =
                attempts.stream()
                .sorted(
                    Comparator.comparing(
                        QuizAttempt::getSubmittedAt
                    ).reversed()
                )
                .limit(5)
                .toList();



        List<RecentAttemptDTO> recentAttemptDTOList =
                new ArrayList<>();


        for (QuizAttempt attempt : recentAttempts) {


            RecentAttemptDTO dto =
                    new RecentAttemptDTO();


            dto.setQuizTitle(
                    attempt.getQuiz().getTitle()
            );


            dto.setScore(
                    attempt.getScore()
            );


            dto.setTotalQuestions(
                    attempt.getTotalQuestions()
            );


            double percentage =
                    ((double) attempt.getScore() /
                    attempt.getTotalQuestions()) * 100;


            dto.setPercentage(
                    Math.round(percentage * 100.0) / 100.0
            );


            dto.setSubmittedAt(
                    attempt.getSubmittedAt()
            );


            recentAttemptDTOList.add(dto);
        }



        response.setStudentId(studentId);

        response.setStudentName(
                attempts.get(0).getStudent().getFirstName()
                + " "
                + attempts.get(0).getStudent().getLastName()
        );

        response.setTotalQuizzesAttempted(
                attempts.size()
        );


        response.setAverageScore(
                Math.round(averageScore * 100.0) / 100.0
        );


        response.setHighestScore(
                highestPercentage
        );


        response.setRecentAttempts(
                recentAttemptDTOList
        );


        return response;
    }
}
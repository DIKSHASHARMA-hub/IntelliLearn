package com.intellilearn.service.interfaces;

import com.intellilearn.dto.response.DashboardResponseDTO;

public interface DashboardService {

    DashboardResponseDTO getStudentDashboard(Long studentId);

}
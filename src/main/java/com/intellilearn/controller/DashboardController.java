package com.intellilearn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intellilearn.dto.response.DashboardResponseDTO;
import com.intellilearn.service.interfaces.DashboardService;


@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;


    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    @GetMapping("/student/{studentId}")
    public ResponseEntity<DashboardResponseDTO> getStudentDashboard(
            @PathVariable Long studentId) {

        DashboardResponseDTO response =
                dashboardService.getStudentDashboard(studentId);

        return ResponseEntity.ok(response);
    }
}
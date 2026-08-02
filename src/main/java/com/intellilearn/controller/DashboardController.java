package com.intellilearn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intellilearn.dto.response.DashboardResponseDTO;
import com.intellilearn.security.service.SecurityUtils;
import com.intellilearn.service.interfaces.DashboardService;

import org.springframework.security.access.AccessDeniedException;


@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final SecurityUtils securityUtils;


    public DashboardController(DashboardService dashboardService, SecurityUtils securityUtils) {
        this.dashboardService = dashboardService;
        this.securityUtils = securityUtils;
    }


    @GetMapping("/student/{studentId}")
    public ResponseEntity<DashboardResponseDTO> getStudentDashboard(
            @PathVariable Long studentId) {

        
        boolean isOwnDashboard = securityUtils.isCurrentUser(studentId);
        boolean isTeacher = securityUtils.currentUserHasRole("TEACHER");

        if (!isOwnDashboard && !isTeacher) {
            throw new AccessDeniedException("You can only view your own dashboard.");
        }

        DashboardResponseDTO response =
                dashboardService.getStudentDashboard(studentId);

        return ResponseEntity.ok(response);
    }
}
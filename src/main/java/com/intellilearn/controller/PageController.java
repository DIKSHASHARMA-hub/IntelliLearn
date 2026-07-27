package com.intellilearn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves server-rendered pages (login/register/home). These return view
 * names, not JSON, unlike the REST controllers in this package — the
 * actual authentication calls are still made client-side via JS against
 * the existing /api/users/** JSON endpoints.
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/teacher/dashboard")
    public String teacherDashboard() {
        return "teacher-dashboard";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard() {
        return "student-dashboard";
    }
}
package com.intellilearn.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.intellilearn.dto.request.LoginRequest;
import com.intellilearn.dto.request.RegisterRequest;
import com.intellilearn.dto.response.UserResponse;
import com.intellilearn.entity.Role;
import com.intellilearn.entity.User;
import com.intellilearn.exception.EmailAlreadyExistsException;
import com.intellilearn.exception.InvalidCredentialsException;
import com.intellilearn.repository.RoleRepository;
import com.intellilearn.repository.UserRepository;
import com.intellilearn.security.jwt.JwtUtil;
import com.intellilearn.service.interfaces.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }

        String roleName = request.getRole().toUpperCase();

        
        if (!roleName.equals("STUDENT") && !roleName.equals("TEACHER")) {
            throw new RuntimeException("Role must be STUDENT or TEACHER.");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found."));

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setPhone(request.getPhone());
        user.setRole(role);

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setRole(savedUser.getRole().getName());
        response.setToken(jwtUtil.generateToken(savedUser));

        return response;
    }

    @Override
    public UserResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().getName());
        response.setToken(jwtUtil.generateToken(user));

        return response;
    }
}
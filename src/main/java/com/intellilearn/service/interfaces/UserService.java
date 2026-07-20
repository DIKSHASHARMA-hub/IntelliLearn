package com.intellilearn.service.interfaces;

import com.intellilearn.dto.request.LoginRequest;
import com.intellilearn.dto.request.RegisterRequest;
import com.intellilearn.dto.response.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);

    UserResponse login(LoginRequest request);

}
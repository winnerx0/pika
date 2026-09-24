package com.winnerx0.pika.auth;

import com.winnerx0.pika.auth.dto.AuthResponse;
import com.winnerx0.pika.auth.dto.LoginRequest;
import com.winnerx0.pika.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);
}

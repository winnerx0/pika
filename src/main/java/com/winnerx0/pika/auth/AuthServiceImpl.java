package com.winnerx0.pika.auth;

import com.winnerx0.pika.auth.dto.AuthResponse;
import com.winnerx0.pika.auth.dto.LoginRequest;
import com.winnerx0.pika.auth.dto.RegisterRequest;
import com.winnerx0.pika.refreshtoken.RefreshToken;
import com.winnerx0.pika.refreshtoken.RefreshTokenRepository;
import com.winnerx0.pika.users.User;
import com.winnerx0.pika.users.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getUsername());

        String accessToken = jwtService.generateAccessToken(Map.of("role", "USER"), user);
        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshToken rt = new RefreshToken();
        rt.setExpiration(LocalDateTime.now().plusDays(7));
        rt.setToken(refreshToken);

        rt.setUser(user);

        userRepository.save(user);

        refreshTokenRepository.save(rt);

        return new AuthResponse("Registered successfully", accessToken, refreshToken);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authenticated = authenticationManager.authenticate(authentication);

        User user = (User) authenticated.getPrincipal();

        String accessToken = jwtService.generateAccessToken(Map.of("role", "USER"), user);
        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshToken rt = new RefreshToken();
        rt.setExpiration(LocalDateTime.now().plusDays(7));
        rt.setToken(refreshToken);

        rt.setUser(user);

        refreshTokenRepository.save(rt);

        return new AuthResponse("Login successfully", accessToken, refreshToken);
    }
}

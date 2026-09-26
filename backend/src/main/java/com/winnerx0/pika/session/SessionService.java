package com.winnerx0.pika.session;

import com.winnerx0.pika.shared.dto.ApiResponse;
import com.winnerx0.pika.session.dto.SessionRequest;
import com.winnerx0.pika.session.dto.SessionResponse;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    ApiResponse<SessionResponse> createSession(SessionRequest sessionRequest);

    ApiResponse<SessionResponse> getSession(UUID sessionId);

    ApiResponse<List<SessionResponse>> getSessions();

    ApiResponse<SessionResponse> updateSession(UUID sessionId, SessionRequest sessionRequest);

    ApiResponse<?> deleteSession(UUID sessionId);
}

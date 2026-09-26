package com.winnerx0.pika.session;

import com.winnerx0.pika.shared.dto.ApiResponse;
import com.winnerx0.pika.messages.MessageRole;
import com.winnerx0.pika.messages.dto.MessageResponse;
import com.winnerx0.pika.session.dto.SessionRequest;
import com.winnerx0.pika.session.dto.SessionResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public ApiResponse<SessionResponse> createSession(SessionRequest sessionRequest) {

        Session session = new Session();
        session.setTitle(sessionRequest.getTitle());

        return new ApiResponse<>("Session created successfully", mapToResponse(sessionRepository.save(session)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<SessionResponse> getSession(UUID sessionId) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        return new ApiResponse<>("Session retrieved successfully", mapToResponse(session));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<SessionResponse>> getSessions() {

        List<SessionResponse> sessions = sessionRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>("Sessions retrieved successfully", sessions);
    }

    @Override
    public ApiResponse<SessionResponse> updateSession(UUID sessionId, SessionRequest sessionRequest) {

        Session existingSession = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        existingSession.setTitle(sessionRequest.getTitle());

        return new ApiResponse<>("Session updated successfully", mapToResponse(sessionRepository.save(existingSession)));
    }

    @Override
    public ApiResponse<?> deleteSession(UUID sessionId) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionId));

        sessionRepository.delete(session);

        return new ApiResponse<>("Session deleted successfully", null);
    }

    private SessionResponse mapToResponse(Session session) {

        List<MessageResponse> messages = session.getMessages() == null ? List.of() : session.getMessages().stream()
                .map(message -> new MessageResponse(message.getId(), message.getContent(), session.getId(), message.getRole()))
                .toList();

        return new SessionResponse(session.getId(), session.getTitle(), messages);
    }
}

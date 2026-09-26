package com.winnerx0.pika.session;

import com.winnerx0.pika.session.dto.SessionRequest;
import com.winnerx0.pika.session.dto.SessionResponse;
import com.winnerx0.pika.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(@RequestBody @Valid SessionRequest sessionRequest){
        return ResponseEntity.ok(sessionService.createSession(sessionRequest));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getSessions(){
        return ResponseEntity.ok(sessionService.getSessions());
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionResponse>> getSession(@PathVariable UUID sessionId){
        return ResponseEntity.ok(sessionService.getSession(sessionId));
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionResponse>> updateSession(@PathVariable UUID sessionId, @RequestBody @Valid SessionRequest sessionRequest){
        return ResponseEntity.ok(sessionService.updateSession(sessionId, sessionRequest));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<?>> deleteSession(@PathVariable UUID sessionId){
        return ResponseEntity.ok(sessionService.deleteSession(sessionId));
    }
}

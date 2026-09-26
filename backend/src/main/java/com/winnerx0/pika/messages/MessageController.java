package com.winnerx0.pika.messages;

import com.winnerx0.pika.messages.dto.MessageRequest;
import com.winnerx0.pika.messages.dto.MessageResponse;
import com.winnerx0.pika.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping(value = "/sse/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> sse(@PathVariable UUID sessionId){
        return ResponseEntity.ok(messageService.sse(sessionId));
    }

    @PostMapping(value = "/session/{sessionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> sendMessage(@PathVariable UUID sessionId, @RequestBody @Valid MessageRequest messageRequest){
        return ResponseEntity.ok(messageService.sendMessage(sessionId, messageRequest));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(@PathVariable UUID sessionId){
        return ResponseEntity.ok(messageService.getMessages(sessionId));
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessage(@PathVariable UUID messageId){
        return ResponseEntity.ok(messageService.getMessage(messageId));
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessage(@PathVariable UUID messageId, @RequestBody @Valid MessageRequest messageRequest){
        return ResponseEntity.ok(messageService.updateMessage(messageId, messageRequest));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<?>> deleteMessage(@PathVariable UUID messageId){
        return ResponseEntity.ok(messageService.deleteMessage(messageId));
    }
}

package com.winnerx0.pika.messages;

import com.winnerx0.pika.shared.dto.ApiResponse;
import com.winnerx0.pika.messages.dto.MessageRequest;
import com.winnerx0.pika.messages.dto.MessageResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    SseEmitter sse(UUID sessionId);

    ApiResponse<String> sendMessage(UUID sessionId, MessageRequest messageRequest);

    ApiResponse<MessageResponse> getMessage(UUID messageId);

    ApiResponse<List<MessageResponse>> getMessages(UUID sessionId);

    ApiResponse<MessageResponse> updateMessage(UUID messageId, MessageRequest messageRequest);

    ApiResponse<?> deleteMessage(UUID messageId);
}

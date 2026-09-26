package com.winnerx0.pika.messages;

import com.winnerx0.pika.session.Session;
import com.winnerx0.pika.session.SessionRepository;
import com.winnerx0.pika.shared.dto.ApiResponse;
import com.winnerx0.pika.messages.dto.MessageRequest;
import com.winnerx0.pika.messages.dto.MessageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    private final PgVectorStore vectorStore;
    private final ChatClient chatClient;
    private final SseService sseService;

    @Override
    public SseEmitter sse(UUID sessionId) {
        return sseService.subscribe(sessionId);
    }

    @Override
    public ApiResponse<String> sendMessage(UUID sessionId, MessageRequest messageRequest) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        vectorStore.similaritySearch(messageRequest.getContent());

        Flux<String> response = chatClient.prompt()
                .system("""
                        You are a Retrieval-Augmented Generation (RAG) assistant specialized in Nigerian tax law.
                        
                        Answer questions only using the retrieved context provided to you. Do not rely on prior knowledge, assumptions, or information outside the context.
                        
                        Rules:
                        - Only answer questions related to Nigerian tax law, taxation, tax administration, obligations, procedures, and related matters.
                        - Base every factual statement on the retrieved context.
                        - Do not invent tax rates, thresholds, deadlines, exemptions, penalties, legal provisions, or interpretations.
                        - When relevant, cite the Act, section, subsection, regulation, or other legal provision contained in the context.
                        - Preserve important exceptions, conditions, dates, and amendments.
                        - If the context contains conflicting provisions, clearly state the conflict instead of choosing one without support.
                        - For calculations, use only rates and rules explicitly provided in the context.
                        - If the context does not contain enough information to answer reliably, respond:
                        
                        "I do not have enough information in the provided context to answer that question."
                        
                        Be concise, professional, and precise. Your priority is accuracy and strict grounding in the provided Nigerian tax law context, not answering every question.
                        """)
                .user(messageRequest.getContent())
                .stream()
                .content();

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setRole(messageRequest.getRole() == null ? MessageRole.USER : messageRequest.getRole());
        message.setSession(session);

        messageRepository.save(message);

        StringBuilder assistantResponse = new StringBuilder();
        response
                .doOnNext(chunk -> {
                    assistantResponse.append(chunk).append(" ");

                    sseService.send(sessionId, chunk + " ");

                })
                .doOnComplete(() -> {
                    if (!assistantResponse.isEmpty()) {
                        Message assistantMessage = new Message();
                        assistantMessage.setContent(assistantResponse.toString());
                        assistantMessage.setRole(MessageRole.ASSISTANT);
                        assistantMessage.setSession(session);
                        messageRepository.save(assistantMessage);
                    }
                })
                .subscribe();
        return new ApiResponse<>("Message sent successfully", null);
    }

    @Override
    public ApiResponse<MessageResponse> getMessage(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        return new ApiResponse<>("Message retrieved successfully", mapToResponse(message));
    }

    @Override
    public ApiResponse<List<MessageResponse>> getMessages(UUID sessionId) {

        sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        List<MessageResponse> messages = messageRepository.findBySessionIdOrderByCreatedDateAsc(sessionId).stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>("Messages retrieved successfully", messages);
    }

    @Override
    public ApiResponse<MessageResponse> updateMessage(UUID messageId, MessageRequest messageRequest) {

        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        existingMessage.setContent(messageRequest.getContent());

        return new ApiResponse<>("Message updated successfully", mapToResponse(messageRepository.save(existingMessage)));
    }

    @Override
    public ApiResponse<?> deleteMessage(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        messageRepository.delete(message);

        return new ApiResponse<>("Message deleted successfully", null);
    }

    private MessageResponse mapToResponse(Message message) {

        return new MessageResponse(message.getId(), message.getContent(), message.getSession().getId(), message.getRole());
    }
}

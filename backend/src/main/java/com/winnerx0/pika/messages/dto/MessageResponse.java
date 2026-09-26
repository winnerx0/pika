package com.winnerx0.pika.messages.dto;

import com.winnerx0.pika.messages.MessageRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private UUID id;

    private String content;

    private UUID sessionId;

    private MessageRole role;
}

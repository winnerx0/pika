package com.winnerx0.pika.messages.dto;

import com.winnerx0.pika.messages.MessageRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotBlank(message = "Content required")
    private String content;

    private MessageRole role = MessageRole.USER;
}

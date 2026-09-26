package com.winnerx0.pika.session.dto;

import com.winnerx0.pika.messages.dto.MessageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    private UUID id;

    private String title;

    private List<MessageResponse> messages;
}

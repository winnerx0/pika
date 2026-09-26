package com.winnerx0.pika.messages;

import com.winnerx0.pika.audit.AuditMetadata;
import com.winnerx0.pika.session.Session;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@ToString
public class Message extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role = MessageRole.USER;

    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id")
    private Session session;

//    @CreatedBy
//    private User user;
}

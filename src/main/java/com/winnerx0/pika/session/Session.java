package com.winnerx0.pika.session;

import com.winnerx0.pika.audit.AuditMetadata;
import com.winnerx0.pika.messages.Message;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@ToString
public class Session extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Message> messages;

}

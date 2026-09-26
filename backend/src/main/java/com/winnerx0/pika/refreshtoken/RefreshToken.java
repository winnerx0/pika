package com.winnerx0.pika.refreshtoken;

import com.winnerx0.pika.audit.AuditMetadata;
import com.winnerx0.pika.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@ToString
public class RefreshToken extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiration;

    @Column(nullable = false)
    private boolean blacklisted;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}

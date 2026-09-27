package com.winnerx0.pika.session;

import com.winnerx0.pika.audit.AuditMetadata;
import com.winnerx0.pika.messages.Message;
import com.winnerx0.pika.users.User;
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

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    @OrderBy("createdDate ASC")
    private List<Message> messages;

//    @ManyToOne
//    @JoinColumn(nullable = false, name = "user_id")
//    private User user;
}

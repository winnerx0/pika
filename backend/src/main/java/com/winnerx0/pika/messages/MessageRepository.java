package com.winnerx0.pika.messages;

import com.winnerx0.pika.session.Session;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySessionIdOrderByCreatedDateAsc(UUID sessionId);

    List<Message> findAllBySessionOrderByCreatedDateAsc(Session session, Limit limit);
}

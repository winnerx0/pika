package com.winnerx0.pika.messages;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID id){

        SseEmitter emitter = new SseEmitter(0L);

        emitters.put(id, emitter);

        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        emitter.onError((e) -> emitters.remove(id));

        try {
            emitter.send(SseEmitter.event().comment("connected"));
        } catch (IOException e) {
            emitters.remove(id);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void send(UUID id, Object data){

        SseEmitter emitter = emitters.get(id);

        if(emitter == null) return;

        try {
            emitter.send(SseEmitter.event().name("message").data(data));
        } catch (IOException e) {
            emitters.remove(id);
            emitter.completeWithError(e);
        }
    }
}

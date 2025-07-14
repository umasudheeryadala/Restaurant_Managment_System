package com.tastes_of_india.restaurantManagement.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/public")
public class StreamResource {

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    private final Logger log= LoggerFactory.getLogger(StreamResource.class);

    @GetMapping(value = "/orders/stream/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOrder(@PathVariable Long id) {
        SseEmitter emitter = new SseEmitter(0L); // 0 = no timeout
        emitters.computeIfAbsent(id, id1 -> new ArrayList<>()).add(emitter);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data(":ping"));
            } catch (IOException e) {
                emitter.completeWithError(e);
                scheduler.shutdown();
            }
        }, 0, 15, TimeUnit.SECONDS);

        emitter.onTimeout(() -> removeEmitter(id, emitter));
        emitter.onCompletion(() -> removeEmitter(id, emitter));
        return emitter;
    }

    private void removeEmitter(Long orderId, SseEmitter emitter) {
        emitters.getOrDefault(orderId, new ArrayList<>()).remove(emitter);
    }

    public void notifyOrderUpdate(Long id, String message) {
        List<SseEmitter> list = emitters.get(id);
        if (list != null) {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().data(message));
                } catch (IllegalStateException | IOException e) {
                    log.warn("Emitter already completed or client disconnected");
                    emitter.complete();
                    emitters.remove(id);
                }

            }
        }
    }
}

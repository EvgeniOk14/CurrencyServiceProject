package com.example.requestcurrecyservice.models;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ResponseFutureManager {
    private final Map<String, CompletableFuture<String>> responseFutures = new ConcurrentHashMap<>();

    // Удаляет CompletableFuture по requestId
    public CompletableFuture<String> removeResponseFuture(String requestId)
    {
        return responseFutures.remove(requestId);
    }

    // Добавляет CompletableFuture с указанным requestId
    public void addResponseFuture(String requestId, CompletableFuture<String> future)
    {
        responseFutures.put(requestId, future);
    }
}

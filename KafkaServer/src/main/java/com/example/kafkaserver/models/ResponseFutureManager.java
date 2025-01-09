package com.example.kafkaserver.models;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс ResponseFutureManager:
 *
 * Управляет асинхронными запросами, используя CompletableFuture.
 * Этот класс позволяет добавлять и удалять CompletableFuture по уникальному идентификатору запроса.
 *
 * Поля:
 * - {@link #responseFutures}: Карта, хранящая CompletableFuture, сопоставленные с уникальными идентификаторами запросов.
 */
@Component
public class ResponseFutureManager
{
    //region Fields
    private final Map<String, CompletableFuture<String>> responseFutures = new ConcurrentHashMap<>();
    //endRegion

    //region Methods
    /**
     * Метод removeResponseFuture:
     * Удаляет CompletableFuture, связанный с указанным уникальным идентификатором запроса.
     *
     * @param requestId уникальный идентификатор запроса.
     * @return CompletableFuture, связанный с указанным requestId, или null, если он не найден.
     */
    public CompletableFuture<String> removeResponseFuture(String requestId)
    {
        return responseFutures.remove(requestId);
    }

    /**
     * Метод addResponseFuture:
     * Добавляет CompletableFuture с указанным уникальным идентификатором запроса.
     *
     * @param requestId уникальный идентификатор запроса.
     * @param future CompletableFuture, который необходимо добавить.
     */
    public void addResponseFuture(String requestId, CompletableFuture<String> future)
    {
        responseFutures.put(requestId, future);
    }
    //endRegion
}

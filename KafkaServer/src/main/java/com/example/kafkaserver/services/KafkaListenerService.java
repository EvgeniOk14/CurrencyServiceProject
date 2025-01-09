package com.example.kafkaserver.services;

import com.example.kafkaserver.models.ResponseFutureManager;
import com.example.kafkaserver.services.kafkaService.CurrencyRequestService;
import com.example.kafkaserver.services.kafkaService.CurrencyResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

/**
 * Класс KafkaListenerService:
 *
 * Предназначен для обработки ответов, полученных из Kafka.
 * Служит слушателем для топика "response-topic" и управляет асинхронной обработкой ответов.
 *
 * Зависимости:
 * - {@link CurrencyRequestService}: Сервис для обработки запросов о курсах валют.
 * - {@link CurrencyResponseService}: Сервис для обработки ответов и отправки их в API Gateway.
 * - {@link ResponseFutureManager}: Менеджер для управления асинхронными ответами.
 *
 * Поля:
 * - {@link #currencyResponseService}: Сервис для обработки ответов и отправки их в API Gateway.
 * - {@link #responseFutureManager}: Менеджер для управления асинхронными ответами.
 *
 * Конструкторы:
 * - Конструктор, который инициализирует зависимости.
 *
 * Методы:
 * - {@link #handleResponseListener(String, String, String)}: Обрабатывает сообщения из топика Kafka и завершает соответствующие Future.
 */
@Service
public class KafkaListenerService
{
    //region Fields
    private static final Logger logger =  LoggerFactory.getLogger(KafkaListenerService.class); // логирование
    private final CurrencyResponseService currencyResponseService;
    private final ResponseFutureManager responseFutureManager;

    //endregion

    //region Constructor
    public KafkaListenerService(CurrencyResponseService currencyResponseService,
                                ResponseFutureManager responseFutureManager)
    {
        this.currencyResponseService = currencyResponseService;
        this.responseFutureManager = responseFutureManager;
    }
    //endregion

    //region Methods
    /**
     * Метод handleResponseListener:
     * Обрабатывает сообщения, полученные из топика "response-topic" Kafka.
     * Завершает Future на основе уникального идентификатора запроса (requestId).
     *
     * @param key Уникальный ключ сообщения из Kafka.
     * @param requestId Уникальный идентификатор запроса.
     * @param message Сообщение, полученное из Kafka.
     */
    @KafkaListener(topics = "response-topic", groupId = "kafka-server-group")
    public void handleResponseListener(@Header(KafkaHeaders.KEY) String key,
                                       @Header(KafkaHeaders.CORRELATION_ID) String requestId,
                                       String message)
    {
        logger.info("Полученные данные из Kafka: {}", message); // логируем сообщение из кафка

        // Асинхронная обработка сообщения
        CompletableFuture.runAsync(() -> {
            System.out.println("Получено сообщение из Kafka: " + message);

            // Завершение Future на основе requestId
            CompletableFuture<String> responseFuture = responseFutureManager.removeResponseFuture(requestId);
            if (responseFuture != null)
            {
                responseFuture.complete(message);
                System.out.println("Ответ завершен для requestId: " + requestId);

                currencyResponseService.processResponse(message, requestId); // отправка сообщения в API GateWay
            }
            else
            {
                System.out.println("Ответ пришел, но запрос не найден для requestId: " + requestId);
            }
        });
    }
    //endregion
}

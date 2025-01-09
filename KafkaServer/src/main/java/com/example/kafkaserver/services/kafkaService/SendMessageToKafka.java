package com.example.kafkaserver.services.kafkaService;

import com.example.kafkaserver.models.ResponseFutureManager;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Класс SendMessageToKafka:
 *
 * Реализует логику отправки сообщений в Kafka-топик "request-currency-topic".
 * Предоставляет метод для отправки сообщений с добавлением заголовка kafka_messageKey.
 *
 * Зависимости:
 * - {@link KafkaTemplate}: шаблон для отправки сообщений в Kafka.
 * - {@link ResponseFutureManager}: менеджер для управления CompletableFuture ответами.
 *
 * Поля:
 * - {@link #logger}: Логгер для ведения журнала событий и ошибок.
 * - {@link #kafkaTemplate}: Шаблон для взаимодействия с Kafka.
 *
 * Конструкторы:
 * - Конструктор, который инициализирует {@link #kafkaTemplate}}.
 *
 * Методы:
 * - {@link #sendMessageToKafka(String, String, CompletableFuture)}: Отправляет сообщение в Kafka с
 *   добавлением заголовка kafka_messageKey и обрабатывает возможные исключения.
 *
 * Исключения:
 * - {@link RuntimeException}: выбрасывается, если возникает ошибка при отправке сообщения в Kafka.
 *
 * Аннотации:
 * - {@link Service}: указывает, что класс является сервисом и управляется Spring-контейнером.
 */
@Service
public class SendMessageToKafka
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(CurrencyRequestService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    //endRegion

    //region Constructor
    public SendMessageToKafka(KafkaTemplate<String, String> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }
    //endRegion

    //region Methods
    /**
     * Метод sendMessageToKafka:
     * Отправляет сообщение в Kafka с добавлением заголовка kafka_messageKey.
     *
     * @param requestId уникальный идентификатор запроса.
     * @param message сообщение, которое нужно отправить в Kafka.
     * @param responseFuture CompletableFuture для обработки ответа.
     */
    public void sendMessageToKafka(String requestId, String message, CompletableFuture<String> responseFuture)
    {
        try
        {
            kafkaTemplate.executeInTransaction(operations -> {
                operations.send(new ProducerRecord<>("request-currency-topic", null, requestId, message,
                        new RecordHeaders().add("kafka_messageKey", requestId.getBytes(StandardCharsets.UTF_8))));
                logger.info("Сообщение с заголовком kafka_messageKey отправлено в Kafka: {}", message);
                return null;
            });
        }
        catch (ProducerFencedException e)
        {
            logger.error("Producer fenced error: {}", e.getMessage());
            responseFuture.completeExceptionally(e);
        }
        catch (Exception e) 
        {
            logger.error("Ошибка при отправке сообщения в Kafka", e);
            responseFuture.completeExceptionally(e);
        }
    }
    //endRegion
}

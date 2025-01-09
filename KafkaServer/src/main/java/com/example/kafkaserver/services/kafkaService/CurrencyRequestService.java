package com.example.kafkaserver.services.kafkaService;

import com.example.kafkaserver.interfacies.TaskExecutor;
import com.example.kafkaserver.models.ResponseFutureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Класс CurrencyRequestService:
 *
 * Обрабатывает сообщения и отправляет их в топик Kafka.
 * Предоставляет методы для создания уникальных идентификаторов запросов и управления
 * асинхронными ответами.
 *
 * Зависимости:
 * - {@link KafkaTemplate}: шаблон для отправки сообщений в Kafka.
 * - {@link ResponseFutureManager}: менеджер для управления CompletableFuture ответами.
 * - {@link SendMessageToKafka}: класс для отправки сообщений в Kafka.
 *
 * Поля:
 * - {@link #logger}: Логгер для ведения журнала событий и ошибок.
 * - {@link #responseFutureManager}: Менеджер для хранения и управления Future ответами.
 * - {@link #sendMessageToKafka}: Класс для отправки сообщений в Kafka.
 *
 * Конструкторы:
 * - Конструктор, который инициализирует {@link #responseFutureManager}
 *   и {@link #sendMessageToKafka}.
 *
 * Методы:
 * - {@link #processRequest(String, String)}: Обрабатывает сообщение и отправляет его в топик Kafka,
 *   возвращая CompletableFuture, который будет завершен, когда ответ будет получен.
 *
 * Исключения:
 * - {@link RuntimeException}: выбрасывается, если возникает ошибка при отправке сообщения в Kafka.
 *
 * Аннотации:
 * - {@link Service}: указывает, что класс является сервисом и управляется Spring-контейнером.
 */
@Service
public class CurrencyRequestService
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(CurrencyRequestService.class);
    private final ResponseFutureManager responseFutureManager;
    private SendMessageToKafka sendMessageToKafka;
    private TaskExecutor taskExecutor;
    //endRegion

    //region Constructor
    public CurrencyRequestService(ResponseFutureManager responseFutureManager, SendMessageToKafka sendMessageToKafka,
                                  TaskExecutor taskExecutor)
    {
        this.responseFutureManager = responseFutureManager;
        this.sendMessageToKafka = sendMessageToKafka;
        this.taskExecutor = taskExecutor;
    }
    //endRegion

    //region Methods
    /**
     * Метод processRequest:
     * Обрабатывает сообщение и отправляет его в топик Kafka.
     *
     * @param messageType Тип сообщения (например, "SINGLE", "FILTER").
     * @param payload Полезная нагрузка сообщения, содержащая необходимые данные.
     * @return CompletableFuture, который будет завершен, когда ответ будет получен.
     * @throws RuntimeException если возникает ошибка при отправке сообщения в Kafka.
     */
    public CompletableFuture<String> processRequest(String messageType, String payload)
    {
            String requestId = UUID.randomUUID().toString(); // создаём уникальный requestId запроса
            String message = messageType + ":" + payload; // сообщение формируется из messageType и payload

            logger.info("Создан запрос: requestId={}, message={}", requestId, message); // логируем создание нового запроса с заданными параметрами

            CompletableFuture<String> responseFuture = new CompletableFuture<>(); // Создаем Future для обработки ответа

            responseFutureManager.addResponseFuture(requestId, responseFuture); // отслеживание активных запросов по requestId

        taskExecutor.submitTask(() -> {
            try
            {
            // Отправляем сообщение в Kafka:
            sendMessageToKafka.sendMessageToKafka(requestId, message, responseFuture); // используем метод sendMessageToKafka сервиса SendMessageToKafka для отправки сообщения в топик кафка
            } catch (Exception e)
            {
                responseFuture.completeExceptionally(e); // Обработка ошибок
            }
            });
            return responseFuture; // Возвращаем Future, чтобы клиент мог ждать ответа
    }
    //endRegion
}

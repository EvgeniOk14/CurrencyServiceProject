package com.example.kafkaserver.services.apiGatewayService;

import com.example.kafkaserver.services.KafkaListenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.concurrent.CompletableFuture;

/**
 * Класс ApiGatewayService:
 *
 * Реализует бизнес-логику для отправки сообщений и сообщений об ошибках в API Gateway.
 * Предоставляет методы для асинхронной отправки данных с использованием WebClient и RetryTemplate.
 *
 * Зависимости:
 * - WebClient: клиент для выполнения HTTP-запросов.
 * - RetryTemplate: шаблон для реализации логики повторных попыток выполнения операций.
 *
 * Методы:
 * - {@link #sendToApiGateway(String)}: Отправляет сообщение в API Gateway и обрабатывает ответ.
 * - {@link #sendErrorToApiGateway(String)}: Отправляет сообщение об ошибке в API Gateway с повторными попытками в случае неудачи.
 *
 * Исключения:
 * - {@link IllegalArgumentException}: стандартное системное исключение выбрасывается, если переданы некорректные аргументы (например, null или пустые строки).
 *
 * Аннотации:
 * - @Service: указывает, что класс является сервисом и управляется Spring-контейнером.
 */
@Service
public class ApiGatewayService
{
    //region Fields
    @Value("${spring.api.gateway-url}") // URL адрес API Gateway для ответа (Response)
    private String API_GATEWAY_URL; // URL адрес API Gateway для ответа (Response)
    private WebClient webClient;
    private final RetryTemplate retryTemplate;
    private static final Logger logger =  LoggerFactory.getLogger(KafkaListenerService.class); // логирование
    //endRegion

    //region Constructor
    public ApiGatewayService(WebClient webClient, RetryTemplate retryTemplate)
    {
        this.webClient = webClient;
        this.retryTemplate = retryTemplate;
    }
    //endRegion

    //region Methods
    /**
     * Метод sendToApiGateway:
     * Служит для отправки сообщения в API Gateway.
     * Использует RetryTemplate для повторных попыток отправки в случае неудачи.
     * Возвращает CompletableFuture, который завершится, когда ответ будет получен или произойдет ошибка.
     *
     * @param message сообщение, которое необходимо отправить в API Gateway.
     * @return возвращает CompletableFuture, содержащий ResponseEntity, который включает статус и тело ответа от API Gateway.
     * @exception IllegalArgumentException если сообщение равно null или пустое.
     */
    public CompletableFuture<ResponseEntity<String>> sendToApiGateway(String message)
    {
        // Проверяем, что сообщение не является null или пустым
        if (message == null || message.isEmpty())
        {
            throw new IllegalArgumentException("Сообщение не может быть null или пустым."); // выбрасываем исключение, если сообщение некорректно
        }

        // Начинаем асинхронную задачу
        return CompletableFuture.supplyAsync(() -> {
            // Логируем попытку отправки
            logger.info("Попытка отправки сообщения в API Gateway: {}", message);
            return message; // Возвращаем сообщение для дальнейшей обработки
        }).thenCompose(msg -> {
            // Используем RetryTemplate для попытки отправки данных с повтором в случае неудачи
            return Mono.just(msg) // Оборачиваем сообщение в Mono
                    .flatMap(m -> retryTemplate.execute(context -> {
                        // Выполняем попытку отправки сообщения
                        return webClient.post() // Создаем POST-запрос
                                .uri(API_GATEWAY_URL) // Указываем URL API Gateway
                                .bodyValue(m) // Устанавливаем тело запроса
                                .retrieve() // Выполняем запрос и получаем ответ
                                .bodyToMono(String.class) // Преобразуем тело ответа в строку
                                .doOnSuccess(response -> {
                                    // Логируем успешный ответ
                                    logger.info("Сообщение {} успешно отправлено в API Gateway.", m);
                                })
                                .doOnError(error -> {
                                    // Логируем ошибку, если отправка не удалась
                                    logger.error("Ошибка при отправке сообщения {} в API Gateway: {}", m, error.getMessage());
                                });
                    }))
                    .toFuture(); // Преобразуем Mono в CompletableFuture
        }).handle((response, ex) -> {
            // Обрабатываем результат выполнения
            if (ex != null)
            {
                // Логируем ошибку, если все попытки завершились неудачей
                logger.error("Ошибка {}, при отправке сообщения {} в API Gateway: ", ex.getMessage(), message);
                return ResponseEntity.status(HttpStatus.CONFLICT) // Возвращаем статус конфликта
                        .body("Ошибка при отправке сообщения " + message + " в API Gateway!"); // Возвращаем тело ответа с ошибкой
            }
            // Если запрос успешен, возвращаем ответ
            return ResponseEntity.status(HttpStatus.OK) // Возвращаем статус успеха
                    .body("Сообщение: " + message + " успешно отправлено в API Gateway!"); // Возвращаем тело ответа с успехом
        });
    }

    /**
     * Метод sendErrorToApiGateway:
     * Служит для отправки сообщения об ошибке в API Gateway.
     * Использует RetryTemplate для повторных попыток отправки в случае неудачи.
     * Возвращает CompletableFuture, который завершится, когда отправка завершится или произойдет ошибка.
     *
     * @param errorMessage сообщение об ошибке, которое необходимо отправить в API Gateway.
     * @return возвращает CompletableFuture<Void>, который завершится после попытки отправки сообщения об ошибке.
     * @exception IllegalArgumentException если сообщение об ошибке равно null или пустое.
     */
    public CompletableFuture<Void> sendErrorToApiGateway(String errorMessage)
    {
        // Проверяем, что сообщение об ошибке не является null или пустым
        if (errorMessage == null || errorMessage.isEmpty())
        {
            throw new IllegalArgumentException("Сообщение об ошибке не может быть null или пустым."); // выбрасываем исключение, если сообщение некорректно
        }

        // Начинаем асинхронную задачу
        return CompletableFuture.runAsync(() -> {
            // Логируем попытку отправки сообщения об ошибке
            logger.info("Попытка отправки ошибки в API Gateway: {}", errorMessage);
        }).thenCompose(aVoid -> {
            // Используем RetryTemplate для попытки отправки ошибки с повтором в случае неудачи
            return Mono.just(errorMessage) // Оборачиваем сообщение об ошибке в Mono
                    .flatMap(msg -> retryTemplate.execute(context -> {
                        // Формируем сообщение с дополнительной информацией
                        String errorPayload = "Error: " + msg;

                        // отправка ошибки в API Gateway:
                        return webClient.post() // Создаем POST-запрос
                                .uri(API_GATEWAY_URL) // Указываем URL API Gateway
                                .bodyValue(errorPayload) // Устанавливаем тело запроса
                                .retrieve() // Выполняем запрос и получаем ответ
                                .bodyToMono(String.class) // Преобразуем тело ответа в строку
                                .doOnSuccess(response -> {
                                    // Логируем успешный ответ
                                    logger.info("Ошибка успешно отправлена в API Gateway.");
                                })
                                .doOnError(error -> {
                                    // Логируем ошибку, если отправка не удалась
                                    logger.error("Ошибка при отправке сообщения об ошибке в API Gateway: {}", error.getMessage());
                                });
                    }))
                    .toFuture(); // Преобразуем Mono в CompletableFuture
        }).handle((result, ex) -> {
            // Обрабатываем результат выполнения
            if (ex != null)
            {
                // Логируем ошибку, если все попытки завершились неудачей
                logger.error("Ошибка при отправке сообщения об ошибке в API Gateway: {}", ex.getMessage());
            }
            return null; // Возвращаем null, так как метод возвращает CompletableFuture<Void>
        });
    }
    //endRegion
}

package com.example.kafkaserver.services.kafkaService;

import com.example.kafkaserver.services.apiGatewayService.ApiGatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Класс CurrencyResponseService:
 *
 * Предназначен для обработки ответов на запросы о курсах валют и отправки их в API Gateway.
 * Обеспечивает дополнительную обработку ответов, включая обработку ошибок и успешных ответов.
 *
 * Зависимости:
 * - {@link ApiGatewayService}: сервис для взаимодействия с API Gateway.
 *
 * Поля:
 * - {@link #logger}: Логгер для ведения журнала событий и ошибок.
 * - {@link #apiGatewayService}: Сервис для взаимодействия с API Gateway.
 *
 * Конструкторы:
 * - Конструктор, который инициализирует зависимости.
 *
 * Методы:
 * - {@link #processResponse(String, String)}: Обрабатывает ответ из Kafka и отправляет его в API Gateway.
 */
@Service
public class CurrencyResponseService
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(CurrencyResponseService.class);
    private ApiGatewayService apiGatewayService;
    //endregion

    //region Constructor
    public CurrencyResponseService(ApiGatewayService apiGatewayService)
    {
        this.apiGatewayService = apiGatewayService;
    }
    //endregion

    //region Methods
    /**
     * Метод processResponse:
     * Обрабатывает ответ из Kafka и отправляет его в API Gateway.
     *
     * @param message   Сообщение из Kafka.
     * @param requestId Уникальный идентификатор запроса.
     */
    public void processResponse(String message, String requestId)
    {
        logger.info("Дополнительная обработка ответа: requestId={}, message={}", requestId, message);


        if (message == null || message.isEmpty())
        {
            logger.error("Обнаружена ошибка в ответе: {}", message);
            apiGatewayService.sendErrorToApiGateway(message); // отправляем ошибку на API Gateway
        }
        else
        {
            logger.info("Ответ успешно обработан: {}", message); // отправляем ответ на API Gateway
            apiGatewayService.sendToApiGateway(message);
        }
    }
    //endregion
}
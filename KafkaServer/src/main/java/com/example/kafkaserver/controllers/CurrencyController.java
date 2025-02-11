package com.example.kafkaserver.controllers;

import com.example.kafkaserver.services.KafkaListenerService;
import com.example.kafkaserver.services.apiGatewayService.ApiGatewayService;
import com.example.kafkaserver.services.kafkaService.CurrencyRequestService;
import com.example.kafkaserver.services.kafkaService.CurrencyResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Класс CurrencyController:
 *
 * Контроллер, который получает запросы через REST API от API Gateway, обрабатывает их
 * и отправляет в соответствующие топики Kafka. Управляет запросами на получение курсов валют,
 * включая получение всех валют, конкретной валюты и фильтрацию валют.
 *
 * Зависимости:
 * - {@link CurrencyRequestService}: Сервис для обработки запросов о курсах валют.
 * - {@link CurrencyResponseService}: Сервис для обработки ответов и отправки их в API Gateway.
 * - {@link ApiGatewayService}: Сервис для взаимодействия с API Gateway.
 * - {@link KafkaListenerService}: Сервис для обработки ответов из Kafka.
 *
 * Методы:
 * - {@link #getAllCurrencies()}: Обрабатывает GET запрос для получения всех доступных курсов валют.
 * - {@link #getSingleCurrency(String)}: Обрабатывает GET запрос для получения курса конкретной валюты.
 * - {@link #getFilteredCurrencies(String)}: Обрабатывает GET запрос для получения курсов валют по фильтру.
 *
 * Доступные конечные точки:
 * - **GET** `http://localhost:8081/currencies/all` - Получает все доступные курсы валют.
 * - **GET** `http://localhost:8081/currencies/single/{currencyCode}` - Получает курс конкретной валюты.
 * - **GET** `http://localhost:8081/currencies/filter/{filter}` - Получает курсы валют по фильтру.
 *
 */
@RestController
@RequestMapping("/currencies")
public class CurrencyController
{
    //region Fields
    private final CurrencyRequestService currencyRequestService;
    private final CurrencyResponseService currencyResponseService;
    private ApiGatewayService apiGatewayService;
    private KafkaListenerService kafkaListenerService;
    //endRegion

    //region Constructor
    /**
     * Конструктор класса CurrencyController.
     *
     * @param currencyRequestService Сервис для обработки запросов о курсах валют.
     * @param apiGatewayService Сервис для взаимодействия с API Gateway.
     * @param kafkaListenerService Сервис для обработки ответов из Kafka.
     * @param currencyResponseService Сервис для обработки ответов.
     */
    public CurrencyController(CurrencyRequestService currencyRequestService, ApiGatewayService apiGatewayService,
                              KafkaListenerService kafkaListenerService, CurrencyResponseService currencyResponseService)
    {
        this.currencyRequestService = currencyRequestService;
        this.apiGatewayService = apiGatewayService;
        this.kafkaListenerService = kafkaListenerService;
        this.currencyResponseService = currencyResponseService;
    }
    //endRegion

    //region Methods
    /**
     * Метод getAllCurrencies:
     * Получает GET запрос через REST API от API Gateway, содержащий в URL запросе строку ALL,
     * обрабатывает запрос и отправляет его в топик "request-currency-topic" Kafka,
     * для получения всех доступных курсов валют от стороннего API.
     *
     * @return ResponseEntity<String> - объект ResponseEntity, содержащий ответ с сообщением на API Gateway.
     *
     * - String response = future.get(10, TimeUnit.SECONDS); Получение результата из CompletableFuture.
     *   Метод get блокирует текущий поток на до 10 секунд, ожидая завершения асинхронной операции.
     *   Если результат не будет получен в течение этого времени, будет выброшено исключение
     */
    @GetMapping("/all")
    public ResponseEntity<String> getAllCurrencies()
    {
        System.out.println("работает метод getAllCurrencies ALL .............." );

        try
        {
            CompletableFuture<String> future = currencyRequestService.processRequest("ALL", "");
            String response = future.get(10, TimeUnit.SECONDS);

            return ResponseEntity.status(HttpStatus.OK).body("По заданным параметрам успешно получен ответ : " + response);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("По заданным параметрам не возможно получить ответ " + e.getMessage());

        }
    }


    /**
     * Метод getSingleCurrency:
     * Получает GET запрос через REST API от API Gateway, содержащий в URL запросе {currencyCode} код валюты,
     * обрабатывает запрос и отправляет его в топик "request-currency-topic" Kafka,
     * для получения конкретной валюты и её курса от стороннего API.
     *
     * @param currencyCode код валюты, для получения курса.
     * @return ResponseEntity<String> - объект ResponseEntity, содержащий ответ с сообщением на API Gateway.
     */
    @GetMapping("/single/{currencyCode}")
    public ResponseEntity<String> getSingleCurrency(@PathVariable String currencyCode)
    {
        System.out.println("работает метод getSingleCurrency .............." );

        try
        {
            CompletableFuture<String> future = currencyRequestService.processRequest("SINGLE", currencyCode);
            String response = future.get(10, TimeUnit.SECONDS);

            return ResponseEntity.status(HttpStatus.OK).body("По заданным параметрам успешно получен ответ : " + response);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении валюты: " + e.getMessage());
        }
    }

    /**
     * Метод getFilteredCurrencies:
     * Получает GET запрос через REST API от API Gateway, содержащий в URL запросе {filter} список с кодом валюты,
     * обрабатывает запрос и отправляет его в топик "request-currency-topic" Kafka,
     * для получения этих валют и их курсов от стороннего API.
     *
     * @param filter строка с кодами валют для фильтрации.
     * @return ResponseEntity<String> - объект ResponseEntity, содержащий ответ с сообщением на API Gateway.
     */
    @GetMapping("/filter/{filter}")
    public ResponseEntity<String> getFilteredCurrencies(@PathVariable String filter)
    {
        System.out.println("работает метод getFilteredCurrencies .............." );

        try
        {
            CompletableFuture<String> future = currencyRequestService.processRequest("FILTER", filter);
            String response = future.get(10, TimeUnit.SECONDS);

            return ResponseEntity.status(HttpStatus.OK).body("По заданным параметрам успешно получен ответ : " + response);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при фильтрации валют: " + e.getMessage());
        }
    }
    //endRegion
}

//    возможный вариант без блокирования потока
//    @GetMapping("/all")
//    public CompletableFuture<ResponseEntity<String>> getAllCurrencies1() {
//        System.out.println("работает метод getAllCurrencies ALL ..............");
//
//        return currencyRequestService.processRequest("ALL", "")
//                .thenApply(response -> ResponseEntity.status(HttpStatus.OK)
//                        .body("По заданным параметрам успешно получен ответ: " + response))
//                .exceptionally(e -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("По заданным параметрам не возможно получить ответ: " + e.getMessage()));
//    }

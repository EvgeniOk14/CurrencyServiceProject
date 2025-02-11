package com.example.apigataway.controller.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Класс RequestController:
 *
 * Контроллер обрабатывает запросы через REST API для получения данных о курсах валют.
 * Использует WebClient для выполнения HTTP-запросов к другому сервису, работающему на порту 8081.
 *
 * Методы:
 * - {@link #getAllCurrencies()} — Получение всех доступных валют.
 * - {@link #getCurrency(String)} — Получение курса конкретной валюты по её коду.
 * - {@link #filterCurrencies(String)} — Получение списка валют по заданному фильтру.
 *
 * Доступные конечные точки:
 * - **GET** `/api/gateway/currencies/all` — Получение всех валют.
 * - **GET** `/api/gateway/currencies/single/{currencyCode}` — Получение одной валюты.
 * - **GET** `/api/gateway/currencies/filter/{filter}` — Получение валют по фильтру.
 *
 * Описание работы c webClient (реактивное программирование):
 *  - WebClient — это асинхронный и неблокирующий клиент HTTP,
 *    который позволяет отправлять запросы и получать ответы от других веб-сервисов.
 *    Он является частью Spring WebFlux и заменяет устаревший RestTemplate.
 *
 *  - Основные характеристики WebClient: (Асинхронность; Неблокирующий; Поддержка реактивного программирования (Mono и Flux))
 *      - Mono: Это реактивный тип, который представляет собой асинхронный поток данных,
 *        содержащий 0 или 1 элемент. Это можно рассматривать как "объект или ничего".
 *      - Flux: Это реактивный тип, который представляет собой асинхронный поток данных, содержащий 0 или более элементов.
 *        Это можно рассматривать как "коллекция объектов"
 *
 *   - webClient.get(): Начинает создание GET-запроса
 *
 *   - uri(url): Указывает URL, к которому будет отправлен запрос.
 *
 *   - retrieve(): Инициирует выполнение запроса. Этот метод возвращает ResponseSpec,
 *     который позволяет указать, как обрабатывать ответ.
 *
 *   - doOnNext(...): Этот оператор позволяет выполнять побочные действия
 *     (в данном случае логирование) с полученным ответом, когда он становится доступным.
 *
 *   - toEntity(String.class): Преобразует ответ в объект ResponseEntity, который включает тело ответа,
 *     заголовки и статусный код.
 *
 *   - onErrorReturn(...): Если во время выполнения запроса возникает ошибка,
 *     этот оператор возвращает ResponseEntity с кодом 500 и сообщением об ошибке.
 *
 */
@RestController
@RequestMapping("/api/gateway/currencies")
public class RequestController
{
    //region Fields
    private final WebClient webClient; // это экземпляр WebClient, для отправки (выполнения) HTTP-запросов к другому сервису
    //endRegion

    //region Constructor
    /**
     * Конструктор RequestController.
     *
     * @param webClient WebClient, настроенный для выполнения запросов.
     */
    public RequestController(WebClient webClient)
    {
        this.webClient = webClient; // Инициализация WebClient
    }
    //endRegion

    //region Methods
    /**
     * Метод getAllCurrencies:
     * Отправляет запрос на другой сервис для получения списка всех валют.
     *
     * @return Mono<ResponseEntity<String>> — Ответ с данными или сообщение об ошибке.
     *
     */
    @GetMapping("/all")
    public Mono<ResponseEntity<String>> getAllCurrencies() // возвращает Mono<ResponseEntity<String>>. Это означает, что ответ будет асинхронно отправлен обратно клиенту, когда он будет готов
    {
        String url = "http://localhost:8081/currencies/all"; // URL-адрес другого сервиса

        return webClient.get() // Выполнение GET-запроса (Начинает создание GET-запроса)
                .uri(url) // Указание URI куда именно отправить запрос ("http://localhost:8081/currencies/all")
                .retrieve() // Инициирует выполнение запроса. Результат будет доступен в будущем, когда ответ будет получен (возвращает Mono<ResponseEntity<String>>, т.к. это неблокирующий запрос).
                .toEntity(String.class) // Преобразование ответа в объект ResponseEntity (который включает как тело ответа, так и заголовки и статусный код)
                .doOnNext(response -> System.out.println("ответ response: " + response)) // Логирование ответа
                .onErrorReturn(ResponseEntity.status(500).body("Ошибка при получении данных")); // Обработка ошибок
    }

    /**
     * Метод getCurrency:
     * Отправляет запрос на другой сервис для получения курса одной валюты.
     *
     * @param currencyCode Код валюты.
     * @return Mono<ResponseEntity<String>> — Ответ с данными или сообщение об ошибке.
     */
    @GetMapping("/single/{currencyCode}")
    public Mono<ResponseEntity<String>> getCurrency(@PathVariable String currencyCode)
    {
        String url = "http://localhost:8081/currencies/single/" + currencyCode; // URL с параметром

        return webClient.get() // Выполнение GET-запроса
                .uri(url) // Указание URI куда именно отправить запрос ("http://localhost:8081/currencies/single/{currencyCode}")
                .retrieve() // Инициирует выполнение запроса. Результат будет доступен в будущем, когда ответ будет получен (возвращает Mono<ResponseEntity<String>>, т.к. это неблокирующий запрос).
                .toEntity(String.class) // Преобразование ответа в объект ResponseEntity (который включает как тело ответа, так и заголовки и статусный код)
                .doOnNext(response -> System.out.println("ответ response: " + response)) // Логирование ответа
                .onErrorReturn(ResponseEntity.status(500).body("Ошибка при получении данных")); // Обработка ошибок
    }

    /**
     * Метод filterCurrencies:
     * Отправляет запрос на другой сервис для получения списка валют по фильтру.
     *
     * @param filter Фильтр валют (например, список кодов через запятую).
     * @return Mono<ResponseEntity<String>> — Ответ с данными или сообщение об ошибке.
     */
    @GetMapping("/filter/{filter}")
    public Mono<ResponseEntity<String>> filterCurrencies(@PathVariable String filter)
    {
        String url = "http://localhost:8081/currencies/filter/" + filter; // URL с параметром

        return webClient.get() // Выполнение GET-запроса
                .uri(url) // Указание URI куда именно отправить запрос ("http://localhost:8081/currencies/filter/{filter}")
                .retrieve() // Инициирует выполнение запроса. Результат будет доступен в будущем, когда ответ будет получен (возвращает Mono<ResponseEntity<String>>, т.к. это неблокирующий запрос).
                .toEntity(String.class) // Преобразование ответа в объект ResponseEntity (который включает как тело ответа, так и заголовки и статусный код)
                .doOnNext(response -> {System.out.println("Ответ успешно отправлен на фронтенд: " + response.getBody());}) // Логирование ответа
                .onErrorReturn(ResponseEntity.status(500).body("Ошибка при получении данных")); // Обработка ошибок
    }
    //endRegion
}


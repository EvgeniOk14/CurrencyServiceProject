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
 */
@RestController
@RequestMapping("/api/gateway/currencies")
public class RequestController
{
    private final WebClient webClient; // WebClient для выполнения HTTP-запросов

    /**
     * Конструктор RequestController.
     *
     * @param webClient WebClient, настроенный для выполнения запросов.
     */
    public RequestController(WebClient webClient)
    {
        this.webClient = webClient; // Инициализация WebClient
    }

    /**
     * Метод getAllCurrencies:
     * Отправляет запрос на другой сервис для получения списка всех валют.
     *
     * @return Mono<ResponseEntity<String>> — Ответ с данными или сообщение об ошибке.
     */
    @GetMapping("/all")
    public Mono<ResponseEntity<String>> getAllCurrencies()
    {
        String url = "http://localhost:8081/currencies/all"; // URL-адрес другого сервиса

        return webClient.get() // Выполнение GET-запроса
                .uri(url) // Указание URI
                .retrieve() // Получение ответа
                .toEntity(String.class) // Преобразование ответа в объект ResponseEntity
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
                .uri(url) // Указание URI
                .retrieve() // Получение ответа
                .toEntity(String.class) // Преобразование ответа в объект ResponseEntity
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
                .uri(url) // Указание URI
                .retrieve() // Получение ответа
                .toEntity(String.class) // Преобразование ответа в объект ResponseEntity
                .doOnNext(response -> {
                    System.out.println("Ответ успешно отправлен на фронтенд: " + response.getBody()); // Логирование ответа
                })
                .onErrorReturn(ResponseEntity.status(500).body("Ошибка при получении данных")); // Обработка ошибок
    }
}


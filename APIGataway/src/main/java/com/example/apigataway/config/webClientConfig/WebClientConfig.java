package com.example.apigataway.config.webClientConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Класс WebClientConfig:
 *
 * Конфигурационный класс для настройки WebClient, который используется для выполнения
 * асинхронных HTTP-запросов в приложении.
 * WebClient является частью Spring WebFlux и предоставляет удобный API
 * для работы с веб-сервисами.
 *
 * Методы:
 * - {@link #webClient(WebClient.Builder)}: Создает и инициализирует экземпляр WebClient.
 */
@Configuration
public class WebClientConfig
{
    //region Methods
    /**
     * Метод webClient:
     * Создает и инициализирует экземпляр WebClient с использованием предоставленного
     * строителя WebClient.
     *
     * @param builder экземпляр WebClient.Builder, используемый для настройки WebClient.
     * @return инициализированный экземпляр WebClient.
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder)
    {

        return builder.build(); // Инициализация WebClient
    }
    //endRegion
}



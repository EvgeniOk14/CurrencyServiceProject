package com.example.requestcurrecyservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Класс AppConfig:
 *
 * Конфигурационный класс для настройки приложения. Содержит определение бина для RestTemplate,
 * который используется для выполнения HTTP-запросов.
 *
 * Методы:
 * - {@link #restTemplate()}: Создает и настраивает экземпляр RestTemplate для выполнения HTTP-запросов.
 *
 * Аннотации:
 * - @Configuration: Указывает, что класс является конфигурационным и управляется Spring-контейнером.
 */
@Configuration
public class AppConfig
{
    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }
}

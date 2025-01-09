package com.example.apigataway.config.swaggerConfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Класс SwaggerConfig:
 *
 * Конфигурационный класс для настройки документации API с использованием Swagger и OpenAPI.
 * Этот класс конфигурирует и настраивает отображение информации о Task Management API.
 *
 * Основные функции:
 * - Настройка информации о API, включая название, версию и описание.
 *
 * Зависимости:
 * - OpenAPI: для конфигурации Swagger/OpenAPI документации.
 *
 * Аннотации:
 * - @Configuration: указывает, что класс является конфигурационным классом Spring, который содержит бины.
 * - @Bean: используется для определения бина, который будет доступен в контексте Spring.
 *
 * Архитектурные особенности:
 * - Использует OpenAPI для конфигурации документации Swagger.
 *
 * Аннотации:
 * - @Configuration: указывает, что класс является конфигурационным классом Spring.
 */
@Configuration
public class SwaggerConfig
{
    /**
     * Метод customOpenAPI:
     * Конфигурирует и возвращает объект OpenAPI для настройки документации Swagger.
     * Настроена информация о API, включая название, описание и версию.
     *
     * @return Объект OpenAPI, настроенный с необходимыми параметрами.
     */
    @Bean // Указывает, что метод создает и возвращает бин, который будет доступен в контексте Spring.
    public OpenAPI customOpenAPI()
    {
        return new OpenAPI() // Создание нового объекта OpenAPI для настройки Swagger
                .info(new Info() // Добавление информации о API
                        .title("Task Management API") // Название API
                        .version("1.0") // Версия API
                        .description("API documentation for Task Management System")); // Описание API

    }
}
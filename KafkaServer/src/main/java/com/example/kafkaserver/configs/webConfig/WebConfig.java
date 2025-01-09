package com.example.kafkaserver.configs.webConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Класс WebConfig конфигурирует параметры CORS (Cross-Origin Resource Sharing) для веб-приложения.
 *
 * Этот класс позволяет настроить разрешенные источники, методы и другие параметры для работы с ApiGataway.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer
{
    /**
     * Настраивает разрешение CORS для всех маршрутов и определенных HTTP-методов.
     *
     * @param registry Объект CorsRegistry для регистрации правил CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**")            // Настройка CORS для всех маршрутов
                .allowedOrigins("http://localhost:8080") // Разрешенный источник (ApiGataway находиться на указанном адресе)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Разрешенные HTTP методы
                .allowCredentials(true);           // Разрешить передачу учетных данных (cookies, заголовки авторизации)
    }
}

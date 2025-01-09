package com.example.apigataway.config.webConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Класс WebConfig конфигурирует параметры CORS (Cross-Origin Resource Sharing) для веб-приложения.
 *
 * Этот класс позволяет настроить разрешенные источники, методы и другие параметры для работы с фронтендом,
 * который запрашивает ресурсы с другого домена.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer
{
    //region Methods
    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**") // Настройка CORS для всех маршрутов
                .allowedOrigins("http://localhost:5174") // Разрешенный источник (фронтенд находиться на указанном адресе)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешенные HTTP методы
                .allowedHeaders("*") // Разрешить заголовки
                .allowCredentials(true); // Разрешить передачу учетных данных (cookies, заголовки авторизации);
    }
    //endRegion
}

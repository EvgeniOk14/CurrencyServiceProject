package com.example.kafkaserver.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Класс KafkaTopicsConfig:
 *
 * Конфигурационный класс для настройки тем Kafka, используемых в приложении.
 * Поля класса соответствуют названиям тем, которые будут считываться
 * из конфигурации приложения (например, из application.yml или application.properties).
 *
 * Методы:
 * - {@link #getFetchCurrency()}: Получает название темы для получения курсов валют.
 * - {@link #getQueryCurrency()}: Получает название темы для запросов валют.
 * - {@link #getResponseCurrency()}: Получает название темы для ответов.
 * - {@link #setFetchCurrency(String)}: Устанавливает название темы для получения курсов валют.
 * - {@link #setQueryCurrency(String)}: Устанавливает название темы для запросов валют.
 * - {@link #setResponseCurrency(String)}: Устанавливает название темы для ответов.
 */
@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
@EnableKafka
public class KafkaTopicsConfig
{
    //region Fields
    private String fetchCurrency; // название топика "fetch-currency-topic"
    private String queryCurrency; // название топика "query-currency-topic"
    private String responseCurrency; // название топика "response-topic"
    //endRegion


    //region Getters
    /**
     * Метод getFetchCurrency:
     * получает поле fetchCurrency
     * **/
    public String getFetchCurrency()
    {
        return fetchCurrency;
    }

    /**
     * Метод getQueryCurrency:
     * получает поле queryCurrency
     * **/
    public String getQueryCurrency()
    {
        return queryCurrency;
    }

    /**
     * Метод getResponseCurrency:
     * получает поле responseCurrency
     * **/
    public String getResponseCurrency()
    {
        return responseCurrency;
    }
    //endRegion

    //region Setters
    /**
     * Метод setFetchCurrency:
     * устанавливает полю fetchCurrency новое значение
     * **/
    public void setFetchCurrency(String fetchCurrency)
    {
        this.fetchCurrency = fetchCurrency;
    }

    /**
     * Метод setQueryCurrency:
     * устанавливает полю queryCurrency новое значение
     * **/
    public void setQueryCurrency(String queryCurrency)
    {
        this.queryCurrency = queryCurrency;
    }

    /**
     * Метод setResponseCurrency:
     * устанавливает полю responseCurrency новое значение
     * **/
    public void setResponseCurrency(String responseCurrency)
    {
        this.responseCurrency = responseCurrency;
    }
    //endRegion
}

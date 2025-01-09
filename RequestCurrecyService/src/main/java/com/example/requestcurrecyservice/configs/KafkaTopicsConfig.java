package com.example.requestcurrecyservice.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Класс KafkaTopicsConfig:
 *
 * Конфигурационный класс, который хранит настройки для Kafka топиков.
 * Использует аннотацию @ConfigurationProperties для связывания свойств из конфигурационного файла
 * с полями класса. Позволяет легко управлять именами топиков, используемыми в приложении.
 *
 * Поля:
 * - fetchCurrency: название топика для получения курсов валют.
 * - queryCurrency: название топика для запросов к курсам валют.
 * - responseCurrency: название топика для отправки ответов с курсами валют.
 * - deadLetter: название топика для мертвых писем (Dead Letter Topic).
 *
 * Методы:
 * - {@link #getFetchCurrency()}: Получает название топика для получения курсов валют.
 * - {@link #getQueryCurrency()}: Получает название топика для запросов к курсам валют.
 * - {@link #getResponseCurrency()}: Получает название топика для отправки ответов с курсами валют.
 * - {@link #getDeadLetter()}: Получает название топика для мертвых писем.
 * - {@link #setFetchCurrency(String)}: Устанавливает новое значение для топика получения курсов валют.
 * - {@link #setQueryCurrency(String)}: Устанавливает новое значение для топика запросов к курсам валют.
 * - {@link #setResponseCurrency(String)}: Устанавливает новое значение для топика ответов с курсами валют.
 * - {@link #setDeadLetter(String)}: Устанавливает новое значение для топика мертвых писем.
 *
 * Аннотации:
 * - @Configuration: Указывает, что класс является конфигурационным и управляется Spring-контейнером.
 * - @ConfigurationProperties: Позволяет связывать свойства из конфигурационного файла с полями класса,
 *                             используя указанный префикс (в данном случае "kafka.topics").
 */
@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicsConfig
{
    //region Fields
    private String fetchCurrency; // название топика "fetch-currency-topic"
    private String queryCurrency; // название топика "query-currency-topic"

    private String responseCurrency; // название топика "response-topic"

    private  String deadLetter;   // название топика "dead-letter-topic"
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

    /**
     * Метод getDeadLetter:
     * получает поле deadLetter
     * **/
    public String getDeadLetter()
    {
        return deadLetter;
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

    /**
     * Метод setDeadLetter:
     * устанавливает полю deadLetter новое значение
     * **/
    public void setDeadLetter(String deadLetter)
    {
        this.deadLetter = deadLetter;
    }

    //endRegion
}

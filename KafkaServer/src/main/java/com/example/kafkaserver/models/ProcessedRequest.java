package com.example.kafkaserver.models;

import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Класс ProcessedRequest:
 *
 * Представляет обработанный запрос на получение курсов валют.
 * Этот класс содержит информацию о курсах, базовой валюте, дате запроса,
 * целевой валюте, идентификаторе запроса и идентификаторе ресурса.
 *
 * Поля:
 * - {@link #rates}: Карта, содержащая курсы валют, где ключом является
 *   название валюты, а значением - курс.
 * - {@link #baseCurrency}: Базовая валюта, относительно которой предоставлены курсы.
 * - {@link #date}: Дата, на которую были получены курсы валют.
 * - {@link #currency}: Целевая валюта запроса.
 * - {@link #requestId}: Уникальный идентификатор запроса.
 * - {@link #resourceId}: Уникальный идентификатор ресурса, к которому относится запрос.
 */
@Component
public class ProcessedRequest
{
    //region Fields
    private Map<String, Double> rates; // Карта курсов валют
    private String baseCurrency; // Базовая валюта
    private String date; // Дата запроса
    private String currency; // Целевая валюта
    private String requestId; // Уникальный идентификатор запроса
    private String resourceId; // Уникальный идентификатор ресурса
    //endRegion

    //region Constructors
    /**
     * Конструктор ProcessedRequest:
     * Инициализирует объект ProcessedRequest с заданными параметрами.
     *
     * @param rates карта курсов валют.
     * @param baseCurrency базовая валюта.
     * @param date дата запроса.
     * @param currency целевая валюта.
     * @param requestId уникальный идентификатор запроса.
     * @param resourceId уникальный идентификатор ресурса.
     */
    public ProcessedRequest(Map<String, Double> rates, String baseCurrency, String date, String currency,
                            String requestId, String resourceId)
    {
        this.rates = rates;
        this.baseCurrency = baseCurrency;
        this.date = date;
        this.currency = currency;
        this.requestId = requestId;
        this.resourceId = resourceId;
    }

    /**
     * Конструктор по умолчанию ProcessedRequest:
     * Инициализирует объект ProcessedRequest без параметров.
     */
    public ProcessedRequest()
    {
        // Конструктор по умолчанию
    }
    //endRegion

    //region Getters
    /**
     * Метод getRates:
     * Возвращает карту курсов валют.
     *
     * @return карта курсов валют.
     */
    public Map<String, Double> getRates()
    {
        return rates;
    }

    /**
     * Метод getDate:
     * Возвращает дату запроса.
     *
     * @return дата запроса.
     */
    public String getDate()
    {
        return date;
    }

    /**
     * Метод getBaseCurrency:
     * Возвращает базовую валюту.
     *
     * @return базовая валюта.
     */
    public String getBaseCurrency()
    {
        return baseCurrency;
    }

    /**
     * Метод getCurrency:
     * Возвращает целевую валюту.
     *
     * @return целевая валюта.
     */
    public String getCurrency()
    {
        return currency;
    }

    /**
     * Метод getRequestId:
     * Возвращает уникальный идентификатор запроса.
     *
     * @return уникальный идентификатор запроса.
     */
    public String getRequestId()
    {
        return requestId;
    }

    /**
     * Метод getResourceId:
     * Возвращает уникальный идентификатор ресурса.
     *
     * @return уникальный идентификатор ресурса.
     */
    public String getResourceId()
    {
        return resourceId;
    }
    //endRegion

    //region Setters
    /**
     * Метод setRates:
     * Устанавливает карту курсов валют.
     *
     * @param rates карта курсов валют.
     */
    public void setRates(Map<String, Double> rates)
    {
        this.rates = rates;
    }

    /**
     * Метод setDate:
     * Устанавливает дату запроса.
     *
     * @param date дата запроса.
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * Метод setBaseCurrency:
     * Устанавливает базовую валюту.
     *
     * @param baseCurrency базовая валюта.
     */
    public void setBaseCurrency(String baseCurrency)
    {
        this.baseCurrency = baseCurrency;
    }

    /**
     * Метод setCurrency:
     * Устанавливает целевую валюту.
     *
     * @param currency целевая валюта.
     */
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    /**
     * Метод setRequestId:
     * Устанавливает уникальный идентификатор запроса.
     *
     * @param requestId уникальный идентификатор запроса.
     */
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    /**
     * Метод setResourceId:
     * Устанавливает уникальный идентификатор ресурса.
     *
     * @param resourceId уникальный идентификатор ресурса.
     */
    public void setResourceId(String resourceId)
    {
        this.resourceId = resourceId;
    }
    //endRegion
}

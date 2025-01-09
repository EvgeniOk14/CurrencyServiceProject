package com.example.fetchcurrencyservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Класс ExchangeRateResponseFromAPI:
 *
 * Представляет ответ от стороннего API с курсами валют. Содержит поля, которые соответствуют
 * структуре ответа API, а также предоставляет конструкторы, геттеры и сеттеры для работы с этими полями.
 *
 * Поля:
 * - id: уникальный идентификатор записи.
 * - success: параметр успешного ответа от стороннего API.
 * - timestamp: время, когда были получены курсы валют.
 * - base: базовая валюта, относительно которой представлены курсы.
 * - date: дата, на момент которой предоставляются курсы валют.
 * - rates: карта, где ключом является код валюты, а значением - соответствующий обменный курс.
 *
 * Конструкторы:
 * - {@link #ExchangeRateResponseFromAPI(boolean, long, String, String, Map)}: Конструктор с параметрами
 *   для инициализации всех полей класса.
 * - {@link #ExchangeRateResponseFromAPI()}: Конструктор по умолчанию.
 *
 * Методы:
 * - {@link #isSuccess()}: Возвращает значение поля success.
 * - {@link #getTimestamp()}: Возвращает значение поля timestamp.
 * - {@link #getBase()}: Возвращает значение поля base.
 * - {@link #getDate()}: Возвращает значение поля date.
 * - {@link #getRates()}: Возвращает значение поля rates.
 * - {@link #setSuccess(boolean)}: Устанавливает значение поля success.
 * - {@link #setTimestamp(long)}: Устанавливает значение поля timestamp.
 * - {@link #setBase(String)}: Устанавливает значение поля base.
 * - {@link #setDate(String)}: Устанавливает значение поля date.
 * - {@link #setRates(Map)}: Устанавливает значение поля rates.
 *
 * Аннотации:
 * - @Component: Указывает, что класс является компонентом Spring и может быть управляем контейнером.
 * - @Id: Указывает, что поле id является уникальным идентификатором.
 * - @GeneratedValue: Указывает, что значение поля id будет автоматически сгенерировано.
 * - @JsonProperty: Указывает, как поля класса будут сериализованы/десериализованы в/из JSON.
 */
@Component
public class ExchangeRateResponseFromAPI
{
    //region Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("success")
    private boolean success; // Параметр успешного ответа от стороннего API, поле присутствует в ответе

    @JsonProperty("timestamp")
    private long timestamp; // Время, когда были получены курсы валют

    @JsonProperty("base")
    private String base; // Базовая валюта, относительно которой представлены курсы валют

    @JsonProperty("date")
    private String date; // Дата, на момент которой предоставляются курсы валют

    @JsonProperty("rates")
    private Map<String, Double> rates; // Ключом является - код валют, значением - соответствующий обменный курс этой валюты по отношению к базовой валюте
    //endRegion

    //region Constructors
    public ExchangeRateResponseFromAPI(boolean success, long timestamp, String base, String date, Map<String, Double> rates)
    {
        this.success = success;
        this.timestamp = timestamp;
        this.base = base;
        this.date = date;
        this.rates = rates;
    }
    public ExchangeRateResponseFromAPI()
    {
        //default Constructor
    }
    //endRegion

    //region Getters
    public boolean isSuccess()
    {
        return success;
    }
    public long getTimestamp()
    {
        return timestamp;
    }
    public String getBase()
    {
        return base;
    }
    public String getDate()
    {
        return date;
    }
    public Map<String, Double> getRates()
    {
        return rates;
    }
    //endRegion

    //region Setters

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setBase(String base)
    {
        this.base = base;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public void setRates(Map<String, Double> rates)
    {
        this.rates = rates;
    }
    //endRegion

}

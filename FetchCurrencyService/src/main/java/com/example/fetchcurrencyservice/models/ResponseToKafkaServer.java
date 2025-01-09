package com.example.fetchcurrencyservice.models;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Класс ResponseToKafkaServer:
 *
 * Представляет сущность, которая соответствует записи в таблице "response_to_kafka" в базе данных.
 * Этот класс используется для хранения данных ответа, полученных от Kafka-сервера, включая курсы валют,
 * базовую валюту, дату, валюту и идентификатор запроса.
 *
 * Поля:
 * - id: уникальный идентификатор записи в таблице "response_to_kafka".
 * - rates: карта, где ключом является код валюты, а значением - соответствующий обменный курс.
 * - baseCurrency: базовая валюта, относительно которой представлены курсы валют.
 * - date: дата, на момент которой предоставляются курсы валют.
 * - currency: валюта, для которой предоставлены курсы.
 * - requestId: уникальный идентификатор запроса, связанного с этим ответом.
 *
 * Конструкторы:
 * - {@link #ResponseToKafkaServer(Map, String, String, String, String)}: Конструктор с параметрами для инициализации всех полей.
 * - {@link #ResponseToKafkaServer()}: Конструктор по умолчанию.
 *
 * Методы:
 * - {@link #getRates()}: Возвращает карту курсов валют.
 * - {@link #getDate()}: Возвращает дату, на момент которой предоставляются курсы.
 * - {@link #getBaseCurrency()}: Возвращает базовую валюту.
 * - {@link #getCurrency()}: Возвращает валюту, для которой предоставлены курсы.
 * - {@link #getRequestId()}: Возвращает идентификатор запроса.
 * - {@link #setRates(Map)}: Устанавливает карту курсов валют.
 * - {@link #setDate(String)}: Устанавливает дату, на момент которой предоставляются курсы.
 * - {@link #setBaseCurrency(String)}: Устанавливает базовую валюту.
 * - {@link #setCurrency(String)}: Устанавливает валюту, для которой предоставлены курсы.
 * - {@link #setRequestId(String)}: Устанавливает идентификатор запроса.
 *
 * Аннотации:
 * - @Component: Указывает, что класс является компонентом Spring и может быть управляем контейнером.
 * - @Entity: Указывает, что класс является сущностью и будет отображен в таблице базы данных.
 * - @Table: Определяет имя таблицы в базе данных, с которой связана сущность.
 * - @Id: Указывает, что поле id является уникальным идентификатором.
 * - @GeneratedValue: Указывает, что значение поля id будет автоматически сгенерировано.
 * - @ElementCollection: Указывает, что поле rates представляет собой коллекцию элементов.
 * - @CollectionTable: Определяет таблицу, в которой будут храниться элементы коллекции.
 * - @MapKeyColumn: Указывает имя столбца для ключей карты.
 * - @Column: Указывает свойства столбцов в таблице (имя столбца и ограничения).
 */
@Component
@Entity
@Table(name = "response_to_kafka")
public class ResponseToKafkaServer
{
    //region Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Добавлено поле id для идентификации записи

    @ElementCollection
    @CollectionTable(name = "exchange_rates", joinColumns = @JoinColumn(name = "response_id"))
    @MapKeyColumn(name = "currency")
    @Column(name = "rate")
    private Map<String, Double> rates;

    @Column(name = "base_currency")
    private String baseCurrency;
    @Column(name = "date")
    private String date;
    @Column(name = "currency")
    private String currency;
    @Column(name = "request_id")
    private String requestId;// Устанавливаем requestId в ответ
    //endRegion

    //region Constructors

    public ResponseToKafkaServer(Map<String, Double> rates, String baseCurrency, String date, String currency, String requestId)
    {
        this.rates = rates;
        this.baseCurrency = baseCurrency;
        this.date = date;
        this.currency = currency;
        this.requestId = requestId;
    }
    // Пустой конструктор
    public ResponseToKafkaServer()
    {
        // default constructor
    }
    //endRegion

    //region Getters
    public Map<String, Double> getRates()
    {
        return rates;
    }
    public String getDate()
    {
        return date;
    }

    public String getBaseCurrency()
    {
        return baseCurrency;
    }

    public String getCurrency()
    {
        return currency;
    }

    public String getRequestId()
    {
        return requestId;
    }
    //endRegion

    //region Setters
    public void setRates(Map<String, Double> rates)
    {
        this.rates = rates;
    }
    public void setDate(String date)
    {
        this.date = date;
    }

    public void setBaseCurrency(String baseCurrency)
    {
        this.baseCurrency = baseCurrency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }
    //endRegion
}

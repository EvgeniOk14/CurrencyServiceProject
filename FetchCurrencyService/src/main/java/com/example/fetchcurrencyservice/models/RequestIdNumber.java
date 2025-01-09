package com.example.fetchcurrencyservice.models;

import jakarta.persistence.Id;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * Класс RequestIdNumber:
 *
 * Представляет сущность, которая соответствует документу в коллекции "requestId-collection" в базе данных MongoDB.
 * Содержит уникальный идентификатор запроса, дату истечения срока действия и идентификатор документа.
 *
 * Поля:
 * - id: уникальный номер, под которым хранятся записи в коллекции "requestId-collection".
 * - requestId: уникальный номер сообщения из Kafka-топика.
 * - expirationDate: дата истечения срока действия номера запроса.
 *
 * Конструкторы:
 * - {@link #RequestIdNumber(String, Date)}: Конструктор с параметрами для инициализации полей requestId и expirationDate.
 * - {@link #RequestIdNumber()}: Конструктор по умолчанию.
 *
 * Методы:
 * - {@link #getId()}: Возвращает значение поля id.
 * - {@link #getRequestId()}: Возвращает значение поля requestId.
 * - {@link #getExpirationDate()}: Возвращает значение поля expirationDate.
 * - {@link #setRequestId(String)}: Устанавливает значение поля requestId.
 * - {@link #setExpirationDate(Date)}: Устанавливает значение поля expirationDate.
 *
 * Аннотации:
 * - @Component: Указывает, что класс является компонентом Spring и может быть управляем контейнером.
 * - @Document: Указывает, что класс является документом в MongoDB и определяет имя коллекции, с которой он связан.
 * - @Id: Указывает, что поле id является уникальным идентификатором документа в коллекции.
 */
@Component
@Document(collection = "requestId-collection")
public class RequestIdNumber
{
    //region Fields
    @Id
    private ObjectId id; // уникальный номер под которым храняться записи в коллекции "requestId-collection" в базе данных MongoDB
    private String requestId; // уникальный номер сообщения из топика кафка
    private Date expirationDate; // дата истечения срока действия номера
    //endRegion

    //region Constructors
    public RequestIdNumber(String requestId, Date expirationDate)
    {
        this.requestId = requestId;
        this.expirationDate = expirationDate;
    }
    public RequestIdNumber()
    {
        // default constructor
    }
    //endregion

    //region Getters
    public ObjectId getId()
    {
        return id;
    }
    public String getRequestId()
    {
        return requestId;
    }
    public Date getExpirationDate()
    {
        return expirationDate;
    }
    //endRegion

    //region Setters
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }
    public void setExpirationDate(Date expirationDate)
    {
        this.expirationDate = expirationDate;
    }
    //endRegion
}
